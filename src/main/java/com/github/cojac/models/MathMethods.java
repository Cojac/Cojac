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
import java.lang.Math;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.Opcodes;
public class MathMethods {
    private static Map<Class<?>, String> types = new HashMap<Class<?>, String>();
    static{
        types.put(int.class, "I");
        types.put(float.class, "F");
        types.put(double.class, "D");
        types.put(long.class, "J");
        types.put(short.class, "S");
        types.put(char.class, "C");
        types.put(byte.class, "B");
        types.put(String.class, "Ljava/lang/String;");
    }
    public static ArrayList<Operation> operations = new ArrayList<Operation>();
    static{
        Method[] methods = Math.class.getMethods();//could be a generic class
        for(Method method:methods){
            int modifiers = method.getModifiers();
            if(Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers)){
                operations.add(toStaticOperation(method));
            }
        }
    }
    
    public static Operation toStaticOperation(Method m){   
        String signature ="";
        
        signature += "(";
        for (Type c: m.getParameterTypes()) {
            signature += types.get(c);
        }
        signature += ")";
        signature += types.get(m.getReturnType());
        //System.out.println("Name: "+method.getName()+" signature: " + signature);
       return new Operation(Opcodes.INVOKESTATIC,m.getName(), signature, m.getParameterTypes());
        
    }
    
}
