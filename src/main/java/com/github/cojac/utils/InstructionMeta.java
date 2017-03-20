package com.github.cojac.utils;

public class InstructionMeta {
    private final int opCode;
    private final String opName;
    private final String invokedMethod;
    private final String behaviour;

    public InstructionMeta(int opCode, String opName, String invokedMethod, String behaviour) {
        this.opCode = opCode;
        this.opName = opName;
        this.invokedMethod = invokedMethod;
        this.behaviour = behaviour;
    }

    public int getOpCode() {
        return opCode;
    }

    public String getOpName() {
        return opName;
    }

    public String getInvokedMethod() {
        return invokedMethod;
    }

    public String getBehaviour() {
        return behaviour;
    }

}
