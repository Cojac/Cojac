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

import ch.eiafr.cojac.instrumenters.OpCodeInstrumenterFactory;
import ch.eiafr.cojac.reactions.Reaction;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.EmptyVisitor;

import java.util.ArrayList;
import java.util.List;

public final class SimpleClassInstrumenter implements ClassInstrumenter {
    private final Args args;
    private final InstrumentationStats stats;
    private final Reaction reaction;
    private final OpCodeInstrumenterFactory factory;

    public SimpleClassInstrumenter(Args args, InstrumentationStats stats, Reaction reaction, OpCodeInstrumenterFactory factory) {
        super();

        this.args = args;
        this.stats = stats;
        this.reaction = reaction;
        this.factory = factory;
    }

    @Override
    public byte[] instrument(byte[] byteCode) {
        return transformBytecode(byteCode, computeMethods(byteCode));
    }

    private static Methods computeMethods(byte[] byteCode) {
        ClassReader cr = new ClassReader(byteCode);

        MethodCollector methodCollector = new MethodCollector();

        cr.accept(methodCollector, 0);

        return new Methods(methodCollector.getMethods());
    }

    private byte[] transformBytecode(byte[] byteCode, Methods methods) {
        ClassReader cr = new ClassReader(byteCode);
        ClassWriter cw = new ClassWriter(cr, getFlags());

        cr.accept(new CojacClassVisitor(cw, stats, args, methods, reaction, factory), ClassReader.EXPAND_FRAMES);

        return cw.toByteArray();
    }

    private int getFlags() {
        if (args.isSpecified(Arg.FRAMES)) {
            return ClassWriter.COMPUTE_FRAMES;
        }

        return ClassWriter.COMPUTE_MAXS;
    }

    private static final class MethodCollector extends EmptyVisitor {
        private final List<String> methods = new ArrayList<String>(15);

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            methods.add(name);

            return super.visitMethod(access, name, desc, signature, exceptions);
        }

        public List<String> getMethods() {
            return methods;
        }
    }
}