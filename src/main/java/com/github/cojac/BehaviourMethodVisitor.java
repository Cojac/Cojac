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
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;

import com.github.cojac.instrumenters.IOpcodeInstrumenter;
import com.github.cojac.instrumenters.BehaviourInstrumenter;
import com.github.cojac.models.Operations;
import com.github.cojac.utils.BehaviourLoader;
import com.github.cojac.utils.Instruction;
import com.github.cojac.utils.InstructionWriter;
import com.github.cojac.instrumenters.IOpcodeInstrumenterFactory;

import static org.objectweb.asm.util.Printer.OPCODES;

/**
 * Class called for each instrumented method, which can change a variable for another, 
 * and change the method's behaviour.
 * @author Valentin
 */

import java.util.BitSet;
import java.util.HashMap;

final class BehaviourMethodVisitor extends LocalVariablesSorter {
    private final IOpcodeInstrumenterFactory factory;
    private static BitSet constLoadInst = new BitSet(255);// there is 255
                                                          // possible opcodes
    static {
        for (Operations op : Operations.values()) {
            if (op.loadsConst)
                constLoadInst.set(op.opCodeVal);
        }
    }
    private final InstrumentationStats stats;
    private final Args args;
    private final String classPath;
    private final CojacReferences references;
    private boolean instrumentMethod = false;
    private int lineNb = 0;
    private String methodName;
    private String desc;

    private BehaviourClassVisitor bcv;
    BehaviourInstrumenter instrumenter;

    private int localInstructionCounter;
    private HashMap<Integer, Instruction> methodMap;
    private HashMap<Integer, Instruction> methodBehaviourMap;

    BehaviourMethodVisitor(int access, String desc, MethodVisitor mv, InstrumentationStats stats, Args args, String classPath, IOpcodeInstrumenterFactory factory, CojacReferences references, String methodName, BehaviourClassVisitor behaviourClassVisitor) {
        super(Opcodes.ASM5, access, desc, mv);

        this.stats = stats;
        this.args = args;
        this.factory = factory;

        this.classPath = classPath;
        this.references = references;
        this.methodName = methodName;
        this.desc = desc;
        instrumentMethod = references.hasToBeInstrumented(classPath, methodName +
                desc);
        instrumenter = BehaviourInstrumenter.getInstance(args, stats);
        this.bcv = behaviourClassVisitor;
        if (args.isSpecified(Arg.LISTING_INSTRUCTIONS))
            methodMap = new HashMap<Integer, Instruction>();
        if (args.isSpecified(Arg.LOAD_BEHAVIOUR_MAP))
            if (BehaviourLoader.getinstance().containsMethodBehaviourMap(classPath, methodName +
                    desc))
                methodBehaviourMap = BehaviourLoader.getinstance().getMethodBehaviourMap(classPath, methodName +
                        desc);
    }

    @Override
    public void visitEnd() {
        if (args.isSpecified(Arg.LISTING_INSTRUCTIONS))
            bcv.putMethodMap(methodName + desc, methodMap);
        super.visitEnd();
    }

    @Override
    public void visitInsn(int opCode) {
        System.out.println("BehaviourMethodVisitor.visitInsn()");
        // incrémente le compteur global (au niveau de la classe)
        bcv.incInstructionCounter();
        int instructionNb = bcv.getInstructionCounter();
        // incrémente le compteur local (au niveau de la méthode)
        localInstructionCounter++;
        // ----------------------------------------------------------
        if (args.isSpecified(Arg.LISTING_INSTRUCTIONS))
            methodMap.put(localInstructionCounter, new Instruction(opCode, opCodeToString(opCode), lineNb, instructionNb, localInstructionCounter, "", "IGNORE"));
        // ----------------------------------------------------------

        IOpcodeInstrumenter instrumenter = factory.getInstrumenter(opCode);

        if (args.isSpecified(Arg.LOAD_BEHAVIOUR_MAP)) {
            if (isBehaviourDefinesFor(localInstructionCounter) &&
                    instrumenter != null) {
                if (constLoadInst.get(opCode)) {// the operation is a constant
                                                // loading one
                    super.visitInsn(opCode);// load the constant
                    visitConstantLoading(Operations.getReturnType(opCode));// transform
                                                                           // it
                } else {
                    instrumenter.instrument(mv, opCode); // , classPath,
                                                         // methods, reaction,
                                                         // this);
                }
            } else {
                super.visitInsn(opCode);
            }
        } // System.out.println("Has to be instrumented:
          // "+references.hasToBeInstrumented(classPath, lineNb,
          // instructionNb));
          // Delegate to parent
        else {
            if ((references.hasToBeInstrumented(classPath, lineNb, instructionNb, opCode) ||
                    instrumentMethod) && instrumenter != null) {
                if (constLoadInst.get(opCode)) {// the operation is a constant
                                                // loading one
                    super.visitInsn(opCode);// load the constant
                    visitConstantLoading(Operations.getReturnType(opCode));// transform
                                                                           // it
                } else {
                    instrumenter.instrument(mv, opCode); // , classPath,
                                                         // methods, reaction,
                                                         // this);
                }
            } else {
                super.visitInsn(opCode);
            }
        }
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        System.out.println("BehaviourMethodVisitor.visitMethodInsn()");
        // incrémente le compteur global (au niveau de la classe)
        bcv.incInstructionCounter();
        int instructionNb = bcv.getInstructionCounter();
        // incrémente le compteur local (au niveau de la méthode)
        localInstructionCounter++;
        // ----------------------------------------------------------
        if (args.isSpecified(Arg.LISTING_INSTRUCTIONS))
            methodMap.put(localInstructionCounter, new Instruction(opcode, opCodeToString(opcode), lineNb, instructionNb, localInstructionCounter, owner +
                    "/" + name + desc, "IGNORE"));
        // ----------------------------------------------------------

        if (args.isSpecified(Arg.LOAD_BEHAVIOUR_MAP)) {
            if (isBehaviourDefinesFor(localInstructionCounter) &&
                    instrumenter.wantsToInstrumentMethod(opcode, owner, name, desc)) {
                instrumenter.instrumentMethod(mv, owner, name, desc);
            } else {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
            }
        } else {
            if ((references.hasToBeInstrumented(classPath, lineNb, instructionNb, opcode) ||
                    instrumentMethod) &&
                    instrumenter.wantsToInstrumentMethod(opcode, owner, name, desc)) {
                instrumenter.instrumentMethod(mv, owner, name, desc);
            } else {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
            }
        }

    }

    @Override
    public void visitLdcInsn(Object cst) {
        System.out.println("BehaviourMethodVisitor.visitLdcInsn()");
        // incrémente le compteur global (au niveau de la classe)
        bcv.incInstructionCounter();
        int instructionNb = bcv.getInstructionCounter();
        // incrémente le compteur local (au niveau de la méthode)
        localInstructionCounter++;
        // ----------------------------------------------------------
        if (args.isSpecified(Arg.LISTING_INSTRUCTIONS))
            methodMap.put(localInstructionCounter, new Instruction(Opcodes.LDC, opCodeToString(Opcodes.LDC), lineNb, instructionNb, localInstructionCounter, "", "IGNORE"));
        // ----------------------------------------------------------
        super.visitLdcInsn(cst);
        if (args.isSpecified(Arg.LOAD_BEHAVIOUR_MAP)) {
            if (isBehaviourDefinesFor(localInstructionCounter) &&
                    instrumenter.wantsToInstrumentConstLoading(cst.getClass())) {
                // instrumenter.instrumentLDC(mv, cst);
                visitConstantLoading(cst.getClass());
            } else {
                // super.visitLdcInsn(cst);
            }
        }
        // System.out.println("Has to be instrumented:
        // "+references.hasToBeInstrumented(classPath, lineNb, instructionNb));
        else {
            if ((references.hasToBeInstrumented(classPath, lineNb, instructionNb, Opcodes.LDC) ||
                    instrumentMethod) &&
                    instrumenter.wantsToInstrumentConstLoading(cst.getClass())) {
                // instrumenter.instrumentLDC(mv, cst);
                visitConstantLoading(cst.getClass());
            } else {
                // super.visitLdcInsn(cst);
            }
        }
    }

    private void visitConstantLoading(Class<?> cl) {
        System.out.println("BehaviourMethodVisitor.visitConstantLoading()");
        instrumenter.instrumentConstLoading(mv, cl);
    }

    @Override
    public void visitLineNumber(int line, Label start) {
        System.out.println("BehaviourMethodVisitor.visitLineNumber()");
        lineNb = line;
        super.visitLineNumber(line, start);
    }

    private static String opCodeToString(int opCode) {
        if (opCode < 0 || opCode >= OPCODES.length)
            return "UNKNOWN";
        return OPCODES[opCode];
    }

    private boolean isBehaviourDefinesFor(int localInstructionNumber) {
        if (methodBehaviourMap == null)
            return false;
        return methodBehaviourMap.containsKey(localInstructionNumber) &&
                !methodBehaviourMap.get(localInstructionNumber).behaviour.equals("IGNORE");
    }

}