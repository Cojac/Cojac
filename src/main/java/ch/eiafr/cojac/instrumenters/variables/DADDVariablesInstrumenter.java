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

package ch.eiafr.cojac.instrumenters.variables;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.LocalVariablesSorter;

import ch.eiafr.cojac.Methods;
import ch.eiafr.cojac.instrumenters.OpCodeInstrumenter;
import ch.eiafr.cojac.reactions.Reaction;
import ch.eiafr.cojac.utils.BytecodeUtils;

import static org.objectweb.asm.Opcodes.*;

public final class DADDVariablesInstrumenter implements OpCodeInstrumenter {
    @Override
    public void instrument(MethodVisitor mv, int opCode, String classPath, Methods methods, Reaction reaction, LocalVariablesSorter src) {
        int a = src.newLocal(Type.DOUBLE_TYPE);//0
        int b = src.newLocal(Type.DOUBLE_TYPE);//2
        int r = src.newLocal(Type.DOUBLE_TYPE);//6

        BytecodeUtils.addDup4(mv);
        src.visitVarInsn(DSTORE, b);
        src.visitVarInsn(DSTORE, a);
        mv.visitInsn(DADD);
        src.visitVarInsn(DSTORE, r);
         
        src.visitVarInsn(DLOAD, b);
        mv.visitInsn(DCONST_0);
        mv.visitInsn(DCMPL);
        Label l0 = new Label();
        mv.visitJumpInsn(IFEQ, l0);
        src.visitVarInsn(DLOAD, r);
        src.visitVarInsn(DLOAD, a);
        mv.visitInsn(DCMPL);
        Label l1 = new Label();
        mv.visitJumpInsn(IFEQ, l1);
        mv.visitLabel(l0);
        src.visitVarInsn(DLOAD, a);
        mv.visitInsn(DCONST_0);
        mv.visitInsn(DCMPL);
        Label l2 = new Label();
        mv.visitJumpInsn(IFEQ, l2);
        src.visitVarInsn(DLOAD, r);
        src.visitVarInsn(DLOAD, b);
        mv.visitInsn(DCMPL);
        mv.visitJumpInsn(IFNE, l2);
        mv.visitLabel(l1);
        reaction.insertReactionCall(mv, "Precision error : DADD", methods, classPath);
        Label l3 = new Label();
        mv.visitJumpInsn(GOTO, l3);
        mv.visitLabel(l2);
        src.visitVarInsn(DLOAD, a);
        mv.visitLdcInsn(new Double("Infinity"));
        mv.visitInsn(DCMPL);
        mv.visitJumpInsn(IFEQ, l3);
        src.visitVarInsn(DLOAD, a);
        mv.visitLdcInsn(new Double("-Infinity"));
        mv.visitInsn(DCMPL);
        mv.visitJumpInsn(IFEQ, l3);
        src.visitVarInsn(DLOAD, b);
        mv.visitLdcInsn(new Double("Infinity"));
        mv.visitInsn(DCMPL);
        mv.visitJumpInsn(IFEQ, l3);
        src.visitVarInsn(DLOAD, b);
        mv.visitLdcInsn(new Double("-Infinity"));
        mv.visitInsn(DCMPL);
        mv.visitJumpInsn(IFEQ, l3);
        src.visitVarInsn(DLOAD, r);
        mv.visitLdcInsn(new Double("Infinity"));
        mv.visitInsn(DCMPL);
        Label l4 = new Label();
        mv.visitJumpInsn(IFNE, l4);
        reaction.insertReactionCall(mv, "Result is Double.POSITIVE_INFINITY : DADD", methods, classPath);
        mv.visitJumpInsn(GOTO, l3);
        mv.visitLabel(l4);
        src.visitVarInsn(DLOAD, r);
        mv.visitLdcInsn(new Double("-Infinity"));
        mv.visitInsn(DCMPL);
        mv.visitJumpInsn(IFNE, l3);
        reaction.insertReactionCall(mv, "Result is Double.NEGATIVE_INFINITY : DADD", methods, classPath);
        mv.visitLabel(l3);
        src.visitVarInsn(DLOAD, r);
    }
}