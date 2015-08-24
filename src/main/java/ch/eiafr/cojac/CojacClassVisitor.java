/*
 * *
 *    Copyright 2011-2014 Baptiste Wicht, Frédéric Bapst & Romain Monnard
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

import static ch.eiafr.cojac.models.FloatReplacerClasses.*;
import ch.eiafr.cojac.instrumenters.IOpcodeInstrumenterFactory;
import static ch.eiafr.cojac.instrumenters.InvokableMethod.replaceFloatMethodDescription;
import ch.eiafr.cojac.instrumenters.ReplaceFloatsMethods;
import java.util.ArrayList;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AnalyzerAdapter;
import org.objectweb.asm.commons.LocalVariablesSorter;

final class CojacClassVisitor extends ClassVisitor {
    private final IOpcodeInstrumenterFactory factory;
    private final InstrumentationStats stats;
    private final Args args;
    
	private final CojacReferences references;
	
    private String crtClassName;
    
    private final CojacAnnotationVisitor cav;
    
    private ArrayList<String> proxyMethods;
	
    private FloatProxyMethod fpm;

    CojacClassVisitor(ClassVisitor cv, CojacReferences references, CojacAnnotationVisitor cav) {
		super(Opcodes.ASM5, cv);

		this.references = references;
        this.stats = references.getStats();
        this.args = references.getArgs();
        this.factory = references.getOpCodeInstrumenterFactory();
        this.cav = cav;
        proxyMethods = new ArrayList<>();    
    }

    @Override
    public void visit(int version, int access, String name, String signature, String supername, String[] interfaces) {
        cv.visit(version, access, name, signature, supername, interfaces);

        crtClassName = name;
        fpm = new FloatProxyMethod(this, crtClassName);
    }

    public boolean isProxyMethod(String name, String desc){
        return proxyMethods.contains(name+"_"+desc);
    }
    
    public MethodVisitor addProxyMethod(int access, String name, String desc, String signature, String[] exceptions){
        proxyMethods.add(name+"_"+desc);
        return cv.visitMethod(access, name, desc, signature, exceptions);
    }
    
    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        String oldDesc = desc;
        if (args.isSpecified(Arg.REPLACE_FLOATS)) {
            desc=replaceFloatMethodDescription(desc);   
        }
		
        boolean isNative = (access & Opcodes.ACC_NATIVE) > 0;
		//boolean isAbstrac = (access & Opcodes.ACC_ABSTRACT) > 0;
		//boolean isInterface = (access & Opcodes.ACC_INTERFACE) > 0;
       
        if(isNative && desc.equals(oldDesc) == false){
			/*  If the native method has not the same descriptor, create a 
			     method to transform types and call the good native method. */
            cv.visitMethod(access, name, oldDesc, signature, exceptions);
            fpm.nativeCall(null, access, crtClassName, name, oldDesc);
            return null;
        }
        
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);

        String currentMethodID = crtClassName + '/' + name;

        if (cav.isClassAnnotated() || cav.isMethodAnnotated(currentMethodID)) {
            return mv;
        }
                
        mv.visitEnd();
        
        return instrumentMethod(mv, access, desc, name);
    }

    private MethodVisitor instrumentMethod(MethodVisitor parentMv, int access, String desc, String name) {
        MethodVisitor mv;
        if (args.isSpecified(Arg.REPLACE_FLOATS)){
			// Create the MethodVisitor delegation chain: FloatReplacerMethodVisitor -> LocalVariableSorter -> AnalyzerAdapter -> parentMv
            AnalyzerAdapter aa = new AnalyzerAdapter(crtClassName, access, name, desc, parentMv);
            LocalVariablesSorter lvs = new FloatVariablesSorter(access, desc, aa);
            ReplaceFloatsMethods rfm = new ReplaceFloatsMethods(fpm, crtClassName, references);
            mv = new FloatReplacerMethodVisitor(access, desc, aa, lvs, rfm, stats, args, crtClassName, factory, references);
        } else {
            mv = new CojacCheckerMethodVisitor(access, desc, parentMv, stats, args, crtClassName, factory);
        }
        mv.visitEnd();

        return mv;
    }


    @Override
    public FieldVisitor visitField(int accessFlags, String fieldName, String fieldType, String genericSignature, Object initValStatic) {
        if (args.isSpecified(Arg.REPLACE_FLOATS)) {
			
            if (fieldType.equals("F")) {
                return super.visitField(accessFlags, fieldName, COJAC_FLOAT_WRAPPER_TYPE_DESCR, genericSignature, null);
            }
            if (fieldType.equals("D")) {
                return super.visitField(accessFlags, fieldName, COJAC_DOUBLE_WRAPPER_TYPE_DESCR, genericSignature, null);
            }
            
            if(fieldType.equals(Type.getType(Float.class).getDescriptor())){
                return super.visitField(accessFlags, fieldName, COJAC_FLOAT_WRAPPER_TYPE_DESCR, genericSignature, null);
            }
            if(fieldType.equals(Type.getType(Double.class).getDescriptor())){
                return super.visitField(accessFlags, fieldName, COJAC_DOUBLE_WRAPPER_TYPE_DESCR, genericSignature, null);
            }
            
            Type type = Type.getType(fieldType);
            if(type.getSort() == Type.ARRAY){
                if(type.getElementType().equals(Type.FLOAT_TYPE)){
                    String desc = "";
                    for(int i=0 ; i <type.getDimensions() ; i++){
                        desc += "[";
                    }
                    desc += COJAC_FLOAT_WRAPPER_TYPE_DESCR;
                    return super.visitField(accessFlags, fieldName, desc, genericSignature, null);
                }
                if(type.getElementType().equals(Type.DOUBLE_TYPE)){
                    String desc = "";
                    for(int i=0 ; i <type.getDimensions() ; i++){
                        desc += "[";
                    }
                    desc += COJAC_DOUBLE_WRAPPER_TYPE_DESCR;
                    return super.visitField(accessFlags, fieldName, desc, genericSignature, null);
                }
            }
            
            
        }
        return super.visitField(accessFlags, fieldName, fieldType, genericSignature, initValStatic);
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