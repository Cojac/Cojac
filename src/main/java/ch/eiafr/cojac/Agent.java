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

public final class Agent implements ClassFileTransformer {
    private final boolean PRINT_INSTR_RESULT=false;
    private final CojacReferences references;
    private final IClassInstrumenter instrumenter;
    private final boolean VERBOSE;
    
    public Agent(final CojacReferences references) {
        try {
            this.references = references;
            this.VERBOSE = references.getArgs().isSpecified(Arg.VERBOSE);
            this.instrumenter = new ClassLoaderInstrumenter(references); 
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
		
		try {
			if(className.equals("ch/eiafr/cojac/models/FloatReplacerClasses")){
				return setGlobalFields(classfileBuffer, loader);
			}
            if (!references.hasToBeInstrumented(className)) {
                if (VERBOSE) {
                    System.out.println("Agent NOT instrumenting "+className +" under "+loader);
                }
                return classfileBuffer;
            }
            if (VERBOSE) {
                System.out.println("Agent instrumenting "+className +" under "+loader);
            }
            byte[] instrumented= instrumenter.instrument(classfileBuffer, loader);
            if (VERBOSE) {
				// TODO - FAIL in verbose mode when the instrumented application use interfaces (only with -R option)
				/*
				The interfaces are loaded by this class, the loading of a class by the agent is done without the instrumentation.
				One the interface is loaded, it is never reloaded for the same classloader.
				That means the interface will never be instrumented in verbose mode. 
				*/
				CheckClassAdapter.verify(new ClassReader(instrumented), PRINT_INSTR_RESULT, new PrintWriter(System.out));
			}
			
			
            return instrumented;
        } catch (Throwable e) {
			System.out.println("EXCEPTION FOR CLASS "+className);
            e.printStackTrace();  // Otherwise it'll be hidden!
            throw e;
        }
    }
	
	/**
	 * This method works only with the FloatReplacerClasses class
	 * It instrument it to create a static initializer block to set
	 * all the static variables used by the agent and injected in the 
	 * instrumented application.
	 * This is used when there is more than one classloader in the application
	 * @param byteCode
	 * @param loader
	 * @return 
	 */
	public byte[] setGlobalFields(byte[] byteCode, ClassLoader loader) {
        ClassReader cr = new ClassReader(byteCode);
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES);
        ClassVisitor cv = new ClassVisitor(Opcodes.ASM5, cw) {
			@Override
			public void visit(int version, int access, String name, String signature, String superName, String[] interfaces){
				super.visit(version, access, name, signature, superName, interfaces);
				MethodVisitor mv = cv.visitMethod(Opcodes.ACC_STATIC, "<clinit>", "()V", null, null);
				mv.visitLdcInsn(references.getDoubleWrapper());
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, name, "setDoubleWrapper", "(Ljava/lang/String;)V", false);
				mv.visitLdcInsn(references.getFloatWrapper());
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, name, "setFloatWrapper", "(Ljava/lang/String;)V", false);
				mv.visitInsn(Opcodes.RETURN);
				mv.visitMaxs(0, 0);
			}
		};
		cr.accept(cv, ClassReader.EXPAND_FRAMES);
        return cw.toByteArray();
    }
	
}
