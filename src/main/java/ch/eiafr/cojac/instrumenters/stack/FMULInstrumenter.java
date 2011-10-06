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

import ch.eiafr.cojac.Methods;
import ch.eiafr.cojac.instrumenters.OpCodeInstrumenter;
import ch.eiafr.cojac.reactions.Reaction;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.LocalVariablesSorter;

import static ch.eiafr.cojac.models.CheckedDoubles.*;
import static org.objectweb.asm.Opcodes.*;

public final class FMULInstrumenter implements OpCodeInstrumenter {
    @Override
    public void instrument(MethodVisitor mv, int opCode, String classPath, Methods methods, Reaction reaction, LocalVariablesSorter src) {
        Label l0 = new Label();
        Label l1 = new Label();
        Label labelTestUnderflow = new Label();
        mv.visitInsn(DUP);  // a,b,b
        mv.visitLdcInsn(new Float("Infinity")); //a,b,b,inf
        mv.visitInsn(FCMPL); //a,b,b?inf
        mv.visitJumpInsn(IFEQ, l0); //a,b
        mv.visitInsn(DUP); //a,b,b
        mv.visitLdcInsn(new Float("-Infinity"));//a,b,b,-inf
        mv.visitInsn(FCMPL);// a,b,b?-inf
        mv.visitJumpInsn(IFEQ, l0);  //a,b
        mv.visitInsn(DUP2);  //a,b,a,b
        mv.visitInsn(POP);   //a,b,a
        mv.visitLdcInsn(new Float("Infinity"));//a,b,a,inf
        mv.visitInsn(FCMPL); //a,b,a?inf
        mv.visitJumpInsn(IFEQ, l0);//a,b
        mv.visitInsn(DUP2);//a,b,a,b
        mv.visitInsn(POP); //a,b,a
        mv.visitLdcInsn(new Float("-Infinity")); //a,b,a,-inf
        mv.visitInsn(FCMPL); //a,b,a?-inf
        mv.visitJumpInsn(IFEQ, l0); //a,b
        mv.visitInsn(DUP2); //a,b,a,b
        mv.visitInsn(FMUL); //a,b,a*b
        mv.visitLdcInsn(new Float("Infinity")); //a,b,a*b,inf
        mv.visitInsn(FCMPL);//a,b,a*b ? inf
        mv.visitJumpInsn(IFNE, l1); //a,b
        reaction.insertReactionCall(mv, RESULT_IS_POS_INF_MSG + "FMUL", methods, classPath);
        mv.visitJumpInsn(GOTO, l0);
        mv.visitLabel(l1);
        mv.visitInsn(DUP2);  //a,b,a,b
        mv.visitInsn(FMUL);  //a,b,a*b
        mv.visitLdcInsn(new Float("-Infinity")); //a,b,a*b,-inf
        mv.visitInsn(FCMPL); //a,b,a*b?-inf
        mv.visitJumpInsn(IFNE, labelTestUnderflow); //a,b
        reaction.insertReactionCall(mv, RESULT_IS_NEG_INF_MSG + "FMUL", methods, classPath);
        mv.visitJumpInsn(GOTO, l0);
        mv.visitLabel(labelTestUnderflow);
        mv.visitInsn(DUP2);  //a,b,a,b
        mv.visitInsn(FMUL);  //a,b,a*b
        mv.visitLdcInsn(new Float(0.0f)); //a,b,a*b,0
        mv.visitInsn(FCMPL); //a,b,a*b?0
        mv.visitJumpInsn(IFNE, l0); //a,b
        mv.visitInsn(DUP);  //a,b,b
        mv.visitLdcInsn(new Float(0.0f)); //a,b,b,0
        mv.visitInsn(FCMPL); //a,b,b?0
        mv.visitJumpInsn(IFEQ, l0); //a,b
        mv.visitInsn(DUP2);  //a,b,a,b
        mv.visitInsn(POP);  //a,b,a
        mv.visitLdcInsn(new Float(0.0f)); //a,b,a,0
        mv.visitInsn(FCMPL); //a,b,a?0
        mv.visitJumpInsn(IFEQ, l0); //a,b
        reaction.insertReactionCall(mv, UNDERFLOW_MSG + "FMUL", methods, classPath);

        mv.visitLabel(l0);
        mv.visitInsn(FMUL);
    }
}