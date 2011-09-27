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

import static ch.eiafr.cojac.models.CheckedDoubles.*;
import static org.objectweb.asm.Opcodes.*;

public final class FDIVInstrumenter implements OpCodeInstrumenter {
    @Override
    public void instrument(MethodVisitor mv, int opCode, String classPath, Methods methods, Reaction reaction, LocalVariablesSorter src) {
        mv.visitInsn(DUP);
        mv.visitInsn(DUP);
        mv.visitInsn(FCMPL);
        Label l0 = new Label();
        mv.visitJumpInsn(IFNE, l0);
        mv.visitInsn(DUP2);
        mv.visitInsn(POP);
        mv.visitInsn(DUP);
        mv.visitInsn(FCMPL);
        mv.visitJumpInsn(IFNE, l0);
        mv.visitInsn(DUP2);
        mv.visitInsn(FDIV);
        mv.visitInsn(DUP);
        mv.visitInsn(FCMPL);
        mv.visitJumpInsn(IFEQ, l0);
        reaction.insertReactionCall(mv, RESULT_IS_NAN_MSG+"FDIV", methods, classPath);
        Label l1 = new Label();
        mv.visitJumpInsn(GOTO, l1);
        mv.visitLabel(l0);
        mv.visitInsn(DUP);
        mv.visitLdcInsn(new Float("Infinity"));
        mv.visitInsn(FCMPL);
        mv.visitJumpInsn(IFEQ, l1);
        mv.visitInsn(DUP);
        mv.visitLdcInsn(new Float("-Infinity"));
        mv.visitInsn(FCMPL);
        mv.visitJumpInsn(IFEQ, l1);
        mv.visitInsn(DUP2);
        mv.visitInsn(POP);
        mv.visitLdcInsn(new Float("Infinity"));
        mv.visitInsn(FCMPL);
        mv.visitJumpInsn(IFEQ, l1);
        mv.visitInsn(DUP2);
        mv.visitInsn(POP);
        mv.visitLdcInsn(new Float("-Infinity"));
        mv.visitInsn(FCMPL);
        mv.visitJumpInsn(IFEQ, l1);
        mv.visitInsn(DUP2);
        mv.visitInsn(FDIV);
        mv.visitLdcInsn(new Float("Infinity"));
        mv.visitInsn(FCMPL);
        Label l2 = new Label();
        mv.visitJumpInsn(IFNE, l2);
        reaction.insertReactionCall(mv, RESULT_IS_POS_INF_MSG+"FMUL", methods, classPath);
        mv.visitJumpInsn(GOTO, l1);
        mv.visitLabel(l2);
        mv.visitInsn(DUP2);
        mv.visitInsn(FDIV);
        mv.visitLdcInsn(new Float("-Infinity"));
        mv.visitInsn(FCMPL);
        mv.visitJumpInsn(IFNE, l1);
        reaction.insertReactionCall(mv, RESULT_IS_NEG_INF_MSG+"FMUL", methods, classPath);
        mv.visitLabel(l1);
        mv.visitInsn(FDIV);
    }
}