package com.github.cojac.instrumenters;

import static com.github.cojac.instrumenters.InvokableMethod.afterFloatReplacement;
import static com.github.cojac.instrumenters.InvokableMethod.replaceFloatMethodDescription;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.Type;

//========================================================================
public class ConversionContext {
    private static final Set<String> methodsWithDisguisedObjPrm= new HashSet<>();
    public final int opcode; 
    public final String owner, name, jDesc, cDesc;
    /** with double, Float etc... */
    public final Type[] outArgs;
    /** with DW, FW etc... */
    public final Type[] inArgs;
    public final Map<Integer, Type> typeConversions = new HashMap<>();
    public final Map<Integer, Type> convertedArrays = new HashMap<>();   
    public final boolean shouldObjParamBeConverted;
    
    static {
        /* Those methods declare Object/Object[] parameters while relying
         * strongly on some instances being Float/Double
         * Maybe consider adding the description (parameter types) to the string
         * if needed (here we take overloaded methods as a group)
         */
        
        methodsWithDisguisedObjPrm.add("java/io/PrintStream/printf");
        methodsWithDisguisedObjPrm.add("java/io/PrintWriter/printf");
        methodsWithDisguisedObjPrm.add("java/io/Console/printf");
        methodsWithDisguisedObjPrm.add("java/lang/String/format");
        methodsWithDisguisedObjPrm.add("java/util/Formatter/format");
        methodsWithDisguisedObjPrm.add("java/text/Format/format");
        methodsWithDisguisedObjPrm.add("java/text/MessageFormat/format");
        methodsWithDisguisedObjPrm.add("java/text/NumberFormat/format");
    }
    
    public ConversionContext(int opcode, String owner, String name, String desc) {
        this.opcode=opcode;
        this.owner=owner;
        this.name=name;
        this.jDesc=desc;
        this.cDesc=replaceFloatMethodDescription(desc);
        outArgs = Type.getArgumentTypes(desc);
        inArgs = typesAfterReplacement(outArgs);
        rememberArrays();
        String m=owner+"/"+name;
        shouldObjParamBeConverted = methodsWithDisguisedObjPrm.contains(m);
    }
    
    public boolean needsConversion(int i) {
        return typeConversions.containsKey(i);
    }
    
    public Type asOriginalJavaType(int i) {
        return outArgs[i];
    }

    private Type[] typesAfterReplacement(Type[] javArgs) {
        Type cojArgs[] = new Type[javArgs.length];
        for (int i = 0; i < javArgs.length; i++) {
            cojArgs[i] = afterFloatReplacement(javArgs[i]);
            if(cojArgs[i].equals(javArgs[i]) == false){
                typeConversions.put(i, javArgs[i]);
            }
        }
        return cojArgs;
    }
    
    private void rememberArrays() {
        // If it is an array, keep both arrays references (cojac & original)
        for (int i=0 ; i < inArgs.length; i++) {
            if(inArgs[i].getSort() == Type.ARRAY) { 
                convertedArrays.put(i, inArgs[i]);
            }
        }
    }
    
    public boolean hasReturn() {
        return !Type.getReturnType(jDesc).equals(Type.VOID_TYPE);
    }
    
    public boolean hasPrimitiveResultInCojacVersion() {
        return hasReturn() && 
                Type.getReturnType(cDesc).getSort() != Type.ARRAY && 
                Type.getReturnType(cDesc).getSort() != Type.OBJECT;
    }
}
//========================================================================