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

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.LocalVariablesSorter;

import com.github.cojac.instrumenters.IOpcodeInstrumenter;
import com.github.cojac.instrumenters.BehaviourInstrumenter;
import com.github.cojac.models.Operations;
import com.github.cojac.instrumenters.IOpcodeInstrumenterFactory;

/**
 * Class called for each instrumented method, which can change a variable for another, 
 * and change the method's behaviour.
 * @author Valentin
 */

import java.util.BitSet;
final class BehaviourMethodVisitor extends LocalVariablesSorter {
    private final IOpcodeInstrumenterFactory factory;
    private static BitSet constLoadInst = new BitSet(255);//there is 255 possible opcodes
    static{
        for(Operations op: Operations.values()){
            if(op.loadsConst)
                constLoadInst.set(op.opCodeVal);
        }
    }
    private final InstrumentationStats stats;
    private final Args args;
    private final String classPath;
    private final CojacReferences references;
    private boolean instrumentMethod = false;
    private int lineNb=0;
    private BehaviourClassVisitor bcv;
    BehaviourInstrumenter instrumenter ;
    BehaviourMethodVisitor(int access, String desc, MethodVisitor mv, InstrumentationStats stats, Args args, String classPath,
            IOpcodeInstrumenterFactory factory, CojacReferences references, String MethodName, BehaviourClassVisitor behaviourClassVisitor) {
        super(Opcodes.ASM5, access, desc, mv);

        this.stats = stats;
        this.args = args;
        this.factory = factory;

        this.classPath = classPath;
        this.references = references;
        instrumentMethod = references.hasToBeInstrumented(classPath, MethodName+desc);
        instrumenter = BehaviourInstrumenter.getInstance(args, stats);
        this.bcv = behaviourClassVisitor;
    }

    @Override
    public void visitInsn(int opCode) {
        bcv.incInstructionCounter();
        int instructionNb = bcv.getInstructionCounter();
        IOpcodeInstrumenter instrumenter = factory.getInstrumenter(opCode);
        //System.out.println("Has to be instrumented: "+references.hasToBeInstrumented(classPath, lineNb, instructionNb));
        //Delegate to parent
        if ((references.hasToBeInstrumented(classPath, lineNb, instructionNb, opCode)||instrumentMethod) &&instrumenter != null ) {
            if(constLoadInst.get(opCode)){//the operation is a constant loading one
                super.visitInsn(opCode);//load the constant
                visitConstantLoading(Operations.getReturnType(opCode));//transform it
            }else{
                instrumenter.instrument(mv, opCode); //, classPath, methods, reaction, this);
            }
        } else{
            super.visitInsn(opCode);
        }
    }
    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        bcv.incInstructionCounter();
        int instructionNb = bcv.getInstructionCounter();
        //System.out.println("Has to be instrumented: "+references.hasToBeInstrumented(classPath, lineNb, instructionNb));
        if ((references.hasToBeInstrumented(classPath, lineNb, instructionNb, opcode)||instrumentMethod) && instrumenter.wantsToInstrumentMethod(opcode, owner,name,desc)){
            instrumenter.instrumentMethod(mv,owner, name, desc);
        }else{
            super.visitMethodInsn(opcode, owner, name, desc, itf);
        }
   
    }
    @Override
    public void visitLdcInsn(Object cst){
        bcv.incInstructionCounter();
        int instructionNb = bcv.getInstructionCounter();
        super.visitLdcInsn(cst);
        //System.out.println("Has to be instrumented: "+references.hasToBeInstrumented(classPath, lineNb, instructionNb));
        if((references.hasToBeInstrumented(classPath, lineNb, instructionNb, Opcodes.LDC)||instrumentMethod)&&instrumenter.wantsToInstrumentConstLoading(cst.getClass())){
            //instrumenter.instrumentLDC(mv, cst);
            visitConstantLoading(cst.getClass());
        }else{
         //   super.visitLdcInsn(cst);
        }
    }
    private void visitConstantLoading(Class<?> cl){
        instrumenter.instrumentConstLoading(mv, cl);
    }
    @Override
    public void visitLineNumber(int line, Label start){
        lineNb = line; 
        super.visitLineNumber(line, start);
    }
}