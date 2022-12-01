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

import org.objectweb.asm.ClassVisitor;
//import org.objectweb.asm.Opcodes;

import com.github.cojac.instrumenters.IOpcodeInstrumenterFactory;
import static com.github.cojac.CojacCommonConstants.ASM_VERSION;

public class CojacClassVisitor extends ClassVisitor {
    protected final IOpcodeInstrumenterFactory factory;
    protected final InstrumentationStats stats;
    protected final Args args;
    protected final CojacReferences references;
    protected final CojacAnnotationVisitor cav;
    protected String crtClassName;    

    public CojacClassVisitor(ClassVisitor cv, CojacReferences references, CojacAnnotationVisitor cav) {
		super(ASM_VERSION, cv);
		this.references = references;
        this.stats = references.getStats();
        this.args = references.getArgs();
        this.factory = references.getOpCodeInstrumenterFactory();
        this.cav = cav;
    }


    @Override
    public void visit(int version, int access, String name, String signature, String supername, String[] interfaces) {
        crtClassName = name;
        super.visit(version, access, name, signature, supername, interfaces);
    }

// NOT USED ANYMORE (replaced by the "behaviour" mechanism)

//    @Override
//    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
//        //boolean isNative = (access & Opcodes.ACC_NATIVE) > 0;
//        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
//        String currentMethodID = crtClassName + '/' + name;
//        if (cav.isClassAnnotated() || cav.isMethodAnnotated(currentMethodID)) {
//            return mv;
//        }      
//        return instrumentMethod(mv, access, desc);
//    }
//
//    private MethodVisitor instrumentMethod(MethodVisitor parentMv, int access, String desc) {
//        MethodVisitor mv=null;
//        mv = new CojacCheckerMethodVisitor(access, desc, parentMv, stats, args, crtClassName, factory);
//        return mv;
//    }
}