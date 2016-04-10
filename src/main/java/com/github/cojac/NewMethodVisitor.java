/*
 * *
 *    Copyright 2011-2016 Valentin Gazzola & Frédéric Bapst
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

package com.github.cojac;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.LocalVariablesSorter;

import com.github.cojac.instrumenters.IOpcodeInstrumenter;
import com.github.cojac.instrumenters.NewInstrumenter;
import com.github.cojac.instrumenters.IOpcodeInstrumenterFactory;
import com.github.cojac.models.CheckedMaths;

import java.util.Arrays;
import java.util.List;

import static org.objectweb.asm.Opcodes.*;

final class NewMethodVisitor extends LocalVariablesSorter {
    private final IOpcodeInstrumenterFactory factory;
    private final InstrumentationStats stats;
    private final Args args;
    private final String classPath;
    NewInstrumenter instrumenter ;
    NewMethodVisitor(int access, String desc, MethodVisitor mv, InstrumentationStats stats, Args args, String classPath, IOpcodeInstrumenterFactory factory) {
        super(Opcodes.ASM5, access, desc, mv);

        this.stats = stats;
        this.args = args;
        this.factory = factory;

        this.classPath = classPath;
        instrumenter = NewInstrumenter.getInstance(args, stats);
    }

    @Override
    public void visitInsn(int opCode) {
        IOpcodeInstrumenter instrumenter = factory.getInstrumenter(opCode);

        //Delegate to parent
        if (instrumenter == null) {
            super.visitInsn(opCode);
        } else {
            instrumenter.instrument(mv, opCode); //, classPath, methods, reaction, this);
        }
    }

   /* @Override
    public void visitIincInsn(int index, int value) {
        int opCode=Opcodes.IINC;
        IOpcodeInstrumenter instrumenter = factory.getInstrumenter(opCode);
        if (args.isOperationEnabled(Arg.IINC)) {
            visitVarInsn(ILOAD, index);
            mv.visitLdcInsn(value);
            instrumenter.instrument(mv, opCode);//, classPath, methods, reaction, this);
            visitVarInsn(ISTORE, index);
            stats.incrementCounterValue(Opcodes.IINC); //Arg.IINC);
        } else {
            super.visitIincInsn(index, value);
        }
    }
*/
    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        //System.out.println("visitMethodInsn: "+name+ " Description: "+desc);
        if (opcode == INVOKESTATIC /*&& args.isOperationEnabled(Arg.MATHS)*/ &&
            ("java/lang/Math".equals(owner) || "java/lang/StrictMath".equals(owner))) {
            /*if ("(D)D".equals(desc) && UNARY_METHODS.contains(name) || "(DD)D".equals(desc) && BINARY_METHODS.contains(name)) {
                String msg= owner + '.' + name + desc;
                String logFileName=""; 
                if (args.isSpecified(Arg.CALL_BACK)) {
                    logFileName = args.getValue(Arg.CALL_BACK); // No, I'm not proud of that trick...
                } else {
                    logFileName = args.getValue(Arg.LOG_FILE);
                }
                int reactionType=args.getReactionType().value();
                mv.visitMethodInsn(INVOKESTATIC, owner, name, desc, itf);
                protectMethodInvocation(reactionType, logFileName, msg);
            } else {
                mv.visitMethodInsn(INVOKESTATIC, owner, name, desc, itf);
            }*/
            
            if (instrumenter.wantsToInstrumentMethod(opcode, name,desc)){
                instrumenter.instrumentMethod(mv, name, desc);
            }else{
                super.visitMethodInsn(opcode, owner, name, desc, itf);
            }
        } else {
            super.visitMethodInsn(opcode, owner, name, desc, itf);
        }
    }

    //checkMathMethodResult(double r, int reaction, 
    //                      String logFileName, String operationName)
    
    private void protectMethodInvocation(int reactionType, String logFileName, String msg) {
        mv.visitInsn(DUP2);
        mv.visitLdcInsn(new Integer(reactionType));
        mv.visitLdcInsn(logFileName);
        mv.visitLdcInsn(msg);
        mv.visitMethodInsn(INVOKESTATIC, CheckedMaths.CHECK_MATH_RESULT_PATH, CheckedMaths.CHECK_MATH_RESULT_NAME, Signatures.CHECK_MATH_RESULT, false);
    }
}