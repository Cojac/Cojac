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

import java.io.PrintWriter;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
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
            if (!references.hasToBeInstrumented(className)) {
                if (VERBOSE) {
                    System.out.println("Agent NOT instrumenting "+className +" under "+loader);
                }
                return classfileBuffer;
            }
            if (VERBOSE) {
                System.out.println("Agent instrumenting "+className +" under "+loader);
            }
            byte[] instrumented= instrumenter.instrument(classfileBuffer);
            if (VERBOSE) {
//                if (className.startsWith("sun/launcher")){
//                    CheckClassAdapter.verify(new ClassReader(classfileBuffer), true, new PrintWriter(System.out));
//                }
				// TODO - FAIL in verbose mode when class code contains call to an interface (which is instrumented after...)
				//byte[] noInterface = removeInterfaces(instrumented);
				//System.out.println("LENGTH: "+instrumented.length+" // "+noInterface.length);
				CheckClassAdapter.verify(new ClassReader(instrumented), PRINT_INSTR_RESULT, new PrintWriter(System.out));
			}
			
			
            return instrumented;
        } catch (Throwable e) {
			System.out.println("EXCEPTION FOR CLASS "+className);
            e.printStackTrace();  // Otherwise it'll be hidden!
            throw e;
        }
    }
	
	private byte[] removeInterfaces(final byte[] byteCode){
		ClassReader cr = new ClassReader(byteCode);
        ClassWriter cw = new ClassWriter(cr, 0);
		InterfaceRemover ir = new InterfaceRemover(cw);
		cr.accept(ir, ClassReader.EXPAND_FRAMES);
        return cw.toByteArray();
	}
	
	private class InterfaceRemover extends ClassVisitor{
		public InterfaceRemover(ClassVisitor cv) {
			super(Opcodes.ASM4, cv);
		}
		@Override
		public void visit(int version, int access, String name, String signature, String supername, String[] interfaces) {
			super.visit(version, access, name, signature, supername, null);
		}
	}
	
}
