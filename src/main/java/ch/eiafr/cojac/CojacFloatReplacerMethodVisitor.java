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

import ch.eiafr.cojac.instrumenters.OpCodeInstrumenter;
import ch.eiafr.cojac.instrumenters.OpCodeInstrumenterFactory;
import ch.eiafr.cojac.instrumenters.InvokableMethod;
import static ch.eiafr.cojac.instrumenters.InvokableMethod.*;
import ch.eiafr.cojac.models.CheckedMaths;
import ch.eiafr.cojac.models.FloatWrapper;
import ch.eiafr.cojac.reactions.Reaction;

import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.LocalVariablesSorter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.objectweb.asm.Opcodes.*;

final class CojacFloatReplacerMethodVisitor extends LocalVariablesSorter {
    private final OpCodeInstrumenterFactory factory;
    private final InstrumentationStats stats;
    private final Args args;
    private final Methods methods;
    private final Reaction reaction;
    private final String classPath;

    private final Map<Integer, Integer> varMap = new HashMap<>();

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

    CojacFloatReplacerMethodVisitor(int access, String desc, MethodVisitor mv, InstrumentationStats stats, Args args, Methods methods, Reaction reaction, String classPath, OpCodeInstrumenterFactory factory) {
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
        if (instrumenter != null) {
            instrumenter.instrument(mv, opCode, classPath, methods, reaction, this);
        } else { //Delegate to parent
            super.visitInsn(opCode);
        }
    }

    @Override
    public void visitIincInsn(int index, int value) {
        int opCode=Opcodes.IINC;
        OpCodeInstrumenter instrumenter = factory.getInstrumenter(opCode, Arg.fromOpCode(opCode));
        if (args.isOperationEnabled(Arg.IINC)) {
            if ( methods != null) {// Maybe make better than methods != null
                visitVarInsn(ILOAD, index);
                mv.visitLdcInsn(value);
                mv.visitMethodInsn(INVOKESTATIC, classPath, methods.getMethod(IINC), Signatures.CHECK_INTEGER_BINARY);
                visitVarInsn(ISTORE, index);
            } else {
                visitVarInsn(ILOAD, index);
                mv.visitLdcInsn(value);
                instrumenter.instrument(mv, opCode, classPath, methods, reaction, this);
                visitVarInsn(ISTORE, index);
            }

            stats.incrementCounterValue(Arg.IINC);
        } else {
            super.visitIincInsn(index, value);
        }
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc) {
        desc=replaceFloatMethodDescription(desc);
        // TODO: something smarter, taking into account the method call kinds ?
        super.visitMethodInsn(opcode, owner, name, desc);
    }

    //checkMathMethodResult(double r, int reaction, 
    //                      String logFileName, String operationName)
    
//    private void protectMethodInvocation(int reactionType, String logFileName, String msg) {
//        mv.visitInsn(DUP2);
//        mv.visitLdcInsn(new Integer(reactionType));
//        mv.visitLdcInsn(logFileName);
//        mv.visitLdcInsn(msg);
//        mv.visitMethodInsn(INVOKESTATIC, CheckedMaths.CHECK_MATH_RESULT_PATH, CheckedMaths.CHECK_MATH_RESULT_NAME, Signatures.CHECK_MATH_RESULT);
//    }

    @Override
    public void visitVarInsn(int opcode, int var) {
        if (opcode==FLOAD) {
            instrumentLoad(mv, opcode, var);
        } else if (opcode == FSTORE) {
            instrumentStore(mv, opcode, var);
        } else {
            super.visitVarInsn(opcode, var);
        }
    }

    private void instrumentStore(MethodVisitor mv, int opcode, int var) {
        int rvar;
        if (varMap.containsKey(var)) {
            rvar=varMap.get(var);
        } else {
            rvar=newLocal(COJAC_FLOAT_WRAPPER_TYPE);
            varMap.put(var, rvar);
        }
        //mv.visitVarInsn(ASTORE, rvar);        
        mv.visitVarInsn(ASTORE, var);        
    }

    private void instrumentLoad(MethodVisitor mv, int opcode, int var) {
        int rvar;
        if (varMap.containsKey(var)) {
            rvar=varMap.get(var);
        } else {
            rvar=newLocal(COJAC_FLOAT_WRAPPER_TYPE);
            varMap.put(var, rvar);
        }
        //mv.visitVarInsn(ALOAD, rvar);                
        mv.visitVarInsn(ALOAD, var);                
    }

    
    @Override
    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
        desc=afterFloatReplacement(desc);
        super.visitLocalVariable(name, desc, signature, start, end, index);
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
        desc=afterFloatReplacement(desc);
        super.visitFieldInsn(opcode, owner, name, desc);
    }

    @Override
    public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
        super.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
    }

    @Override
    public void visitLdcInsn(Object cst) {
        super.visitLdcInsn(cst);
        if (! (cst instanceof Float)) {
            return;
        }
        // Ok, it's a float constant
        InvokableMethod.FROM_FLOAT.invokeStatic(mv);        
    }
}