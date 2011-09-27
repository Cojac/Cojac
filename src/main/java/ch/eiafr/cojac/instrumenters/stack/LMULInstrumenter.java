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

import static org.objectweb.asm.Opcodes.*;

public final class LMULInstrumenter implements OpCodeInstrumenter {
    @Override
    public void instrument(MethodVisitor mv, int opCode, String classPath, Methods methods, Reaction reaction, LocalVariablesSorter src) {
        Label label = new Label();
        Label label2 = new Label();
        Label label22 = new Label();
        Label label23 = new Label();
        Label label3 = new Label();
        Label label4 = new Label();
        Label label5 = new Label();
        Label label6 = new Label();
        Label label7 = new Label();
        Label label8 = new Label();
        Label label9 = new Label();
        Label label10 = new Label();
        Label label11 = new Label();
        Label fin = new Label();
        BytecodeUtils.addDup4(mv);
        BytecodeUtils.addSwap2(mv);
        BytecodeUtils.addDup4(mv);
        mv.visitInsn(LCONST_0);
        mv.visitInsn(LCMP);
        mv.visitJumpInsn(IFEQ, label4);
        mv.visitInsn(LCONST_0);
        mv.visitInsn(LCMP);
        mv.visitJumpInsn(IFEQ, label5);
        mv.visitInsn(LCONST_0);
        mv.visitInsn(LCMP);
        mv.visitJumpInsn(IFLT, label);
        mv.visitInsn(LCONST_0);
        mv.visitInsn(LCMP);
        mv.visitJumpInsn(IFLT, label2);
        BytecodeUtils.addDup4(mv);
        BytecodeUtils.addSwap2(mv);
        mv.visitLdcInsn(Long.MAX_VALUE);
        BytecodeUtils.addSwap2(mv);
        mv.visitInsn(LDIV);
        mv.visitInsn(LSUB);
        mv.visitInsn(LCONST_0);
        mv.visitInsn(LCMP);
        mv.visitJumpInsn(IFLE, fin);
        reaction.insertReactionCall(mv, "Overflow : LMUL", methods, classPath);
        mv.visitJumpInsn(GOTO, fin);
        mv.visitLabel(label);
        mv.visitInsn(LCONST_0);
        mv.visitInsn(LCMP);
        mv.visitJumpInsn(IFLT, label3);
        BytecodeUtils.addDup4(mv);
        BytecodeUtils.addSwap2(mv);
        mv.visitLdcInsn(Long.MIN_VALUE);
        BytecodeUtils.addSwap2(mv);
        mv.visitInsn(LDIV);
        mv.visitInsn(LSUB);// 2
        mv.visitInsn(LCONST_0);
        mv.visitInsn(LCMP);
        mv.visitJumpInsn(IFLE, fin);
        reaction.insertReactionCall(mv, "Overflow : LMUL", methods, classPath);
        mv.visitJumpInsn(GOTO, fin);
        mv.visitLabel(label2);
        BytecodeUtils.addDup4(mv);
        BytecodeUtils.addSwap2(mv);
        BytecodeUtils.addDup4(mv);
        mv.visitInsn(LCONST_1);
        mv.visitInsn(LCMP);
        mv.visitJumpInsn(IFNE, label22);
        mv.visitJumpInsn(GOTO, label4);
        mv.visitLabel(label22);
        mv.visitLdcInsn(Long.MIN_VALUE);
        mv.visitInsn(LCMP);
        mv.visitJumpInsn(IFNE, label23);
        reaction.insertReactionCall(mv, "Overflow : LMUL", methods, classPath);
        mv.visitJumpInsn(GOTO, label5);
        mv.visitLabel(label23);
        mv.visitLdcInsn(Long.MAX_VALUE);
        BytecodeUtils.addSwap2(mv);
        mv.visitInsn(LDIV);
        BytecodeUtils.addSwap2(mv);
        mv.visitInsn(LNEG);
        mv.visitInsn(LCMP);
        mv.visitJumpInsn(IFGE, fin);
        reaction.insertReactionCall(mv, "Overflow : LMUL", methods, classPath);
        mv.visitJumpInsn(GOTO, fin);
        mv.visitLabel(label3);
        BytecodeUtils.addDup4(mv);
        BytecodeUtils.addDup4(mv);
        mv.visitInsn(ICONST_M1);
        mv.visitInsn(I2L);
        mv.visitInsn(LCMP);
        mv.visitJumpInsn(IFEQ, label6);
        mv.visitInsn(ICONST_M1);
        mv.visitInsn(I2L);
        mv.visitInsn(LCMP);
        mv.visitJumpInsn(IFEQ, label7);
        mv.visitJumpInsn(GOTO, label8);
        mv.visitLabel(label9);
        mv.visitInsn(POP2);
        mv.visitLabel(label8);
        BytecodeUtils.addDup4(mv);
        mv.visitLdcInsn(Long.MIN_VALUE);
        mv.visitInsn(LCMP);
        mv.visitJumpInsn(IFNE, label10);
        mv.visitInsn(POP2);
        reaction.insertReactionCall(mv, "Overflow : LMUL", methods, classPath);
        mv.visitJumpInsn(GOTO, label5);
        mv.visitLabel(label10);
        mv.visitInsn(POP2);
        mv.visitLabel(label11);
        BytecodeUtils.addSwap2(mv);
        mv.visitLdcInsn(Long.MIN_VALUE);
        BytecodeUtils.addSwap2(mv);
        mv.visitInsn(LDIV);
        BytecodeUtils.addSwap2(mv);
        mv.visitInsn(LNEG);
        mv.visitInsn(LCMP);
        mv.visitJumpInsn(IFGE, fin);
        reaction.insertReactionCall(mv, "Overflow : LMUL", methods, classPath);
        mv.visitJumpInsn(GOTO, fin);
        mv.visitLabel(label6);
        mv.visitLdcInsn(Long.MIN_VALUE);
        mv.visitInsn(LCMP);
        mv.visitJumpInsn(IFNE, label11);
        reaction.insertReactionCall(mv, "Overflow : LMUL", methods, classPath);
        mv.visitJumpInsn(GOTO, label5);
        mv.visitLabel(label7);
        BytecodeUtils.addDup4(mv);
        mv.visitLdcInsn(Long.MIN_VALUE);
        mv.visitInsn(LCMP);
        mv.visitJumpInsn(IFNE, label9);
        mv.visitInsn(POP2);
        reaction.insertReactionCall(mv, "Overflow : LMUL", methods, classPath);
        mv.visitJumpInsn(GOTO, label5);
        mv.visitLabel(label4);
        mv.visitInsn(POP2);
        mv.visitLabel(label5);
        mv.visitInsn(POP2);
        mv.visitInsn(POP2);
        mv.visitLabel(fin);
        mv.visitInsn(LMUL);
    }
}
