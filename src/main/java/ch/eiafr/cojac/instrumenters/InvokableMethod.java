package ch.eiafr.cojac.instrumenters;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

// TODO - Take user implementation of wrapper with an option of the agent
//import com.monnard.utils.FloatWrapper;
//import com.monnard.utils.DoubleWrapper;
import ch.eiafr.cojac.models.FloatWrapper;
import ch.eiafr.cojac.models.DoubleWrapper;
import org.objectweb.asm.Opcodes;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;


public final class InvokableMethod {

    public static final String COJAC_DOUBLE_WRAPPER_INTERNAL_NAME = Type.getInternalName(DoubleWrapper.class);
    public static final Type   COJAC_DOUBLE_WRAPPER_TYPE = Type.getType(DoubleWrapper.class);
    public static final String COJAC_DOUBLE_WRAPPER_TYPE_DESCR = COJAC_DOUBLE_WRAPPER_TYPE.getDescriptor();
    public static final String REPLACED_FROM_DOUBLE   = "(D)"+COJAC_DOUBLE_WRAPPER_TYPE_DESCR;
    public static final InvokableMethod FROM_DOUBLE = new InvokableMethod(COJAC_DOUBLE_WRAPPER_INTERNAL_NAME, "fromDouble", REPLACED_FROM_DOUBLE, INVOKESTATIC);
    
    public static final String COJAC_FLOAT_WRAPPER_INTERNAL_NAME = Type.getInternalName(FloatWrapper.class);
    public static final Type   COJAC_FLOAT_WRAPPER_TYPE = Type.getType(FloatWrapper.class);
    public static final String COJAC_FLOAT_WRAPPER_TYPE_DESCR = COJAC_FLOAT_WRAPPER_TYPE.getDescriptor();
    public static final String REPLACED_FROM_FLOAT   = "(F)"+COJAC_FLOAT_WRAPPER_TYPE_DESCR;
    public static final InvokableMethod FROM_FLOAT = new InvokableMethod(COJAC_FLOAT_WRAPPER_INTERNAL_NAME, "fromFloat", REPLACED_FROM_FLOAT, INVOKESTATIC);
    
    
    //TODO the wrapper could be given as a Cojac parameter

    //-----------------------------
    
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