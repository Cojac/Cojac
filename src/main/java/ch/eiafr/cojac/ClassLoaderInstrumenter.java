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

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

public final class ClassLoaderInstrumenter implements IClassInstrumenter {
    private final Args args;
    private final InstrumentationStats stats;

	private final CojacReferences references;
	
    public ClassLoaderInstrumenter(CojacReferences references) {
        super();

		this.references = references;
        this.args = references.getArgs();
        this.stats = references.getStats();
    }

    @Override
    public byte[] instrument(byte[] byteCode, ClassLoader loader) {
        ClassReader cr = new ClassReader(byteCode);
        ClassWriter cw = new ModifiedClassWriter(cr, CojacReferences.getFlags(args), loader);

        CojacAnnotationVisitor cav = new CojacAnnotationVisitor(stats);
        cr.accept(cav, ClassReader.EXPAND_FRAMES); 
        CojacClassVisitor ccv;
        if(references.getArgs().isSpecified(Arg.REPLACE_FLOATS))
            ccv = new FloatReplaceClassVisitor(cw, references, cav);
        else 
            ccv = new CojacClassVisitor(cw, references, cav);
		cr.accept(ccv, ClassReader.EXPAND_FRAMES);

        return cw.toByteArray();
    }
}
