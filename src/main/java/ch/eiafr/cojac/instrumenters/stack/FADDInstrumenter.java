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
//import ch.eiafr.cojac.utils.BytecodeUtils;

import static ch.eiafr.cojac.models.CheckedDoubles.*;
import static org.objectweb.asm.Opcodes.*;

public final class FADDInstrumenter implements OpCodeInstrumenter {
  @Override
  public void instrument(MethodVisitor mv, int opCode, String classPath, Methods methods, Reaction reaction, LocalVariablesSorter src) {
    Label labelBisZero = new Label();
    Label labelCheckPosInf = new Label();
    Label labelEndWithFadd = new Label();
    Label labelCheckNegInf = new Label();
    Label labelCancelDetection = new Label();
    Label labelReactPrecision = new Label();
    Label labelKillInfPrecision = new Label();
    {
      mv.visitInsn(DUP);
      mv.visitInsn(FCONST_0);
      mv.visitInsn(FCMPL);
      mv.visitJumpInsn(IFEQ, labelBisZero);
      mv.visitInsn(DUP2);
      mv.visitInsn(SWAP);
      mv.visitInsn(DUP2);
      mv.visitInsn(FADD);
      mv.visitInsn(FCMPL);
      mv.visitInsn(SWAP);
      mv.visitInsn(POP);
      mv.visitJumpInsn(IFEQ, labelKillInfPrecision);
    }
    mv.visitLabel(labelBisZero); {
      mv.visitInsn(DUP2);
      mv.visitInsn(POP);
      mv.visitInsn(FCONST_0);
      mv.visitInsn(FCMPL);
      mv.visitJumpInsn(IFEQ, labelCheckPosInf);
      mv.visitInsn(DUP2);
      mv.visitInsn(DUP2);
      mv.visitInsn(FADD);
      mv.visitInsn(FCMPL);
      mv.visitInsn(SWAP);
      mv.visitInsn(POP);
      mv.visitJumpInsn(IFNE, labelCheckPosInf);
    }
    mv.visitLabel(labelKillInfPrecision); {
      mv.visitInsn(DUP2);
      mv.visitInsn(FADD);
      mv.visitInsn(DUP);
      mv.visitLdcInsn(new Float(2.0f));
      mv.visitInsn(FMUL);
      mv.visitInsn(FCMPL);
      mv.visitJumpInsn(IFEQ, labelEndWithFadd);
    }
    mv.visitLabel(labelReactPrecision); {
      reaction.insertReactionCall(mv, PRECISION_MSG+"FADD", methods, classPath);
      mv.visitJumpInsn(GOTO, labelEndWithFadd);
    }
    mv.visitLabel(labelCheckPosInf); {
      mv.visitInsn(DUP);
      mv.visitLdcInsn(new Float("Infinity"));
      mv.visitInsn(FCMPL);
      mv.visitJumpInsn(IFEQ, labelEndWithFadd);
      mv.visitInsn(DUP);
      mv.visitLdcInsn(new Float("-Infinity"));
      mv.visitInsn(FCMPL);
      mv.visitJumpInsn(IFEQ, labelEndWithFadd);
      mv.visitInsn(DUP2);
      mv.visitInsn(POP);
      mv.visitLdcInsn(new Float("Infinity"));
      mv.visitInsn(FCMPL);
      mv.visitJumpInsn(IFEQ, labelEndWithFadd);
      mv.visitInsn(DUP2);
      mv.visitInsn(POP);
      mv.visitLdcInsn(new Float("-Infinity"));
      mv.visitInsn(FCMPL);
      mv.visitJumpInsn(IFEQ, labelEndWithFadd);
      
      mv.visitInsn(DUP2);                        // a,b,a,b
      mv.visitInsn(FADD);                        // a,b,(a-b)
      mv.visitInsn(FCONST_0);                    // a,b,(a-b),0
      mv.visitInsn(FCMPL);                       // a,b,(a-b ? 0)
      mv.visitJumpInsn(IFEQ, labelEndWithFadd);  // a,b
   
      mv.visitInsn(DUP2);
      mv.visitInsn(FADD);
      mv.visitLdcInsn(new Float("Infinity"));
      mv.visitInsn(FCMPL);
      mv.visitJumpInsn(IFNE, labelCheckNegInf);
      reaction.insertReactionCall(mv, RESULT_IS_POS_INF_MSG+"FADD", methods, classPath);
      mv.visitJumpInsn(GOTO, labelEndWithFadd);
    }
    mv.visitLabel(labelCheckNegInf); {
      mv.visitInsn(DUP2);
      mv.visitInsn(FADD);
      mv.visitLdcInsn(new Float("-Infinity"));
      mv.visitInsn(FCMPL);
      mv.visitJumpInsn(IFNE, labelCancelDetection);
      reaction.insertReactionCall(mv, RESULT_IS_NEG_INF_MSG+"FADD", methods, classPath);
      mv.visitJumpInsn(GOTO, labelEndWithFadd);
    }
    mv.visitLabel(labelCancelDetection); {
      mv.visitInsn(DUP2);
      mv.visitInsn(FADD);
      mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "abs", "(F)F");
      mv.visitInsn(DUP2);
      mv.visitInsn(POP);
      mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "ulp", "(F)F");
      mv.visitLdcInsn(new Float(CANCELLATION_ULP_FACTOR_FLOAT));
      mv.visitInsn(FMUL);
      mv.visitInsn(FCMPG);
      mv.visitJumpInsn(IFGT, labelEndWithFadd);
      reaction.insertReactionCall(mv, CANCELLATION_MSG+"FADD", methods, classPath);
    }
    mv.visitLabel(labelEndWithFadd); {
      mv.visitInsn(FADD);
    }
  }

}