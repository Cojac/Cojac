package ch.eiafr.cojac;

import org.objectweb.asm.Attribute;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;

public class CojacFieldVisitor extends FieldVisitor {
    // TODO: either use this class, or delete it...
    public CojacFieldVisitor(FieldVisitor parentFv, int arg0, String arg1, String arg2, String arg3, Object arg4) {
        super(Opcodes.ASM4, parentFv);
    }


    @Override
    public void visitAttribute(Attribute attr) {
        super.visitAttribute(attr);
    }

//    @Override
//    public void visitEnd() {
//        super.visitEnd();
//    }
//
//    @Override
//    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
//        return super.visitAnnotation(desc, visible);
//    }

}
