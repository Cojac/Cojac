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

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import ch.eiafr.cojac.instrumenters.OpCodeInstrumenterFactory;
import ch.eiafr.cojac.methods.CojacMethodAdder;
import ch.eiafr.cojac.reactions.Reaction;

final class CojacClassVisitor extends ClassAdapter {
    private final CojacMethodAdder methodAdder;
    private final OpCodeInstrumenterFactory factory;
    private final InstrumentationStats stats;
    private final Args args;
    private final Methods methods;
    private final Reaction reaction;

    private boolean first = true;
    private String classPath;

    CojacClassVisitor(ClassVisitor cv, InstrumentationStats stats, Args args, Methods methods, Reaction reaction, OpCodeInstrumenterFactory factory) {
        super(cv);

        this.stats = stats;
        this.args = args;
        this.methods = methods;
        this.reaction = reaction;
        this.factory = factory;

        methodAdder = methods != null ? new CojacMethodAdder(args, reaction) : null;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String supername, String[] interfaces) {
        cv.visit(version, access, name, signature, supername, interfaces);

        classPath = name;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);

        if (methods != null && first && "<init>".equals(name)) {
            first = false;

            methodAdder.insertMethods(cv, methods, classPath);
        }

        mv.visitEnd();

        return instrumentMethod(mv, access, desc);

    }


    private MethodVisitor instrumentMethod(MethodVisitor parentMv, int access, String desc) {
        MethodVisitor mv = new CojacCheckerMethodVisitor(access, desc, parentMv, stats, args, methods, reaction, classPath, factory);

        mv.visitEnd();

        return mv;
    }
}