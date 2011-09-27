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

import static ch.eiafr.cojac.models.CheckedDoubles.VERY_CLOSE_MSG;
import static ch.eiafr.cojac.models.CheckedDoubles.CLOSENESS_ULP_FACTOR_FLOAT;
import static org.objectweb.asm.Opcodes.*;

public final class FCMPInstrumenter implements OpCodeInstrumenter {
    @Override
    public void instrument(MethodVisitor mv, int opCode, String classPath, Methods methods, Reaction reaction, LocalVariablesSorter src) {
        Label l3 = new Label();

        mv.visitInsn(DUP2);
        mv.visitInsn(FCMPL);
        mv.visitJumpInsn(IFEQ, l3);

        mv.visitInsn(DUP2);
        mv.visitInsn(DUP2);
        mv.visitInsn(FSUB);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "abs", "(F)F");

        mv.visitInsn(SWAP);
        mv.visitInsn(POP);
        mv.visitInsn(SWAP);

        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "ulp", "(F)F");
        mv.visitLdcInsn(new Float(CLOSENESS_ULP_FACTOR_FLOAT));
        mv.visitInsn(FMUL);

        mv.visitInsn(FCMPG);
        mv.visitJumpInsn(IFGT, l3);
        reaction.insertReactionCall(mv, VERY_CLOSE_MSG+"FCMP", methods, classPath);
        mv.visitLabel(l3);
        mv.visitInsn(FCMPL);
    }
}