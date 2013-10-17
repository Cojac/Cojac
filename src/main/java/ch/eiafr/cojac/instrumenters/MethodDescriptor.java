package ch.eiafr.cojac.instrumenters;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import ch.eiafr.cojac.models.FloatWrapper;

final class MethodDescriptor {
    private static final String RFL=Type.getType(FloatWrapper.class).getDescriptor();
    public static final String REPLACED_FROM_FLOAT   = "(F)"+RFL;
    private static final String FLOAT_WRAPPER = "ch/eiafr/cojac/models/FloatWrapper";
    
    public final MethodDescriptor FROM_FLOAT = new MethodDescriptor(FLOAT_WRAPPER, "fromFloat", REPLACED_FROM_FLOAT);
    
    //-----------------------------
    
    final String classPath;
    final String method;
    final String signature;

    MethodDescriptor(String classPath, String method, String signature) {
        super();

        this.classPath = classPath;
        this.method = method;
        this.signature = signature;
    }
    
    void invokeStatic(MethodVisitor mv) {
        mv.visitMethodInsn(INVOKESTATIC, classPath, method, signature);
    }

}