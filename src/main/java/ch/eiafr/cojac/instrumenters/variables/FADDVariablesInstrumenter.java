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

public final class FADDVariablesInstrumenter implements OpCodeInstrumenter {
    @Override
    public void instrument(MethodVisitor mv, int opCode, String classPath, Methods methods, Reaction reaction, LocalVariablesSorter src) {
        int a = src.newLocal(Type.FLOAT_TYPE);//0
        int b = src.newLocal(Type.FLOAT_TYPE);//1
        int r = src.newLocal(Type.FLOAT_TYPE);//4

        mv.visitInsn(DUP2);
        src.visitVarInsn(FSTORE, b);
        src.visitVarInsn(FSTORE, a);
        mv.visitInsn(FADD);
        src.visitVarInsn(FSTORE, r);

        src.visitVarInsn(FLOAD, b);
        mv.visitInsn(FCONST_0);
        mv.visitInsn(FCMPL);
        Label l0 = new Label();
        mv.visitJumpInsn(IFEQ, l0);
        src.visitVarInsn(FLOAD, r);
        src.visitVarInsn(FLOAD, a);
        mv.visitInsn(FCMPL);
        Label l1 = new Label();
        mv.visitJumpInsn(IFEQ, l1);
        mv.visitLabel(l0);
        src.visitVarInsn(FLOAD, a);
        mv.visitInsn(FCONST_0);
        mv.visitInsn(FCMPL);
        Label l2 = new Label();
        mv.visitJumpInsn(IFEQ, l2);
        src.visitVarInsn(FLOAD, r);
        src.visitVarInsn(FLOAD, b);
        mv.visitInsn(FCMPL);
        mv.visitJumpInsn(IFNE, l2);
        mv.visitLabel(l1);
        reaction.insertReactionCall(mv, "Precision error : FADD", methods, classPath);
        Label l3 = new Label();
        mv.visitJumpInsn(GOTO, l3);
        mv.visitLabel(l2);
        src.visitVarInsn(FLOAD, a);
        mv.visitLdcInsn(new Float("Infinity"));
        mv.visitInsn(FCMPL);
        mv.visitJumpInsn(IFEQ, l3);
        src.visitVarInsn(FLOAD, a);
        mv.visitLdcInsn(new Float("-Infinity"));
        mv.visitInsn(FCMPL);
        mv.visitJumpInsn(IFEQ, l3);
        src.visitVarInsn(FLOAD, b);
        mv.visitLdcInsn(new Float("Infinity"));
        mv.visitInsn(FCMPL);
        mv.visitJumpInsn(IFEQ, l3);
        src.visitVarInsn(FLOAD, b);
        mv.visitLdcInsn(new Float("-Infinity"));
        mv.visitInsn(FCMPL);
        mv.visitJumpInsn(IFEQ, l3);
        src.visitVarInsn(FLOAD, r);
        mv.visitLdcInsn(new Float("Infinity"));
        mv.visitInsn(FCMPL);
        Label l4 = new Label();
        mv.visitJumpInsn(IFNE, l4);
        reaction.insertReactionCall(mv, "Result is Float.POSITIVE_INFINITY : FADD", methods, classPath);
        mv.visitJumpInsn(GOTO, l3);
        mv.visitLabel(l4);
        src.visitVarInsn(FLOAD, r);
        mv.visitLdcInsn(new Float("-Infinity"));
        mv.visitInsn(FCMPL);
        mv.visitJumpInsn(IFNE, l3);
        reaction.insertReactionCall(mv, "Result is Float.NEGATIVE_INFINITY : FADD", methods, classPath);
        mv.visitLabel(l3);
        src.visitVarInsn(FLOAD, r);
    }
}