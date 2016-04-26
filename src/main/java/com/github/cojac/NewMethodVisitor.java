/*
 * *
 *    Copyright 2011-2016 Valentin Gazzola & Frédéric Bapst
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

package com.github.cojac;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.LocalVariablesSorter;

import com.github.cojac.instrumenters.IOpcodeInstrumenter;
import com.github.cojac.instrumenters.NewInstrumenter;
import com.github.cojac.instrumenters.IOpcodeInstrumenterFactory;
import com.github.cojac.instrumenters.InvokableMethod;

import static org.objectweb.asm.Opcodes.*;
/**
 * Class called for each instrumented method, which can change an variable for another, 
 * and change the method's behaviour.
 * @author Valentin
 */
final class NewMethodVisitor extends LocalVariablesSorter {
    private final IOpcodeInstrumenterFactory factory;
    private final InstrumentationStats stats;
    private final Args args;
    private final String classPath;
    NewInstrumenter instrumenter ;
    NewMethodVisitor(int access, String desc, MethodVisitor mv, InstrumentationStats stats, Args args, String classPath, IOpcodeInstrumenterFactory factory) {
        super(Opcodes.ASM5, access, desc, mv);

        this.stats = stats;
        this.args = args;
        this.factory = factory;

        this.classPath = classPath;
        instrumenter = NewInstrumenter.getInstance(args, stats);
    }

    @Override
    public void visitInsn(int opCode) {
        IOpcodeInstrumenter instrumenter = factory.getInstrumenter(opCode);

        //Delegate to parent
        if (instrumenter == null) {
            super.visitInsn(opCode);
        } else {
            instrumenter.instrument(mv, opCode); //, classPath, methods, reaction, this);
        }
    }
    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
   
        if (instrumenter.wantsToInstrumentMethod(opcode, owner,name,desc)){
            instrumenter.instrumentMethod(mv,owner, name, desc);
        }else{
            super.visitMethodInsn(opcode, owner, name, desc, itf);
        }
   
    }
    @Override
    public void visitLdcInsn(Object cst){
        if(instrumenter.wantsToInstrumentLDC(cst)){
            instrumenter.instrumentLDC(mv, cst);
        }else{
            super.visitLdcInsn(cst);
        }
        
    }
    
}