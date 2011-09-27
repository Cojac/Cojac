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
import ch.eiafr.cojac.utils.BytecodeUtils;

import static org.objectweb.asm.Opcodes.*;
import static ch.eiafr.cojac.models.CheckedDoubles.*;

public final class DADDInstrumenter implements OpCodeInstrumenter {
  @Override
  public void instrument(MethodVisitor mv, int opCode, String classPath, Methods methods, Reaction reaction, LocalVariablesSorter src) {
    Label lA = new Label();
    Label labelKillInfPrecision = new Label();
    Label lC = new Label();
    Label labelEndWithDadd = new Label();
    Label l2 = new Label();
    Label labelCancelDetection=new Label();
    Label labelReactPrecision=new Label();
    {
      mv.visitInsn(DUP2);
      mv.visitInsn(DCONST_0);
      mv.visitInsn(DCMPL);
      mv.visitJumpInsn(IFEQ, lA);

      BytecodeUtils.addDup4(mv);
      BytecodeUtils.addSwap2(mv);
      BytecodeUtils.addDup4(mv);
      mv.visitInsn(DADD);
      mv.visitInsn(DCMPL);
      mv.visitInsn(DUP_X2);
      mv.visitInsn(POP);
      mv.visitInsn(POP2);
      mv.visitJumpInsn(IFEQ, labelKillInfPrecision);
    }
    mv.visitLabel(lA); {
      BytecodeUtils.addDup4(mv);
      mv.visitInsn(POP2);
      mv.visitInsn(DCONST_0);
      mv.visitInsn(DCMPL);
      mv.visitJumpInsn(IFEQ, lC);
      BytecodeUtils.addDup4(mv);
      BytecodeUtils.addDup4(mv);
      mv.visitInsn(DADD);
      mv.visitInsn(DCMPL);
      mv.visitInsn(DUP_X2);
      mv.visitInsn(POP);
      mv.visitInsn(POP2);
      mv.visitJumpInsn(IFNE, lC);
    }
    mv.visitLabel(labelKillInfPrecision); {
      BytecodeUtils.addDup4(mv);
      mv.visitInsn(DADD);
      mv.visitInsn(DUP2);
      mv.visitLdcInsn(new Double(2.0));
      mv.visitInsn(DMUL);
      mv.visitInsn(DCMPL);
      mv.visitJumpInsn(IFEQ, labelEndWithDadd);
    }
    mv.visitLabel(labelReactPrecision); {
      reaction.insertReactionCall(mv, PRECISION_MSG+"DADD", methods, classPath);
      mv.visitJumpInsn(GOTO, labelEndWithDadd);
    }
    mv.visitLabel(lC); {
      mv.visitInsn(DUP2);
      mv.visitLdcInsn(new Double("Infinity"));
      mv.visitInsn(DCMPL);
      mv.visitJumpInsn(IFEQ, labelEndWithDadd);
      mv.visitInsn(DUP2);
      mv.visitLdcInsn(new Double("-Infinity"));
      mv.visitInsn(DCMPL);
      mv.visitJumpInsn(IFEQ, labelEndWithDadd);
      BytecodeUtils.addDup4(mv);
      mv.visitInsn(POP2);
      mv.visitLdcInsn(new Double("Infinity"));
      mv.visitInsn(DCMPL);
      mv.visitJumpInsn(IFEQ, labelEndWithDadd);
      BytecodeUtils.addDup4(mv);
      mv.visitInsn(POP2);
      mv.visitLdcInsn(new Double("-Infinity"));
      mv.visitInsn(DCMPL);
      mv.visitJumpInsn(IFEQ, labelEndWithDadd);
      
      BytecodeUtils.addDup4(mv);                 // a,b,a,b
      mv.visitInsn(DADD);                        // a,b,(a-b)
      mv.visitInsn(DCONST_0);                    // a,b,(a-b),0
      mv.visitInsn(DCMPL);                       // a,b,(a-b ? 0)
      mv.visitJumpInsn(IFEQ, labelEndWithDadd);  // a,b

      BytecodeUtils.addDup4(mv);
      mv.visitInsn(DADD);
      mv.visitLdcInsn(new Double("Infinity"));
      mv.visitInsn(DCMPL);
      mv.visitJumpInsn(IFNE, l2);
      reaction.insertReactionCall(mv, RESULT_IS_POS_INF_MSG+"DADD", methods, classPath);
      mv.visitJumpInsn(GOTO, labelEndWithDadd);
    }
    mv.visitLabel(l2); {
      BytecodeUtils.addDup4(mv);
      mv.visitInsn(DADD);
      mv.visitLdcInsn(new Double("-Infinity"));
      mv.visitInsn(DCMPL);
      mv.visitJumpInsn(IFNE, labelCancelDetection);
      reaction.insertReactionCall(mv, RESULT_IS_NEG_INF_MSG+"DADD", methods, classPath);
      mv.visitJumpInsn(GOTO, labelEndWithDadd);
    }
    mv.visitLabel(labelCancelDetection); {
      BytecodeUtils.addDup4(mv);
      mv.visitInsn(DADD);
      mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "abs", "(D)D");
      BytecodeUtils.addDup4(mv);
      mv.visitInsn(POP2);
      mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "ulp", "(D)D");
      mv.visitLdcInsn(new Double(CANCELLATION_ULP_FACTOR_DOUBLE));
      mv.visitInsn(DMUL);
      mv.visitInsn(DCMPG);
      mv.visitJumpInsn(IFGT, labelEndWithDadd);
      reaction.insertReactionCall(mv, CANCELLATION_MSG+"DADD", methods, classPath);
    }
    mv.visitLabel(labelEndWithDadd); {
      mv.visitInsn(DADD);
    }
  }

}
