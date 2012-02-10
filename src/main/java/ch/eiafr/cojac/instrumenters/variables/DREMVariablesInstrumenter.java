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

import ch.eiafr.cojac.Methods;
import ch.eiafr.cojac.instrumenters.OpCodeInstrumenter;
import ch.eiafr.cojac.reactions.Reaction;
import ch.eiafr.cojac.utils.BytecodeUtils;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.LocalVariablesSorter;

import static org.objectweb.asm.Opcodes.*;

public final class DREMVariablesInstrumenter implements OpCodeInstrumenter {
    @Override
    public void instrument(MethodVisitor mv, int opCode, String classPath, Methods methods, Reaction reaction, LocalVariablesSorter src) {
        int a = src.newLocal(Type.DOUBLE_TYPE);
        int b = src.newLocal(Type.DOUBLE_TYPE);
        int r = src.newLocal(Type.DOUBLE_TYPE);

        BytecodeUtils.addDup4(mv);
        src.visitVarInsn(DSTORE, b);
        src.visitVarInsn(DSTORE, a);
        mv.visitInsn(DREM);
        src.visitVarInsn(DSTORE, r);
        src.visitVarInsn(DLOAD, b);
        src.visitVarInsn(DLOAD, b);
        mv.visitInsn(DCMPL);
        Label l0 = new Label();
        mv.visitJumpInsn(IFNE, l0);
        src.visitVarInsn(DLOAD, a);
        src.visitVarInsn(DLOAD, a);
        mv.visitInsn(DCMPL);
        mv.visitJumpInsn(IFNE, l0);
        src.visitVarInsn(DLOAD, r);
        src.visitVarInsn(DLOAD, r);
        mv.visitInsn(DCMPL);
        mv.visitJumpInsn(IFEQ, l0);
        reaction.insertReactionCall(mv, "Result is Double.NaN : DDIV", methods, classPath);
        mv.visitLabel(l0);
        src.visitVarInsn(DLOAD, r);
    }
}