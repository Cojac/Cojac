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

import static org.objectweb.asm.Opcodes.*;

public final class D2FInstrumenter implements OpCodeInstrumenter {
    @Override
    public void instrument(MethodVisitor mv, int opCode, String classPath, Methods methods, Reaction reaction, LocalVariablesSorter src) {
        mv.visitInsn(DUP2);
        mv.visitLdcInsn(new Double("-3.4028234663852886E38"));
        mv.visitInsn(DCMPG);
        Label l0 = new Label();
        mv.visitJumpInsn(IFLT, l0);
        mv.visitInsn(DUP2);
        mv.visitLdcInsn(new Double("3.4028234663852886E38"));
        mv.visitInsn(DCMPL);
        Label l1 = new Label();
        mv.visitJumpInsn(IFLE, l1);
        mv.visitLabel(l0);
        reaction.insertReactionCall(mv, "Overflow error : D2F", methods, classPath);
        mv.visitLabel(l1);
        mv.visitInsn(D2F);
    }
}