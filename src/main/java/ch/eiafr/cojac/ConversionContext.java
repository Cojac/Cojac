package ch.eiafr.cojac;

import static ch.eiafr.cojac.instrumenters.InvokableMethod.afterFloatReplacement;
import static ch.eiafr.cojac.instrumenters.InvokableMethod.replaceFloatMethodDescription;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.Type;

//========================================================================
public class ConversionContext {
    public final int opcode; 
    public final String owner, name, jDesc, cDesc;
    /** with double, Float etc... */
    public final Type[] outArgs;
    /** with DW, FW etc... */
    public final Type[] inArgs;
    public final Map<Integer, Type> typeConversions = new HashMap<>();
    public final Map<Integer, Type> convertedArrays = new HashMap<>();   
    public final boolean shouldObjParamBeConverted;
    
    public ConversionContext(int opcode, String owner, String name, String desc) {
        this.opcode=opcode;
        this.owner=owner;
        this.name=name;
        this.jDesc=desc;
        this.cDesc=replaceFloatMethodDescription(desc);
        outArgs = Type.getArgumentTypes(desc);
        inArgs = typesAfterReplacement(outArgs);
        rememberArrays();
        shouldObjParamBeConverted= (name.equals("printf") &&
                                   owner.equals("java/io/PrintStream"));
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