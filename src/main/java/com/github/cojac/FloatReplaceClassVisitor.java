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

package com.github.cojac;

import static com.github.cojac.instrumenters.InvokableMethod.replaceFloatMethodDescription;
import static com.github.cojac.models.FloatReplacerClasses.*;

import java.io.PrintWriter;
import java.util.ArrayList;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AnalyzerAdapter;
import org.objectweb.asm.commons.LocalVariablesSorter;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.TraceMethodVisitor;

import com.github.cojac.instrumenters.FloatProxyMethod;
import com.github.cojac.instrumenters.ReplaceFloatsMethods;
import static com.github.cojac.CojacCommonConstants.ASM_VERSION;

public final class FloatReplaceClassVisitor extends CojacClassVisitor {
    private static final String DUMP_CLASS = null; // "" or "Dump"
    private static final String DUMP_METHOD = "instanceMethod"; // "Dump"

    private final ArrayList<String> proxyMethods;
    private FloatProxyMethod fpm;

    FloatReplaceClassVisitor(ClassVisitor cv, CojacReferences references, CojacAnnotationVisitor cav) {
		super(cv, references, cav);
        proxyMethods = new ArrayList<>();    
    }

    @Override
    public void visit(int version, int access, String name, String signature, String supername, String[] interfaces) {
        super.visit(version, access, name, signature, supername, interfaces);
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
        desc=replaceFloatMethodDescription(desc);   	
        boolean isNative = (access & Opcodes.ACC_NATIVE) > 0;
		//boolean isAbstrac = (access & Opcodes.ACC_ABSTRACT) > 0;
		//boolean isInterface = (access & Opcodes.ACC_INTERFACE) > 0;   
        if(isNative && !desc.equals(oldDesc)){
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
        return instrumentMethod(mv, access, desc, oldDesc, name);
    }

    private MethodVisitor instrumentMethod(MethodVisitor parentMv, int access, String desc, String oldDesc, String name) {
        MethodVisitor mv;
        // Create the MethodVisitor delegation chain: 
        // MyLocalAdder                    adds two local variables
        //  -> FloatReplacerMethodVisitor  main bytecode transformation (with helper class FloatProxyMethod)
        //   -> FloatVariableSorter        remaps parameters index (due to the replacement of double (2 slots) as objects (1 slot))
        //   -> AnalyzerAdapter            keeps track of effective stack, so that we know the type of the top
        //    -> parentMv                  typically the final Writer
        // the last one (parentMv) is typically a MethodWriter
        // With maybe some intermediate TraceMethodVisitor to help debugging...

        if (wantsToDump(crtClassName, name)) { // name.contains("dumpMethod")) { // "dumpMethod"
            parentMv = new TraceMethodVisitor(parentMv, newPrinter("FINAL  "+name+desc));
        }
        AnalyzerAdapter aa = new AnalyzerAdapter(crtClassName, access, name, desc, parentMv);
        FloatVariablesSorter lvs = new FloatVariablesSorter(access, oldDesc, aa);
        MethodVisitor aux=lvs;
        if (wantsToDump(crtClassName, name)) {
            aux = new TraceMethodVisitor(lvs, newPrinter("JUST BEFORE LVS "+name+desc));
        }
        ReplaceFloatsMethods rfm = new ReplaceFloatsMethods(fpm, crtClassName, references);
        FloatReplacerMethodVisitor frmv = new FloatReplacerMethodVisitor(aa, aux, rfm, stats, factory, references);
        MyLocalAdder mla = new MyLocalAdder(access, oldDesc, frmv);
        fpm.setUsefulPartners(aa, mla);
        mv = mla;
        if (wantsToDump(crtClassName, name))
            mv= new TraceMethodVisitor(mv, newPrinter("BEFORE "+name+desc));
        return mv;
    }
    
    private boolean wantsToDump(String className, String methName) {
        if (DUMP_CLASS==null || DUMP_METHOD==null) return false;
        return className.contains(DUMP_CLASS) && methName.contains(DUMP_METHOD);
    }

    private Printer newPrinter(final String s) {
        Printer printer=new ASMifier(ASM_VERSION, "mv", 0) {  // or Textifier(ASM5)
            @Override public void visitMethodEnd() {
                System.out.println("======================== "+s+" ==================");
                PrintWriter p=new PrintWriter(System.out);
                print(p);
                p.flush(); // don't forget that!
            }
        };
        return printer;
    }
    
    @Override
    public FieldVisitor visitField(int accessFlags, String fieldName, String fieldType, String genericSignature, Object initValStatic) {			
        if (fieldType.equals("F")) {
            return super.visitField(accessFlags, fieldName, COJAC_FLOAT_WRAPPER_TYPE_DESCR, genericSignature, null);
        } else if (fieldType.equals("D")) {
            return super.visitField(accessFlags, fieldName, COJAC_DOUBLE_WRAPPER_TYPE_DESCR, genericSignature, null);
        } else if(fieldType.equals(Type.getType(Float.class).getDescriptor())){
            return super.visitField(accessFlags, fieldName, COJAC_FLOAT_WRAPPER_TYPE_DESCR, genericSignature, null);
        } else if(fieldType.equals(Type.getType(Double.class).getDescriptor())){
            return super.visitField(accessFlags, fieldName, COJAC_DOUBLE_WRAPPER_TYPE_DESCR, genericSignature, null);
        }

        Type type = Type.getType(fieldType);
        if(type.getSort() == Type.ARRAY) {
            if(type.getElementType().equals(Type.FLOAT_TYPE)) {
                String desc = arrayDescriptor(type.getDimensions(), 
                        COJAC_FLOAT_WRAPPER_TYPE_DESCR);
                return super.visitField(accessFlags, fieldName, desc, genericSignature, null);
            } else if(type.getElementType().equals(Type.DOUBLE_TYPE)) {
                String desc = arrayDescriptor(type.getDimensions(), 
                                              COJAC_DOUBLE_WRAPPER_TYPE_DESCR);
                return super.visitField(accessFlags, fieldName, desc, genericSignature, null);
            }                     
        }
        return super.visitField(accessFlags, fieldName, fieldType, genericSignature, initValStatic);
    }

    static String arrayDescriptor(int dimensions, String eltType) {
        StringBuilder desc = new StringBuilder();
        desc.append("[".repeat(Math.max(0, dimensions)));
        desc.append(eltType);
        return desc.toString();
    }
    
//    private FieldVisitor instrumentField(FieldVisitor parentFv, int arg0, String arg1, String arg2, String arg3, Object arg4) {
//        FieldVisitor fv = new CojacFieldVisitor(parentFv, arg0, arg1, arg2, arg3, arg4);
//        fv.visitEnd();
//        return fv;
//    }

    //========================================================================
    public static class MyLocalAdder extends LocalVariablesSorter {
        private static final Type OBJ_ARRAY_TYPE=Type.getType("[Ljava/lang/Object;");
        private static final Type     OBJ_TYPE = Type.getType(Object.class);

        private int paramArrayVar=-1;
        private int targetVar=-1;

        protected MyLocalAdder(int access, String desc, MethodVisitor mv) {
            super(ASM_VERSION, access, desc, mv);
        }
        
        public int paramArrayVar() {
            if (paramArrayVar<0)
                paramArrayVar = newLocal(OBJ_ARRAY_TYPE);
            return paramArrayVar;
        }

        public int targetVar() {
            if (targetVar<0)
                targetVar = newLocal(OBJ_TYPE);
            return targetVar;
        }
    }
    //========================================================================

}