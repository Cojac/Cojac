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

package ch.eiafr.cojac.instrumenters.stack;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.LocalVariablesSorter;

import ch.eiafr.cojac.Methods;
import ch.eiafr.cojac.instrumenters.OpCodeInstrumenter;
import ch.eiafr.cojac.reactions.Reaction;
import ch.eiafr.cojac.utils.BytecodeUtils;
import static ch.eiafr.cojac.models.CheckedDoubles.*;

import static org.objectweb.asm.Opcodes.*;

public final class DDIVInstrumenter implements OpCodeInstrumenter {
    @Override
    public void instrument(MethodVisitor mv, int opCode, String classPath, Methods methods, Reaction reaction, LocalVariablesSorter src) {
        mv.visitInsn(DUP2);
        mv.visitInsn(DUP2);
        mv.visitInsn(DCMPL);
        Label l0 = new Label();
        Label labelTestUnderflow =new Label();
        mv.visitJumpInsn(IFNE, l0);
        BytecodeUtils.addDup4(mv);
        mv.visitInsn(POP2);
        mv.visitInsn(DUP2);
        mv.visitInsn(DCMPL);
        mv.visitJumpInsn(IFNE, l0);
        BytecodeUtils.addDup4(mv);
        mv.visitInsn(DDIV);
        mv.visitInsn(DUP2);
        mv.visitInsn(DCMPL);
        mv.visitJumpInsn(IFEQ, l0);
        reaction.insertReactionCall(mv, RESULT_IS_NAN_MSG+"DDIV", methods, classPath);
        Label labelEnd = new Label();
        mv.visitJumpInsn(GOTO, labelEnd);
        mv.visitLabel(l0);
        mv.visitInsn(DUP2);
        mv.visitLdcInsn(new Double("Infinity"));
        mv.visitInsn(DCMPL);
        mv.visitJumpInsn(IFEQ, labelEnd);
        mv.visitInsn(DUP2);
        mv.visitLdcInsn(new Double("-Infinity"));
        mv.visitInsn(DCMPL);
        mv.visitJumpInsn(IFEQ, labelEnd);
        BytecodeUtils.addDup4(mv);
        mv.visitInsn(POP2);
        mv.visitLdcInsn(new Double("Infinity"));
        mv.visitInsn(DCMPL);
        mv.visitJumpInsn(IFEQ, labelEnd);
        BytecodeUtils.addDup4(mv);
        mv.visitInsn(POP2);
        mv.visitLdcInsn(new Double("-Infinity"));
        mv.visitInsn(DCMPL);
        mv.visitJumpInsn(IFEQ, labelEnd);
        BytecodeUtils.addDup4(mv);
        mv.visitInsn(DDIV);
        mv.visitLdcInsn(new Double("Infinity"));
        mv.visitInsn(DCMPL);
        Label l2 = new Label();
        mv.visitJumpInsn(IFNE, l2);
        reaction.insertReactionCall(mv, RESULT_IS_POS_INF_MSG+"DDIV", methods, classPath);
        mv.visitJumpInsn(GOTO, labelEnd);
        mv.visitLabel(l2);
        BytecodeUtils.addDup4(mv);
        mv.visitInsn(DDIV);
        mv.visitLdcInsn(new Double("-Infinity"));
        mv.visitInsn(DCMPL);
        mv.visitJumpInsn(IFNE, labelTestUnderflow);
        reaction.insertReactionCall(mv, RESULT_IS_NEG_INF_MSG+"DDIV", methods, classPath);
        mv.visitJumpInsn(GOTO, labelEnd);

        
        mv.visitLabel(labelTestUnderflow);
        BytecodeUtils.addDup4(mv);
        mv.visitInsn(DDIV);  //a,b,a/b
        mv.visitLdcInsn(new Double(0.0)); //a,b,a/b,0
        mv.visitInsn(DCMPL); //a,b,a/b?0
        mv.visitJumpInsn(IFNE, labelEnd); //a,b
        BytecodeUtils.addDup4(mv);
        mv.visitInsn(POP2);  //a,b,a
        mv.visitLdcInsn(new Double(0.0)); //a,b,a,0
        mv.visitInsn(DCMPL); //a,b,a?0
        mv.visitJumpInsn(IFEQ, labelEnd); //a,b
        reaction.insertReactionCall(mv, UNDERFLOW_MSG+"DDIV", methods, classPath);

        
        mv.visitLabel(labelEnd);
        mv.visitInsn(DDIV);
    }
}
