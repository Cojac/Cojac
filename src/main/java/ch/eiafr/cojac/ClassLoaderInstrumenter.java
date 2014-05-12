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

import ch.eiafr.cojac.instrumenters.IOpcodeInstrumenterFactory;
import ch.eiafr.cojac.reactions.IReaction;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

public final class ClassLoaderInstrumenter implements IClassInstrumenter {
    private final Args args;
    private final InstrumentationStats stats;
    private final IReaction reaction;
    private final IOpcodeInstrumenterFactory factory;
	private final String[] bypassList;

	private final CojacReferences references;
	
    public ClassLoaderInstrumenter(CojacReferences references) {
        super();

		this.references = references;
        this.args = references.getArgs();
        this.stats = references.getStats();
        this.reaction = references.getReaction();
        this.factory = references.getOpCodeInstrumenterFactory();
		this.bypassList = references.getBypassList();
    }

    @Override
    public byte[] instrument(byte[] byteCode, ClassLoader loader) {
        ClassReader cr = new ClassReader(byteCode);
        ClassWriter cw = new ModifiedClassWriter(cr, CojacReferences.getFlags(args), loader);

        CojacAnnotationVisitor cav = new CojacAnnotationVisitor(stats);
        cr.accept(cav, ClassReader.EXPAND_FRAMES); 
        //CojacClassVisitor ccv = new CojacClassVisitor(cw, stats, args, null, reaction, factory, loadedClasses, bypassList, cav);
        CojacClassVisitor ccv = new CojacClassVisitor(cw, null, references, cav);
        
		cr.accept(ccv, ClassReader.EXPAND_FRAMES);

        return cw.toByteArray();
    }
}
