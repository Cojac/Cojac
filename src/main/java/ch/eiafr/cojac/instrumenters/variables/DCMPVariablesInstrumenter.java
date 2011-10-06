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
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.LocalVariablesSorter;

import static org.objectweb.asm.Opcodes.*;

public final class DCMPVariablesInstrumenter implements OpCodeInstrumenter {
    @Override
    public void instrument(MethodVisitor mv, int opCode, String classPath, Methods methods, Reaction reaction, LocalVariablesSorter src) {
        int a = src.newLocal(Type.DOUBLE_TYPE);
        int b = src.newLocal(Type.DOUBLE_TYPE);
        int r = src.newLocal(Type.INT_TYPE);

        src.visitVarInsn(DSTORE, b);
        src.visitVarInsn(DSTORE, a);
        src.visitVarInsn(DLOAD, a);
        src.visitVarInsn(DLOAD, b);
        mv.visitInsn(DCMPL);
        src.visitVarInsn(ISTORE, r);
        src.visitVarInsn(ILOAD, r);
        Label l3 = new Label();
        mv.visitJumpInsn(IFEQ, l3);
        src.visitVarInsn(DLOAD, a);
        src.visitVarInsn(DLOAD, b);
        mv.visitInsn(DSUB);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "abs", "(D)D");
        mv.visitLdcInsn(new Double("16.0"));
        src.visitVarInsn(DLOAD, a);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "ulp", "(D)D");
        mv.visitInsn(DMUL);
        mv.visitInsn(DCMPG);
        mv.visitJumpInsn(IFGE, l3);
        reaction.insertReactionCall(mv, "DCMP returned false but number are very close", methods, classPath);
        mv.visitLabel(l3);
        mv.visitFrame(F_APPEND, 1, new Object[]{INTEGER}, 0, null);
        src.visitVarInsn(ILOAD, r);
    }
}