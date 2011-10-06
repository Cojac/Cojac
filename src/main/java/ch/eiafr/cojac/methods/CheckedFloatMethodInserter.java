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

package ch.eiafr.cojac.methods;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import ch.eiafr.cojac.Arg;
import ch.eiafr.cojac.Args;
import ch.eiafr.cojac.Methods;
import ch.eiafr.cojac.Signatures;
import ch.eiafr.cojac.reactions.Reaction;

import static ch.eiafr.cojac.models.CheckedDoubles.*;
import static org.objectweb.asm.Opcodes.*;

public final class CheckedFloatMethodInserter implements MethodInserter {
    @Override
    public void insertMethods(ClassVisitor cv, Args args, Methods methods, Reaction reaction, String classPath) {
        if (args.isOperationEnabled(Arg.FADD)) {
            addFaddCheckMethod(cv, methods, reaction, classPath);
        }

        if (args.isOperationEnabled(Arg.FSUB)) {
            addFsubCheckMethod(cv, methods, reaction, classPath);
        }

        if (args.isOperationEnabled(Arg.FMUL)) {
            addFmulCheckMethod(cv, methods, reaction, classPath);
        }

        if (args.isOperationEnabled(Arg.FDIV)) {
            addFdivCheckMethod(cv, methods, reaction, classPath);
        }

        if (args.isOperationEnabled(Arg.FREM)) {
            addFremCheckMethod(cv, methods, reaction, classPath);
        }

        if (args.isOperationEnabled(Arg.FCMP)) {
            addFCMPGCheckMethod(cv, methods, reaction, classPath);
            addFCMPLCheckMethod(cv, methods, reaction, classPath);
        }
    }

    private static void addFaddCheckMethod(ClassVisitor cv, Methods methods, Reaction reaction, String classPath) {
      MethodVisitor mv = cv.visitMethod(ACC_PUBLIC + ACC_STATIC, methods.getMethod(FADD), Signatures.CHECK_FLOAT_BINARY, null, null);
      mv.visitCode();
      Label l0 = new Label();
      mv.visitLabel(l0);
      mv.visitLineNumber(78, l0);
      mv.visitVarInsn(FLOAD, 0);
      mv.visitVarInsn(FLOAD, 1);
      mv.visitInsn(FADD);
      mv.visitVarInsn(FSTORE, 4);
      Label l1 = new Label();
      mv.visitLabel(l1);
      mv.visitLineNumber(80, l1);
      mv.visitVarInsn(FLOAD, 1);
      mv.visitInsn(FCONST_0);
      mv.visitInsn(FCMPL);
      Label l2 = new Label();
      mv.visitJumpInsn(IFEQ, l2);
      mv.visitVarInsn(FLOAD, 4);
      mv.visitVarInsn(FLOAD, 0);
      mv.visitInsn(FCMPL);
      Label l3 = new Label();
      mv.visitJumpInsn(IFEQ, l3);
      mv.visitLabel(l2);
      mv.visitVarInsn(FLOAD, 0);
      mv.visitInsn(FCONST_0);
      mv.visitInsn(FCMPL);
      Label l4 = new Label();
      mv.visitJumpInsn(IFEQ, l4);
      mv.visitVarInsn(FLOAD, 4);
      mv.visitVarInsn(FLOAD, 1);
      mv.visitInsn(FCMPL);
      mv.visitJumpInsn(IFNE, l4);
      mv.visitLabel(l3);
      mv.visitLineNumber(81, l3);
      mv.visitVarInsn(FLOAD, 4);
      mv.visitInsn(F2D);
      mv.visitLdcInsn(new Double("2.0"));
      mv.visitVarInsn(FLOAD, 4);
      mv.visitInsn(F2D);
      mv.visitInsn(DMUL);
      mv.visitInsn(DCMPL);
      Label l5 = new Label();
      mv.visitJumpInsn(IFEQ, l5);
      Label l6 = new Label();
      mv.visitLabel(l6);
      mv.visitLineNumber(82, l6);
      reaction.insertReactionCall(mv, PRECISION_MSG+"FADD", methods, classPath);
      mv.visitJumpInsn(GOTO, l5);
      mv.visitLabel(l4);
      mv.visitLineNumber(83, l4);
      mv.visitVarInsn(FLOAD, 0);
      mv.visitLdcInsn(new Float("Infinity"));
      mv.visitInsn(FCMPL);
      mv.visitJumpInsn(IFEQ, l5);
      mv.visitVarInsn(FLOAD, 0);
      mv.visitLdcInsn(new Float("-Infinity"));
      mv.visitInsn(FCMPL);
      mv.visitJumpInsn(IFEQ, l5);
      Label l7 = new Label();
      mv.visitLabel(l7);
      mv.visitLineNumber(84, l7);
      mv.visitVarInsn(FLOAD, 1);
      mv.visitLdcInsn(new Float("Infinity"));
      mv.visitInsn(FCMPL);
      mv.visitJumpInsn(IFEQ, l5);
      mv.visitVarInsn(FLOAD, 1);
      mv.visitLdcInsn(new Float("-Infinity"));
      mv.visitInsn(FCMPL);
      mv.visitJumpInsn(IFEQ, l5);
      
      mv.visitVarInsn(FLOAD, 4);
      mv.visitInsn(FCONST_0);
      mv.visitInsn(FCMPL);
      mv.visitJumpInsn(IFEQ, l5);
      
      Label l8 = new Label();
      mv.visitLabel(l8);
      mv.visitLineNumber(85, l8);
      mv.visitVarInsn(FLOAD, 4);
      mv.visitLdcInsn(new Float("Infinity"));
      mv.visitInsn(FCMPL);
      Label l9 = new Label();
      mv.visitJumpInsn(IFNE, l9);
      Label l10 = new Label();
      mv.visitLabel(l10);
      mv.visitLineNumber(86, l10);
      reaction.insertReactionCall(mv, RESULT_IS_POS_INF_MSG+"FADD", methods, classPath);
      mv.visitJumpInsn(GOTO, l5);
      mv.visitLabel(l9);
      mv.visitLineNumber(87, l9);
      mv.visitVarInsn(FLOAD, 4);
      mv.visitLdcInsn(new Float("-Infinity"));
      mv.visitInsn(FCMPL);
      Label l11 = new Label();
      mv.visitJumpInsn(IFNE, l11);
      Label l12 = new Label();
      mv.visitLabel(l12);
      mv.visitLineNumber(88, l12);
      reaction.insertReactionCall(mv, RESULT_IS_NEG_INF_MSG+"FADD", methods, classPath);
      mv.visitJumpInsn(GOTO, l5);
      mv.visitLabel(l11);
      mv.visitLineNumber(89, l11);
      mv.visitVarInsn(FLOAD, 4);
      mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "abs", "(F)F");
      mv.visitLdcInsn(new Float(CANCELLATION_ULP_FACTOR_FLOAT));
      mv.visitVarInsn(FLOAD, 0);
      mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "ulp", "(F)F");
      mv.visitInsn(FMUL);
      mv.visitInsn(FCMPG);
      mv.visitJumpInsn(IFGT, l5);
      Label l13 = new Label();
      mv.visitLabel(l13);
      mv.visitLineNumber(90, l13);
      reaction.insertReactionCall(mv, CANCELLATION_MSG+"FADD", methods, classPath);
      mv.visitLabel(l5);
      mv.visitLineNumber(93, l5);
      mv.visitVarInsn(FLOAD, 4);
      mv.visitInsn(FRETURN);
      Label l14 = new Label();
      mv.visitLabel(l14);
      mv.visitLocalVariable("a", "F", null, l0, l14, 0);
      mv.visitLocalVariable("b", "F", null, l0, l14, 1);
      mv.visitLocalVariable("reaction", "I", null, l0, l14, 2);
      mv.visitLocalVariable("logFileName", "Ljava/lang/String;", null, l0, l14, 3);
      mv.visitLocalVariable("r", "F", null, l1, l14, 4);
      mv.visitMaxs(6, 5);
      mv.visitEnd();
  }

    private static void addFsubCheckMethod(ClassVisitor cv, Methods methods, Reaction reaction, String classPath) {
      MethodVisitor mv = cv.visitMethod(ACC_PUBLIC + ACC_STATIC, methods.getMethod(FSUB), Signatures.CHECK_FLOAT_BINARY, null, null);
      mv.visitCode();
      Label l0 = new Label();
      mv.visitLabel(l0);
      mv.visitLineNumber(97, l0);
      mv.visitVarInsn(FLOAD, 0);
      mv.visitVarInsn(FLOAD, 1);
      mv.visitInsn(FSUB);
      mv.visitVarInsn(FSTORE, 4);
      Label l1 = new Label();
      mv.visitLabel(l1);
      mv.visitLineNumber(98, l1);
      mv.visitVarInsn(FLOAD, 1);
      mv.visitInsn(FCONST_0);
      mv.visitInsn(FCMPL);
      Label l2 = new Label();
      mv.visitJumpInsn(IFEQ, l2);
      mv.visitVarInsn(FLOAD, 4);
      mv.visitVarInsn(FLOAD, 0);
      mv.visitInsn(FCMPL);
      Label l3 = new Label();
      mv.visitJumpInsn(IFEQ, l3);
      mv.visitLabel(l2);
      mv.visitVarInsn(FLOAD, 0);
      mv.visitInsn(FCONST_0);
      mv.visitInsn(FCMPL);
      Label l4 = new Label();
      mv.visitJumpInsn(IFEQ, l4);
      mv.visitVarInsn(FLOAD, 4);
      mv.visitVarInsn(FLOAD, 1);
      mv.visitInsn(FNEG);
      mv.visitInsn(FCMPL);
      mv.visitJumpInsn(IFNE, l4);
      mv.visitLabel(l3);
      mv.visitLineNumber(99, l3);
      mv.visitVarInsn(FLOAD, 4);
      mv.visitInsn(F2D);
      mv.visitLdcInsn(new Double("2.0"));
      mv.visitVarInsn(FLOAD, 4);
      mv.visitInsn(F2D);
      mv.visitInsn(DMUL);
      mv.visitInsn(DCMPL);
      Label l5 = new Label();
      mv.visitJumpInsn(IFEQ, l5);
      Label l6 = new Label();
      mv.visitLabel(l6);
      mv.visitLineNumber(100, l6);
      reaction.insertReactionCall(mv, PRECISION_MSG+"FSUB", methods, classPath);
      mv.visitJumpInsn(GOTO, l5);
      mv.visitLabel(l4);
      mv.visitLineNumber(101, l4);
      mv.visitVarInsn(FLOAD, 0);
      mv.visitLdcInsn(new Float("Infinity"));
      mv.visitInsn(FCMPL);
      mv.visitJumpInsn(IFEQ, l5);
      mv.visitVarInsn(FLOAD, 0);
      mv.visitLdcInsn(new Float("-Infinity"));
      mv.visitInsn(FCMPL);
      mv.visitJumpInsn(IFEQ, l5);
      Label l7 = new Label();
      mv.visitLabel(l7);
      mv.visitLineNumber(102, l7);
      mv.visitVarInsn(FLOAD, 1);
      mv.visitLdcInsn(new Float("Infinity"));
      mv.visitInsn(FCMPL);
      mv.visitJumpInsn(IFEQ, l5);
      mv.visitVarInsn(FLOAD, 1);
      mv.visitLdcInsn(new Float("-Infinity"));
      mv.visitInsn(FCMPL);
      mv.visitJumpInsn(IFEQ, l5);
      
      mv.visitVarInsn(FLOAD, 4);
      mv.visitInsn(FCONST_0);
      mv.visitInsn(FCMPL);
      mv.visitJumpInsn(IFEQ, l5);

      
      Label l8 = new Label();
      mv.visitLabel(l8);
      mv.visitLineNumber(103, l8);
      mv.visitVarInsn(FLOAD, 4);
      mv.visitLdcInsn(new Float("Infinity"));
      mv.visitInsn(FCMPL);
      Label l9 = new Label();
      mv.visitJumpInsn(IFNE, l9);
      Label l10 = new Label();
      mv.visitLabel(l10);
      mv.visitLineNumber(104, l10);
      reaction.insertReactionCall(mv, RESULT_IS_POS_INF_MSG+"FSUB", methods, classPath);
      mv.visitJumpInsn(GOTO, l5);
      mv.visitLabel(l9);
      mv.visitLineNumber(105, l9);
      mv.visitVarInsn(FLOAD, 4);
      mv.visitLdcInsn(new Float("-Infinity"));
      mv.visitInsn(FCMPL);
      Label l11 = new Label();
      mv.visitJumpInsn(IFNE, l11);
      Label l12 = new Label();
      mv.visitLabel(l12);
      mv.visitLineNumber(106, l12);
      reaction.insertReactionCall(mv, RESULT_IS_NEG_INF_MSG+"FSUB", methods, classPath);
      mv.visitJumpInsn(GOTO, l5);
      mv.visitLabel(l11);
      mv.visitLineNumber(107, l11);
      mv.visitVarInsn(FLOAD, 4);
      mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "abs", "(F)F");
      mv.visitLdcInsn(new Float(CANCELLATION_ULP_FACTOR_FLOAT));
      mv.visitVarInsn(FLOAD, 0);
      mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "ulp", "(F)F");
      mv.visitInsn(FMUL);
      mv.visitInsn(FCMPG);
      mv.visitJumpInsn(IFGT, l5);
      Label l13 = new Label();
      mv.visitLabel(l13);
      mv.visitLineNumber(108, l13);
      reaction.insertReactionCall(mv, CANCELLATION_MSG+"FSUB", methods, classPath);
      mv.visitLabel(l5);
      mv.visitLineNumber(112, l5);
      mv.visitVarInsn(FLOAD, 4);
      mv.visitInsn(FRETURN);
      Label l14 = new Label();
      mv.visitLabel(l14);
      mv.visitLocalVariable("a", "F", null, l0, l14, 0);
      mv.visitLocalVariable("b", "F", null, l0, l14, 1);
      mv.visitLocalVariable("reaction", "I", null, l0, l14, 2);
      mv.visitLocalVariable("logFileName", "Ljava/lang/String;", null, l0, l14, 3);
      mv.visitLocalVariable("r", "F", null, l1, l14, 4);
      mv.visitMaxs(6, 5);
      mv.visitEnd();
    }

    private static void addFmulCheckMethod(ClassVisitor cv, Methods methods, Reaction reaction, String classPath) {
        MethodVisitor mv = cv.visitMethod(ACC_PUBLIC + ACC_STATIC, methods.getMethod(FMUL), Signatures.CHECK_FLOAT_BINARY, null, null);

        {
          mv.visitCode();
          Label l0 = new Label();
          mv.visitLabel(l0);
          mv.visitLineNumber(43, l0);
          mv.visitVarInsn(FLOAD, 0);
          mv.visitVarInsn(FLOAD, 1);
          mv.visitInsn(FMUL);
          mv.visitVarInsn(FSTORE, 4);
          Label l1 = new Label();
          mv.visitLabel(l1);
          mv.visitLineNumber(45, l1);
          mv.visitVarInsn(FLOAD, 0);
          mv.visitLdcInsn(new Float("Infinity"));
          mv.visitInsn(FCMPL);
          Label l2 = new Label();
          mv.visitJumpInsn(IFEQ, l2);
          mv.visitVarInsn(FLOAD, 0);
          mv.visitLdcInsn(new Float("-Infinity"));
          mv.visitInsn(FCMPL);
          mv.visitJumpInsn(IFEQ, l2);
          mv.visitVarInsn(FLOAD, 1);
          mv.visitLdcInsn(new Float("Infinity"));
          mv.visitInsn(FCMPL);
          mv.visitJumpInsn(IFEQ, l2);
          mv.visitVarInsn(FLOAD, 1);
          mv.visitLdcInsn(new Float("-Infinity"));
          mv.visitInsn(FCMPL);
          mv.visitJumpInsn(IFEQ, l2);
          Label l3 = new Label();
          mv.visitLabel(l3);
          mv.visitLineNumber(46, l3);
          mv.visitVarInsn(FLOAD, 4);
          mv.visitLdcInsn(new Float("Infinity"));
          mv.visitInsn(FCMPL);
          Label l4 = new Label();
          mv.visitJumpInsn(IFNE, l4);
          Label l5 = new Label();
          mv.visitLabel(l5);
          mv.visitLineNumber(47, l5);
          reaction.insertReactionCall(mv, RESULT_IS_POS_INF_MSG+"FMUL", methods, classPath);          mv.visitJumpInsn(GOTO, l2);
          mv.visitLabel(l4);
          mv.visitLineNumber(48, l4);
          mv.visitVarInsn(FLOAD, 4);
          mv.visitLdcInsn(new Float("-Infinity"));
          mv.visitInsn(FCMPL);
          Label l6 = new Label();
          mv.visitJumpInsn(IFNE, l6);
          Label l7 = new Label();
          mv.visitLabel(l7);
          mv.visitLineNumber(49, l7);
          reaction.insertReactionCall(mv, RESULT_IS_NEG_INF_MSG+"FMUL", methods, classPath);
          mv.visitJumpInsn(GOTO, l2);
          mv.visitLabel(l6);
          mv.visitLineNumber(50, l6);
          mv.visitVarInsn(FLOAD, 4);
          mv.visitInsn(FCONST_0);
          mv.visitInsn(FCMPL);
          mv.visitJumpInsn(IFNE, l2);
          mv.visitVarInsn(FLOAD, 0);
          mv.visitInsn(FCONST_0);
          mv.visitInsn(FCMPL);
          mv.visitJumpInsn(IFEQ, l2);
          mv.visitVarInsn(FLOAD, 1);
          mv.visitInsn(FCONST_0);
          mv.visitInsn(FCMPL);
          mv.visitJumpInsn(IFEQ, l2);
          Label l8 = new Label();
          mv.visitLabel(l8);
          mv.visitLineNumber(51, l8);
          reaction.insertReactionCall(mv, UNDERFLOW_MSG+"FMUL", methods, classPath);
          mv.visitLabel(l2);
          mv.visitLineNumber(55, l2);
          mv.visitVarInsn(FLOAD, 4);
          mv.visitInsn(FRETURN);
          Label l9 = new Label();
          mv.visitLabel(l9);
          mv.visitLocalVariable("a", "F", null, l0, l9, 0);
          mv.visitLocalVariable("b", "F", null, l0, l9, 1);
          mv.visitLocalVariable("r", "F", null, l1, l9, 4);
          mv.visitMaxs(2, 5);
          mv.visitEnd();
        }
    }

    private static void addFdivCheckMethod(ClassVisitor cv, Methods methods, Reaction reaction, String classPath) {
        MethodVisitor mv = cv.visitMethod(ACC_PUBLIC + ACC_STATIC, methods.getMethod(FDIV), Signatures.CHECK_FLOAT_BINARY, null, null);
        {
          mv.visitCode();
          Label l0 = new Label();
          mv.visitLabel(l0);
          mv.visitLineNumber(59, l0);
          mv.visitVarInsn(FLOAD, 0);
          mv.visitVarInsn(FLOAD, 1);
          mv.visitInsn(FDIV);
          mv.visitVarInsn(FSTORE, 4);
          Label l1 = new Label();
          mv.visitLabel(l1);
          mv.visitLineNumber(61, l1);
          mv.visitVarInsn(FLOAD, 0);
          mv.visitVarInsn(FLOAD, 0);
          mv.visitInsn(FCMPL);
          Label l2 = new Label();
          mv.visitJumpInsn(IFNE, l2);
          mv.visitVarInsn(FLOAD, 1);
          mv.visitVarInsn(FLOAD, 1);
          mv.visitInsn(FCMPL);
          mv.visitJumpInsn(IFNE, l2);
          mv.visitVarInsn(FLOAD, 4);
          mv.visitVarInsn(FLOAD, 4);
          mv.visitInsn(FCMPL);
          mv.visitJumpInsn(IFEQ, l2);
          Label l3 = new Label();
          mv.visitLabel(l3);
          mv.visitLineNumber(62, l3);
          reaction.insertReactionCall(mv, RESULT_IS_NAN_MSG+"FDIV", methods, classPath);
          Label l4 = new Label();
          mv.visitJumpInsn(GOTO, l4);
          mv.visitLabel(l2);
          mv.visitLineNumber(63, l2);
          mv.visitVarInsn(FLOAD, 0);
          mv.visitLdcInsn(new Float("Infinity"));
          mv.visitInsn(FCMPL);
          mv.visitJumpInsn(IFEQ, l4);
          mv.visitVarInsn(FLOAD, 0);
          mv.visitLdcInsn(new Float("-Infinity"));
          mv.visitInsn(FCMPL);
          mv.visitJumpInsn(IFEQ, l4);
          mv.visitVarInsn(FLOAD, 1);
          mv.visitLdcInsn(new Float("Infinity"));
          mv.visitInsn(FCMPL);
          mv.visitJumpInsn(IFEQ, l4);
          mv.visitVarInsn(FLOAD, 1);
          mv.visitLdcInsn(new Float("-Infinity"));
          mv.visitInsn(FCMPL);
          mv.visitJumpInsn(IFEQ, l4);
          Label l5 = new Label();
          mv.visitLabel(l5);
          mv.visitLineNumber(64, l5);
          mv.visitVarInsn(FLOAD, 4);
          mv.visitLdcInsn(new Float("Infinity"));
          mv.visitInsn(FCMPL);
          Label l6 = new Label();
          mv.visitJumpInsn(IFNE, l6);
          Label l7 = new Label();
          mv.visitLabel(l7);
          mv.visitLineNumber(65, l7);
          reaction.insertReactionCall(mv, RESULT_IS_POS_INF_MSG+"FDIV", methods, classPath);
          mv.visitJumpInsn(GOTO, l4);
          mv.visitLabel(l6);
          mv.visitLineNumber(66, l6);
          mv.visitVarInsn(FLOAD, 4);
          mv.visitLdcInsn(new Float("-Infinity"));
          mv.visitInsn(FCMPL);
          Label l8 = new Label();
          mv.visitJumpInsn(IFNE, l8);
          Label l9 = new Label();
          mv.visitLabel(l9);
          mv.visitLineNumber(67, l9);
          reaction.insertReactionCall(mv, RESULT_IS_NEG_INF_MSG+"FDIV", methods, classPath);
          mv.visitJumpInsn(GOTO, l4);
          mv.visitLabel(l8);
          mv.visitLineNumber(68, l8);
          mv.visitVarInsn(FLOAD, 4);
          mv.visitInsn(FCONST_0);
          mv.visitInsn(FCMPL);
          mv.visitJumpInsn(IFNE, l4);
          mv.visitVarInsn(FLOAD, 0);
          mv.visitInsn(FCONST_0);
          mv.visitInsn(FCMPL);
          mv.visitJumpInsn(IFEQ, l4);
          Label l10 = new Label();
          mv.visitLabel(l10);
          mv.visitLineNumber(69, l10);
          reaction.insertReactionCall(mv, UNDERFLOW_MSG+"FDIV", methods, classPath);
          mv.visitLabel(l4);
          mv.visitLineNumber(73, l4);
          mv.visitVarInsn(FLOAD, 4);
          mv.visitInsn(FRETURN);
          Label l11 = new Label();
          mv.visitLabel(l11);
          mv.visitLocalVariable("a", "F", null, l0, l11, 0);
          mv.visitLocalVariable("b", "F", null, l0, l11, 1);
          mv.visitLocalVariable("r", "F", null, l1, l11, 4);
          mv.visitMaxs(2, 5);
          mv.visitEnd();
        }

    }


    private static void addFremCheckMethod(ClassVisitor cv, Methods methods, Reaction reaction, String classPath) {
      MethodVisitor mv = cv.visitMethod(ACC_PUBLIC + ACC_STATIC, methods.getMethod(FREM), Signatures.CHECK_FLOAT_BINARY, null, null);
      mv.visitCode();
      Label l0 = new Label();
      mv.visitLabel(l0);
      mv.visitLineNumber(108, l0);
      mv.visitVarInsn(FLOAD, 0);
      mv.visitVarInsn(FLOAD, 1);
      mv.visitInsn(FREM);
      mv.visitVarInsn(FSTORE, 4);
      Label l1 = new Label();
      mv.visitLabel(l1);
      mv.visitLineNumber(110, l1);
      mv.visitVarInsn(FLOAD, 0);
      mv.visitVarInsn(FLOAD, 0);
      mv.visitInsn(FCMPL);
      Label l2 = new Label();
      mv.visitJumpInsn(IFNE, l2);
      mv.visitVarInsn(FLOAD, 1);
      mv.visitVarInsn(FLOAD, 1);
      mv.visitInsn(FCMPL);
      mv.visitJumpInsn(IFNE, l2);
      mv.visitVarInsn(FLOAD, 4);
      mv.visitVarInsn(FLOAD, 4);
      mv.visitInsn(FCMPL);
      mv.visitJumpInsn(IFEQ, l2);
      Label l3 = new Label();
      mv.visitLabel(l3);
      mv.visitLineNumber(111, l3);
      reaction.insertReactionCall(mv, RESULT_IS_NAN_MSG+"FREM", methods, classPath);
      mv.visitLabel(l2);
      mv.visitLineNumber(113, l2);
      mv.visitVarInsn(FLOAD, 0);
      mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "ulp", "(F)F");
      mv.visitVarInsn(FLOAD, 1);
      mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "abs", "(F)F");
      mv.visitInsn(FCMPL);
      Label l4 = new Label();
      mv.visitJumpInsn(IFLE, l4);
      Label l5 = new Label();
      mv.visitLabel(l5);
      mv.visitLineNumber(114, l5);
      reaction.insertReactionCall(mv, PRECISION_MSG+"FREM", methods, classPath);
      mv.visitLabel(l4);
      mv.visitLineNumber(117, l4);
      mv.visitVarInsn(FLOAD, 4);
      mv.visitInsn(FRETURN);
      Label l6 = new Label();
      mv.visitLabel(l6);
      mv.visitLocalVariable("a", "F", null, l0, l6, 0);
      mv.visitLocalVariable("b", "F", null, l0, l6, 1);
      mv.visitLocalVariable("reaction", "I", null, l0, l6, 2);
      mv.visitLocalVariable("logFileName", "Ljava/lang/String;", null, l0, l6, 3);
      mv.visitLocalVariable("r", "F", null, l1, l6, 4);
      mv.visitMaxs(2, 5);
      mv.visitEnd();
  }

    private static void addFCMPGCheckMethod(ClassVisitor cv, Methods methods, Reaction reaction, String classPath) {
        MethodVisitor mv = cv.visitMethod(ACC_PUBLIC + ACC_STATIC, methods.getMethod(FCMPG), Signatures.CHECK_FLOAT_CMP, null, null);
        {
          mv.visitCode();
          Label l0 = new Label();
          mv.visitLabel(l0);
          mv.visitLineNumber(20, l0);
          mv.visitVarInsn(FLOAD, 0);
          mv.visitVarInsn(FLOAD, 1);
          mv.visitInsn(FCMPL);
          Label l1 = new Label();
          mv.visitJumpInsn(IFNE, l1);
          mv.visitInsn(ICONST_0);
          Label l2 = new Label();
          mv.visitJumpInsn(GOTO, l2);
          mv.visitLabel(l1);
          mv.visitVarInsn(FLOAD, 0);
          mv.visitVarInsn(FLOAD, 1);
          mv.visitInsn(FCMPG);
          Label l3 = new Label();
          mv.visitJumpInsn(IFGE, l3);
          mv.visitInsn(ICONST_M1);
          mv.visitJumpInsn(GOTO, l2);
          mv.visitLabel(l3);
          mv.visitInsn(ICONST_1);
          mv.visitLabel(l2);
          mv.visitVarInsn(ISTORE, 4);
          Label l4 = new Label();
          mv.visitLabel(l4);
          mv.visitLineNumber(21, l4);
          mv.visitVarInsn(FLOAD, 0);
          mv.visitInsn(FCONST_2);
          mv.visitVarInsn(FLOAD, 0);
          mv.visitInsn(FMUL);
          mv.visitInsn(FCMPL);
          Label l5 = new Label();
          mv.visitJumpInsn(IFEQ, l5);
          mv.visitVarInsn(FLOAD, 1);
          mv.visitInsn(FCONST_2);
          mv.visitVarInsn(FLOAD, 1);
          mv.visitInsn(FMUL);
          mv.visitInsn(FCMPL);
          Label l6 = new Label();
          mv.visitJumpInsn(IFNE, l6);
          mv.visitLabel(l5);
          mv.visitLineNumber(22, l5);
          mv.visitVarInsn(ILOAD, 4);
          mv.visitInsn(IRETURN);
          mv.visitLabel(l6);
          mv.visitLineNumber(23, l6);
          mv.visitVarInsn(ILOAD, 4);
          Label l7 = new Label();
          mv.visitJumpInsn(IFEQ, l7);
          mv.visitVarInsn(FLOAD, 0);
          mv.visitVarInsn(FLOAD, 1);
          mv.visitInsn(FSUB);
          mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "abs", "(F)F");
          mv.visitLdcInsn(new Float(CLOSENESS_ULP_FACTOR_FLOAT));
          mv.visitVarInsn(FLOAD, 0);
          mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "ulp", "(F)F");
          mv.visitInsn(FMUL);
          mv.visitInsn(FCMPG);
          mv.visitJumpInsn(IFGT, l7);
          Label l8 = new Label();
          mv.visitLabel(l8);
          mv.visitLineNumber(24, l8);
          reaction.insertReactionCall(mv, VERY_CLOSE_MSG+"FCMP", methods, classPath);
          mv.visitLabel(l7);
          mv.visitLineNumber(26, l7);
          mv.visitVarInsn(ILOAD, 4);
          mv.visitInsn(IRETURN);
          Label l9 = new Label();
          mv.visitLabel(l9);
          mv.visitLocalVariable("a", "F", null, l0, l9, 0);
          mv.visitLocalVariable("b", "F", null, l0, l9, 1);
          mv.visitLocalVariable("reaction", "I", null, l0, l9, 2);
          mv.visitLocalVariable("logFileName", "Ljava/lang/String;", null, l0, l9, 3);
          mv.visitLocalVariable("r", "I", null, l4, l9, 4);
          mv.visitMaxs(3, 5);
          mv.visitEnd();
          }
    }
    
    private static void addFCMPLCheckMethod(ClassVisitor cv, Methods methods, Reaction reaction, String classPath) {
      MethodVisitor mv = cv.visitMethod(ACC_PUBLIC + ACC_STATIC, methods.getMethod(FCMPL), Signatures.CHECK_FLOAT_CMP, null, null);
      {
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitLineNumber(20, l0);
        mv.visitVarInsn(FLOAD, 0);
        mv.visitVarInsn(FLOAD, 1);
        mv.visitInsn(FCMPL);
        Label l1 = new Label();
        mv.visitJumpInsn(IFNE, l1);
        mv.visitInsn(ICONST_0);
        Label l2 = new Label();
        mv.visitJumpInsn(GOTO, l2);
        mv.visitLabel(l1);
        mv.visitVarInsn(FLOAD, 0);
        mv.visitVarInsn(FLOAD, 1);
        mv.visitInsn(FCMPG);
        Label l3 = new Label();
        mv.visitJumpInsn(IFGE, l3);
        mv.visitInsn(ICONST_M1);
        mv.visitJumpInsn(GOTO, l2);
        mv.visitLabel(l3);
        mv.visitInsn(ICONST_1);
        mv.visitLabel(l2);
        mv.visitVarInsn(ISTORE, 4);
        Label l4 = new Label();
        mv.visitLabel(l4);
        mv.visitLineNumber(21, l4);
        mv.visitVarInsn(FLOAD, 0);
        mv.visitInsn(FCONST_2);
        mv.visitVarInsn(FLOAD, 0);
        mv.visitInsn(FMUL);
        mv.visitInsn(FCMPL);
        Label l5 = new Label();
        mv.visitJumpInsn(IFEQ, l5);
        mv.visitVarInsn(FLOAD, 1);
        mv.visitInsn(FCONST_2);
        mv.visitVarInsn(FLOAD, 1);
        mv.visitInsn(FMUL);
        mv.visitInsn(FCMPL);
        Label l6 = new Label();
        mv.visitJumpInsn(IFNE, l6);
        mv.visitLabel(l5);
        mv.visitLineNumber(22, l5);
        mv.visitVarInsn(ILOAD, 4);
        mv.visitInsn(IRETURN);
        mv.visitLabel(l6);
        mv.visitLineNumber(23, l6);
        mv.visitVarInsn(ILOAD, 4);
        Label l7 = new Label();
        mv.visitJumpInsn(IFEQ, l7);
        mv.visitVarInsn(FLOAD, 0);
        mv.visitVarInsn(FLOAD, 1);
        mv.visitInsn(FSUB);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "abs", "(F)F");
        mv.visitLdcInsn(new Float(CLOSENESS_ULP_FACTOR_FLOAT));
        mv.visitVarInsn(FLOAD, 0);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "ulp", "(F)F");
        mv.visitInsn(FMUL);
        mv.visitInsn(FCMPG);
        mv.visitJumpInsn(IFGT, l7);
        Label l8 = new Label();
        mv.visitLabel(l8);
        mv.visitLineNumber(24, l8);
        reaction.insertReactionCall(mv, VERY_CLOSE_MSG+"FCMP", methods, classPath);
        mv.visitLabel(l7);
        mv.visitLineNumber(26, l7);
        mv.visitVarInsn(ILOAD, 4);
        mv.visitInsn(IRETURN);
        Label l9 = new Label();
        mv.visitLabel(l9);
        mv.visitLocalVariable("a", "F", null, l0, l9, 0);
        mv.visitLocalVariable("b", "F", null, l0, l9, 1);
        mv.visitLocalVariable("reaction", "I", null, l0, l9, 2);
        mv.visitLocalVariable("logFileName", "Ljava/lang/String;", null, l0, l9, 3);
        mv.visitLocalVariable("r", "I", null, l4, l9, 4);
        mv.visitMaxs(3, 5);
        mv.visitEnd();
      }
    }
}