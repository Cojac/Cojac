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
import com.github.cojac.models.Reactions;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;


public final class NewInstrumenter implements IOpcodeInstrumenter {

    private final InstrumentationStats stats;
    private final Map<Integer, InvokableMethod> invocations = new HashMap<Integer, InvokableMethod>(50);
    private final Map<String, InvokableMethod> methods = new HashMap<String, InvokableMethod>(50);
    
    private final String[] BEHAVIOURS;
    private final String[] FULLY_QUALIFIED_BEHAVIOURS;
    private static NewInstrumenter instance= null;
    private NewInstrumenter(Args args, InstrumentationStats stats) {
        super();
        BEHAVIOURS = args.getBehaviour().split(";");
        //System.out.println(BEHAVIOUR);
        int i=0;
        FULLY_QUALIFIED_BEHAVIOURS = new String[BEHAVIOURS.length];
        for(String s: BEHAVIOURS){
            FULLY_QUALIFIED_BEHAVIOURS[i++] = s.replace('/', '.');
        }
        
        if (args.isSpecified(Arg.CALL_BACK))
            Reactions.theLogFilename = args.getValue(Arg.CALL_BACK); // No, I'm not proud of that trick...
        else
            Reactions.theLogFilename = args.getValue(Arg.LOG_FILE);
        
        Reactions.theReactionType = args.getReactionType();
        this.stats = stats;
        checkMethods();
        fillMethods();
    }
    public static NewInstrumenter getInstance(Args args, InstrumentationStats stats){
        if(instance == null){
            instance = new NewInstrumenter(args, stats);
        }
        return instance;
    }
    private void checkMethods() {
        try {
            for (int i = 0; i < BEHAVIOURS.length; i++) {
                for(Method m:Class.forName(FULLY_QUALIFIED_BEHAVIOURS[i]).getMethods()){
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
            for (int i = 0; i < BEHAVIOURS.length; i++) {
                try {
                    //behaviourClass.getClass().getMethod(op.opCodeName, op.parameters);
                    
                    Class.forName(FULLY_QUALIFIED_BEHAVIOURS[i]).getMethod(op.name(), op.parameters);
                    
                    
                    //NewDoubles.class.getMethod(op.opCodeName, op.parameters);
                    //System.out.println("method \""+behaviour+op.opCodeName+"\" modified.");
                    invocations.put(op.opCodeVal, new InvokableMethod(BEHAVIOURS[i], op.name(), op.signature));
                    break;
                } catch (NoSuchMethodException e) {
                    //Method not implemented, no problem.
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
        /*Populate methods*/
        for(Operation op: MathMethods.operations){
            for (int i = 0; i < BEHAVIOURS.length; i++) {
                try {
                    //behaviourClass.getClass().getMethod(op.opCodeName, op.parameters);
                    Class.forName(FULLY_QUALIFIED_BEHAVIOURS[i]).getMethod(op.opCodeName, op.parameters);
                    //NewDoubles.class.getMethod(op.opCodeName, op.parameters);
                    //System.out.println("method \""+behaviour+op.opCodeName+"\" modified.");
                    methods.put(op.opCodeName+op.signature, new InvokableMethod(BEHAVIOURS[i], op.opCodeName, op.signature));
                } catch (NoSuchMethodException e) {
                    //Method not implemented, no problem.
                }catch(Exception e){
                    e.printStackTrace();
                }
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
