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
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.TraceMethodVisitor;
import org.objectweb.asm.commons.LocalVariablesSorter;

public class CojacClassVisitor extends ClassVisitor {
    protected final IOpcodeInstrumenterFactory factory;
    protected final InstrumentationStats stats;
    protected final Args args;
    protected final CojacReferences references;
    protected final CojacAnnotationVisitor cav;
    protected String crtClassName;    

    public CojacClassVisitor(ClassVisitor cv, CojacReferences references, CojacAnnotationVisitor cav) {
		super(Opcodes.ASM5, cv);
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

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        //boolean isNative = (access & Opcodes.ACC_NATIVE) > 0;
		//boolean isAbstrac = (access & Opcodes.ACC_ABSTRACT) > 0;
		//boolean isInterface = (access & Opcodes.ACC_INTERFACE) > 0;       
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        String currentMethodID = crtClassName + '/' + name;
        if (cav.isClassAnnotated() || cav.isMethodAnnotated(currentMethodID)) {
            return mv;
        }      
        return instrumentMethod(mv, access, desc, desc, name);
    }

    private MethodVisitor instrumentMethod(MethodVisitor parentMv, int access, String desc, String oldDesc, String name) {
        MethodVisitor mv=null;
        mv = new CojacCheckerMethodVisitor(access, desc, parentMv, stats, args, crtClassName, factory);
        return mv;
    }
}