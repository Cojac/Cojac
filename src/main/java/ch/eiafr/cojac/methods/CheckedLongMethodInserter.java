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
import ch.eiafr.cojac.reactions.Reaction;

import static org.objectweb.asm.Opcodes.*;

public final class CheckedLongMethodInserter implements MethodInserter {
    @Override
    public void insertMethods(ClassVisitor cv, Args args, Methods methods, Reaction reaction, String classPath) {
        if (args.isOperationEnabled(Arg.LADD)) {
            addLaddCheckMethod(cv, methods, reaction, classPath);
        }

        if (args.isOperationEnabled(Arg.LSUB)) {
            addLsubCheckMethod(cv, methods, reaction, classPath);
        }

        if (args.isOperationEnabled(Arg.LMUL)) {
            addLmulCheckMethod(cv, methods, reaction, classPath);
        }

        if (args.isOperationEnabled(Arg.LDIV)) {
            addLdivCheckMethod(cv, methods, reaction, classPath);
        }

        if (args.isOperationEnabled(Arg.LNEG)) {
            addLnegCheckMethod(cv, methods, reaction, classPath);
        }
    }

    private static void addLaddCheckMethod(ClassVisitor cv, Methods methods, Reaction reaction, String classPath) {
        MethodVisitor mv = cv.visitMethod(ACC_PRIVATE + ACC_STATIC, methods.getMethod(LADD), "(JJ)J", null, null);
        mv.visitCode();
        mv.visitVarInsn(LLOAD, 0);
        mv.visitVarInsn(LLOAD, 2);
        mv.visitInsn(LADD);
        mv.visitVarInsn(LSTORE, 6);
        mv.visitVarInsn(LLOAD, 0);
        mv.visitVarInsn(LLOAD, 6);
        mv.visitInsn(LXOR);
        mv.visitVarInsn(LLOAD, 2);
        mv.visitVarInsn(LLOAD, 6);
        mv.visitInsn(LXOR);
        mv.visitInsn(LAND);
        mv.visitInsn(LCONST_0);
        mv.visitInsn(LCMP);
        Label l0 = new Label();
        mv.visitJumpInsn(IFGE, l0);
        reaction.insertReactionCall(mv, "Overflow : LADD", methods, classPath);
        mv.visitLabel(l0);
        mv.visitFrame(F_APPEND, 1, new Object[]{LONG}, 0, null);
        mv.visitVarInsn(LLOAD, 6);
        mv.visitInsn(LRETURN);
        mv.visitMaxs(6, 8);
        mv.visitEnd();
    }

    private static void addLsubCheckMethod(ClassVisitor cv, Methods methods, Reaction reaction, String classPath) {
        MethodVisitor mv = cv.visitMethod(ACC_PRIVATE + ACC_STATIC, methods.getMethod(LSUB), "(JJ)J", null, null);
        mv.visitCode();
        mv.visitVarInsn(LLOAD, 0);
        mv.visitVarInsn(LLOAD, 2);
        mv.visitInsn(LSUB);
        mv.visitVarInsn(LSTORE, 6);
        mv.visitVarInsn(LLOAD, 0);
        mv.visitVarInsn(LLOAD, 2);
        mv.visitInsn(LXOR);
        mv.visitVarInsn(LLOAD, 0);
        mv.visitVarInsn(LLOAD, 6);
        mv.visitInsn(LXOR);
        mv.visitInsn(LAND);
        mv.visitInsn(LCONST_0);
        mv.visitInsn(LCMP);
        Label l0 = new Label();
        mv.visitJumpInsn(IFGE, l0);
        reaction.insertReactionCall(mv, "Overflow : LSUB", methods, classPath);
        mv.visitLabel(l0);
        mv.visitFrame(F_APPEND, 1, new Object[]{LONG}, 0, null);
        mv.visitVarInsn(LLOAD, 6);
        mv.visitInsn(LRETURN);
        mv.visitMaxs(6, 8);
        mv.visitEnd();
    }

    private static void addLdivCheckMethod(ClassVisitor cv, Methods methods, Reaction reaction, String classPath) {
        MethodVisitor mv = cv.visitMethod(ACC_PRIVATE + ACC_STATIC, methods.getMethod(LDIV), "(JJ)J", null, null);
        mv.visitCode();
        mv.visitVarInsn(LLOAD, 0);
        mv.visitVarInsn(LLOAD, 2);
        mv.visitInsn(LDIV);
        mv.visitVarInsn(LSTORE, 6);
        mv.visitVarInsn(LLOAD, 0);
        mv.visitLdcInsn(-9223372036854775808L);
        mv.visitInsn(LCMP);
        Label l0 = new Label();
        mv.visitJumpInsn(IFNE, l0);
        mv.visitVarInsn(LLOAD, 2);
        mv.visitLdcInsn(-1L);
        mv.visitInsn(LCMP);
        mv.visitJumpInsn(IFNE, l0);
        reaction.insertReactionCall(mv, "Overflow : LDIV", methods, classPath);
        mv.visitLabel(l0);
        mv.visitFrame(F_APPEND, 1, new Object[]{LONG}, 0, null);
        mv.visitVarInsn(LLOAD, 6);
        mv.visitInsn(LRETURN);
        mv.visitMaxs(4, 8);
        mv.visitEnd();
    }

    private static void addLmulCheckMethod(ClassVisitor cv, Methods methods, Reaction reaction, String classPath) {
        MethodVisitor mv = cv.visitMethod(ACC_PRIVATE + ACC_STATIC, methods.getMethod(LMUL), "(JJ)J", null, null);
        mv.visitCode();
        mv.visitVarInsn(LLOAD, 0);
        mv.visitVarInsn(LLOAD, 2);
        mv.visitInsn(LMUL);
        mv.visitVarInsn(LSTORE, 6);
        mv.visitVarInsn(LLOAD, 2);
        mv.visitInsn(LCONST_0);
        mv.visitInsn(LCMP);
        Label l0 = new Label();
        mv.visitJumpInsn(IFNE, l0);
        mv.visitVarInsn(LLOAD, 6);
        mv.visitInsn(LRETURN);
        mv.visitLabel(l0);
        mv.visitFrame(F_APPEND, 1, new Object[]{LONG}, 0, null);
        mv.visitVarInsn(LLOAD, 2);
        mv.visitLdcInsn(-1L);
        mv.visitInsn(LCMP);
        Label l1 = new Label();
        mv.visitJumpInsn(IFNE, l1);
        mv.visitVarInsn(LLOAD, 0);
        mv.visitLdcInsn(-9223372036854775808L);
        mv.visitInsn(LCMP);
        Label l2 = new Label();
        mv.visitJumpInsn(IFEQ, l2);
        mv.visitLabel(l1);
        mv.visitFrame(F_SAME, 0, null, 0, null);
        mv.visitVarInsn(LLOAD, 6);
        mv.visitVarInsn(LLOAD, 2);
        mv.visitInsn(LDIV);
        mv.visitVarInsn(LLOAD, 0);
        mv.visitInsn(LCMP);
        Label l3 = new Label();
        mv.visitJumpInsn(IFEQ, l3);
        mv.visitLabel(l2);
        mv.visitFrame(F_SAME, 0, null, 0, null);
        reaction.insertReactionCall(mv, "Overflow : LMUL", methods, classPath);
        mv.visitLabel(l3);
        mv.visitFrame(F_SAME, 0, null, 0, null);
        mv.visitVarInsn(LLOAD, 6);
        mv.visitInsn(LRETURN);
        mv.visitMaxs(4, 8);
        mv.visitEnd();
    }

    private static void addLnegCheckMethod(ClassVisitor cv, Methods methods, Reaction reaction, String classPath) {
        MethodVisitor mv = cv.visitMethod(ACC_PRIVATE + ACC_STATIC, methods.getMethod(LNEG), "(J)J", null, null);
        mv.visitCode();
        mv.visitVarInsn(LLOAD, 0);
        mv.visitLdcInsn(-9223372036854775808L);
        mv.visitInsn(LCMP);
        Label l0 = new Label();
        mv.visitJumpInsn(IFNE, l0);
        reaction.insertReactionCall(mv, "Overflow : LNEG", methods, classPath);
        mv.visitLabel(l0);
        mv.visitFrame(F_SAME, 0, null, 0, null);
        mv.visitVarInsn(LLOAD, 0);
        mv.visitInsn(LNEG);
        mv.visitInsn(LRETURN);
        mv.visitMaxs(4, 4);
        mv.visitEnd();
    }
}