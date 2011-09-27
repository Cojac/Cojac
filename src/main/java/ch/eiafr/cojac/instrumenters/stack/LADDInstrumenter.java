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

public final class LADDInstrumenter implements OpCodeInstrumenter {
    @Override
    public void instrument(MethodVisitor mv, int opCode, String classPath, Methods methods, Reaction reaction, LocalVariablesSorter src) {
        Label label = new Label();
        Label fin = new Label();
        BytecodeUtils.addDup4(mv);
        BytecodeUtils.addSwap2(mv);
        mv.visitInsn(LCONST_0);
        mv.visitInsn(LCMP);
        mv.visitJumpInsn(IFLT, label);
        mv.visitInsn(LCONST_0);
        mv.visitInsn(LCMP);
        mv.visitJumpInsn(IFLT, fin);
        BytecodeUtils.addDup4(mv);
        BytecodeUtils.addSwap2(mv);
        mv.visitLdcInsn(Long.MAX_VALUE);
        BytecodeUtils.addSwap2(mv);
        mv.visitInsn(LSUB);
        mv.visitInsn(LSUB);
        mv.visitInsn(LCONST_0);
        mv.visitInsn(LCMP);
        mv.visitJumpInsn(IFLE, fin);
        reaction.insertReactionCall(mv, "Overflow : LADD", methods, classPath);
        mv.visitJumpInsn(GOTO, fin);
        mv.visitLabel(label);
        mv.visitInsn(LCONST_0);
        mv.visitInsn(LCMP);
        mv.visitJumpInsn(IFGT, fin);
        BytecodeUtils.addDup4(mv);
        BytecodeUtils.addSwap2(mv);
        mv.visitLdcInsn(Long.MIN_VALUE);
        BytecodeUtils.addSwap2(mv);
        mv.visitInsn(LSUB);
        mv.visitInsn(LSUB);
        mv.visitInsn(LCONST_0);
        mv.visitInsn(LCMP);
        mv.visitJumpInsn(IFGE, fin);
        reaction.insertReactionCall(mv, "Overflow : LADD", methods, classPath);
        mv.visitLabel(fin);
        mv.visitInsn(LADD);
    }
}
