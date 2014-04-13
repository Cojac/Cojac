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

import static ch.eiafr.cojac.Arg.ALL;
import static ch.eiafr.cojac.Arg.HELP;

import java.lang.instrument.Instrumentation;

import ch.eiafr.cojac.CojacReferences.CojacReferencesBuilder;
import java.lang.instrument.UnmodifiableClassException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class CojacAgent {
    public static void premain(String agentArgs, Instrumentation inst) {
        String[] parsedArgs = {ALL.shortOpt()};
        if (agentArgs != null) {
            parsedArgs = agentArgs.split(" ");
        }

        Args args = new Args();
        if (!args.parse(parsedArgs) || args.isSpecified(HELP)) {
            args.printHelpAndExit();
        }

        
        
        CojacReferencesBuilder builder = new CojacReferencesBuilder(args);
        builder.setSplitter(new CojacReferences.AgentSplitter());
        
        Agent agent = new Agent(builder.build());
        
        
        inst.addTransformer(agent, inst.isRetransformClassesSupported());
        
        //inst.setNativeMethodPrefix(agent, "$$$COJAC_NATIVE_METHOD$$$_");
        
        // retransforming existing classes when REPLACE_FLOATS
        if (args.isSpecified(Arg.REPLACE_FLOATS)){
            if (!inst.isRetransformClassesSupported()) {
                System.out.println("RetransformClasses not supported");
            } else {
                    
                    ArrayList<Class> listClasses = new ArrayList<>();
                    
                    
                    
                    listClasses.remove(Float.class);

                    Class[] cl = inst.getAllLoadedClasses();
                    for (Class class1 : cl) {
                    //if (inst.isModifiableClass(class1)) {
                    listClasses.add(class1);
                    //}
                    }
                    
                    for (Class class1 : listClasses) {
                        //System.out.println("RETRANSFORM "+class1);
                }
                    
                    //try {
                    Class[] cl2 = new Class[listClasses.size()];
                    listClasses.toArray(cl2);
                    //inst.retransformClasses(cl2);
                    //} catch (UnmodifiableClassException ex) {
                   // Logger.getLogger(CojacAgent.class.getName()).log(Level.SEVERE, null, ex);
                    //}
            }
        }
    }
}
