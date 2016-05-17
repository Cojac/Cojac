package com.github.cojac.models;

import org.objectweb.asm.Opcodes;
import static com.github.cojac.models.Signatures.*;
import static com.github.cojac.models.Parameters.*;
public enum ConstTransform {
    doubleTransform(new Operation(Opcodes.INVOKESTATIC,"fromUninstrumentedDouble", DOUBLE_UNARY.description,
            DOUBLE_UNARY_PARAMS.params), double.class),
    floatTransform(new Operation(Opcodes.INVOKESTATIC,"fromUninstrumentedFloat", FLOAT_UNARY.description,
            FLOAT_BINARY_PARAMS.params), float.class),
    intTransform(new Operation(Opcodes.INVOKESTATIC,"fromUninstrumentedInt", INTEGER_UNARY.description,
            INTEGER_BINARY_PARAMS.params), int.class),
    longTransform(new Operation(Opcodes.INVOKESTATIC,"fromUninstrumentedLong", LONG_UNARY.description,
            LONG_BINARY_PARAMS.params), long.class);
    public final Operation operation;
    public final Class<?> constType;
    private ConstTransform(Operation operation, Class<?> constType) {
        // TODO Auto-generated constructor stub
        this.operation = operation;
        this.constType = constType;
    }
    public static ConstTransform getMethodForClass(Class<?> c){
        for(ConstTransform ct: ConstTransform.values()){
            if(classEquals(ct.constType, c))
                return ct;
        }
        return null;
    }
    private static boolean classEquals(Class<?> a, Class<?> b){
        return unwrap(a) == unwrap(b);
    }
    private static Class<?> unwrap(Class<?> a){
        if(a == Double.class)
            return double.class;
        else if(a == Long.class)
            return long.class;
        else if(a == Integer.class)
            return int.class;
        else if(a == Float.class)
            return float.class;
        return a;
        
    }
    
}
