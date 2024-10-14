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

import java.lang.instrument.Instrumentation;

import org.objectweb.asm.Type;

import com.github.cojac.CojacReferences.CojacReferencesBuilder;

public final class CojacAgent {
    public static void premain(String agentArgs, Instrumentation inst) {
        //System.out.println("premyeahhh "+(java.util.function.DoubleUnaryOperator)Math::sqrt);System.out.flush();

        String[] parsedArgs = {Arg.ALL.shortOpt()};
        if (agentArgs != null) {
            parsedArgs = agentArgs.split(" ");
        }

        Args args = new Args();
        if (!args.parse(parsedArgs) || args.isSpecified(Arg.HELP)) {
            args.printHelpAndExit();
        }
        Class<?>[] loadedClasses = inst.getAllLoadedClasses();
        String[] strLoadedClasses = new String[loadedClasses.length];
        for (int i = 0; i < strLoadedClasses.length; i++)
            strLoadedClasses[i] = Type.getType(loadedClasses[i]).getInternalName();

        CojacReferencesBuilder builder = new CojacReferencesBuilder(args, strLoadedClasses);
        
        Agent agent = new Agent(builder.build());
         
        inst.addTransformer(agent);	
    }
}
