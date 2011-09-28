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
import static ch.eiafr.cojac.models.CheckedDoubles.*;

import static org.objectweb.asm.Opcodes.*;

public final class DCMPLInstrumenter implements OpCodeInstrumenter {
    @Override
    public void instrument(MethodVisitor mv, int opCode, String classPath, Methods methods, Reaction reaction, LocalVariablesSorter src) {
        Label l3 = new Label();
                               // a,b
        mv.visitInsn(DUP2);    // a,b,b
        mv.visitInsn(DUP2);    // a,b,b,b
        mv.visitLdcInsn(new Double(2.0)); // a,b,b,b,2
        mv.visitInsn(DMUL);   // a,b,b,2b
        mv.visitInsn(DCMPL);  // a,b,(b?2b)
        mv.visitJumpInsn(IFEQ, l3);  //a,b

        BytecodeUtils.addDup4(mv); // a,b,a,b
        mv.visitInsn(POP2);    // a,b,a
        mv.visitInsn(DUP2);    // a,b,a,a
        mv.visitLdcInsn(new Double(2.0)); // a,b,a,a,2
        mv.visitInsn(DMUL);   // a,b,a,2a
        mv.visitInsn(DCMPL);  // a,b,(a?2a)
        mv.visitJumpInsn(IFEQ, l3);  //a,b

                              // a,b
        BytecodeUtils.addDup4(mv); //a,b,a,b
        mv.visitInsn(DCMPL);
        mv.visitJumpInsn(IFEQ, l3);

        BytecodeUtils.addDup4(mv);
        BytecodeUtils.addDup4(mv);
        mv.visitInsn(DSUB);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "abs", "(D)D");

        BytecodeUtils.addSwap2(mv);
        mv.visitInsn(POP2);
        BytecodeUtils.addSwap2(mv);

        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "ulp", "(D)D");
        mv.visitLdcInsn(new Double(CLOSENESS_ULP_FACTOR_DOUBLE));
        mv.visitInsn(DMUL);

        mv.visitInsn(DCMPG);
        mv.visitJumpInsn(IFGT, l3);
        reaction.insertReactionCall(mv, VERY_CLOSE_MSG+"DCMP", methods, classPath);
        mv.visitLabel(l3);
        mv.visitInsn(DCMPL);
    }
}