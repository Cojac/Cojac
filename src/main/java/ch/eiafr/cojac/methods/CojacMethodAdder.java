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

package ch.eiafr.cojac.methods;

import org.objectweb.asm.ClassVisitor;

import java.util.ArrayList;
import java.util.Collection;

import ch.eiafr.cojac.Arg;
import ch.eiafr.cojac.Args;
import ch.eiafr.cojac.Methods;
import ch.eiafr.cojac.reactions.Reaction;

public final class CojacMethodAdder {
    private final Args args;
    private final Reaction reaction;

    private static final Collection<MethodInserter> INSERTERS = new ArrayList<MethodInserter>(3);

    static {
        INSERTERS.add(new CheckedIntMethodInserter());
        INSERTERS.add(new CheckedCastMethodInserter());
        INSERTERS.add(new CheckedLongMethodInserter());
        INSERTERS.add(new CheckedDoubleMethodInserter());
        INSERTERS.add(new CheckedFloatMethodInserter());
    }

    public CojacMethodAdder(Args args, Reaction reaction) {
        super();

        this.args = args;
        this.reaction = reaction;
    }

    public void insertMethods(ClassVisitor cv, Methods methods, String classPath) {
        reaction.insertReactionMethod(cv, methods);

        if (!args.isSpecified(Arg.WASTE_SIZE)) {
            for (MethodInserter inserter : INSERTERS) {
                inserter.insertMethods(cv, args, methods, reaction, classPath);
            }
        }
    }
}