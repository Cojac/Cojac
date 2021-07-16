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

import java.io.PrintWriter;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.*;

import static com.github.cojac.CojacCommonConstants.ASM_VERSION;

public final class Agent implements ClassFileTransformer {
    private final boolean PRINT_INSTR_RESULT=false;
    private final CojacReferences references;
    private final IClassInstrumenter instrumenter;
    private final boolean VERBOSE;
    private final boolean REPLACE_FLOATS;

    public Agent(final CojacReferences references) {
        try {
            this.references = references;
            this.VERBOSE = references.getArgs().isSpecified(Arg.VERBOSE);
            this.REPLACE_FLOATS = references.getArgs().isSpecified(Arg.REPLACE_FLOATS);
            this.instrumenter = new ClassLoaderInstrumenter(references); 
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @SuppressWarnings("unused")
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        // TODO: verify if we correctly handle lambdas... : for them className==null;
        //       we try to discover the "enclosing class" from classfileBuffer, 
        //       to decide if we should instrument or not.

        // TODO: correct this method. If no change is done, it should return null

//        if (VERBOSE && className==null) {
//            System.out.println("null className... "+extractedClassname(classfileBuffer));
//            //dumpIt(classfileBuffer);
//            //CheckClassAdapter.verify(new ClassReader(classfileBuffer), true, new PrintWriter(System.out));
//        }
        if (className==null) 
            className=extractedClassname(classfileBuffer);
        if(loader==null || loader==ClassLoader.getSystemClassLoader().getParent()) 
            return classfileBuffer;  // maybe the bootstrapLoader??
        try {
            if("com/github/cojac/models/FloatReplacerClasses".equals(className)) {
                if (VERBOSE) {
                    System.out.println("Agent handling the FloatReplacerClasses under "+loader);
                }
                return setGlobalFields(classfileBuffer, loader);
            }
            if("com/github/cojac/utils/PolyBehaviourLogger".equals(className)) {
                if (VERBOSE) {
                    System.out.println("Agent NOT instrumenting  PolyBehaviourLogger");
                }
                return classfileBuffer;
            }
            if (!references.hasToBeInstrumented(className)) {
                if (VERBOSE) {
                    System.out.println("Agent NOT instrumenting "+className +" under "+loader);
                }
                return classfileBuffer;
            }
            if (VERBOSE) {
                System.out.println("Agent     instrumenting "+className +" under "+loader);
            }
            byte[] instrumented= instrumenter.instrument(classfileBuffer, loader);
            if (VERBOSE) {
                System.out.println("Agent DONE instrumenting "+className +" under "+loader);
            }

            trackForDebuggingPurposes(className, instrumented);
            if (VERBOSE) {
				/*
				The interfaces are loaded by this class, the loading of a class 
				by the agent is done without the instrumentation.
				Once the interface is loaded, it is never reloaded for the same 
				classloader. That means the interface will never be instrumented 
				in verbose mode. 
				*/
                // The verify() can fail because some classes are used but
                // not yet defined... So better turn this off!
                // if (! REPLACE_FLOATS)
                //    CheckClassAdapter.verify(new ClassReader(instrumented), PRINT_INSTR_RESULT, new PrintWriter(System.out));
			}
			
            return instrumented;
            
        } catch (Throwable e) {
			System.err.println("COJAC-AGENT EXCEPTION FOR CLASS "+className);
            e.printStackTrace();  // Otherwise it'll be hidden!
            System.err.flush();
            throw e;
        }
    }
	
    static int nDumped=0;
    private static void dumpIt(byte[] t) {
        String name = "D"+nDumped++;
        try {
            java.io.FileOutputStream fos = new java.io.FileOutputStream(name+".class");
            //    org.objectweb.asm.util.CheckClassAdapter.verify(
            //        new ClassReader(dump()), false, new PrintWriter(System.err));
            fos.write(t);
            fos.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
	private void trackForDebuggingPurposes(String className, byte[] instrumented) {
        if (!className.contains("CojacDebugDump")) return;
        System.out.println("being dumped: "+className);
        dumpIt(instrumented);
        // and then: e:\_apps\java\jdk1.8.0_5\bin\javap -p -c -l -v D*.class
        // TODO: disable that debugging dump, once we have a perfect Cojac ;)
    }

    /**
	 * This method works only with the FloatReplacerClasses class
	 * It instruments it to create a static initializer block to set
	 * all the static variables used by the agent and injected in the 
	 * instrumented application.
	 * Warning: this is not the only place to set these variables, see class
	 * "CojacReferences" !
	 * This is used when there is more than one classloader in the application
	 */
	private byte[] setGlobalFields(byte[] byteCode, @SuppressWarnings("unused") ClassLoader loader) {
        ClassReader cr = new ClassReader(byteCode);
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES);
        ClassVisitor cv = new ClassVisitor(ASM_VERSION, cw) {
			@Override
			public void visit(int version, int access, String name, String signature, String superName, String[] interfaces){
				super.visit(version, access, name, signature, superName, interfaces);
				MethodVisitor mv = cv.visitMethod(Opcodes.ACC_STATIC, "<clinit>", "()V", null, null);
				mv.visitLdcInsn(references.getDoubleWrapper());
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, name, "setNgWrapper", "(Ljava/lang/String;)V", false);
                mv.visitLdcInsn(references.getNgWrapper());
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, name, "setDoubleWrapper", "(Ljava/lang/String;)V", false);
				mv.visitLdcInsn(references.getFloatWrapper());
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, name, "setFloatWrapper", "(Ljava/lang/String;)V", false);
				mv.visitLdcInsn(references.getBigDecimalPrecision());
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, name, "setBigDecimalPrecision", "(I)V", false);
                mv.visitLdcInsn(references.getStabilityThreshold());
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, name, "setStabilityThreshold", "(D)V", false);
                mv.visitLdcInsn(references.getCheckUnstableComparisons()?1:0);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, name, "setCheckUnstableComparisons", "(Z)V", false);
				mv.visitInsn(Opcodes.RETURN);
				mv.visitMaxs(0, 0);
			}
		};
		cr.accept(cv, ClassReader.EXPAND_FRAMES);
        return cw.toByteArray();
    }
	
	static String extractedClassname(byte[] t) {
	    ClassNameExtractor cv=new ClassNameExtractor();
	    ClassReader cr=new ClassReader(t);
	    cr.accept(cv, 0);
	    return cv.unitName;
	}
	//=======================================================================
	static class ClassNameExtractor extends ClassVisitor {
	    public String unitName=null;
        public ClassNameExtractor() { super(ASM_VERSION); }
        @SuppressWarnings("unused")
        @Override
        public void visit(int version, int access, String name, String signature, String supername, String[] interfaces) {
            unitName=name;
        }
	}
}
