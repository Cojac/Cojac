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
import com.github.cojac.models.NewDoubles;
import com.github.cojac.models.Operation;
import com.github.cojac.models.Operations;
import com.github.cojac.models.ReactionType;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;


final class NewInstrumenter implements IOpcodeInstrumenter {

    private final InstrumentationStats stats;
    private final BitSet implementedMethods = new BitSet(256);
    private final Map<Integer, InvokableMethod> invocations = new HashMap<Integer, InvokableMethod>(50);

    
    private static final String NEW_DOUBLES = "com/github/cojac/models/NewDoubles";

    NewInstrumenter(Args args, InstrumentationStats stats) {
        super();

        this.stats = stats;


        fillMethods();
    }
    private void fillMethods() {
        for(Operation op: Operations.OPERATIONS){
            
            try {
                NewDoubles.class.getMethod(op.opCodeName, op.parameters);
                //System.out.println("method \""+op.opCodeName+"\" modified.");
                invocations.put(op.opCodeVal, new InvokableMethod(NEW_DOUBLES, op.opCodeName, op.signature));
                implementedMethods.set(op.opCodeVal);
            } catch (NoSuchMethodException e) {
                //Method not implemented, no problem.
            }catch(Exception e){
                e.printStackTrace();
            }
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
        return implementedMethods.get(opcode);
    }

}
