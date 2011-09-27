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

package ch.eiafr.cojac.instrumenters.variables;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.LocalVariablesSorter;

import ch.eiafr.cojac.Methods;
import ch.eiafr.cojac.instrumenters.OpCodeInstrumenter;
import ch.eiafr.cojac.reactions.Reaction;
import ch.eiafr.cojac.utils.BytecodeUtils;

import static org.objectweb.asm.Opcodes.*;

public final class LMULVariablesInstrumenter implements OpCodeInstrumenter {
    @Override
    public void instrument(MethodVisitor mv, int opCode, String classPath, Methods methods, Reaction reaction, LocalVariablesSorter src) {
        int a = src.newLocal(Type.LONG_TYPE);
        int b = src.newLocal(Type.LONG_TYPE);
        int r = src.newLocal(Type.LONG_TYPE);

        BytecodeUtils.addDup4(mv);
        src.visitVarInsn(LSTORE, b);
        src.visitVarInsn(LSTORE, a);
        mv.visitInsn(LMUL);
        src.visitVarInsn(LSTORE, r);
        src.visitVarInsn(LLOAD, b);
        mv.visitInsn(LCONST_0);
        mv.visitInsn(LCMP);
        Label l3 = new Label();
        mv.visitJumpInsn(IFEQ, l3);
        src.visitVarInsn(LLOAD, b);
        mv.visitLdcInsn(-1L);
        mv.visitInsn(LCMP);
        Label l1 = new Label();
        mv.visitJumpInsn(IFNE, l1);
        src.visitVarInsn(LLOAD, a);
        mv.visitLdcInsn(-9223372036854775808L);
        mv.visitInsn(LCMP);
        Label l2 = new Label();
        mv.visitJumpInsn(IFEQ, l2);
        mv.visitLabel(l1);
        src.visitVarInsn(LLOAD, r);
        src.visitVarInsn(LLOAD, b);
        mv.visitInsn(LDIV);
        src.visitVarInsn(LLOAD, a);
        mv.visitInsn(LCMP);
        mv.visitJumpInsn(IFEQ, l3);
        mv.visitLabel(l2);
        reaction.insertReactionCall(mv, "Overflow : LMUL", methods, classPath);
        mv.visitLabel(l3);
        src.visitVarInsn(LLOAD, r);
    }
}