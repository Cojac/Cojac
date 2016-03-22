package com.github.cojac.models;
import static org.objectweb.asm.Opcodes.*;
public class Operations {
    private static final String INTEGER_BINARY = "(II)I";
    private static final String INTEGER_UNARY = "(I)I";
   
    private static final String LONG_BINARY = "(JJ)J";
    private static final String LONG_UNARY = "(J)J";
    
    private static final String DOUBLE_BINARY = "(DD)D";
    private static final String DOUBLE_UNARY = "(D)D";
    private static final String DOUBLE_CMP = "(DD)I";
    
    private static final String FLOAT_BINARY = "(FF)F";
    private static final String FLOAT_UNARY = "(F)F";
    private static final String FLOAT_CMP = "(FF)I";
    
    private static final String I2S_CAST = "(I)I";
    private static final String I2C_CAST = "(I)I";
    private static final String I2B_CAST = "(I)I";
    private static final String I2F_CAST = "(I)F";
    private static final String I2L_CAST = "(I)L";
    private static final String I2D_CAST = "(I)D";
    
    private static final String L2I_CAST = "(J)I";
    private static final String L2D_CAST = "(J)D";
    private static final String L2F_CAST = "(J)F";
    
    private static final String F2I_CAST = "(F)I";
    private static final String F2L_CAST = "(F)J";
    private static final String F2D_CAST = "(F)D";
    
    private static final String D2F_CAST = "(D)F";
    private static final String D2I_CAST = "(D)I";
    private static final String D2L_CAST = "(D)J";
    
    private static final Class<?>[] INTEGER_BINARY_PARAMS = {int.class, int.class};
    private static final Class<?>[] INTEGER_UNARY_PARAMS = {int.class};
    private static final Class<?>[] LONG_BINARY_PARAMS = {long.class, long.class};
    private static final Class<?>[] LONG_UNARY_PARAMS = {long.class};
    private static final Class<?>[] FLOAT_BINARY_PARAMS = {float.class, float.class};
    private static final Class<?>[] FLOAT_UNARY_PARAMS = {float.class};
    private static final Class<?>[] DOUBLE_BINARY_PARAMS = {double.class, double.class};
    private static final Class<?>[] DOUBLE_UNARY_PARAMS = {double.class};
   
    
    public static final Operation[] OPERATIONS={
            /*INTEGERS*/
            new Operation(IADD, "IADD",INTEGER_BINARY, INTEGER_BINARY_PARAMS),
            new Operation(ISUB, "ISUB",INTEGER_BINARY, INTEGER_BINARY_PARAMS),
            new Operation(IMUL, "IMUL",INTEGER_BINARY, INTEGER_BINARY_PARAMS),
            new Operation(IDIV, "IDIV",INTEGER_BINARY, INTEGER_BINARY_PARAMS),
            
            new Operation(INEG, "INEG",INTEGER_UNARY, INTEGER_UNARY_PARAMS),
            //new Operation(IINC, "IINC",INTEGER_UNARY, INTEGER_UNARY_PARAMS),
            
            /*LONGS*/
            new Operation(LADD, "LADD",LONG_BINARY, LONG_BINARY_PARAMS),
            new Operation(LSUB, "LSUB",LONG_BINARY, LONG_BINARY_PARAMS),
            new Operation(LMUL, "LMUL",LONG_BINARY, LONG_BINARY_PARAMS),
            new Operation(LDIV, "LDIV",LONG_BINARY, LONG_BINARY_PARAMS),
            
            new Operation(LNEG, "LNEG",LONG_UNARY, LONG_UNARY_PARAMS),
            
            /*FLOATS*/
            new Operation(FADD, "FADD",FLOAT_BINARY, FLOAT_BINARY_PARAMS),
            new Operation(FSUB, "FSUB",FLOAT_BINARY, FLOAT_BINARY_PARAMS),
            new Operation(FMUL, "FMUL",FLOAT_BINARY, FLOAT_BINARY_PARAMS),
            new Operation(FDIV, "FDIV",FLOAT_BINARY, FLOAT_BINARY_PARAMS),
            new Operation(FREM, "FREM",FLOAT_BINARY, FLOAT_BINARY_PARAMS),
            new Operation(FCMPL, "FCMPL",FLOAT_CMP, FLOAT_UNARY_PARAMS),//  -1 If NaN
            new Operation(FCMPG, "FCMPG",FLOAT_CMP, FLOAT_UNARY_PARAMS),//  1 if NaN
            
            new Operation(FNEG, "FNEG",FLOAT_UNARY, FLOAT_BINARY_PARAMS),
            
            /*DOUBLES*/
            new Operation(DADD, "DADD",DOUBLE_BINARY, DOUBLE_BINARY_PARAMS),
            new Operation(DSUB, "DSUB",DOUBLE_BINARY, DOUBLE_BINARY_PARAMS),
            new Operation(DMUL, "DMUL",DOUBLE_BINARY, DOUBLE_BINARY_PARAMS),
            new Operation(DDIV, "DDIV",DOUBLE_BINARY, DOUBLE_BINARY_PARAMS),
            new Operation(DREM, "DREM",DOUBLE_BINARY, DOUBLE_BINARY_PARAMS),
            new Operation(DCMPL, "DCMPL",DOUBLE_CMP, DOUBLE_UNARY_PARAMS),//  -1 If NaN
            new Operation(DCMPG, "DCMPG",DOUBLE_CMP, DOUBLE_UNARY_PARAMS),//  1 if NaN
            
            new Operation(DNEG, "DNEG",DOUBLE_UNARY, DOUBLE_UNARY_PARAMS),
            
            /*CASTING*/
            
            /*INT TO ...*/
            new Operation(I2S, "I2S",I2S_CAST, INTEGER_UNARY_PARAMS),
            new Operation(I2C, "I2C",I2C_CAST, INTEGER_UNARY_PARAMS),
            new Operation(I2B, "I2B",I2B_CAST, INTEGER_UNARY_PARAMS),
            new Operation(I2F, "I2F",I2F_CAST, INTEGER_UNARY_PARAMS),
            new Operation(I2L, "I2L",I2L_CAST, INTEGER_UNARY_PARAMS),
            new Operation(I2D, "I2D",I2D_CAST, INTEGER_UNARY_PARAMS),
            
            /*LONG TO ...*/
            new Operation(L2I, "L2I",L2I_CAST, LONG_UNARY_PARAMS),
            new Operation(L2F, "L2F",L2F_CAST, LONG_UNARY_PARAMS),
            new Operation(L2D, "L2D",L2D_CAST, LONG_UNARY_PARAMS),
            
            /*FLOAT TO ...*/
            new Operation(F2I, "F2I",F2I_CAST, FLOAT_UNARY_PARAMS),
            new Operation(F2D, "F2D",F2D_CAST, FLOAT_UNARY_PARAMS),
            new Operation(F2L, "F2L",F2L_CAST, FLOAT_UNARY_PARAMS),
            
            /*DOUBLE TO ...*/
            new Operation(D2F, "D2F",D2F_CAST, DOUBLE_UNARY_PARAMS),
            new Operation(D2I, "D2I",D2I_CAST, DOUBLE_UNARY_PARAMS),
            new Operation(D2L, "D2L",D2L_CAST, DOUBLE_UNARY_PARAMS)
    };
    
}
