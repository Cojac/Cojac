/*
 * *
 *    Copyright 2011-2016 Valentin Gazzola & Frédéric Bapst
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
package com.github.cojac.models;
import  org.objectweb.asm.Opcodes; //for opcode real numeric values
import static com.github.cojac.models.Parameters.*;
import static com.github.cojac.models.Signatures.*;
/**
 * Represents the enumeration of opcode which can be instrumented
 * 
 * @author Valentin
 *
 */
public enum Operations {
            /*INTEGERS*/
            IADD(Opcodes.IADD,INTEGER_BINARY.description, INTEGER_BINARY_PARAMS.params),
            ISUB(Opcodes.ISUB,INTEGER_BINARY.description, INTEGER_BINARY_PARAMS.params),
            IMUL(Opcodes.IMUL,INTEGER_BINARY.description, INTEGER_BINARY_PARAMS.params),
            IDIV(Opcodes.IDIV,INTEGER_BINARY.description, INTEGER_BINARY_PARAMS.params),
            
            INEG(Opcodes.INEG,INTEGER_UNARY.description, INTEGER_UNARY_PARAMS.params),
            IINC(Opcodes.IINC,INTEGER_BINARY.description, INTEGER_BINARY_PARAMS.params),
            ICONST_M1(Opcodes.ICONST_M1, INTEGER_VOID.description, VOID_PARAM.params, true, int.class),
            ICONST_0(Opcodes.ICONST_0, INTEGER_VOID.description, VOID_PARAM.params, true, int.class ),
            ICONST_1(Opcodes.ICONST_1, INTEGER_VOID.description, VOID_PARAM.params, true, int.class ),
            ICONST_2(Opcodes.ICONST_2, INTEGER_VOID.description, VOID_PARAM.params, true, int.class ),
            ICONST_3(Opcodes.ICONST_3, INTEGER_VOID.description, VOID_PARAM.params, true, int.class ),
            ICONST_4(Opcodes.ICONST_4, INTEGER_VOID.description, VOID_PARAM.params, true, int.class ),
            ICONST_5(Opcodes.ICONST_5, INTEGER_VOID.description, VOID_PARAM.params, true, int.class ),
            
            /*LONGS*/
            LADD(Opcodes.LADD,LONG_BINARY.description, LONG_BINARY_PARAMS.params),
            LSUB(Opcodes.LSUB,LONG_BINARY.description, LONG_BINARY_PARAMS.params),
            LMUL(Opcodes.LMUL,LONG_BINARY.description, LONG_BINARY_PARAMS.params),
            LDIV(Opcodes.LDIV,LONG_BINARY.description, LONG_BINARY_PARAMS.params),
            
            LNEG(Opcodes.LNEG,LONG_UNARY.description, LONG_UNARY_PARAMS.params),
            LCONST_0(Opcodes.LCONST_0, LONG_VOID.description, VOID_PARAM.params, true, int.class),
            LCONST_1(Opcodes.LCONST_1, LONG_VOID.description, VOID_PARAM.params, true, int.class),
            
            /*FLOATS*/
            FADD(Opcodes.FADD,FLOAT_BINARY.description, FLOAT_BINARY_PARAMS.params),
            FSUB(Opcodes.FSUB,FLOAT_BINARY.description, FLOAT_BINARY_PARAMS.params),
            FMUL(Opcodes.FMUL,FLOAT_BINARY.description, FLOAT_BINARY_PARAMS.params),
            FDIV(Opcodes.FDIV,FLOAT_BINARY.description, FLOAT_BINARY_PARAMS.params),
            FREM(Opcodes.FREM,FLOAT_BINARY.description, FLOAT_BINARY_PARAMS.params),
            FCMPL(Opcodes.FCMPL,FLOAT_CMP.description, FLOAT_BINARY_PARAMS.params),//  -1 If NaN
            FCMPG(Opcodes.FCMPG,FLOAT_CMP.description, FLOAT_BINARY_PARAMS.params),//  1 if NaN
            
            FNEG(Opcodes.FNEG,FLOAT_UNARY.description, FLOAT_UNARY_PARAMS.params),
            FCONST_0(Opcodes.FCONST_0, FLOAT_VOID.description, VOID_PARAM.params, true, int.class),
            FCONST_1(Opcodes.FCONST_1, FLOAT_VOID.description, VOID_PARAM.params, true, int.class),
            FCONST_2(Opcodes.FCONST_2, FLOAT_VOID.description, VOID_PARAM.params, true, int.class),
            
            /*DOUBLES*/
            DADD(Opcodes.DADD,DOUBLE_BINARY.description, DOUBLE_BINARY_PARAMS.params),
            DSUB(Opcodes.DSUB,DOUBLE_BINARY.description, DOUBLE_BINARY_PARAMS.params),
            DMUL(Opcodes.DMUL,DOUBLE_BINARY.description, DOUBLE_BINARY_PARAMS.params),
            DDIV(Opcodes.DDIV,DOUBLE_BINARY.description, DOUBLE_BINARY_PARAMS.params),
            DREM(Opcodes.DREM,DOUBLE_BINARY.description, DOUBLE_BINARY_PARAMS.params),
            DCMPL(Opcodes.DCMPL,DOUBLE_CMP.description, DOUBLE_BINARY_PARAMS.params),//  -1 If NaN
            DCMPG(Opcodes.DCMPG,DOUBLE_CMP.description, DOUBLE_BINARY_PARAMS.params),//  1 if NaN
            
            DNEG(Opcodes.DNEG,DOUBLE_UNARY.description, DOUBLE_UNARY_PARAMS.params),
            DCONST_0(Opcodes.DCONST_0, DOUBLE_VOID.description, VOID_PARAM.params, true, int.class),
            DCONST_1(Opcodes.DCONST_1, DOUBLE_VOID.description, VOID_PARAM.params, true, int.class),
            
            /*CASTING*/
            
            /*INT TO ...*/
            I2S(Opcodes.I2S,I2S_CAST.description, INTEGER_UNARY_PARAMS.params),
            I2C(Opcodes.I2C,I2C_CAST.description, INTEGER_UNARY_PARAMS.params),
            I2B(Opcodes.I2B,I2B_CAST.description, INTEGER_UNARY_PARAMS.params),
            I2F(Opcodes.I2F,I2F_CAST.description, INTEGER_UNARY_PARAMS.params),
            I2L(Opcodes.I2L,I2L_CAST.description, INTEGER_UNARY_PARAMS.params),
            I2D(Opcodes.I2D,I2D_CAST.description, INTEGER_UNARY_PARAMS.params),
            
            /*LONG TO ...*/
            L2I(Opcodes.L2I,L2I_CAST.description, LONG_UNARY_PARAMS.params),
            L2F(Opcodes.L2F,L2F_CAST.description, LONG_UNARY_PARAMS.params),
            L2D(Opcodes.L2D,L2D_CAST.description, LONG_UNARY_PARAMS.params),
            
            /*FLOAT TO ...*/
            F2I(Opcodes.F2I,F2I_CAST.description, FLOAT_UNARY_PARAMS.params),
            F2D(Opcodes.F2D,F2D_CAST.description, FLOAT_UNARY_PARAMS.params),
            F2L(Opcodes.F2L,F2L_CAST.description, FLOAT_UNARY_PARAMS.params),
            
            /*DOUBLE TO ...*/
            D2F(Opcodes.D2F,D2F_CAST.description, DOUBLE_UNARY_PARAMS.params),
            D2I(Opcodes.D2I,D2I_CAST.description, DOUBLE_UNARY_PARAMS.params),
            D2L(Opcodes.D2L,D2L_CAST.description, DOUBLE_UNARY_PARAMS.params);

    
    
    public final int opCodeVal;
    //public final String opCodeName;
    public final String signature;
    public final Class<?>[] parameters;
    public final boolean loadsConst;
    public final Class<?> returnType;
    
    /**
     * Represents an opcode operation
     * 
     * @param opCodeVal the value of the opcode (specified by the JVM)
     * @param signature (A String signature described in @see com.github.cojac.models.Signatures
     * @param parameters (A Class<?> array representing the classes of the parameters @see com.github.cojac.models.Parameters
     */
    private Operations(int opCodeVal, String signature, Class<?>[] parameters ) {
       this.opCodeVal=opCodeVal;
       //this.opCodeName=opCodeName;
       this.signature = signature;
       this.parameters = parameters;
       this.loadsConst = false;
       this.returnType = void.class;
    }
    /**
     * Represents an opcode operation
     * 
     * @param opCodeVal the value of the opcode (specified by the JVM)
     * @param signature A String signature described in @see com.github.cojac.models.Signatures
     * @param parameters A Class<?> array representing the classes of the parameters @see com.github.cojac.models.Parameters
     * @param loadsConst Does it load a constant
     */
    private Operations(int opCodeVal, String signature, Class<?>[] parameters , boolean loadsConst, Class<?> returnType) {
       this.opCodeVal=opCodeVal;
       //this.opCodeName=opCodeName;
       this.signature = signature;
       this.parameters = parameters;
       this.loadsConst = loadsConst;
       this.returnType = returnType;
    }
    public static Class<?> getReturnType(int OpCode){
        for (Operations op : Operations.values()) {
            if(op.opCodeVal==OpCode)
                return op.returnType;
        }
        return void.class;
    }
    public static Operations[] getLoadConstOp(Class<?> returnType){
        Operations[] ops = new Operations[countLoadConstOp(returnType)];
        int i=0;
        for(Operations op: Operations.values()){
            if(op.loadsConst && op.returnType==returnType)
                ops[i++]=op;
        }
        return ops;
    }
    private static int countLoadConstOp(Class<?> returnType){
        int nb = 0;
        for(Operations op: Operations.values()){
            if(op.loadsConst && op.returnType==returnType)
                ++nb;
        }
        return nb;
    }
}
