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

public final class CheckedDoubleMethodInserter implements MethodInserter {
    @Override
    public void insertMethods(ClassVisitor cv, Args args, Methods methods, Reaction reaction, String classPath) {
        if (args.isOperationEnabled(Arg.DADD)) {
            addDaddCheckMethod(cv, methods, reaction, classPath);
        }
        if (args.isOperationEnabled(Arg.DSUB)) {
            addDsubCheckMethod(cv, methods, reaction, classPath);
        }
        if (args.isOperationEnabled(Arg.DMUL)) {
            addDmulCheckMethod(cv, methods, reaction, classPath);
        }
        if (args.isOperationEnabled(Arg.DDIV)) {
            addDdivCheckMethod(cv, methods, reaction, classPath);
        }
        if (args.isOperationEnabled(Arg.DREM)) {
            addDremCheckMethod(cv, methods, reaction, classPath);
        }
        if (args.isOperationEnabled(Arg.DCMP)) {
            addDCMPGCheckMethod(cv, methods, reaction, classPath);
            addDCMPLCheckMethod(cv, methods, reaction, classPath);
        }
    }

    // asmified from models.CheckedDoubles.checkedDADD
    private static void addDaddCheckMethod(ClassVisitor cv, Methods methods, Reaction reaction, String classPath) {
      MethodVisitor mv = cv.visitMethod(ACC_PUBLIC + ACC_STATIC, methods.getMethod(DADD), Signatures.CHECK_DOUBLE_BINARY, null, null);
      mv.visitCode();
      Label l0 = new Label();
      mv.visitLabel(l0);
      mv.visitLineNumber(14, l0);
      mv.visitVarInsn(DLOAD, 0);
      mv.visitVarInsn(DLOAD, 2);
      mv.visitInsn(DADD);
      mv.visitVarInsn(DSTORE, 6);
      Label l1 = new Label();
      mv.visitLabel(l1);
      mv.visitLineNumber(16, l1);
      mv.visitVarInsn(DLOAD, 2);
      mv.visitInsn(DCONST_0);
      mv.visitInsn(DCMPL);
      Label l2 = new Label();
      mv.visitJumpInsn(IFEQ, l2);
      mv.visitVarInsn(DLOAD, 6);
      mv.visitVarInsn(DLOAD, 0);
      mv.visitInsn(DCMPL);
      Label l3 = new Label();
      mv.visitJumpInsn(IFEQ, l3);
      mv.visitLabel(l2);
      mv.visitVarInsn(DLOAD, 0);
      mv.visitInsn(DCONST_0);
      mv.visitInsn(DCMPL);
      Label l4 = new Label();
      mv.visitJumpInsn(IFEQ, l4);
      mv.visitVarInsn(DLOAD, 6);
      mv.visitVarInsn(DLOAD, 2);
      mv.visitInsn(DCMPL);
      mv.visitJumpInsn(IFNE, l4);
      mv.visitLabel(l3);
      mv.visitLineNumber(17, l3);
      mv.visitVarInsn(DLOAD, 6);
      mv.visitLdcInsn(new Double("2.0"));
      mv.visitVarInsn(DLOAD, 6);
      mv.visitInsn(DMUL);
      mv.visitInsn(DCMPL);
      Label l5 = new Label();
      mv.visitJumpInsn(IFEQ, l5);
      Label l6 = new Label();
      mv.visitLabel(l6);
      mv.visitLineNumber(18, l6);
      reaction.insertReactionCall(mv, PRECISION_MSG+"DADD", methods, classPath);
      mv.visitJumpInsn(GOTO, l5);
      mv.visitLabel(l4);
      mv.visitLineNumber(19, l4);
      mv.visitVarInsn(DLOAD, 0);
      mv.visitLdcInsn(new Double("Infinity"));
      mv.visitInsn(DCMPL);
      mv.visitJumpInsn(IFEQ, l5);
      mv.visitVarInsn(DLOAD, 0);
      mv.visitLdcInsn(new Double("-Infinity"));
      mv.visitInsn(DCMPL);
      mv.visitJumpInsn(IFEQ, l5);
      mv.visitVarInsn(DLOAD, 2);
      mv.visitLdcInsn(new Double("Infinity"));
      mv.visitInsn(DCMPL);
      mv.visitJumpInsn(IFEQ, l5);
      mv.visitVarInsn(DLOAD, 2);
      mv.visitLdcInsn(new Double("-Infinity"));
      mv.visitInsn(DCMPL);
      mv.visitJumpInsn(IFEQ, l5);
      
      mv.visitVarInsn(DLOAD, 6);
      mv.visitInsn(DCONST_0);
      mv.visitInsn(DCMPL);
      mv.visitJumpInsn(IFEQ, l5);

      
      Label l7 = new Label();
      mv.visitLabel(l7);
      mv.visitLineNumber(20, l7);
      mv.visitVarInsn(DLOAD, 6);
      mv.visitLdcInsn(new Double("Infinity"));
      mv.visitInsn(DCMPL);
      Label l8 = new Label();
      mv.visitJumpInsn(IFNE, l8);
      Label l9 = new Label();
      mv.visitLabel(l9);
      mv.visitLineNumber(21, l9);
      reaction.insertReactionCall(mv, RESULT_IS_POS_INF_MSG+"DADD", methods, classPath);
      mv.visitJumpInsn(GOTO, l5);
      mv.visitLabel(l8);
      mv.visitLineNumber(22, l8);
      mv.visitVarInsn(DLOAD, 6);
      mv.visitLdcInsn(new Double("-Infinity"));
      mv.visitInsn(DCMPL);
      Label l10 = new Label();
      mv.visitJumpInsn(IFNE, l10);
      Label l11 = new Label();
      mv.visitLabel(l11);
      mv.visitLineNumber(23, l11);
      reaction.insertReactionCall(mv, RESULT_IS_NEG_INF_MSG+"DADD", methods, classPath);
      mv.visitJumpInsn(GOTO, l5);
      mv.visitLabel(l10);
      mv.visitLineNumber(24, l10);
      mv.visitVarInsn(DLOAD, 6);
      mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "abs", "(D)D");
      mv.visitLdcInsn(new Double(CANCELLATION_ULP_FACTOR_DOUBLE));
      mv.visitVarInsn(DLOAD, 0);
      mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "ulp", "(D)D");
      mv.visitInsn(DMUL);
      mv.visitInsn(DCMPG);
      mv.visitJumpInsn(IFGT, l5);
      Label l12 = new Label();
      mv.visitLabel(l12);
      mv.visitLineNumber(25, l12);
      reaction.insertReactionCall(mv, CANCELLATION_MSG+"DADD", methods, classPath);
      mv.visitLabel(l5);
      mv.visitLineNumber(29, l5);
      mv.visitVarInsn(DLOAD, 6);
      mv.visitInsn(DRETURN);
      Label l13 = new Label();
      mv.visitLabel(l13);
      mv.visitLocalVariable("a", "D", null, l0, l13, 0);
      mv.visitLocalVariable("b", "D", null, l0, l13, 2);
      mv.visitLocalVariable("reaction", "I", null, l0, l13, 4);
      mv.visitLocalVariable("logFileName", "Ljava/lang/String;", null, l0, l13, 5);
      mv.visitLocalVariable("r", "D", null, l1, l13, 6);
      mv.visitMaxs(6, 8);
      mv.visitEnd();
    }

    private static void addDsubCheckMethod(ClassVisitor cv, Methods methods, Reaction reaction, String classPath) {
      MethodVisitor mv = cv.visitMethod(ACC_PUBLIC + ACC_STATIC, methods.getMethod(DSUB), Signatures.CHECK_DOUBLE_BINARY, null, null);
      mv.visitCode();
      Label l0 = new Label();
      mv.visitLabel(l0);
      mv.visitLineNumber(32, l0);
      mv.visitVarInsn(DLOAD, 0);
      mv.visitVarInsn(DLOAD, 2);
      mv.visitInsn(DSUB);
      mv.visitVarInsn(DSTORE, 6);
      Label l1 = new Label();
      mv.visitLabel(l1);
      mv.visitLineNumber(34, l1);
      mv.visitVarInsn(DLOAD, 2);
      mv.visitInsn(DCONST_0);
      mv.visitInsn(DCMPL);
      Label l2 = new Label();
      mv.visitJumpInsn(IFEQ, l2);
      mv.visitVarInsn(DLOAD, 6);
      mv.visitVarInsn(DLOAD, 0);
      mv.visitInsn(DCMPL);
      Label l3 = new Label();
      mv.visitJumpInsn(IFEQ, l3);
      mv.visitLabel(l2);
      mv.visitVarInsn(DLOAD, 0);
      mv.visitInsn(DCONST_0);
      mv.visitInsn(DCMPL);
      Label l4 = new Label();
      mv.visitJumpInsn(IFEQ, l4);
      mv.visitVarInsn(DLOAD, 6);
      mv.visitVarInsn(DLOAD, 2);
      mv.visitInsn(DNEG);
      mv.visitInsn(DCMPL);
      mv.visitJumpInsn(IFNE, l4);
      mv.visitLabel(l3);
      mv.visitLineNumber(35, l3);
      mv.visitVarInsn(DLOAD, 6);
      mv.visitLdcInsn(new Double("2.0"));
      mv.visitVarInsn(DLOAD, 6);
      mv.visitInsn(DMUL);
      mv.visitInsn(DCMPL);
      Label l5 = new Label();
      mv.visitJumpInsn(IFEQ, l5);
      Label l6 = new Label();
      mv.visitLabel(l6);
      mv.visitLineNumber(36, l6);
      reaction.insertReactionCall(mv, PRECISION_MSG+"DSUB", methods, classPath);
      mv.visitJumpInsn(GOTO, l5);
      mv.visitLabel(l4);
      mv.visitLineNumber(37, l4);
      mv.visitVarInsn(DLOAD, 0);
      mv.visitLdcInsn(new Double("Infinity"));
      mv.visitInsn(DCMPL);
      mv.visitJumpInsn(IFEQ, l5);
      mv.visitVarInsn(DLOAD, 0);
      mv.visitLdcInsn(new Double("-Infinity"));
      mv.visitInsn(DCMPL);
      mv.visitJumpInsn(IFEQ, l5);
      mv.visitVarInsn(DLOAD, 2);
      mv.visitLdcInsn(new Double("Infinity"));
      mv.visitInsn(DCMPL);
      mv.visitJumpInsn(IFEQ, l5);
      mv.visitVarInsn(DLOAD, 2);
      mv.visitLdcInsn(new Double("-Infinity"));
      mv.visitInsn(DCMPL);
      mv.visitJumpInsn(IFEQ, l5);
      
      mv.visitVarInsn(DLOAD, 6);
      mv.visitInsn(DCONST_0);
      mv.visitInsn(DCMPL);
      mv.visitJumpInsn(IFEQ, l5);

      Label l7 = new Label();
      mv.visitLabel(l7);
      mv.visitLineNumber(38, l7);
      mv.visitVarInsn(DLOAD, 6);
      mv.visitLdcInsn(new Double("Infinity"));
      mv.visitInsn(DCMPL);
      Label l8 = new Label();
      mv.visitJumpInsn(IFNE, l8);
      Label l9 = new Label();
      mv.visitLabel(l9);
      mv.visitLineNumber(39, l9);
      reaction.insertReactionCall(mv, RESULT_IS_POS_INF_MSG+"DSUB", methods, classPath);
      mv.visitJumpInsn(GOTO, l5);
      mv.visitLabel(l8);
      mv.visitLineNumber(40, l8);
      mv.visitVarInsn(DLOAD, 6);
      mv.visitLdcInsn(new Double("-Infinity"));
      mv.visitInsn(DCMPL);
      Label l10 = new Label();
      mv.visitJumpInsn(IFNE, l10);
      Label l11 = new Label();
      mv.visitLabel(l11);
      mv.visitLineNumber(41, l11);
      reaction.insertReactionCall(mv, RESULT_IS_NEG_INF_MSG+"DSUB", methods, classPath);
      mv.visitJumpInsn(GOTO, l5);
      mv.visitLabel(l10);
      mv.visitLineNumber(42, l10);
      mv.visitVarInsn(DLOAD, 6);
      mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "abs", "(D)D");
      mv.visitLdcInsn(new Double(CANCELLATION_ULP_FACTOR_DOUBLE));
      mv.visitVarInsn(DLOAD, 0);
      mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "ulp", "(D)D");
      mv.visitInsn(DMUL);
      mv.visitInsn(DCMPG);
      mv.visitJumpInsn(IFGT, l5);
      Label l12 = new Label();
      mv.visitLabel(l12);
      mv.visitLineNumber(43, l12);
      reaction.insertReactionCall(mv, CANCELLATION_MSG+"DSUB", methods, classPath);
      mv.visitLabel(l5);
      mv.visitLineNumber(47, l5);
      mv.visitVarInsn(DLOAD, 6);
      mv.visitInsn(DRETURN);
      Label l13 = new Label();
      mv.visitLabel(l13);
      mv.visitLocalVariable("a", "D", null, l0, l13, 0);
      mv.visitLocalVariable("b", "D", null, l0, l13, 2);
      mv.visitLocalVariable("reaction", "I", null, l0, l13, 4);
      mv.visitLocalVariable("logFileName", "Ljava/lang/String;", null, l0, l13, 5);
      mv.visitLocalVariable("r", "D", null, l1, l13, 6);
      mv.visitMaxs(6, 8);
      mv.visitEnd();
    }

    private static void addDmulCheckMethod(ClassVisitor cv, Methods methods, Reaction reaction, String classPath) {
        MethodVisitor mv = cv.visitMethod(ACC_PUBLIC + ACC_STATIC, methods.getMethod(DMUL), Signatures.CHECK_DOUBLE_BINARY, null, null);
        mv.visitCode();
        mv.visitVarInsn(DLOAD, 0);
        mv.visitVarInsn(DLOAD, 2);
        mv.visitInsn(DMUL);
        mv.visitVarInsn(DSTORE, 6);
        mv.visitVarInsn(DLOAD, 0);
        mv.visitLdcInsn(new Double("Infinity"));
        mv.visitInsn(DCMPL);
        Label l0 = new Label();
        mv.visitJumpInsn(IFEQ, l0);
        mv.visitVarInsn(DLOAD, 0);
        mv.visitLdcInsn(new Double("-Infinity"));
        mv.visitInsn(DCMPL);
        mv.visitJumpInsn(IFEQ, l0);
        mv.visitVarInsn(DLOAD, 2);
        mv.visitLdcInsn(new Double("Infinity"));
        mv.visitInsn(DCMPL);
        mv.visitJumpInsn(IFEQ, l0);
        mv.visitVarInsn(DLOAD, 2);
        mv.visitLdcInsn(new Double("-Infinity"));
        mv.visitInsn(DCMPL);
        mv.visitJumpInsn(IFEQ, l0);
        mv.visitVarInsn(DLOAD, 6);
        mv.visitLdcInsn(new Double("Infinity"));
        mv.visitInsn(DCMPL);
        Label l1 = new Label();
        mv.visitJumpInsn(IFNE, l1);
        reaction.insertReactionCall(mv, RESULT_IS_POS_INF_MSG+"DMUL", methods, classPath);
        mv.visitJumpInsn(GOTO, l0);
        mv.visitLabel(l1);
        mv.visitFrame(F_APPEND, 1, new Object[]{DOUBLE}, 0, null);
        mv.visitVarInsn(DLOAD, 6);
        mv.visitLdcInsn(new Double("-Infinity"));
        mv.visitInsn(DCMPL);
        mv.visitJumpInsn(IFNE, l0);
        reaction.insertReactionCall(mv, RESULT_IS_NEG_INF_MSG+"DMUL", methods, classPath);
        mv.visitLabel(l0);
        mv.visitFrame(F_SAME, 0, null, 0, null);
        mv.visitVarInsn(DLOAD, 6);
        mv.visitInsn(DRETURN);
        mv.visitMaxs(4, 8);
        mv.visitEnd();
    }

    private static void addDdivCheckMethod(ClassVisitor cv, Methods methods, Reaction reaction, String classPath) {
        MethodVisitor mv = cv.visitMethod(ACC_PUBLIC + ACC_STATIC, methods.getMethod(DDIV), Signatures.CHECK_DOUBLE_BINARY, null, null);
        mv.visitCode();
        mv.visitVarInsn(DLOAD, 0);
        mv.visitVarInsn(DLOAD, 2);
        mv.visitInsn(DDIV);
        mv.visitVarInsn(DSTORE, 6);
        mv.visitVarInsn(DLOAD, 0);
        mv.visitVarInsn(DLOAD, 0);
        mv.visitInsn(DCMPL);
        Label l0 = new Label();
        mv.visitJumpInsn(IFNE, l0);
        mv.visitVarInsn(DLOAD, 2);
        mv.visitVarInsn(DLOAD, 2);
        mv.visitInsn(DCMPL);
        mv.visitJumpInsn(IFNE, l0);
        mv.visitVarInsn(DLOAD, 6);
        mv.visitVarInsn(DLOAD, 6);
        mv.visitInsn(DCMPL);
        mv.visitJumpInsn(IFEQ, l0);
        reaction.insertReactionCall(mv, RESULT_IS_NAN_MSG+"DDIV", methods, classPath);
        Label l1 = new Label();
        mv.visitJumpInsn(GOTO, l1);
        mv.visitLabel(l0);
        mv.visitFrame(F_APPEND, 1, new Object[]{DOUBLE}, 0, null);
        mv.visitVarInsn(DLOAD, 0);
        mv.visitLdcInsn(new Double("Infinity"));
        mv.visitInsn(DCMPL);
        mv.visitJumpInsn(IFEQ, l1);
        mv.visitVarInsn(DLOAD, 0);
        mv.visitLdcInsn(new Double("-Infinity"));
        mv.visitInsn(DCMPL);
        mv.visitJumpInsn(IFEQ, l1);
        mv.visitVarInsn(DLOAD, 2);
        mv.visitLdcInsn(new Double("Infinity"));
        mv.visitInsn(DCMPL);
        mv.visitJumpInsn(IFEQ, l1);
        mv.visitVarInsn(DLOAD, 2);
        mv.visitLdcInsn(new Double("-Infinity"));
        mv.visitInsn(DCMPL);
        mv.visitJumpInsn(IFEQ, l1);
        mv.visitVarInsn(DLOAD, 6);
        mv.visitLdcInsn(new Double("Infinity"));
        mv.visitInsn(DCMPL);
        Label l2 = new Label();
        mv.visitJumpInsn(IFNE, l2);
        reaction.insertReactionCall(mv, RESULT_IS_POS_INF_MSG+"DDIV", methods, classPath);
        mv.visitJumpInsn(GOTO, l1);
        mv.visitLabel(l2);
        mv.visitFrame(F_SAME, 0, null, 0, null);
        mv.visitVarInsn(DLOAD, 6);
        mv.visitLdcInsn(new Double("-Infinity"));
        mv.visitInsn(DCMPL);
        mv.visitJumpInsn(IFNE, l1);
        reaction.insertReactionCall(mv, RESULT_IS_NEG_INF_MSG+"DDIV", methods, classPath);
        mv.visitLabel(l1);
        mv.visitFrame(F_SAME, 0, null, 0, null);
        mv.visitVarInsn(DLOAD, 6);
        mv.visitInsn(DRETURN);
        mv.visitMaxs(4, 8);
        mv.visitEnd();
    }

    private static void addDremCheckMethod(ClassVisitor cv, Methods methods, Reaction reaction, String classPath) {
      MethodVisitor mv = cv.visitMethod(ACC_PUBLIC + ACC_STATIC, methods.getMethod(DREM), Signatures.CHECK_DOUBLE_BINARY, null, null);
      mv.visitCode();
      Label l0 = new Label();
      mv.visitLabel(l0);
      mv.visitLineNumber(109, l0);
      mv.visitVarInsn(DLOAD, 0);
      mv.visitVarInsn(DLOAD, 2);
      mv.visitInsn(DREM);
      mv.visitVarInsn(DSTORE, 6);
      Label l1 = new Label();
      mv.visitLabel(l1);
      mv.visitLineNumber(111, l1);
      mv.visitVarInsn(DLOAD, 0);
      mv.visitVarInsn(DLOAD, 0);
      mv.visitInsn(DCMPL);
      Label l2 = new Label();
      mv.visitJumpInsn(IFNE, l2);
      mv.visitVarInsn(DLOAD, 2);
      mv.visitVarInsn(DLOAD, 2);
      mv.visitInsn(DCMPL);
      mv.visitJumpInsn(IFNE, l2);
      mv.visitVarInsn(DLOAD, 6);
      mv.visitVarInsn(DLOAD, 6);
      mv.visitInsn(DCMPL);
      mv.visitJumpInsn(IFEQ, l2);
      Label l3 = new Label();
      mv.visitLabel(l3);
      mv.visitLineNumber(112, l3);
      reaction.insertReactionCall(mv, RESULT_IS_NAN_MSG+"DREM", methods, classPath);
      mv.visitLabel(l2);
      mv.visitLineNumber(114, l2);
      mv.visitVarInsn(DLOAD, 0);
      mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "ulp", "(D)D");
      mv.visitVarInsn(DLOAD, 2);
      mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "abs", "(D)D");
      mv.visitInsn(DCMPL);
      Label l4 = new Label();
      mv.visitJumpInsn(IFLE, l4);
      Label l5 = new Label();
      mv.visitLabel(l5);
      mv.visitLineNumber(115, l5);
      reaction.insertReactionCall(mv, PRECISION_MSG+"DREM", methods, classPath);      
      mv.visitLabel(l4);
      mv.visitLineNumber(117, l4);
      mv.visitVarInsn(DLOAD, 6);
      mv.visitInsn(DRETURN);
      Label l6 = new Label();
      mv.visitLabel(l6);
      mv.visitLocalVariable("a", "D", null, l0, l6, 0);
      mv.visitLocalVariable("b", "D", null, l0, l6, 2);
      mv.visitLocalVariable("reaction", "I", null, l0, l6, 4);
      mv.visitLocalVariable("logFileName", "Ljava/lang/String;", null, l0, l6, 5);
      mv.visitLocalVariable("r", "D", null, l1, l6, 6);
      mv.visitMaxs(4, 8);
      mv.visitEnd();
  }

    private static void addDCMPGCheckMethod(ClassVisitor cv, Methods methods, Reaction reaction, String classPath) {
      MethodVisitor mv = cv.visitMethod(ACC_PUBLIC + ACC_STATIC, methods.getMethod(DCMPG), Signatures.CHECK_DOUBLE_CMP, null, null);
      {
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitLineNumber(9, l0);
        mv.visitVarInsn(DLOAD, 0);
        mv.visitVarInsn(DLOAD, 2);
        mv.visitInsn(DCMPL);
        Label l1 = new Label();
        mv.visitJumpInsn(IFNE, l1);
        mv.visitInsn(ICONST_0);
        Label l2 = new Label();
        mv.visitJumpInsn(GOTO, l2);
        mv.visitLabel(l1);
        mv.visitVarInsn(DLOAD, 0);
        mv.visitVarInsn(DLOAD, 2);
        mv.visitInsn(DCMPG);
        Label l3 = new Label();
        mv.visitJumpInsn(IFGE, l3);
        mv.visitInsn(ICONST_M1);
        mv.visitJumpInsn(GOTO, l2);
        mv.visitLabel(l3);
        mv.visitInsn(ICONST_1);
        mv.visitLabel(l2);
        mv.visitVarInsn(ISTORE, 6);
        Label l4 = new Label();
        mv.visitLabel(l4);
        mv.visitLineNumber(10, l4);
        mv.visitVarInsn(DLOAD, 0);
        mv.visitLdcInsn(new Double("2.0"));
        mv.visitVarInsn(DLOAD, 0);
        mv.visitInsn(DMUL);
        mv.visitInsn(DCMPL);
        Label l5 = new Label();
        mv.visitJumpInsn(IFEQ, l5);
        mv.visitVarInsn(DLOAD, 2);
        mv.visitLdcInsn(new Double("2.0"));
        mv.visitVarInsn(DLOAD, 2);
        mv.visitInsn(DMUL);
        mv.visitInsn(DCMPL);
        Label l6 = new Label();
        mv.visitJumpInsn(IFNE, l6);
        mv.visitLabel(l5);
        mv.visitLineNumber(11, l5);
        mv.visitVarInsn(ILOAD, 6);
        mv.visitInsn(IRETURN);
        mv.visitLabel(l6);
        mv.visitLineNumber(12, l6);
        mv.visitVarInsn(ILOAD, 6);
        Label l7 = new Label();
        mv.visitJumpInsn(IFEQ, l7);
        mv.visitVarInsn(DLOAD, 0);
        mv.visitVarInsn(DLOAD, 2);
        mv.visitInsn(DSUB);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "abs", "(D)D");
        mv.visitLdcInsn(new Double(CLOSENESS_ULP_FACTOR_DOUBLE));
        mv.visitVarInsn(DLOAD, 0);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "ulp", "(D)D");
        mv.visitInsn(DMUL);
        mv.visitInsn(DCMPG);
        mv.visitJumpInsn(IFGT, l7);
        Label l8 = new Label();
        mv.visitLabel(l8);
        mv.visitLineNumber(13, l8);
        reaction.insertReactionCall(mv, VERY_CLOSE_MSG+"DCMP", methods, classPath);
        mv.visitLabel(l7);
        mv.visitLineNumber(15, l7);
        mv.visitVarInsn(ILOAD, 6);
        mv.visitInsn(IRETURN);
        Label l9 = new Label();
        mv.visitLabel(l9);
        mv.visitLocalVariable("a", "D", null, l0, l9, 0);
        mv.visitLocalVariable("b", "D", null, l0, l9, 2);
        mv.visitLocalVariable("reaction", "I", null, l0, l9, 4);
        mv.visitLocalVariable("logFileName", "Ljava/lang/String;", null, l0, l9, 5);
        mv.visitLocalVariable("r", "I", null, l4, l9, 6);
        mv.visitMaxs(6, 7);
        mv.visitEnd();

      }

      
      
      /*
        mv.visitCode();
        mv.visitVarInsn(DLOAD, 0);
        mv.visitVarInsn(DLOAD, 2);
        mv.visitInsn(DCMPL);
        Label l0 = new Label();
        mv.visitJumpInsn(IFNE, l0);
        mv.visitInsn(ICONST_0);
        Label l1 = new Label();
        mv.visitJumpInsn(GOTO, l1);
        mv.visitLabel(l0);
        mv.visitFrame(F_SAME, 0, null, 0, null);
        mv.visitVarInsn(DLOAD, 0);
        mv.visitVarInsn(DLOAD, 2);
        mv.visitInsn(DCMPG);
        Label l2 = new Label();
        mv.visitJumpInsn(IFGE, l2);
        mv.visitInsn(ICONST_M1);
        mv.visitJumpInsn(GOTO, l1);
        mv.visitLabel(l2);
        mv.visitFrame(F_SAME, 0, null, 0, null);
        mv.visitInsn(ICONST_1);
        mv.visitLabel(l1);
        mv.visitFrame(F_SAME1, 0, null, 1, new Object[]{INTEGER});
        mv.visitVarInsn(ISTORE, 6);
        mv.visitVarInsn(ILOAD, 6);
        Label l3 = new Label();
        mv.visitJumpInsn(IFEQ, l3);
        mv.visitVarInsn(DLOAD, 0);
        mv.visitVarInsn(DLOAD, 2);
        mv.visitInsn(DSUB);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "abs", "(D)D");
        mv.visitLdcInsn(new Double(CLOSENESS_ULP_FACTOR_DOUBLE));
        mv.visitVarInsn(DLOAD, 0);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "ulp", "(D)D");
        mv.visitInsn(DMUL);
        mv.visitInsn(DCMPG);
        mv.visitJumpInsn(IFGT, l3);
        reaction.insertReactionCall(mv, VERY_CLOSE_MSG+"DCMP", methods, classPath);
        mv.visitLabel(l3);
        mv.visitFrame(F_APPEND, 1, new Object[]{INTEGER}, 0, null);
        mv.visitVarInsn(ILOAD, 6);
        mv.visitInsn(IRETURN);
        mv.visitMaxs(6, 7);
        mv.visitEnd();
        */
    }
    private static void addDCMPLCheckMethod(ClassVisitor cv, Methods methods, Reaction reaction, String classPath) {
      MethodVisitor mv = cv.visitMethod(ACC_PUBLIC + ACC_STATIC, methods.getMethod(DCMPL), Signatures.CHECK_DOUBLE_CMP, null, null);
      {
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitLineNumber(9, l0);
        mv.visitVarInsn(DLOAD, 0);
        mv.visitVarInsn(DLOAD, 2);
        mv.visitInsn(DCMPL);
        Label l1 = new Label();
        mv.visitJumpInsn(IFNE, l1);
        mv.visitInsn(ICONST_0);
        Label l2 = new Label();
        mv.visitJumpInsn(GOTO, l2);
        mv.visitLabel(l1);
        mv.visitVarInsn(DLOAD, 0);
        mv.visitVarInsn(DLOAD, 2);
        mv.visitInsn(DCMPG);
        Label l3 = new Label();
        mv.visitJumpInsn(IFGE, l3);
        mv.visitInsn(ICONST_M1);
        mv.visitJumpInsn(GOTO, l2);
        mv.visitLabel(l3);
        mv.visitInsn(ICONST_1);
        mv.visitLabel(l2);
        mv.visitVarInsn(ISTORE, 6);
        Label l4 = new Label();
        mv.visitLabel(l4);
        mv.visitLineNumber(10, l4);
        mv.visitVarInsn(DLOAD, 0);
        mv.visitLdcInsn(new Double("2.0"));
        mv.visitVarInsn(DLOAD, 0);
        mv.visitInsn(DMUL);
        mv.visitInsn(DCMPL);
        Label l5 = new Label();
        mv.visitJumpInsn(IFEQ, l5);
        mv.visitVarInsn(DLOAD, 2);
        mv.visitLdcInsn(new Double("2.0"));
        mv.visitVarInsn(DLOAD, 2);
        mv.visitInsn(DMUL);
        mv.visitInsn(DCMPL);
        Label l6 = new Label();
        mv.visitJumpInsn(IFNE, l6);
        mv.visitLabel(l5);
        mv.visitLineNumber(11, l5);
        mv.visitVarInsn(ILOAD, 6);
        mv.visitInsn(IRETURN);
        mv.visitLabel(l6);
        mv.visitLineNumber(12, l6);
        mv.visitVarInsn(ILOAD, 6);
        Label l7 = new Label();
        mv.visitJumpInsn(IFEQ, l7);
        mv.visitVarInsn(DLOAD, 0);
        mv.visitVarInsn(DLOAD, 2);
        mv.visitInsn(DSUB);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "abs", "(D)D");
        mv.visitLdcInsn(new Double(CLOSENESS_ULP_FACTOR_DOUBLE));
        mv.visitVarInsn(DLOAD, 0);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "ulp", "(D)D");
        mv.visitInsn(DMUL);
        mv.visitInsn(DCMPG);
        mv.visitJumpInsn(IFGT, l7);
        Label l8 = new Label();
        mv.visitLabel(l8);
        mv.visitLineNumber(13, l8);
        reaction.insertReactionCall(mv, VERY_CLOSE_MSG+"DCMP", methods, classPath);
        mv.visitLabel(l7);
        mv.visitLineNumber(15, l7);
        mv.visitVarInsn(ILOAD, 6);
        mv.visitInsn(IRETURN);
        Label l9 = new Label();
        mv.visitLabel(l9);
        mv.visitLocalVariable("a", "D", null, l0, l9, 0);
        mv.visitLocalVariable("b", "D", null, l0, l9, 2);
        mv.visitLocalVariable("reaction", "I", null, l0, l9, 4);
        mv.visitLocalVariable("logFileName", "Ljava/lang/String;", null, l0, l9, 5);
        mv.visitLocalVariable("r", "I", null, l4, l9, 6);
        mv.visitMaxs(6, 7);
        mv.visitEnd();
      }
    }

}