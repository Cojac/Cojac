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

public final class ClassLoaderInstrumenter implements ClassInstrumenter {
    private final Args args;
    private final InstrumentationStats stats;
    private final Reaction reaction;
    private final OpCodeInstrumenterFactory factory;

    public ClassLoaderInstrumenter(CojacReferences references) {
        super();

        this.args = references.getArgs();
        this.stats = references.getStats();
        this.reaction = references.getReaction();
        this.factory = references.getOpCodeInstrumenterFactory();
    }

    @Override
    public byte[] instrument(byte[] byteCode) {
        ClassReader cr = new ClassReader(byteCode);
        ClassWriter cw = new ClassWriter(cr, CojacReferences.getFlags(args));

        CojacAnnotationVisitor cav = new CojacAnnotationVisitor(stats);
        cr.accept(cav, ClassReader.EXPAND_FRAMES);
        cr.accept(new CojacClassVisitor(cw, stats, args, null, reaction, factory, cav), ClassReader.EXPAND_FRAMES);

        return cw.toByteArray();
    }
}
