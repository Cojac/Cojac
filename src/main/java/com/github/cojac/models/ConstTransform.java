package com.github.cojac.models;

import org.objectweb.asm.Opcodes;

public enum ConstTransform {
    doubleTransform(new Operation(Opcodes.INVOKESTATIC,"fromUninstrumentedDouble", "(D)D", Parameters.DOUBLE_BINARY_PARAMS.params), double.class);
    
    public final Operation operation;
    public final Class<?> constType;
    private ConstTransform(Operation operation, Class<?> constType) {
        // TODO Auto-generated constructor stub
        this.operation = operation;
        this.constType = constType;
    }
    public static ConstTransform getMethodForClass(Class<?> c){
        for(ConstTransform ct: ConstTransform.values()){
            if(ct.constType == c)
                return ct;
        }
        return null;
    }
}
