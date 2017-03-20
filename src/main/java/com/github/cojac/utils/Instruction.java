package com.github.cojac.utils;

public class Instruction {
    public int opCode;
    public String opName;
    public int lineNumber;
    public int globalInstructionNumber;
    public int localInstructionNumber;
    public String invokedMethod;
    public String behaviour;

    public Instruction() {
    }

    public Instruction(int opCode, String opName, int lineNumber, int globalInstructionNumber, int localInstructionNumber, String invokedMethod, String behaviour) {
        this.opCode = opCode;
        this.opName = opName;
        this.lineNumber = lineNumber;
        this.globalInstructionNumber = globalInstructionNumber;
        this.localInstructionNumber = localInstructionNumber;
        this.invokedMethod = invokedMethod;
        this.behaviour = behaviour;
    }

    public int getOpCode() {
        return opCode;
    }

    public void setOpCode(int opCode) {
        this.opCode = opCode;
    }

    public String getOpName() {
        return opName;
    }

    public void setOpName(String opName) {
        this.opName = opName;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public int getGlobalInstructionNumber() {
        return globalInstructionNumber;
    }

    public void setGlobalInstructionNumber(int globalInstructionNumber) {
        this.globalInstructionNumber = globalInstructionNumber;
    }

    public int getLocalInstructionNumber() {
        return localInstructionNumber;
    }

    public void setLocalInstructionNumber(int localInstructionNumber) {
        this.localInstructionNumber = localInstructionNumber;
    }

    public String getInvokedMethod() {
        return invokedMethod;
    }

    public void setInvokedMethod(String invokedMethod) {
        this.invokedMethod = invokedMethod;
    }

    public String getBehaviour() {
        return behaviour;
    }

    public void setBehaviour(String behaviour) {
        this.behaviour = behaviour;
    }

}
