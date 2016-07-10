package com.github.cojac.utils;

public class InstructionMeta {
    public int opCode;
    public String opName;
    public String invokedMethod;
    public String behaviour;

    public InstructionMeta() {
    }

    public InstructionMeta(int opCode, String opName, String invokedMethod, String behaviour) {
        this.opCode = opCode;
        this.opName = opName;
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
