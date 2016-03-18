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

package com.github.cojac.instrumenters;

import org.objectweb.asm.MethodVisitor;

import com.github.cojac.Arg;
import com.github.cojac.Args;
import com.github.cojac.InstrumentationStats;
import com.github.cojac.models.MathMethods;
import com.github.cojac.models.NewDoubles;
import com.github.cojac.models.Operation;
import com.github.cojac.models.Operations;

import java.util.HashMap;
import java.util.Map;


final class NewInstrumenter implements IOpcodeInstrumenter {

    private final InstrumentationStats stats;
    private final Map<Integer, InvokableMethod> invocations = new HashMap<Integer, InvokableMethod>(50);
    private final Map<String, InvokableMethod> methods = new HashMap<String, InvokableMethod>(50);
    
    private final String behaviour;
    private Object behaviourClass;
    NewInstrumenter(Args args, InstrumentationStats stats) {
        super();
        behaviour = args.getBehaviour();
        System.out.println(behaviour);
        try {
            behaviourClass = Class.forName(behaviour.replace('/', '.'));
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        this.stats = stats;
        System.out.println(behaviour);

        fillMethods();
    }
    private void fillMethods() {
        /*Populate operations*/
        for(Operation op: Operations.OPERATIONS){
            
            try {
                behaviourClass.getClass().getMethod(op.opCodeName, op.parameters);
                //NewDoubles.class.getMethod(op.opCodeName, op.parameters);
                //System.out.println("method \""+behaviour+op.opCodeName+"\" modified.");
                invocations.put(op.opCodeVal, new InvokableMethod(behaviour, op.opCodeName, op.signature));
            } catch (NoSuchMethodException e) {
                //Method not implemented, no problem.
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        /*Populate methods*/
        for(Operation op: MathMethods.operations){
            
            
        }
    }

    @Override
    public void instrument(MethodVisitor mv, int opCode) { 
       /* mv.visitLdcInsn(reaction.value());
        mv.visitLdcInsn(logFileName);*/
        
        Arg arg = Arg.fromOpCode(opCode);
        //System.out.println("instrument: "+opCode);
        if (arg != null) {
            stats.incrementCounterValue(opCode);// arg
            invocations.get(opCode).invokeStatic(mv);
        }
    }
    
    @Override
    public boolean wantsToInstrument(int opcode) {
        return invocations.containsKey(opcode);
    }
    public void instrumentMethod(MethodVisitor mv, int opCode) { 
        
             invocations.get(opCode).invokeStatic(mv);
        
     }
    public boolean wantsToInstrumentMethod(int opcode, String name) {
        return methods.containsKey(name);
    }
}
