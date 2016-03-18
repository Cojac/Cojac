package com.github.cojac.models;
import java.lang.Math;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
public class MathMethods {
    private static Map<Class<?>, String> types = new HashMap<Class<?>, String>();
    static{
        types.put(int.class, "I");
        types.put(float.class, "F");
        types.put(double.class, "D");
        types.put(long.class, "J");
        types.put(int.class, "I");
    }
    public static ArrayList<Operation> operations = new ArrayList<Operation>();
    static{
        Method[] methods = Math.class.getMethods();
        for(Method method:methods){
            int modifiers = method.getModifiers();
            if(Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers)){
                String signature ="";
                //System.out.print("Name: "+method.getName()+" return: "++ "  types: {");
                signature += "(";
                for (Type c: method.getParameterTypes()) {
                    signature += types.get(c);
                }
                signature += ")";
                signature += types.get(method.getReturnType());
                operations.add(new Operation(0,method.getName(), signature, method.getParameterTypes()));
            }
        }
    }
}
