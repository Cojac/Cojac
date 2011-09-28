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

import static ch.eiafr.cojac.models.CheckedDoubles.VERY_CLOSE_MSG;
import static ch.eiafr.cojac.models.CheckedDoubles.CLOSENESS_ULP_FACTOR_FLOAT;
import static org.objectweb.asm.Opcodes.*;

public final class FCMPLInstrumenter implements OpCodeInstrumenter {
    @Override
    public void instrument(MethodVisitor mv, int opCode, String classPath, Methods methods, Reaction reaction, LocalVariablesSorter src) {
        Label l3 = new Label();
        
                              // a,b
        mv.visitInsn(DUP);    // a,b,b
        mv.visitInsn(DUP);    // a,b,b,b
        mv.visitLdcInsn(new Float(2.0f)); // a,b,b,b,2
        mv.visitInsn(FMUL);   // a,b,b,2b
        mv.visitInsn(FCMPL);  // a,b,(b?2b)
        mv.visitJumpInsn(IFEQ, l3);  //a,b
        
        mv.visitInsn(DUP2);    // a,b,a,b
        mv.visitInsn(POP);    // a,b,a
        mv.visitInsn(DUP);    // a,b,a,a
        mv.visitLdcInsn(new Float(2.0f)); // a,b,a,a,2
        mv.visitInsn(FMUL);   // a,b,a,2a
        mv.visitInsn(FCMPL);  // a,b,(a?2a)
        mv.visitJumpInsn(IFEQ, l3);  //a,b

                              // a,b
        mv.visitInsn(DUP2);   // a,b,a,b
        mv.visitInsn(FCMPL);  // a,b,(a?b)
        mv.visitJumpInsn(IFEQ, l3);  //a,b

        mv.visitInsn(DUP2);  //a,b,a,b
        mv.visitInsn(DUP2);  //a,b,a,b,a,b
        mv.visitInsn(FSUB);  //a,b,a,b,(a-b)
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "abs", "(F)F");
                             //a,b,a,b,|a-b|
        mv.visitInsn(SWAP);  //a,b,a,|a-b|,b
        mv.visitInsn(POP);   //a,b,a,|a-b|
        mv.visitInsn(SWAP);  //a,b,|a-b|,a

        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "ulp", "(F)F");
                             //a,b,|a-b|,ulp(a)
        mv.visitLdcInsn(new Float(CLOSENESS_ULP_FACTOR_FLOAT));
        mv.visitInsn(FMUL);  //a,b,|a-b|,ulp(a)*CLOSE

        mv.visitInsn(FCMPG); //a,b,|a-b|?ulp(a)*CLOSE
        mv.visitJumpInsn(IFGT, l3);  //a,b
        reaction.insertReactionCall(mv, VERY_CLOSE_MSG+"FCMP", methods, classPath);
        mv.visitLabel(l3);   //a,b
        mv.visitInsn(FCMPL); //a?b
    }
}