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

public final class FSUBInstrumenter implements OpCodeInstrumenter {
    @Override
    public void instrument(MethodVisitor mv, int opCode, String classPath, Methods methods, Reaction reaction, LocalVariablesSorter src) {
        Label labelEndWithFsub = new Label();
        Label l2 = new Label();
        Label labelCancellation = new Label();
        Label lA = new Label();
        Label lC = new Label();
        Label labelKillInfPrecision = new Label();
        Label labelReactPrecision = new Label();
        {
            mv.visitInsn(DUP);          // a,b,b
            mv.visitInsn(FCONST_0);     // a,b,b,0
            mv.visitInsn(FCMPL);        // a,b,(b?0)
            mv.visitJumpInsn(IFEQ, lA); // a,b
            mv.visitInsn(DUP2);         // a,b,a,b
            mv.visitInsn(SWAP);         // a,b,b,a
            mv.visitInsn(DUP2);         // a,b,b,a,b,a
            mv.visitInsn(SWAP);         // a,b,b,a,a,b
            mv.visitInsn(FSUB);         // a,b,b,a,(a-b)
            mv.visitInsn(FCMPL);        // a,b,b,(a ? (a-b))
            mv.visitInsn(DUP_X1);
            mv.visitInsn(POP);
            mv.visitInsn(POP);          // a,b,(a ? (a-b))
            mv.visitJumpInsn(IFEQ, labelKillInfPrecision); // a,b
        }
        mv.visitLabel(lA);
        {
            mv.visitInsn(DUP2);
            mv.visitInsn(POP);
            mv.visitInsn(FCONST_0);
            mv.visitInsn(FCMPL);
            mv.visitJumpInsn(IFEQ, lC);
            mv.visitInsn(DUP2);
            mv.visitInsn(DUP2);
            mv.visitInsn(FSUB);
            mv.visitInsn(FNEG);
            mv.visitInsn(FCMPL);
            mv.visitInsn(DUP_X1);
            mv.visitInsn(POP);
            mv.visitInsn(POP);
            mv.visitJumpInsn(IFNE, lC);
        }
        mv.visitLabel(labelKillInfPrecision);
        {
            mv.visitInsn(DUP2);
            mv.visitInsn(FADD);
            mv.visitInsn(DUP);
            mv.visitLdcInsn(new Float(2.0f));
            mv.visitInsn(FMUL);
            mv.visitInsn(FCMPL);
            mv.visitJumpInsn(IFEQ, labelEndWithFsub);
        }
        mv.visitLabel(labelReactPrecision);
        {
            reaction.insertReactionCall(mv, PRECISION_MSG + "FSUB", methods, classPath);
            mv.visitJumpInsn(GOTO, labelEndWithFsub);
        }
        mv.visitLabel(lC);
        {
            mv.visitInsn(DUP);
            mv.visitLdcInsn(new Float("Infinity"));
            mv.visitInsn(FCMPL);
            mv.visitJumpInsn(IFEQ, labelEndWithFsub);
            mv.visitInsn(DUP);
            mv.visitLdcInsn(new Float("-Infinity"));
            mv.visitInsn(FCMPL);
            mv.visitJumpInsn(IFEQ, labelEndWithFsub);
            mv.visitInsn(DUP2);
            mv.visitInsn(POP);
            mv.visitLdcInsn(new Float("Infinity"));
            mv.visitInsn(FCMPL);
            mv.visitJumpInsn(IFEQ, labelEndWithFsub);
            mv.visitInsn(DUP2);
            mv.visitInsn(POP);
            mv.visitLdcInsn(new Float("-Infinity"));
            mv.visitInsn(FCMPL);
            mv.visitJumpInsn(IFEQ, labelEndWithFsub);

            mv.visitInsn(DUP2);                        // a,b,a,b
            mv.visitInsn(FSUB);                        // a,b,(a-b)
            mv.visitInsn(FCONST_0);                    // a,b,(a-b),0
            mv.visitInsn(FCMPL);                       // a,b,(a-b ? 0)
            mv.visitJumpInsn(IFEQ, labelEndWithFsub);  // a,b

            mv.visitInsn(DUP2);
            mv.visitInsn(FSUB);
            mv.visitLdcInsn(new Float("Infinity"));
            mv.visitInsn(FCMPL);
            mv.visitJumpInsn(IFNE, l2);
            reaction.insertReactionCall(mv, RESULT_IS_POS_INF_MSG + "FSUB", methods, classPath);
            mv.visitJumpInsn(GOTO, labelEndWithFsub);
        }
        mv.visitLabel(l2);
        {
            mv.visitInsn(DUP2);
            mv.visitInsn(FSUB);
            mv.visitLdcInsn(new Float("-Infinity"));
            mv.visitInsn(FCMPL);
            mv.visitJumpInsn(IFNE, labelCancellation);
            reaction.insertReactionCall(mv, RESULT_IS_NEG_INF_MSG + "FSUB", methods, classPath);
            mv.visitJumpInsn(GOTO, labelEndWithFsub);
        }
        mv.visitLabel(labelCancellation);
        {
            mv.visitInsn(DUP2);
            mv.visitInsn(FSUB);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "abs", "(F)F");
            mv.visitInsn(DUP2);
            mv.visitInsn(POP);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "ulp", "(F)F");
            mv.visitLdcInsn(new Float(CANCELLATION_ULP_FACTOR_FLOAT));
            mv.visitInsn(FMUL);
            mv.visitInsn(FCMPG);
            mv.visitJumpInsn(IFGT, labelEndWithFsub);
            reaction.insertReactionCall(mv, CANCELLATION_MSG + "FADD", methods, classPath);

        }
        mv.visitLabel(labelEndWithFsub);
        {
            mv.visitInsn(FSUB);
        }
    }

}