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

public final class LADDVariablesInstrumenter implements OpCodeInstrumenter {
    @Override
    public void instrument(MethodVisitor mv, int opCode, String classPath, Methods methods, Reaction reaction, LocalVariablesSorter src) {
        int a = src.newLocal(Type.LONG_TYPE);
        int b = src.newLocal(Type.LONG_TYPE);
        int r = src.newLocal(Type.LONG_TYPE);

        BytecodeUtils.addDup4(mv);
        src.visitVarInsn(LSTORE, b);
        src.visitVarInsn(LSTORE, a);
        mv.visitInsn(LADD);
        src.visitVarInsn(LSTORE, r);
        src.visitVarInsn(LLOAD, a);
        src.visitVarInsn(LLOAD, r);
        mv.visitInsn(LXOR);
        src.visitVarInsn(LLOAD, b);
        src.visitVarInsn(LLOAD, r);
        mv.visitInsn(LXOR);
        mv.visitInsn(LAND);
        mv.visitInsn(LCONST_0);
        mv.visitInsn(LCMP);
        Label l0 = new Label();
        mv.visitJumpInsn(IFGE, l0);
        reaction.insertReactionCall(mv, "Overflow : LADD", methods, classPath);
        mv.visitLabel(l0);
        src.visitVarInsn(LLOAD, r);
    }
}