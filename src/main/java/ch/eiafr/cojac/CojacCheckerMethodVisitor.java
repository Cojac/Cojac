/*
 * *
 *    Copyright 2011 Baptiste Wicht & Frédéric Bapst
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package ch.eiafr.cojac;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.LocalVariablesSorter;

import java.util.Arrays;
import java.util.List;

import ch.eiafr.cojac.instrumenters.OpCodeInstrumenter;
import ch.eiafr.cojac.instrumenters.OpCodeInstrumenterFactory;
import ch.eiafr.cojac.reactions.Reaction;

import static org.objectweb.asm.Opcodes.*;

final class CojacCheckerMethodVisitor extends LocalVariablesSorter {
    private final OpCodeInstrumenterFactory factory;
    private final InstrumentationStats stats;
    private final Args args;
    private final Methods methods;
    private final Reaction reaction;
    private final String classPath;

    private static final List<String> UNARY_METHODS = Arrays.asList(
            "ceil", "round", "floor",
            "cos", "sin", "tan",
            "acos", "asin", "atan",
            "cosh", "sinh", "tanh",
            "exp", "expm1",
            "log", "log10", "log1p",
            "sqrt", "cbrt",
            "rint", "nextUp");

    private static final List<String> BINARY_METHODS =
            Arrays.asList("atan2", "pow", "hypot", "copySign", "nextAfter", "scalb");

    CojacCheckerMethodVisitor(int access, String desc, MethodVisitor mv, InstrumentationStats stats, Args args, Methods methods, Reaction reaction, String classPath, OpCodeInstrumenterFactory factory) {
        super(access, desc, mv);

        this.stats = stats;
        this.args = args;
        this.factory = factory;

        this.methods = methods;
        this.reaction = reaction;
        this.classPath = classPath;
    }

    @Override
    public void visitInsn(int opCode) {
        OpCodeInstrumenter instrumenter = factory.getInstrumenter(opCode, Arg.fromOpCode(opCode));

        //Delegate to parent
        if(instrumenter == null){
            super.visitInsn(opCode);
        } else {
            instrumenter.instrument(mv, opCode, classPath, methods, reaction, this);
        }
    }

    @Override
    public void visitIincInsn(int index, int value) {
        if (args.isOperationEnabled(Arg.IINC)) {
            if (args.isSpecified(Arg.OP_SIZE) && methods != null) {//TODO MAKe better than methods != null
                visitVarInsn(ILOAD, index);
                mv.visitLdcInsn(value);
                mv.visitMethodInsn(INVOKESTATIC, classPath, methods.getMethod(IINC), Signatures.CHECK_INTEGER_BINARY);
                visitVarInsn(ISTORE, index);
            } else {
                if(args.isSpecified(Arg.VARIABLES)){
                    iincWithVariables(index, value);
                } else {
                    iinc(index, value);
                }
            }

            stats.incrementCounterValue(Arg.IINC);
        } else {
            super.visitIincInsn(index, value);
        }
    }

    private void iinc(int index, int value) {
        Label fin = new Label();

        visitVarInsn(ILOAD, index);
        visitVarInsn(ILOAD, index);
        mv.visitLdcInsn(value);
        mv.visitInsn(IADD);
        mv.visitInsn(IXOR);
        mv.visitLdcInsn(value);
        visitVarInsn(ILOAD, index);
        mv.visitLdcInsn(value);
        mv.visitInsn(IADD);
        mv.visitInsn(IXOR);
        mv.visitInsn(IAND);
        mv.visitJumpInsn(IFGE, fin);
        reaction.insertReactionCall(mv, "Overflow : IINC", methods, classPath);
        mv.visitLabel(fin);
        visitVarInsn(ILOAD, index);
        mv.visitLdcInsn(value);
        mv.visitInsn(IADD);
        visitVarInsn(ISTORE, index);
    }

    private void iincWithVariables(int index, int value) {
        int r = newLocal(Type.INT_TYPE);

        visitVarInsn(ILOAD, index);
        mv.visitLdcInsn(value);
        mv.visitInsn(IADD);
        visitVarInsn(ISTORE, r);
        visitVarInsn(ILOAD, index);
        visitVarInsn(ILOAD, r);
        mv.visitInsn(IXOR);
        mv.visitLdcInsn(value);
        visitVarInsn(ILOAD, r);
        mv.visitInsn(IXOR);
        mv.visitInsn(IAND);
        Label l0 = new Label();
        mv.visitJumpInsn(IFGE, l0);
        reaction.insertReactionCall(mv, "Overflow : IINC", methods, classPath);
        mv.visitLabel(l0);
        visitVarInsn(ILOAD, r);
        visitVarInsn(ISTORE, index);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc) {
        if (opcode == INVOKESTATIC && args.isOperationEnabled(Arg.MATHS) &&
                ("java/lang/Math".equals(owner) || "java/lang/StrictMath".equals(owner))) {
            if ("(D)D".equals(desc) && UNARY_METHODS.contains(name) || "(DD)D".equals(desc) && BINARY_METHODS.contains(name)) {
                protectMethodInvocation(owner, name, desc);
            } else {
                mv.visitMethodInsn(INVOKESTATIC, owner, name, desc);
            }
        } else {
            super.visitMethodInsn(opcode, owner, name, desc);
        }
    }

    private void protectMethodInvocation(String owner, String name, String desc) {
        mv.visitMethodInsn(INVOKESTATIC, owner, name, desc);

        mv.visitInsn(DUP2);
        mv.visitInsn(DUP2);
        mv.visitInsn(DCMPL);
        Label l0 = new Label();
        mv.visitJumpInsn(IFEQ, l0);

        reaction.insertReactionCall(mv, "Maths error (NaN) with " + owner + '.' + name + desc, methods, classPath);

        mv.visitLabel(l0);

        mv.visitInsn(DUP2);
        mv.visitLdcInsn(new Double("Infinity"));
        mv.visitInsn(DCMPL);
        Label l1 = new Label();
        mv.visitJumpInsn(IFNE, l1);

        reaction.insertReactionCall(mv, "Maths error (Infinity) with " + owner + '.' + name + desc, methods, classPath);

        mv.visitLabel(l1);

        mv.visitInsn(DUP2);
        mv.visitLdcInsn(new Double("-Infinity"));
        mv.visitInsn(DCMPL);
        Label l2 = new Label();
        mv.visitJumpInsn(IFNE, l2);
        reaction.insertReactionCall(mv, "Maths error (-Infinity) with " + owner + '.' + name, methods, classPath);
        mv.visitLabel(l2);
    }
}