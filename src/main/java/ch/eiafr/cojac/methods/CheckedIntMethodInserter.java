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

public final class CheckedIntMethodInserter implements MethodInserter {
    @Override
    public void insertMethods(ClassVisitor cv, Args args, Methods methods, Reaction reaction, String classPath) {
        if (args.isOperationEnabled(Arg.IADD)) {
            addIaddCheckMethod(cv, methods, reaction, classPath);
        }

        if (args.isOperationEnabled(Arg.IINC)) {
            addIincCheckMethod(cv, methods, reaction, classPath);
        }

        if (args.isOperationEnabled(Arg.ISUB)) {
            addIsubCheckMethod(cv, methods, reaction, classPath);
        }

        if (args.isOperationEnabled(Arg.IMUL)) {
            addImulCheckMethod(cv, methods, reaction, classPath);
        }

        if (args.isOperationEnabled(Arg.IDIV)) {
            addIdivCheckMethod(cv, methods, reaction, classPath);
        }

        if (args.isOperationEnabled(Arg.INEG)) {
            addInegCheckMethod(cv, methods, reaction, classPath);
        }
    }

    private static void addIaddCheckMethod(ClassVisitor cv, Methods methods, Reaction reaction, String classPath) {
        MethodVisitor mv = cv.visitMethod(ACC_PUBLIC + ACC_STATIC, methods.getMethod(IADD), Signatures.CHECK_INTEGER_BINARY, null, null);

        mv.visitCode();
        mv.visitVarInsn(ILOAD, 0);
        mv.visitVarInsn(ILOAD, 1);
        mv.visitInsn(IADD);
        mv.visitVarInsn(ISTORE, 4);
        mv.visitVarInsn(ILOAD, 0);
        mv.visitVarInsn(ILOAD, 4);
        mv.visitInsn(IXOR);
        mv.visitVarInsn(ILOAD, 1);
        mv.visitVarInsn(ILOAD, 4);
        mv.visitInsn(IXOR);
        mv.visitInsn(IAND);
        Label l0 = new Label();
        mv.visitJumpInsn(IFGE, l0);
        reaction.insertReactionCall(mv, "Overflow : IADD", methods, classPath);
        mv.visitLabel(l0);
        mv.visitFrame(F_APPEND, 1, new Object[]{INTEGER}, 0, null);
        mv.visitVarInsn(ILOAD, 4);
        mv.visitInsn(IRETURN);
        mv.visitMaxs(3, 5);
        mv.visitEnd();
    }

    private static void addIsubCheckMethod(ClassVisitor cv, Methods methods, Reaction reaction, String classPath) {
        MethodVisitor mv = cv.visitMethod(ACC_PRIVATE + ACC_STATIC, methods.getMethod(ISUB), Signatures.CHECK_INTEGER_BINARY, null, null);
        mv.visitCode();
        mv.visitVarInsn(ILOAD, 0);
        mv.visitVarInsn(ILOAD, 1);
        mv.visitInsn(ISUB);
        mv.visitVarInsn(ISTORE, 4);
        mv.visitVarInsn(ILOAD, 0);
        mv.visitVarInsn(ILOAD, 1);
        mv.visitInsn(IXOR);
        mv.visitVarInsn(ILOAD, 0);
        mv.visitVarInsn(ILOAD, 4);
        mv.visitInsn(IXOR);
        mv.visitInsn(IAND);
        Label l0 = new Label();
        mv.visitJumpInsn(IFGE, l0);
        reaction.insertReactionCall(mv, "Overflow : ISUB", methods, classPath);
        mv.visitLabel(l0);
        mv.visitFrame(F_APPEND, 1, new Object[]{INTEGER}, 0, null);
        mv.visitVarInsn(ILOAD, 4);
        mv.visitInsn(IRETURN);
        mv.visitMaxs(3, 5);
        mv.visitEnd();
    }

    private static void addImulCheckMethod(ClassVisitor cv, Methods methods, Reaction reaction, String classPath) {
        MethodVisitor mv = cv.visitMethod(ACC_PRIVATE + ACC_STATIC, methods.getMethod(IMUL), Signatures.CHECK_INTEGER_BINARY, null, null);
        mv.visitCode();
        mv.visitVarInsn(ILOAD, 0);
        mv.visitVarInsn(ILOAD, 1);
        mv.visitInsn(IMUL);
        mv.visitVarInsn(ISTORE, 4);
        mv.visitVarInsn(ILOAD, 1);
        Label l0 = new Label();
        mv.visitJumpInsn(IFNE, l0);
        mv.visitVarInsn(ILOAD, 4);
        mv.visitInsn(IRETURN);
        mv.visitLabel(l0);
        mv.visitFrame(F_APPEND, 1, new Object[]{INTEGER}, 0, null);
        mv.visitVarInsn(ILOAD, 1);
        mv.visitInsn(ICONST_M1);
        Label l1 = new Label();
        mv.visitJumpInsn(IF_ICMPNE, l1);
        mv.visitVarInsn(ILOAD, 0);
        mv.visitLdcInsn(-2147483648);
        Label l2 = new Label();
        mv.visitJumpInsn(IF_ICMPEQ, l2);
        mv.visitLabel(l1);
        mv.visitFrame(F_SAME, 0, null, 0, null);
        mv.visitVarInsn(ILOAD, 4);
        mv.visitVarInsn(ILOAD, 1);
        mv.visitInsn(IDIV);
        mv.visitVarInsn(ILOAD, 0);
        Label l3 = new Label();
        mv.visitJumpInsn(IF_ICMPEQ, l3);
        mv.visitLabel(l2);
        mv.visitFrame(F_SAME, 0, null, 0, null);
        reaction.insertReactionCall(mv, "Overflow : IDIV", methods, classPath);
        mv.visitLabel(l3);
        mv.visitFrame(F_SAME, 0, null, 0, null);
        mv.visitVarInsn(ILOAD, 4);
        mv.visitInsn(IRETURN);
        mv.visitMaxs(3, 5);
        mv.visitEnd();
    }

    private static void addIdivCheckMethod(ClassVisitor cv, Methods methods, Reaction reaction, String classPath) {
        MethodVisitor mv = cv.visitMethod(ACC_PRIVATE + ACC_STATIC, methods.getMethod(IDIV), Signatures.CHECK_INTEGER_BINARY, null, null);
        mv.visitCode();
        mv.visitVarInsn(ILOAD, 0);
        mv.visitVarInsn(ILOAD, 1);
        mv.visitInsn(IDIV);
        mv.visitVarInsn(ISTORE, 4);
        mv.visitVarInsn(ILOAD, 0);
        mv.visitLdcInsn(-2147483648);
        Label l0 = new Label();
        mv.visitJumpInsn(IF_ICMPNE, l0);
        mv.visitVarInsn(ILOAD, 1);
        mv.visitInsn(ICONST_M1);
        mv.visitJumpInsn(IF_ICMPNE, l0);
        reaction.insertReactionCall(mv, "Overflow : IDIV", methods, classPath);
        mv.visitLabel(l0);
        mv.visitFrame(F_APPEND, 1, new Object[]{INTEGER}, 0, null);
        mv.visitVarInsn(ILOAD, 4);
        mv.visitInsn(IRETURN);
        mv.visitMaxs(3, 5);
        mv.visitEnd();
    }

    private static void addIincCheckMethod(ClassVisitor cv, Methods methods, Reaction reaction, String classPath) {
        MethodVisitor mv = cv.visitMethod(ACC_PRIVATE + ACC_STATIC, methods.getMethod(IINC), Signatures.CHECK_INTEGER_BINARY, null, null);
        mv.visitCode();
        mv.visitVarInsn(ILOAD, 0);
        mv.visitVarInsn(ILOAD, 1);
        mv.visitInsn(IADD);
        mv.visitVarInsn(ISTORE, 4);
        mv.visitVarInsn(ILOAD, 0);
        mv.visitVarInsn(ILOAD, 4);
        mv.visitInsn(IXOR);
        mv.visitVarInsn(ILOAD, 1);
        mv.visitVarInsn(ILOAD, 4);
        mv.visitInsn(IXOR);
        mv.visitInsn(IAND);
        Label l0 = new Label();
        mv.visitJumpInsn(IFGE, l0);
        reaction.insertReactionCall(mv, "Overflow : IINC", methods, classPath);
        mv.visitLabel(l0);
        mv.visitFrame(F_APPEND, 1, new Object[]{INTEGER}, 0, null);
        mv.visitVarInsn(ILOAD, 4);
        mv.visitInsn(IRETURN);
        mv.visitMaxs(3, 5);
        mv.visitEnd();

    }

    private static void addInegCheckMethod(ClassVisitor cv, Methods methods, Reaction reaction, String classPath) {
        MethodVisitor mv = cv.visitMethod(ACC_PRIVATE + ACC_STATIC, methods.getMethod(INEG), Signatures.CHECK_INTEGER_UNARY, null, null);
        mv.visitCode();
        mv.visitVarInsn(ILOAD, 0);
        mv.visitLdcInsn(-2147483648);
        Label l0 = new Label();
        mv.visitJumpInsn(IF_ICMPNE, l0);
        reaction.insertReactionCall(mv, "Overflow : INEG", methods, classPath);
        mv.visitLabel(l0);
        mv.visitFrame(F_SAME, 0, null, 0, null);
        mv.visitVarInsn(ILOAD, 0);
        mv.visitInsn(INEG);
        mv.visitInsn(IRETURN);
        mv.visitMaxs(3, 3);
        mv.visitEnd();
    }
}