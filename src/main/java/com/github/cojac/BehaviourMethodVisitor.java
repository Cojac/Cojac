/*
 * *
 *    Copyright 2011-2016 Valentin Gazzola & Frédéric Bapst & Badoud
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

import static org.objectweb.asm.util.Printer.OPCODES;

/**
 * Class called for each instrumented method, which can change a variable for another, 
 * and change the method's behaviour.
 * @author Valentin
 */

import java.util.BitSet;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.LocalVariablesSorter;

import com.github.cojac.instrumenters.BehaviourInstrumenter;
import com.github.cojac.instrumenters.IOpcodeInstrumenter;
import com.github.cojac.instrumenters.IOpcodeInstrumenterFactory;
import com.github.cojac.models.Operations;
import com.github.cojac.utils.BehaviourLoader;
import com.github.cojac.utils.InstructionWriter;

final class BehaviourMethodVisitor extends LocalVariablesSorter {
    private final IOpcodeInstrumenterFactory factory;
    private static BitSet constLoadInst = new BitSet(255);// there are 255
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
    private int insnNb = 0;
    private String methodName;
    private String desc;

    private BehaviourClassVisitor bcv;
    private BehaviourInstrumenter instrumenter;

    public BehaviourMethodVisitor(int access, String desc, MethodVisitor mv, InstrumentationStats stats, Args args, String classPath, IOpcodeInstrumenterFactory factory, CojacReferences references, String methodName, BehaviourClassVisitor behaviourClassVisitor) {
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
    }

    @Override
    public void visitInsn(int opCode) {
        // increment global instruction counter (class level)
        bcv.incInstructionCounter();
        int instructionNb = bcv.getInstructionCounter();
        // increment local instruction counter (line level)
        insnNb++;
        // if the has no behaviour define in behaviour class ( the operation is
        // not instrumentable)
        IOpcodeInstrumenter instrumenter = factory.getInstrumenter(opCode);
        if (instrumenter == null) {
            super.visitInsn(opCode);
            return;
        }
        // ----------------------------------------------------------
        // log the current instruction (badoud)
        // ----------------------------------------------------------
        if (args.isSpecified(Arg.LISTING_INSTRUCTIONS))
            InstructionWriter.getinstance().logInstruction(classPath, methodName +
                    this.desc, lineNb, insnNb, opCode, opCodeToString(opCode), "");
        // ----------------------------------------------------------
        // instrument as defined in file (badoud)
        // ----------------------------------------------------------
        if (args.isSpecified(Arg.LOAD_BEHAVIOUR_MAP)) {
            if (!isSpecifiedBehaviour()) {
                super.visitInsn(opCode);
                return;
            }
            // if the operation is constant loading operation
            if (constLoadInst.get(opCode)) {
                // load the constant and transform it
                super.visitInsn(opCode);
                this.instrumenter.instrumentConstLoading(mv, Operations.getReturnType(opCode));
            }
            // instrument the operation
            else
                instrumenter.instrument(mv, opCode);
        }
        // ----------------------------------------------------------
        // instrument as defined in syntax (gazzola)
        // ----------------------------------------------------------
        else if ((references.hasToBeInstrumented(classPath, lineNb, instructionNb, opCode) ||
                instrumentMethod)) {
            // if the operation is constant loading operation
            if (constLoadInst.get(opCode)) {
                // load the constant and transform it
                super.visitInsn(opCode);
                this.instrumenter.instrumentConstLoading(mv, Operations.getReturnType(opCode));
            }
            // instrument the operation
            else
                instrumenter.instrument(mv, opCode);
        }
        // ----------------------------------------------------------
        // no instrumentation (continue visiting)
        // ----------------------------------------------------------
        else {
            super.visitInsn(opCode);
        }
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        // increment global instruction counter (class level)
        bcv.incInstructionCounter();
        int instructionNb = bcv.getInstructionCounter();
        // increment local instruction counter (line level)
        insnNb++;
        // if the has no behaviour define in behaviour class ( the operation is
        // not instrumentable)
        if (!instrumenter.wantsToInstrumentMethod(opcode, owner, name, desc)) {
            super.visitMethodInsn(opcode, owner, name, desc, itf);
            return;
        }
        // ----------------------------------------------------------
        // log the current instruction (badoud)
        // ----------------------------------------------------------
        if (args.isSpecified(Arg.LISTING_INSTRUCTIONS))
            InstructionWriter.getinstance().logInstruction(classPath, methodName +
                    this.desc, lineNb, insnNb, opcode, opCodeToString(opcode), owner +
                            "/" + name + desc);
        // ----------------------------------------------------------
        // instrument as defined in file (badoud)
        // ----------------------------------------------------------
        if (args.isSpecified(Arg.LOAD_BEHAVIOUR_MAP)) {
            if (!isSpecifiedBehaviour()) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                return;
            }
            instrumenter.instrumentMethod(mv, owner, name, desc);
        }
        // ----------------------------------------------------------
        // instrument as defined in syntax (gazzola)
        // ----------------------------------------------------------
        else if (references.hasToBeInstrumented(classPath, lineNb, instructionNb, opcode) ||
                instrumentMethod) {
            instrumenter.instrumentMethod(mv, owner, name, desc);
        }
        // ----------------------------------------------------------
        // no instrumentation (continue visiting)
        // ----------------------------------------------------------
        else
            super.visitMethodInsn(opcode, owner, name, desc, itf);

    }

    @Override
    public void visitLdcInsn(Object cst) {
        // increment global instruction counter (class level)
        bcv.incInstructionCounter();
        int instructionNb = bcv.getInstructionCounter();
        // increment local instruction counter (line level)
        insnNb++;
        // if the has no behaviour define in behaviour class ( the operation is
        // not instrumentable)
        if (!instrumenter.wantsToInstrumentConstLoading(cst.getClass())) {
            super.visitLdcInsn(cst);
            return;
        }
        // ----------------------------------------------------------
        // log the current instruction (badoud)
        // ----------------------------------------------------------
        if (args.isSpecified(Arg.LISTING_INSTRUCTIONS))
            InstructionWriter.getinstance().logInstruction(classPath, methodName +
                    this.desc, lineNb, insnNb, Opcodes.LDC, opCodeToString(Opcodes.LDC), "");
        // ----------------------------------------------------------
        // instrument as defined in file (badoud)
        // ----------------------------------------------------------
        if (args.isSpecified(Arg.LOAD_BEHAVIOUR_MAP)) {
            super.visitLdcInsn(cst);
            if (!isSpecifiedBehaviour())
                return;
            instrumenter.instrumentConstLoading(mv, cst.getClass());
        }
        // ----------------------------------------------------------
        // instrument as defined in syntax (gazzola)
        // ----------------------------------------------------------
        else if (references.hasToBeInstrumented(classPath, lineNb, instructionNb, Opcodes.LDC) ||
                instrumentMethod) {
            super.visitLdcInsn(cst);
            instrumenter.instrumentConstLoading(mv, cst.getClass());
        }
        // ----------------------------------------------------------
        // no instrumentation (continue visiting)
        // ----------------------------------------------------------
        else {
            super.visitLdcInsn(cst);
        }
    }

    @Override
    public void visitLineNumber(int line, Label start) {
        lineNb = line;
        insnNb = 0;
        super.visitLineNumber(line, start);
    }

    private static String opCodeToString(int opCode) {
        if (opCode < 0 || opCode >= OPCODES.length)
            return "UNKNOWN";
        return OPCODES[opCode];
    }

    // check there is a behaviour associated with the current instruction
    private boolean isSpecifiedBehaviour() {
        BehaviourLoader bl=BehaviourLoader.getinstance();
        String m=methodName + desc;
        return bl.isSpecifiedBehaviour(classPath, m, lineNb, insnNb) &&
             ! bl.getSpecifiedBehaviour(classPath, m, lineNb, insnNb).equals("IGNORE");
    }
}