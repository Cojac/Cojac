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

import ch.eiafr.cojac.instrumenters.IOpcodeInstrumenter;
import ch.eiafr.cojac.instrumenters.IOpcodeInstrumenterFactory;
import ch.eiafr.cojac.instrumenters.InvokableMethod;
import static ch.eiafr.cojac.instrumenters.InvokableMethod.*;
import ch.eiafr.cojac.reactions.IReaction;

import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.LocalVariablesSorter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.objectweb.asm.Opcodes.*;

final class FloatReplacerMethodVisitor extends LocalVariablesSorter {
    private final IOpcodeInstrumenterFactory factory;
    private final InstrumentationStats stats;
    private final Args args;
    private final Methods methods;
    private final IReaction reaction;
    private final String classPath;

    private final Map<Integer, Integer> varMap = new HashMap<>();
    private final Set<Integer> floatVars = new HashSet<>();
    private final Set<Integer> nonFloatVars = new HashSet<>();

    FloatReplacerMethodVisitor(int access, String desc, MethodVisitor mv, InstrumentationStats stats, Args args, Methods methods, IReaction reaction, String classPath, IOpcodeInstrumenterFactory factory) {
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
        IOpcodeInstrumenter instrumenter = factory.getInstrumenter(opCode);
        if (instrumenter != null) {
            instrumenter.instrument(mv, opCode, classPath, methods, reaction, this);
        } else { //Delegate to parent
            super.visitInsn(opCode);
        }
    }

    @Override
    public void visitIincInsn(int index, int value) {
        int opCode=Opcodes.IINC;
        IOpcodeInstrumenter instrumenter = factory.getInstrumenter(opCode);
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

    @Override
    public void visitVarInsn(int opcode, int var) {
        if (opcode==FLOAD || opcode==FSTORE) {
            int replacedOpcode = (opcode==FLOAD) ? ALOAD:ASTORE;
            int replacedVar=var;
            if (nonFloatVars.contains(var)) {
                if (varMap.containsKey(var)) {
                    replacedVar=varMap.get(var);
                } else {
                    replacedVar=newLocal(COJAC_FLOAT_WRAPPER_TYPE);
                    varMap.put(var, replacedVar);
                }
            } else {
                floatVars.add(var);
            }
            mv.visitVarInsn(replacedOpcode, replacedVar);
        } else {
            int replacedVar=var;
            if (floatVars.contains(var)) {
                if (varMap.containsKey(var)) {
                    replacedVar=varMap.get(var);
                } else {
                    replacedVar=newLocal(typeFromVarInsn(opcode));
                    varMap.put(var, replacedVar);
                }
            } else {
                nonFloatVars.add(var);
            }
            mv.visitVarInsn(opcode, replacedVar);
        }
    }

    private Type typeFromVarInsn(int opcode) {
        Type objt = Type.getObjectType("java/lang/Object");
        switch(opcode) {
        case RET: // TODO: verify RET uses an int variable
        case ISTORE:
        case ILOAD: return Type.INT_TYPE;
        case LSTORE:
        case LLOAD: return Type.LONG_TYPE;
        case DSTORE:
        case DLOAD: return Type.DOUBLE_TYPE;
        case ASTORE:
        case ALOAD: return objt;  // TODO: verify if ALOAD can use an java.lang.Object variable
        }
        return null;
    }

    @Override
    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
        //TODO something coherent, even if it is only for the benefit of the debuggers...
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
        if (cst instanceof Float) {
            InvokableMethod.FROM_FLOAT.invokeStatic(mv);        
        }
    }
}