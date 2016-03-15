package com.github.cojac.models;

public class Operation {
    public final int opCodeVal;
    public final String opCodeName;
    public final String signature;
    public final Class<?>[] parameters;
    
    public Operation(int opCodeVal,String opCodeName, String signature, Class<?>[] parameters ) {
       this.opCodeVal=opCodeVal;
       this.opCodeName=opCodeName;
       this.signature = signature;
       this.parameters = parameters;
    }
}