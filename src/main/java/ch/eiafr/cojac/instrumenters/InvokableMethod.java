package ch.eiafr.cojac.instrumenters;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import ch.eiafr.cojac.models.FloatWrapper;
import ch.eiafr.cojac.models.DoubleWrapper;

public final class InvokableMethod {

    public static final String COJAC_DOUBLE_WRAPPER_INTERNAL_NAME = Type.getInternalName(DoubleWrapper.class);
    public static final Type   COJAC_DOUBLE_WRAPPER_TYPE = Type.getType(DoubleWrapper.class);
    public static final String COJAC_DOUBLE_WRAPPER_TYPE_DESCR = COJAC_DOUBLE_WRAPPER_TYPE.getDescriptor();
    public static final String REPLACED_FROM_DOUBLE   = "(D)"+COJAC_DOUBLE_WRAPPER_TYPE_DESCR;
    public static final InvokableMethod FROM_DOUBLE = new InvokableMethod(COJAC_DOUBLE_WRAPPER_INTERNAL_NAME, "fromDouble", REPLACED_FROM_DOUBLE);
    
    public static final String COJAC_FLOAT_WRAPPER_INTERNAL_NAME = Type.getInternalName(FloatWrapper.class);
    public static final Type   COJAC_FLOAT_WRAPPER_TYPE = Type.getType(FloatWrapper.class);
    public static final String COJAC_FLOAT_WRAPPER_TYPE_DESCR = COJAC_FLOAT_WRAPPER_TYPE.getDescriptor();
    public static final String REPLACED_FROM_FLOAT   = "(F)"+COJAC_FLOAT_WRAPPER_TYPE_DESCR;
    public static final InvokableMethod FROM_FLOAT = new InvokableMethod(COJAC_FLOAT_WRAPPER_INTERNAL_NAME, "fromFloat", REPLACED_FROM_FLOAT);
    
    
    public static final String COJAC_REPLACE_WRAPPER_TYPE = "COJAC_WRAPPER_TO_REPLACE";
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
    
    public void invokeStatic(MethodVisitor mv, String replacedClassPath, String replacedWrapper) {
        mv.visitMethodInsn(INVOKESTATIC, replacedClassPath, method, signature.replaceAll(COJAC_REPLACE_WRAPPER_TYPE, replacedWrapper));
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
        if (myType.getSort()==Type.DOUBLE)
            return COJAC_DOUBLE_WRAPPER_TYPE;
        return myType;
    }
    
    public static String afterFloatReplacement(String typeDescr) {
        return afterFloatReplacement(Type.getType(typeDescr)).getDescriptor();
    }


    
}