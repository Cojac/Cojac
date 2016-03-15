package com.github.cojac.models;
import static org.objectweb.asm.Opcodes.*;
public class Operations {
    private static final String INTEGER_BINARY = "(II)I";
    private static final String INTEGER_UNARY = "(I)I";
    
    private static final String LONG_BINARY = "(JJ)J";
    private static final String LONG_UNARY = "(J)J";
    
    private static final String DOUBLE_BINARY = "(DD)D";
    private static final String DOUBLE_UNARY = "(D)D";
    
    private static final String FLOAT_BINARY = "(FF)F";
    private static final String FLOAT_UNARY = "(F)F";
    
    
    public static final Operation[] operations={
            new Operation(IADD,"IADD",INTEGER_BINARY),
            new Operation(ISUB,"ISUB",INTEGER_BINARY),
            new Operation(IMUL,"IMUL",INTEGER_BINARY),
            new Operation(IDIV,"IDIV",INTEGER_BINARY),
            
            new Operation(INEG,"INEG",INTEGER_UNARY),
            new Operation(IINC,"IINC",INTEGER_UNARY),
            
            
            
    };
    
}
