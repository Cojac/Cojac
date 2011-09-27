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

import static org.objectweb.asm.Opcodes.*;

public final class IMULInstrumenter implements OpCodeInstrumenter {
    @Override
    public void instrument(MethodVisitor methodVisitor, int opCode, String classPath, Methods methods, Reaction reaction, LocalVariablesSorter src) {
        Label label = new Label();
        Label label1 = new Label();
        Label label2 = new Label();
        Label label3 = new Label();
        Label label4 = new Label();
        Label label5 = new Label();
        Label fin = new Label();

        methodVisitor.visitInsn(DUP2);
        methodVisitor.visitInsn(DUP2);
        methodVisitor.visitInsn(DUP2);
        methodVisitor.visitInsn(ICONST_M1);
        methodVisitor.visitJumpInsn(IF_ICMPEQ, label2);
        methodVisitor.visitInsn(ICONST_M1);
        methodVisitor.visitJumpInsn(IF_ICMPEQ, label3);
        methodVisitor.visitJumpInsn(GOTO, label4);
        methodVisitor.visitLabel(label5);
        methodVisitor.visitInsn(POP);
        methodVisitor.visitLabel(label4);
        methodVisitor.visitJumpInsn(IFEQ, label);
        methodVisitor.visitJumpInsn(IFEQ, label1);
        methodVisitor.visitInsn(IMUL);
        methodVisitor.visitInsn(DUP_X2);
        methodVisitor.visitInsn(SWAP);
        methodVisitor.visitInsn(IDIV);
        methodVisitor.visitJumpInsn(IF_ICMPEQ, fin);
        reaction.insertReactionCall(methodVisitor, "Overflow : IMUL", methods, classPath);
        methodVisitor.visitJumpInsn(GOTO, fin);
        methodVisitor.visitLabel(label2);
        methodVisitor.visitLdcInsn(Integer.MIN_VALUE);
        methodVisitor.visitJumpInsn(IF_ICMPNE, label4);
        reaction.insertReactionCall(methodVisitor, "Overflow : IMUL", methods, classPath);
        methodVisitor.visitInsn(POP2);
        methodVisitor.visitJumpInsn(GOTO, label1);
        methodVisitor.visitLabel(label3);
        methodVisitor.visitInsn(DUP2);
        methodVisitor.visitLdcInsn(Integer.MIN_VALUE);
        methodVisitor.visitJumpInsn(IF_ICMPNE, label5);
        methodVisitor.visitInsn(POP);
        methodVisitor.visitInsn(POP2);
        reaction.insertReactionCall(methodVisitor, "Overflow : IMUL", methods, classPath);
        methodVisitor.visitJumpInsn(GOTO, label1);
        methodVisitor.visitLabel(label);
        methodVisitor.visitInsn(POP);
        methodVisitor.visitLabel(label1);
        methodVisitor.visitInsn(POP2);
        methodVisitor.visitInsn(IMUL);
        methodVisitor.visitLabel(fin);
    }
}
