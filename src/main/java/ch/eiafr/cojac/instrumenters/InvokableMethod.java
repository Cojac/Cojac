package ch.eiafr.cojac.instrumenters;

import static ch.eiafr.cojac.models.FloatReplacerClasses.*;
import org.objectweb.asm.MethodVisitor;
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
    
	// TODO - is this needed?
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
	
    public static String afterFloatReplacement(String typeDescr) {
        return afterFloatReplacement(Type.getType(typeDescr)).getDescriptor();
    }
    
}