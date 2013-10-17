package ch.eiafr.cojac.instrumenters;

import static ch.eiafr.cojac.instrumenters.InvokableMethod.COJAC_FLOAT_WRAPPER_TYPE;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import ch.eiafr.cojac.models.FloatWrapper;

public final class InvokableMethod {
    //private static final String RFL=Type.getType(FloatWrapper.class).getDescriptor();
    //public static final String FLOAT_WRAPPER = "ch/eiafr/cojac/models/FloatWrapper"; 

    //public static final Type REPLACEMENT_FLOAT_TYPE=Type.getType(FloatWrapper.class);
    
    public static final String COJAC_FLOAT_WRAPPER_INTERNAL_NAME = Type.getInternalName(FloatWrapper.class);
    public static final Type   COJAC_FLOAT_WRAPPER_TYPE = Type.getType(FloatWrapper.class);
    public static final String COJAC_FLOAT_WRAPPER_TYPE_DESCR = COJAC_FLOAT_WRAPPER_TYPE.getDescriptor();
    public static final String REPLACED_FROM_FLOAT   = "(F)"+COJAC_FLOAT_WRAPPER_TYPE_DESCR;
    public static final InvokableMethod FROM_FLOAT = new InvokableMethod(COJAC_FLOAT_WRAPPER_INTERNAL_NAME, "fromFloat", REPLACED_FROM_FLOAT);
    //TODO the wrapper could be given as a Cojac parameter

    //-----------------------------
    
    final String classPath;
    final String method;
    final String signature;

    InvokableMethod(String classPath, String method, String signature) {
        super();

        this.classPath = classPath;
        this.method = method;
        this.signature = signature;
    }
    
    public void invokeStatic(MethodVisitor mv) {
        mv.visitMethodInsn(INVOKESTATIC, classPath, method, signature);
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

    public static Type afterFloatReplacement(Type myType) {
        if (myType.getSort()==Type.FLOAT)
            return COJAC_FLOAT_WRAPPER_TYPE;
        return myType;
    }
    
    public static String afterFloatReplacement(String typeDescr) {
        return afterFloatReplacement(Type.getType(typeDescr)).getDescriptor();
    }


    
}