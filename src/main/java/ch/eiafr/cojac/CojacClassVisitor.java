/*
 * *
 *    Copyright 2011 Baptiste Wicht & Frédéric Bapst
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

package ch.eiafr.cojac;

import ch.eiafr.cojac.instrumenters.IOpcodeInstrumenterFactory;
import ch.eiafr.cojac.methods.CojacMethodAdder;
import ch.eiafr.cojac.reactions.IReaction;


//import org.objectweb.asm.AnnotationVisitor;
//import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import static ch.eiafr.cojac.instrumenters.InvokableMethod.*;

final class CojacClassVisitor extends ClassVisitor {
    private final CojacMethodAdder methodAdder;
    private final IOpcodeInstrumenterFactory factory;
    private final InstrumentationStats stats;
    private final Args args;
    private final Methods methods;
    private final IReaction reaction;

    private boolean first = true;
    private String classPath;
    
    //public static final String COJAC_FIELD_SUFFIX = "_C$O$J$A$C";
    
    private CojacAnnotationVisitor cav;

    CojacClassVisitor(ClassVisitor cv, InstrumentationStats stats, Args args, Methods methods, IReaction reaction, IOpcodeInstrumenterFactory factory, CojacAnnotationVisitor cav) {
        super(Opcodes.ASM4, cv);

        this.stats = stats;
        this.args = args;
        this.methods = methods;
        this.reaction = reaction;
        this.factory = factory;
        this.cav = cav;
        methodAdder = methods != null ? new CojacMethodAdder(args, reaction) : null;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String supername, String[] interfaces) {
        cv.visit(version, access, name, signature, supername, interfaces);

        classPath = name;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if (args.isSpecified(Arg.REPLACE_FLOATS)) {
            desc=replaceFloatMethodDescription(desc);   
        }
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);

        String currentMethodID = classPath + '/' + name;

        if (cav.isClassAnnotated() || cav.isMethodAnnotated(currentMethodID)) {
            return mv;
        }

        if (methods != null && first && "<init>".equals(name)) {
            first = false;

            methodAdder.insertMethods(cv, methods, classPath);
        }

        mv.visitEnd();

        return instrumentMethod(mv, access, desc);
    }

    //        if (args.isSpecified(Arg.REPLACE_FLOATS))
    //          desc=replaceFloatMethodDescription(desc);

    private MethodVisitor instrumentMethod(MethodVisitor parentMv, int access, String desc) {
        MethodVisitor mv = null;
        if (args.isSpecified(Arg.REPLACE_FLOATS))
            mv = new FloatReplacerMethodVisitor(access, desc, parentMv, stats, args, methods, reaction, classPath, factory);
        else 
            mv = new CojacCheckerMethodVisitor(access, desc, parentMv, stats, args, methods, reaction, classPath, factory);

        mv.visitEnd();

        return mv;
    }


    @Override
    public FieldVisitor visitField(int accessFlags, String fieldName, String fieldType, String genericSignature, Object initValStatic) {
        if (args.isSpecified(Arg.REPLACE_FLOATS)) {
            if (fieldType.equals("F")) {
                //TODO correcty handle initial float initialization for static fields
                return super.visitField(accessFlags, fieldName, COJAC_FLOAT_WRAPPER_INTERNAL_NAME, genericSignature, null);
                //FieldVisitor fv=cv.visitField(accessFlags, fieldName+COJAC_FIELD_SUFFIX, COJAC_FLOAT_WRAPPER, genericSignature, initValStatic);
                //if (fv!=null) fv.visitEnd();
                // ...if we want to replace field (instead of adding a new one...)
            }
        }
        return super.visitField(accessFlags, fieldName, fieldType, genericSignature, initValStatic);
//        FieldVisitor fv = cv.visitField(accessFlags, fieldName, fieldType, genericSignature, initValStatic);
//        return instrumentField(fv, accessFlags, fieldName, fieldType, genericSignature, initValStatic);
    }

//    private FieldVisitor instrumentField(FieldVisitor parentFv, int arg0, String arg1, String arg2, String arg3, Object arg4) {
//        FieldVisitor fv = new CojacFieldVisitor(parentFv, arg0, arg1, arg2, arg3, arg4);
//        fv.visitEnd();
//        return fv;
//    }

    
//  @Override
//  public AnnotationVisitor visitAnnotation(String arg0, boolean arg1) {
//      return super.visitAnnotation(arg0, arg1);
//  }

//  @Override
//  public void visitAttribute(Attribute arg0) {
//      super.visitAttribute(arg0);
//  }

//  @Override
//  public void visitEnd() {
//      super.visitEnd();
//  }
//
//    @Override
//    public void visitInnerClass(String arg0, String arg1, String arg2, int arg3) {
//        super.visitInnerClass(arg0, arg1, arg2, arg3);
//    }
//
//    @Override
//    public void visitOuterClass(String arg0, String arg1, String arg2) {
//        super.visitOuterClass(arg0, arg1, arg2);
//    }
//
//    @Override
//    public void visitSource(String arg0, String arg1) {
//        super.visitSource(arg0, arg1);
//    }
    

}