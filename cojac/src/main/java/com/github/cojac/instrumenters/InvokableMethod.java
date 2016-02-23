/*
 * *
 *    Copyright 2014 Romain Monnard & Frédéric Bapst
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

package com.github.cojac.instrumenters;

import org.objectweb.asm.MethodVisitor;

import static com.github.cojac.models.FloatReplacerClasses.*;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import org.objectweb.asm.Type;


public final class InvokableMethod {
    
    final String classPath;
    final String method;
    final String signature;
    final int opCode;

    InvokableMethod(String classPath, String method, String signature, int opCode) {
        super();

        this.classPath = classPath;
        this.method = method;
        this.signature = signature;
        this.opCode = opCode;
    }
    
    InvokableMethod(String classPath, String method, String signature) {
        super();

        this.classPath = classPath;
        this.method = method;
        this.signature = signature;
        this.opCode = INVOKESTATIC;
    }
    
    public void invokeStatic(MethodVisitor mv) {
        mv.visitMethodInsn(INVOKESTATIC, classPath, method, signature, false);
    }
    
    public void invoke(MethodVisitor mv) {
        mv.visitMethodInsn(opCode, classPath, method, signature, (opCode == INVOKEINTERFACE));
    }
    
    // ---------------------------
    
    public static String replaceFloatMethodDescription(String desc) {
        Type[] arguments = Type.getArgumentTypes(desc);
        Type returnType = Type.getReturnType(desc);
        
        Type[] after = new Type[arguments.length];
        for(int i=0; i<arguments.length; i++)
            after[i]=afterFloatReplacement(arguments[i]);
        Type returnAfter = afterFloatReplacement(returnType);
        return Type.getMethodDescriptor(returnAfter, after);
    }

    /** Float|float|Double|double [array] -> CojacWrapper */
    public static Type afterFloatReplacement(Type type){
        if(type.equals(Type.getType(Float.class)))
            return COJAC_FLOAT_WRAPPER_TYPE;
        if(type.equals(Type.FLOAT_TYPE))
            return COJAC_FLOAT_WRAPPER_TYPE;
        if(type.equals(Type.getType(Double.class)))
            return COJAC_DOUBLE_WRAPPER_TYPE;
        if(type.equals(Type.DOUBLE_TYPE))
            return COJAC_DOUBLE_WRAPPER_TYPE;
        if(type.getSort() == Type.ARRAY){
            Type newType = afterFloatReplacement(type.getElementType());
            if(type.equals(newType))
                return type;
            String desc = "";
            for(int i=0 ; i <type.getDimensions() ; i++)
                desc += "[";
            desc += newType.getDescriptor();
            return Type.getType(desc);
        }
        return type; 
    }
	
    /** Float|float|Double|double [array] -> CojacWrapper */
    public static String afterFloatReplacement(String typeDescr) {
        return afterFloatReplacement(Type.getType(typeDescr)).getDescriptor();
    }
    
}