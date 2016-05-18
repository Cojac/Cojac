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

import org.objectweb.asm.Opcodes;
import static com.github.cojac.models.Signatures.*;
import static com.github.cojac.models.Parameters.*;

/**
 * Enumeration of the constant-transforming methods, with the type they are associed to. 
 * @author Gazzola Valentin
 *
 */
public enum ConstTransform {
    doubleTransform(new Operation(Opcodes.INVOKESTATIC,"fromUninstrumentedDouble", DOUBLE_UNARY.description,
            DOUBLE_UNARY_PARAMS.params), double.class),
    floatTransform(new Operation(Opcodes.INVOKESTATIC,"fromUninstrumentedFloat", FLOAT_UNARY.description,
            FLOAT_BINARY_PARAMS.params), float.class),
    intTransform(new Operation(Opcodes.INVOKESTATIC,"fromUninstrumentedInt", INTEGER_UNARY.description,
            INTEGER_BINARY_PARAMS.params), int.class),
    longTransform(new Operation(Opcodes.INVOKESTATIC,"fromUninstrumentedLong", LONG_UNARY.description,
            LONG_BINARY_PARAMS.params), long.class);
    public final Operation operation; //method called for each constant transformation
    public final Class<?> constType; //type of the constant
    private ConstTransform(Operation operation, Class<?> constType) {
        this.operation = operation;
        this.constType = constType;
    }
    /**
     * get the method definition for a given type
     * @param c type of the constant
     * @return method to call for the type, may be null if unsupported type.
     */
    public static ConstTransform getMethodForClass(Class<?> c){
        for(ConstTransform ct: ConstTransform.values()){
            if(unwrap(ct.constType) == unwrap(c))
                return ct;
        }
        return null;
    }
    /**
     * Unwrapping method for primitive types (for comparison sake)
     * @param a the (possibly wrapped) type
     * @return the unwrapper type, if type supported.
     */
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
