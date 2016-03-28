/*
 * *
 *    Copyright 2011-2016 Valentin Gazzola & Frédéric Bapst
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
import org.objectweb.asm.Opcodes;

import com.github.cojac.Arg;
import com.github.cojac.Args;
import com.github.cojac.InstrumentationStats;
import com.github.cojac.models.MathMethods;
import com.github.cojac.models.Operation;
import com.github.cojac.models.Operations;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;


public final class NewInstrumenter implements IOpcodeInstrumenter {

    private final InstrumentationStats stats;
    private final Map<Integer, InvokableMethod> invocations = new HashMap<Integer, InvokableMethod>(50);
    private final Map<String, InvokableMethod> methods = new HashMap<String, InvokableMethod>(50);
    
    private final String BEHAVIOUR;
    private final String FULLY_QUALIFIED_BEHAVIOUR;
    public NewInstrumenter(Args args, InstrumentationStats stats) {
        super();
        BEHAVIOUR = args.getBehaviour();
        //System.out.println(BEHAVIOUR);

        FULLY_QUALIFIED_BEHAVIOUR = BEHAVIOUR.replace('/', '.');
        
        this.stats = stats;
        //TODO: fix the 3 times called issue.
        System.out.println("Called one time");
        checkMethods();
        fillMethods();
    }
    private void checkMethods() {
        try {
            for(Method m:Class.forName(FULLY_QUALIFIED_BEHAVIOUR).getMethods()){
                //Operation op = MathMethods.toStaticOperation(m);
                int modifiers = m.getModifiers();
                Operation methodOperation = MathMethods.toStaticOperation(m);
                if(!Modifier.isPublic(modifiers) || !Modifier.isStatic(modifiers)){
                    continue;
                }
                boolean matches = false;
                for(Method mathm : Math.class.getMethods()){
                    modifiers = mathm.getModifiers();
                    if(!Modifier.isPublic(modifiers) || !Modifier.isStatic(modifiers)){
                        continue;
                    }
                    if(mathm.getName().equals(m.getName()) && matchingParameters(mathm.getParameters(), m.getParameters())
                            &&mathm.getReturnType().equals(m.getReturnType())){
                        matches = true;
                        break;
                    }
                }
                
                for(Operations op: Operations.values()){
                    if(op.name().equals(m.getName()) && op.signature.equals(methodOperation.signature) ){
                        matches = true;
                        break;
                    }
                }
                
                if(!matches)
                    System.out.println(m.getName()+methodOperation.signature+
                            " doesn't matches any math method nor any bytecode operation.");
            }
            
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        
    }
    private boolean matchingParameters(Parameter[]a, Parameter[]b){
        if(a.length != b.length)
            return false;
        for(int i = 0; i < a.length; ++i){
            if(!a[i].getType().equals(b[i].getType()))
                return false;
        }
        return true;
        
        
    }
    private void fillMethods() {
        /*Populate operations*/
        for(Operations op: Operations.values()){
            
            try {
                //behaviourClass.getClass().getMethod(op.opCodeName, op.parameters);
                Class.forName(FULLY_QUALIFIED_BEHAVIOUR).getMethod(op.name(), op.parameters);
                //NewDoubles.class.getMethod(op.opCodeName, op.parameters);
                //System.out.println("method \""+behaviour+op.opCodeName+"\" modified.");
                invocations.put(op.opCodeVal, new InvokableMethod(BEHAVIOUR, op.name(), op.signature));
            } catch (NoSuchMethodException e) {
                //Method not implemented, no problem.
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        /*Populate methods*/
        for(Operation op: MathMethods.operations){
            try {
                //behaviourClass.getClass().getMethod(op.opCodeName, op.parameters);
                Class.forName(FULLY_QUALIFIED_BEHAVIOUR).getMethod(op.opCodeName, op.parameters);
                //NewDoubles.class.getMethod(op.opCodeName, op.parameters);
                //System.out.println("method \""+behaviour+op.opCodeName+"\" modified.");
                methods.put(op.opCodeName+op.signature, new InvokableMethod(BEHAVIOUR, op.opCodeName, op.signature));
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
        
       /* Arg arg = Arg.fromOpCode(opCode);
        //System.out.println("instrument: "+opCode);
        if (arg != null) {
            stats.incrementCounterValue(opCode);// arg*/
       // System.out.println("instrumenting opcode: "+opCode);
            invocations.get(opCode).invokeStatic(mv);
       // }
    }
    
    @Override
    public boolean wantsToInstrument(int opcode) {
       // System.out.println("Wants to instrument: "+opcode+"   "+invocations.containsKey(opcode));
        return invocations.containsKey(opcode);
    }
    
    public void instrumentMethod(MethodVisitor mv, String name, String signature) { 
       // System.out.println("instrumenting method: "+name);
        methods.get(name+signature).invokeStatic(mv);
    }
    public boolean wantsToInstrumentMethod(int opcode, String name, String signature) {
       // System.out.println("Wants to instrument method: "+name+"  "+invocations.containsKey(opcode));
        return (opcode == Opcodes.INVOKESTATIC) && methods.containsKey(name+signature);
        
    }
}
