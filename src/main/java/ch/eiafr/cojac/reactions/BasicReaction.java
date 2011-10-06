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

package ch.eiafr.cojac.reactions;

import ch.eiafr.cojac.Arg;
import ch.eiafr.cojac.Args;
import ch.eiafr.cojac.Methods;
import ch.eiafr.cojac.Signatures;
import ch.eiafr.cojac.utils.BytecodeUtils;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public final class BasicReaction implements Reaction {

    private final Args args;

    public BasicReaction(Args args) {
        super();

        this.args = args;
    }

    @Override
    public void insertReactionMethod(ClassVisitor cv, Methods methods) {
        if (args.isSpecified(Arg.PRINT)) {
            if (args.isSpecified(Arg.DETAILED_LOG)) {
                addPrintMethod(cv, methods);
            } else {
                addPrintMethodSmaller(cv, methods);
            }
        } else if (args.isSpecified(Arg.LOG_FILE)) {
            if (args.isSpecified(Arg.DETAILED_LOG)) {
                addLogMethod(cv, methods, args);
            } else {
                addLogMethodSmaller(cv, methods, args);
            }
        } else if (args.isSpecified(Arg.EXCEPTION)) {
            addThrowMethod(cv, methods);
        }
    }

    @Override
    public void insertReactionCall(MethodVisitor mv, String msg, Methods methods, String classPath) {
        mv.visitLdcInsn(msg);

        if (args.isSpecified(Arg.CALL_BACK)) {
            BytecodeUtils.callUserCallBack(mv, args.getValue(Arg.CALL_BACK));
        } else {
            mv.visitMethodInsn(INVOKESTATIC,
                classPath,
                getCurrentMethodReaction(methods),
                Signatures.REACT);
        }
    }

    private String getCurrentMethodReaction(Methods methods) {
        if (args.isSpecified(Arg.PRINT)) {
            return methods.getPrint();
        } else if (args.isSpecified(Arg.LOG_FILE)) {
            return methods.getLog();
        } else if (args.isSpecified(Arg.EXCEPTION)) {
            return methods.getException();
        }

        throw new RuntimeException("System must be in one of this mode !");
    }

    private static void addPrintMethod(ClassVisitor cv, Methods methods) {
        MethodVisitor mv = cv.visitMethod(ACC_PRIVATE + ACC_STATIC, methods.getPrint(), Signatures.REACT, null, null);
        mv.visitCode();
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "err", "Ljava/io/PrintStream;");
        mv.visitLdcInsn("COJAC: ");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "print", "(Ljava/lang/String;)V");
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "err", "Ljava/io/PrintStream;");
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V");
        mv.visitTypeInsn(NEW, "java/lang/Throwable");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Throwable", "<init>", "()V");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Throwable", "getStackTrace", "()[Ljava/lang/StackTraceElement;");
        mv.visitVarInsn(ASTORE, 1);
        mv.visitInsn(ICONST_1);
        mv.visitVarInsn(ISTORE, 2);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitInsn(ARRAYLENGTH);
        mv.visitInsn(ICONST_1);
        Label l0 = new Label();
        mv.visitJumpInsn(IF_ICMPLE, l0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitInsn(ICONST_1);
        mv.visitInsn(AALOAD);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getMethodName", "()Ljava/lang/String;");
        mv.visitLdcInsn("cojacCheck");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "startsWith", "(Ljava/lang/String;)Z");
        mv.visitJumpInsn(IFEQ, l0);
        mv.visitInsn(ICONST_2);
        mv.visitVarInsn(ISTORE, 2);
        mv.visitLabel(l0);
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "err", "Ljava/io/PrintStream;");
        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ILOAD, 2);
        mv.visitIincInsn(2, 1);
        mv.visitInsn(AALOAD);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "toString", "()Ljava/lang/String;");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V");
        mv.visitVarInsn(ILOAD, 2);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitInsn(ARRAYLENGTH);
        mv.visitJumpInsn(IF_ICMPLT, l0);
        mv.visitInsn(RETURN);
        mv.visitMaxs(3, 3);
        mv.visitEnd();
    }

    private static void addPrintMethodSmaller(ClassVisitor cv, Methods methods) {
        MethodVisitor mv = cv.visitMethod(ACC_PRIVATE + ACC_STATIC, methods.getPrint(), Signatures.REACT, null, null);
        mv.visitCode();
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "err", "Ljava/io/PrintStream;");
        mv.visitLdcInsn("COJAC: ");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "print", "(Ljava/lang/String;)V");
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "err", "Ljava/io/PrintStream;");
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "print", "(Ljava/lang/String;)V");
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "err", "Ljava/io/PrintStream;");
        mv.visitIntInsn(BIPUSH, 32);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "print", "(C)V");
        mv.visitTypeInsn(NEW, "java/lang/Throwable");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Throwable", "<init>", "()V");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Throwable", "getStackTrace", "()[Ljava/lang/StackTraceElement;");
        mv.visitVarInsn(ASTORE, 1);
        mv.visitInsn(ICONST_1);
        mv.visitVarInsn(ISTORE, 2);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitInsn(ARRAYLENGTH);
        mv.visitInsn(ICONST_1);
        Label l0 = new Label();
        mv.visitJumpInsn(IF_ICMPLE, l0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitInsn(ICONST_1);
        mv.visitInsn(AALOAD);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getMethodName", "()Ljava/lang/String;");
        mv.visitLdcInsn("cojacCheck");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "startsWith", "(Ljava/lang/String;)Z");
        mv.visitJumpInsn(IFEQ, l0);
        mv.visitInsn(ICONST_2);
        mv.visitVarInsn(ISTORE, 2);
        mv.visitLabel(l0);
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "err", "Ljava/io/PrintStream;");
        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ILOAD, 2);
        mv.visitInsn(AALOAD);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "toString", "()Ljava/lang/String;");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V");
        mv.visitInsn(RETURN);
        mv.visitMaxs(3, 3);
        mv.visitEnd();
    }

    private static void addLogMethod(ClassVisitor cv, Methods methods, Args args) {
        String logFileName = args.getValue(Arg.LOG_FILE);

        MethodVisitor mv = cv.visitMethod(ACC_PRIVATE + ACC_STATIC, methods.getLog(), Signatures.REACT, null, null);
        mv.visitCode();
        Label l0 = new Label();
        Label l1 = new Label();
        Label l2 = new Label();
        mv.visitTryCatchBlock(l0, l1, l2, "java/lang/Exception");
        mv.visitLabel(l0);
        mv.visitTypeInsn(NEW, "java/io/BufferedWriter");
        mv.visitInsn(DUP);
        mv.visitTypeInsn(NEW, "java/io/FileWriter");
        mv.visitInsn(DUP);
        mv.visitLdcInsn(logFileName);
        mv.visitInsn(ICONST_1);
        mv.visitMethodInsn(INVOKESPECIAL, "java/io/FileWriter", "<init>", "(Ljava/lang/String;Z)V");
        mv.visitMethodInsn(INVOKESPECIAL, "java/io/BufferedWriter", "<init>", "(Ljava/io/Writer;)V");
        mv.visitVarInsn(ASTORE, 1);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitLdcInsn("COJAC: ");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/BufferedWriter", "write", "(Ljava/lang/String;)V");
        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/BufferedWriter", "write", "(Ljava/lang/String;)V");
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/BufferedWriter", "newLine", "()V");
        mv.visitTypeInsn(NEW, "java/lang/Throwable");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Throwable", "<init>", "()V");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Throwable", "getStackTrace", "()[Ljava/lang/StackTraceElement;");
        mv.visitVarInsn(ASTORE, 2);
        mv.visitInsn(ICONST_1);
        mv.visitVarInsn(ISTORE, 3);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitInsn(ARRAYLENGTH);
        mv.visitInsn(ICONST_1);
        Label l3 = new Label();
        mv.visitJumpInsn(IF_ICMPLE, l3);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitInsn(ICONST_1);
        mv.visitInsn(AALOAD);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getMethodName", "()Ljava/lang/String;");
        mv.visitLdcInsn("cojacCheck");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "startsWith", "(Ljava/lang/String;)Z");
        mv.visitJumpInsn(IFEQ, l3);
        mv.visitInsn(ICONST_2);
        mv.visitVarInsn(ISTORE, 3);
        mv.visitLabel(l3);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitVarInsn(ILOAD, 3);
        mv.visitIincInsn(3, 1);
        mv.visitInsn(AALOAD);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "toString", "()Ljava/lang/String;");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/BufferedWriter", "write", "(Ljava/lang/String;)V");
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/BufferedWriter", "newLine", "()V");
        mv.visitVarInsn(ILOAD, 3);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitInsn(ARRAYLENGTH);
        mv.visitJumpInsn(IF_ICMPLT, l3);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/BufferedWriter", "newLine", "()V");
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/BufferedWriter", "close", "()V");
        mv.visitLabel(l1);
        Label l4 = new Label();
        mv.visitJumpInsn(GOTO, l4);
        mv.visitLabel(l2);
        mv.visitVarInsn(ASTORE, 1);
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "err", "Ljava/io/PrintStream;");
        mv.visitLdcInsn("COJAC: Error while logging: ");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "print", "(Ljava/lang/String;)V");
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "err", "Ljava/io/PrintStream;");
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Exception", "getMessage", "()Ljava/lang/String;");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V");
        mv.visitLabel(l4);
        mv.visitInsn(RETURN);
        mv.visitMaxs(6, 4);
        mv.visitEnd();
    }

    private static void addLogMethodSmaller(ClassVisitor cv, Methods methods, Args args) {
        String logFileName = args.getValue(Arg.LOG_FILE);

        MethodVisitor mv = cv.visitMethod(ACC_PRIVATE + ACC_STATIC, methods.getLog(), Signatures.REACT, null, null);
        mv.visitCode();
        Label l0 = new Label();
        Label l1 = new Label();
        Label l2 = new Label();
        mv.visitTryCatchBlock(l0, l1, l2, "java/lang/Exception");
        mv.visitLabel(l0);
        mv.visitTypeInsn(NEW, "java/io/BufferedWriter");
        mv.visitInsn(DUP);
        mv.visitTypeInsn(NEW, "java/io/FileWriter");
        mv.visitInsn(DUP);
        mv.visitLdcInsn(logFileName);
        mv.visitInsn(ICONST_1);
        mv.visitMethodInsn(INVOKESPECIAL, "java/io/FileWriter", "<init>", "(Ljava/lang/String;Z)V");
        mv.visitMethodInsn(INVOKESPECIAL, "java/io/BufferedWriter", "<init>", "(Ljava/io/Writer;)V");
        mv.visitVarInsn(ASTORE, 1);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitLdcInsn("COJAC: ");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/BufferedWriter", "write", "(Ljava/lang/String;)V");
        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/BufferedWriter", "write", "(Ljava/lang/String;)V");
        mv.visitVarInsn(ALOAD, 1);
        mv.visitIntInsn(BIPUSH, 32);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/BufferedWriter", "write", "(I)V");
        mv.visitTypeInsn(NEW, "java/lang/Throwable");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Throwable", "<init>", "()V");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Throwable", "getStackTrace", "()[Ljava/lang/StackTraceElement;");
        mv.visitVarInsn(ASTORE, 2);
        mv.visitInsn(ICONST_1);
        mv.visitVarInsn(ISTORE, 3);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitInsn(ARRAYLENGTH);
        mv.visitInsn(ICONST_1);
        Label l3 = new Label();
        mv.visitJumpInsn(IF_ICMPLE, l3);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitInsn(ICONST_1);
        mv.visitInsn(AALOAD);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getMethodName", "()Ljava/lang/String;");
        mv.visitLdcInsn("cojacCheck");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "startsWith", "(Ljava/lang/String;)Z");
        mv.visitJumpInsn(IFEQ, l3);
        mv.visitInsn(ICONST_2);
        mv.visitVarInsn(ISTORE, 3);
        mv.visitLabel(l3);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitVarInsn(ILOAD, 3);
        mv.visitInsn(AALOAD);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "toString", "()Ljava/lang/String;");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/BufferedWriter", "write", "(Ljava/lang/String;)V");
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/BufferedWriter", "newLine", "()V");
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/BufferedWriter", "close", "()V");
        mv.visitLabel(l1);
        Label l4 = new Label();
        mv.visitJumpInsn(GOTO, l4);
        mv.visitLabel(l2);
        mv.visitVarInsn(ASTORE, 1);
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "err", "Ljava/io/PrintStream;");
        mv.visitLdcInsn("COJAC: Error while logging: ");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "print", "(Ljava/lang/String;)V");
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "err", "Ljava/io/PrintStream;");
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Exception", "getMessage", "()Ljava/lang/String;");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V");
        mv.visitLabel(l4);
        mv.visitInsn(RETURN);
        mv.visitMaxs(6, 4);
        mv.visitEnd();

    }

    private static void addThrowMethod(ClassVisitor cv, Methods methods) {
        MethodVisitor mv = cv.visitMethod(ACC_PRIVATE + ACC_STATIC, methods.getException(), Signatures.REACT, null, null);
        mv.visitCode();
        mv.visitTypeInsn(NEW, "java/lang/ArithmeticException");
        mv.visitInsn(DUP);
        mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V");
        mv.visitLdcInsn("COJAC: ");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;");
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;");
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/ArithmeticException", "<init>", "(Ljava/lang/String;)V");
        mv.visitInsn(ATHROW);
        mv.visitMaxs(4, 1);
        mv.visitEnd();
    }
}