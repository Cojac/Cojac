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

import ch.eiafr.cojac.Arg;
import ch.eiafr.cojac.Args;
import ch.eiafr.cojac.Methods;
import ch.eiafr.cojac.Signatures;
import ch.eiafr.cojac.reactions.Reaction;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public final class CheckedCastMethodInserter implements MethodInserter {
    @Override
    public void insertMethods(ClassVisitor cv, Args args, Methods methods, Reaction reaction, String classPath) {
        if (args.isOperationEnabled(Arg.L2I)) {
            addL2iCheckMethod(cv, methods, reaction, classPath);
        }

        if (args.isOperationEnabled(Arg.I2S)) {
            addI2sCheckMethod(cv, methods, reaction, classPath);
        }

        if (args.isOperationEnabled(Arg.I2C)) {
            addI2cCheckMethod(cv, methods, reaction, classPath);
        }

        if (args.isOperationEnabled(Arg.I2B)) {
            addI2bCheckMethod(cv, methods, reaction, classPath);
        }

        if (args.isOperationEnabled(Arg.D2I)) {
            addD2iCheckMethod(cv, methods, reaction, classPath);
        }

        if (args.isOperationEnabled(Arg.D2L)) {
            addD2lCheckMethod(cv, methods, reaction, classPath);
        }

        if (args.isOperationEnabled(Arg.F2I)) {
            addF2iCheckMethod(cv, methods, reaction, classPath);
        }

        if (args.isOperationEnabled(Arg.F2L)) {
            addF2lCheckMethod(cv, methods, reaction, classPath);
        }

        if (args.isOperationEnabled(Arg.D2F)) {
            addD2fCheckMethod(cv, methods, reaction, classPath);
        }
    }

    private static void addL2iCheckMethod(ClassVisitor cv, Methods methods, Reaction reaction, String classPath) {
        MethodVisitor mv = cv.visitMethod(ACC_PRIVATE + ACC_STATIC, methods.getMethod(L2I), Signatures.CHECK_L2I, null, null);
        mv.visitCode();

        mv.visitVarInsn(LLOAD, 0);
        mv.visitLdcInsn(-2147483648L);
        mv.visitInsn(LCMP);
        Label l0 = new Label();
        mv.visitJumpInsn(IFLT, l0);
        mv.visitVarInsn(LLOAD, 0);
        mv.visitLdcInsn(2147483647L);
        mv.visitInsn(LCMP);
        Label l1 = new Label();
        mv.visitJumpInsn(IFLE, l1);
        mv.visitLabel(l0);
        mv.visitFrame(F_SAME, 0, null, 0, null);
        reaction.insertReactionCall(mv, "Overflow : L2I", methods, classPath);
        mv.visitLabel(l1);
        mv.visitFrame(F_SAME, 0, null, 0, null);
        mv.visitVarInsn(LLOAD, 0);
        mv.visitInsn(L2I);
        mv.visitInsn(IRETURN);
        mv.visitMaxs(4, 4);

        mv.visitEnd();
    }

    private static void addI2sCheckMethod(ClassVisitor cv, Methods methods, Reaction reaction, String classPath) {
        MethodVisitor mv = cv.visitMethod(ACC_PRIVATE + ACC_STATIC, methods.getMethod(I2S), Signatures.CHECK_I2S, null, null);
        mv.visitCode();
        mv.visitVarInsn(ILOAD, 0);
        mv.visitIntInsn(SIPUSH, -32768);
        Label l0 = new Label();
        mv.visitJumpInsn(IF_ICMPLT, l0);
        mv.visitVarInsn(ILOAD, 0);
        mv.visitIntInsn(SIPUSH, 32767);
        Label l1 = new Label();
        mv.visitJumpInsn(IF_ICMPLE, l1);
        mv.visitLabel(l0);
        mv.visitFrame(F_SAME, 0, null, 0, null);
        reaction.insertReactionCall(mv, "Overflow : I2S", methods, classPath);
        mv.visitLabel(l1);
        mv.visitFrame(F_SAME, 0, null, 0, null);
        mv.visitVarInsn(ILOAD, 0);
        mv.visitInsn(I2S);
        mv.visitInsn(IRETURN);
        mv.visitMaxs(3, 3);

        mv.visitEnd();
    }

    private static void addI2cCheckMethod(ClassVisitor cv, Methods methods, Reaction reaction, String classPath) {
        MethodVisitor mv = cv.visitMethod(ACC_PRIVATE + ACC_STATIC, methods.getMethod(I2C), Signatures.CHECK_I2C, null, null);
        mv.visitCode();
        mv.visitVarInsn(ILOAD, 0);
        Label l0 = new Label();
        mv.visitJumpInsn(IFLT, l0);
        mv.visitVarInsn(ILOAD, 0);
        mv.visitLdcInsn(65535);
        Label l1 = new Label();
        mv.visitJumpInsn(IF_ICMPLE, l1);
        mv.visitLabel(l0);
        mv.visitFrame(F_SAME, 0, null, 0, null);
        reaction.insertReactionCall(mv, "Overflow : I2C", methods, classPath);
        mv.visitLabel(l1);
        mv.visitFrame(F_SAME, 0, null, 0, null);
        mv.visitVarInsn(ILOAD, 0);
        mv.visitInsn(I2C);
        mv.visitInsn(IRETURN);
        mv.visitMaxs(3, 3);
        mv.visitEnd();
    }

    private static void addI2bCheckMethod(ClassVisitor cv, Methods methods, Reaction reaction, String classPath) {
        MethodVisitor mv = cv.visitMethod(ACC_PRIVATE + ACC_STATIC, methods.getMethod(I2B), Signatures.CHECK_I2B, null, null);
        mv.visitCode();
        mv.visitVarInsn(ILOAD, 0);
        mv.visitIntInsn(BIPUSH, -128);
        Label l0 = new Label();
        mv.visitJumpInsn(IF_ICMPLT, l0);
        mv.visitVarInsn(ILOAD, 0);
        mv.visitIntInsn(BIPUSH, 127);
        Label l1 = new Label();
        mv.visitJumpInsn(IF_ICMPLE, l1);
        mv.visitLabel(l0);
        mv.visitFrame(F_SAME, 0, null, 0, null);
        reaction.insertReactionCall(mv, "Overflow : I2B", methods, classPath);
        mv.visitLabel(l1);
        mv.visitFrame(F_SAME, 0, null, 0, null);
        mv.visitVarInsn(ILOAD, 0);
        mv.visitInsn(IRETURN);
        mv.visitMaxs(3, 3);
        mv.visitEnd();
    }

    private static void addD2iCheckMethod(ClassVisitor cv, Methods methods, Reaction reaction, String classPath) {
        MethodVisitor mv = cv.visitMethod(ACC_PUBLIC + ACC_STATIC, methods.getMethod(D2I), Signatures.CHECK_D2I, null, null);
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitLineNumber(10, l0);
        mv.visitVarInsn(DLOAD, 0);
        mv.visitLdcInsn(new Double("-2.147483648E9"));
        mv.visitInsn(DCMPL);
        Label l1 = new Label();
        mv.visitJumpInsn(IFLT, l1);
        mv.visitVarInsn(DLOAD, 0);
        mv.visitLdcInsn(new Double("2.147483647E9"));
        mv.visitInsn(DCMPG);
        Label l2 = new Label();
        mv.visitJumpInsn(IFLE, l2);
        mv.visitLabel(l1);
        mv.visitLineNumber(11, l1);
        reaction.insertReactionCall(mv, "Overflow : D2I", methods, classPath);
        mv.visitLabel(l2);
        mv.visitLineNumber(14, l2);
        mv.visitVarInsn(DLOAD, 0);
        mv.visitInsn(D2I);
        mv.visitInsn(IRETURN);
        Label l3 = new Label();
        mv.visitLabel(l3);
        mv.visitLocalVariable("a", "D", null, l0, l3, 0);
        mv.visitLocalVariable("reaction", "I", null, l0, l3, 2);
        mv.visitLocalVariable("logFileName", "Ljava/lang/String;", null, l0, l3, 3);
        mv.visitMaxs(4, 4);
        mv.visitEnd();
    }

    private static void addD2lCheckMethod(ClassVisitor cv, Methods methods, Reaction reaction, String classPath) {
        MethodVisitor mv = cv.visitMethod(ACC_PUBLIC + ACC_STATIC, methods.getMethod(D2L), Signatures.CHECK_D2L, null, null);
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitLineNumber(18, l0);
        mv.visitVarInsn(DLOAD, 0);
        mv.visitLdcInsn(new Double("-9.223372036854776E18"));
        mv.visitInsn(DCMPL);
        Label l1 = new Label();
        mv.visitJumpInsn(IFLT, l1);
        mv.visitVarInsn(DLOAD, 0);
        mv.visitLdcInsn(new Double("9.223372036854776E18"));
        mv.visitInsn(DCMPG);
        Label l2 = new Label();
        mv.visitJumpInsn(IFLE, l2);
        mv.visitLabel(l1);
        mv.visitLineNumber(19, l1);
        reaction.insertReactionCall(mv, "Overflow : D2L", methods, classPath);
        mv.visitLabel(l2);
        mv.visitLineNumber(22, l2);
        mv.visitVarInsn(DLOAD, 0);
        mv.visitInsn(D2L);
        mv.visitInsn(LRETURN);
        Label l3 = new Label();
        mv.visitLabel(l3);
        mv.visitLocalVariable("a", "D", null, l0, l3, 0);
        mv.visitLocalVariable("reaction", "I", null, l0, l3, 2);
        mv.visitLocalVariable("logFileName", "Ljava/lang/String;", null, l0, l3, 3);
        mv.visitMaxs(4, 4);
        mv.visitEnd();
    }

    private static void addF2iCheckMethod(ClassVisitor cv, Methods methods, Reaction reaction, String classPath) {
        MethodVisitor mv = cv.visitMethod(ACC_PUBLIC + ACC_STATIC, methods.getMethod(F2I), Signatures.CHECK_F2I, null, null);
        mv.visitCode();
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitLineNumber(26, l0);
        mv.visitVarInsn(FLOAD, 0);
        mv.visitLdcInsn(new Float("-2.14748365E9"));
        mv.visitInsn(FCMPL);
        Label l1 = new Label();
        mv.visitJumpInsn(IFLT, l1);
        mv.visitVarInsn(FLOAD, 0);
        mv.visitLdcInsn(new Float("2.14748365E9"));
        mv.visitInsn(FCMPG);
        Label l2 = new Label();
        mv.visitJumpInsn(IFLE, l2);
        mv.visitLabel(l1);
        mv.visitLineNumber(27, l1);
        reaction.insertReactionCall(mv, "Overflow : F2I", methods, classPath);
        mv.visitLabel(l2);
        mv.visitLineNumber(30, l2);
        mv.visitVarInsn(FLOAD, 0);
        mv.visitInsn(F2I);
        mv.visitInsn(IRETURN);
        Label l3 = new Label();
        mv.visitLabel(l3);
        mv.visitLocalVariable("a", "F", null, l0, l3, 0);
        mv.visitLocalVariable("reaction", "I", null, l0, l3, 1);
        mv.visitLocalVariable("logFileName", "Ljava/lang/String;", null, l0, l3, 2);
        mv.visitMaxs(2, 3);
        mv.visitEnd();
    }

    private static void addF2lCheckMethod(ClassVisitor cv, Methods methods, Reaction reaction, String classPath) {
        MethodVisitor mv = cv.visitMethod(ACC_PUBLIC + ACC_STATIC, methods.getMethod(F2L), Signatures.CHECK_F2L, null, null);
        mv.visitCode();
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitLineNumber(34, l0);
        mv.visitVarInsn(FLOAD, 0);
        mv.visitLdcInsn(new Float("-9.223372E18"));
        mv.visitInsn(FCMPL);
        Label l1 = new Label();
        mv.visitJumpInsn(IFLT, l1);
        mv.visitVarInsn(FLOAD, 0);
        mv.visitLdcInsn(new Float("9.223372E18"));
        mv.visitInsn(FCMPG);
        Label l2 = new Label();
        mv.visitJumpInsn(IFLE, l2);
        mv.visitLabel(l1);
        mv.visitLineNumber(35, l1);
        reaction.insertReactionCall(mv, "Overflow : F2L", methods, classPath);
        mv.visitLabel(l2);
        mv.visitLineNumber(38, l2);
        mv.visitVarInsn(FLOAD, 0);
        mv.visitInsn(F2L);
        mv.visitInsn(LRETURN);
        Label l3 = new Label();
        mv.visitLabel(l3);
        mv.visitLocalVariable("a", "F", null, l0, l3, 0);
        mv.visitLocalVariable("reaction", "I", null, l0, l3, 1);
        mv.visitLocalVariable("logFileName", "Ljava/lang/String;", null, l0, l3, 2);
        mv.visitMaxs(2, 3);
        mv.visitEnd();
    }

    private static void addD2fCheckMethod(ClassVisitor cv, Methods methods, Reaction reaction, String classPath) {
        MethodVisitor mv = cv.visitMethod(ACC_PUBLIC + ACC_STATIC, methods.getMethod(D2F), Signatures.CHECK_D2F, null, null);

        mv.visitCode();
        mv.visitVarInsn(DLOAD, 0);
        mv.visitLdcInsn(new Double("-3.4028234663852886E38"));
        mv.visitInsn(DCMPG);
        Label l0 = new Label();
        mv.visitJumpInsn(IFLT, l0);
        mv.visitVarInsn(DLOAD, 0);
        mv.visitLdcInsn(new Double("3.4028234663852886E38"));
        mv.visitInsn(DCMPL);
        Label l1 = new Label();
        mv.visitJumpInsn(IFLE, l1);
        mv.visitLabel(l0);
        mv.visitFrame(F_SAME, 0, null, 0, null);
        reaction.insertReactionCall(mv, "Overflow : D2F", methods, classPath);
        mv.visitLabel(l1);
        mv.visitFrame(F_SAME, 0, null, 0, null);
        mv.visitVarInsn(DLOAD, 0);
        mv.visitInsn(D2F);
        mv.visitInsn(FRETURN);
        mv.visitMaxs(4, 4);
        mv.visitEnd();
    }
}