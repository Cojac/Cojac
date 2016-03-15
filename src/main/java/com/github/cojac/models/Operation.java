package com.github.cojac.models;

public class Operation {
    public final int opCodeVal;
    public final String opCodeName;
    public final String signature;
    
    public Operation(int opCodeVal,String opCodeName, String signature ) {
       this.opCodeVal=opCodeVal;
       this.opCodeName=opCodeName;
       this.signature = signature;
    }
}