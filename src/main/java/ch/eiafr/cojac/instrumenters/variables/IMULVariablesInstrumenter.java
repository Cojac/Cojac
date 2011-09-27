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

import static org.objectweb.asm.Opcodes.*;

public final class IMULVariablesInstrumenter implements OpCodeInstrumenter {
    @Override
    public void instrument(MethodVisitor mv, int opCode, String classPath, Methods methods, Reaction reaction, LocalVariablesSorter src) {
        int a = src.newLocal(Type.INT_TYPE);
        int b = src.newLocal(Type.INT_TYPE);
        int r = src.newLocal(Type.INT_TYPE);

        mv.visitInsn(DUP2);
        src.visitVarInsn(ISTORE, b);
        src.visitVarInsn(ISTORE, a);
        mv.visitInsn(IMUL);
        src.visitVarInsn(ISTORE, r);
        src.visitVarInsn(ILOAD, b);
        Label l0 = new Label();
        mv.visitJumpInsn(IFEQ, l0);
        src.visitVarInsn(ILOAD, b);
        mv.visitInsn(ICONST_M1);
        Label l1 = new Label();
        mv.visitJumpInsn(IF_ICMPNE, l1);
        src.visitVarInsn(ILOAD, a);
        mv.visitLdcInsn(-2147483648);
        Label l2 = new Label();
        mv.visitJumpInsn(IF_ICMPEQ, l2);
        mv.visitLabel(l1);
        src.visitVarInsn(ILOAD, r);
        src.visitVarInsn(ILOAD, b);
        mv.visitInsn(IDIV);
        src.visitVarInsn(ILOAD, a);
        mv.visitJumpInsn(IF_ICMPEQ, l0);
        mv.visitLabel(l2);
        reaction.insertReactionCall(mv, "Overflow : IMUL", methods, classPath);
        mv.visitLabel(l0);
        src.visitVarInsn(ILOAD, r);
    }
}
