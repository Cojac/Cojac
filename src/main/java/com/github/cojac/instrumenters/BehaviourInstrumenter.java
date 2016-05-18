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
import com.github.cojac.models.ConstTransform;
import com.github.cojac.models.FromClass;
import com.github.cojac.models.MathMethods;
import com.github.cojac.models.Operation;
import com.github.cojac.models.Operations;
import com.github.cojac.models.Reactions;
import com.github.cojac.models.UtilityMethod;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

/**
 * This class instruments code with methods defined in the classes given by an instance of arg 
 * (method getBehaviour)
 * 
 * Its policy regarding instrumentation is: 
 * <ul>
 * <li>Every instrument method in the behaviour classes should be public static and correspond to an opcode, 
 * described in com.github.cojac.models.Operations or to a Java.lang.Math public static method.</li>
 * <li>If a method has to be public but is not an instrument method, use the annotation "@UtilityMethod"</li>
 * <li>If a method is set multiple times in one or more classes, only the first will be used as instrument</li>
 * </ul>
 * 
 * @author Valentin
 *
 */
public final class BehaviourInstrumenter implements IOpcodeInstrumenter {

    private final InstrumentationStats stats;
    private final Map<Integer, InvokableMethod> invocations = new HashMap<Integer, InvokableMethod>(50);
    private final Map<String, InvokableMethod> methods = new HashMap<String, InvokableMethod>(50);
    private boolean instrumentDouble = false;
    
    private final String[] BEHAVIOURS;
    private final String[] FULLY_QUALIFIED_BEHAVIOURS;
    private static BehaviourInstrumenter instance= null;
    /**
     * Constructor, private because only a singleton is available. 
     * Use {@link #getInstance(Args, InstrumentationStats)} to get the instance. 
     * @param args
     * @param stats
     */
    private BehaviourInstrumenter(Args args, InstrumentationStats stats) {
        super();
        BEHAVIOURS = args.getBehaviour().split(";");
        
        int i=0;
        FULLY_QUALIFIED_BEHAVIOURS = new String[BEHAVIOURS.length];
        for(String s: BEHAVIOURS){
            FULLY_QUALIFIED_BEHAVIOURS[i++] = s.replace('/', '.');
           // System.out.println(FULLY_QUALIFIED_BEHAVIOURS[i-1]);
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
    /**
     * Method used to get the singleton instance. If not allready constructed, the params given will be used.
     * 
     * @param args the arguments with which Cojac has been run.
     * @param stats
     * @return
     */
    public static BehaviourInstrumenter getInstance(Args args, InstrumentationStats stats){
        if(instance == null){
            instance = new BehaviourInstrumenter(args, stats);
        }
        return instance;
    }
    /**
     * Check if every method in the behaviour classes correspond to an Opcode or a java.lang.Math method.
     */
    private void checkMethods() {
        try {
            for (int i = 0; i < BEHAVIOURS.length; i++) {
                for(Method m:Class.forName(FULLY_QUALIFIED_BEHAVIOURS[i]).getMethods()){
                    //Operation op = MathMethods.toStaticOperation(m);
                    if (m.isAnnotationPresent(UtilityMethod.class)){
                        break;
                     }
                    int modifiers = m.getModifiers();
                    Operation methodOperation = MathMethods.toStaticOperation(m);
                    if(!Modifier.isPublic(modifiers) || !Modifier.isStatic(modifiers)){
                        continue;
                    }
                    if(m.isAnnotationPresent(FromClass.class)){
                        String classQualifier = m.getAnnotation(FromClass.class).value();
                        try {
                            Class.forName(classQualifier.replace('/', '.')).getMethod(m.getName(), m.getParameterTypes());
                            
                        } catch (NoSuchMethodException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }else{
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
                        if(!matches)
                        for(Operations op: Operations.values()){
                            if(op.name().equals(m.getName()) && op.signature.equals(methodOperation.signature) ){
                                matches = true;
                                break;
                            }
                        } 
                        if(!matches)
                            Reactions.react(m.getName()+methodOperation.signature+
                                    " doesn't matches any math method nor any bytecode operation.");
                    }
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
    /**
     * check if two sets of parameters match
     * @param a the first set
     * @param b the second set
     * @return true if the sets are the same length, and if every parameter of set one match
     * with the corresponding in the set two 
     */
    private boolean matchingParameters(Parameter[]a, Parameter[]b){
        if(a.length != b.length)
            return false;
        for(int i = 0; i < a.length; ++i){
            if(!a[i].getType().equals(b[i].getType()))
                return false;
        }
        return true;
        
        
    }
    /**
     * for every method in the behaviour classes that correspond to an Opcode or a Java.lang.Math method,
     * stores that it's instrumented, and stores the InvokableMethod which will be called when executing.
     */
    private void fillMethods() {
        /*Populate operations*/
        for(Operations op: Operations.values()){
            if(!op.loadsConst){
                for (int i = 0; i < BEHAVIOURS.length; i++) {
                    try {
                        //behaviourClass.getClass().getMethod(op.opCodeName, op.parameters);
                        
                        Method m = Class.forName(FULLY_QUALIFIED_BEHAVIOURS[i]).getMethod(op.name(), op.parameters);
                        if (m.isAnnotationPresent(UtilityMethod.class)){
                           break;
                        }
                        
                        //NewDoubles.class.getMethod(op.opCodeName, op.parameters);
                        //System.out.println("method \""+op.name()+"\" modified.");
                        invocations.put(op.opCodeVal, new InvokableMethod(BEHAVIOURS[i], op.name(), op.signature));
                        break;
                    } catch (NoSuchMethodException e) {
                        //Method not implemented, no problem.
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
            
        }
        /*Populate Constant loading methods*/
        for(ConstTransform ct: ConstTransform.values()){
            Operation op = ct.operation;
            //System.out.println("method \""+ct.name()+"\" check.");
            for (int i = 0; i < BEHAVIOURS.length; i++) {
                try {
                    //behaviourClass.getClass().getMethod(op.opCodeName, op.parameters);
                    
                    Method m = Class.forName(FULLY_QUALIFIED_BEHAVIOURS[i]).getMethod(op.opCodeName, op.parameters);
                    if (m.isAnnotationPresent(UtilityMethod.class)){
                       continue;
                    }
                    //NewDoubles.class.getMethod(op.opCodeName, op.parameters);
                   // System.out.println("method \""+ct.name()+"\" modified.");
                    for(Operations tmp: Operations.getLoadConstOp(ct.constType)){
                    //    System.out.println("\tmethod \""+tmp.name()+" val: "+tmp.opCodeVal+"\" redirected toward "+ op.opCodeName);
                        invocations.put(tmp.opCodeVal, new InvokableMethod(BEHAVIOURS[i], op.opCodeName, op.signature));
                    }
                    methods.put(op.opCodeName, new InvokableMethod(BEHAVIOURS[i], op.opCodeName, op.signature));
                    break;
                } catch (NoSuchMethodException e) {
                    //Method not implemented, no problem.
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
        /* Populate methods (math and others) */
        for (int i = 0; i < BEHAVIOURS.length; i++) {
            try {
                for(Method m:Class.forName(FULLY_QUALIFIED_BEHAVIOURS[i]).getMethods()){
                    //Operation op = MathMethods.toStaticOperation(m);
                    if (m.isAnnotationPresent(UtilityMethod.class)){
                        break;
                     }
                    int modifiers = m.getModifiers();
                    if(!Modifier.isPublic(modifiers) || !Modifier.isStatic(modifiers)){
                        continue;
                    }
                    if(!m.isAnnotationPresent(FromClass.class)){
                        try {
                            Math.class.getMethod(m.getName(), m.getParameterTypes());
                            Operation op = MathMethods.toStaticOperation(m);
                            methods.put("java/lang/Math"+op.opCodeName+op.signature, new InvokableMethod(BEHAVIOURS[i], op.opCodeName, op.signature));
                        } catch (NoSuchMethodException e) {
                            // not a Math method.
                        }
                    }else{
                        String classQualifier = m.getAnnotation(FromClass.class).value();
                        try {
                            Class.forName(classQualifier.replace('/', '.')).getMethod(m.getName(), m.getParameterTypes());
                            Operation op = MathMethods.toStaticOperation(m);
                           // System.out.println("method \""+op.opCodeName+op.signature+"\" from "+classQualifier+" modified.");
                            methods.put(classQualifier+op.opCodeName+op.signature, new InvokableMethod(BEHAVIOURS[i], op.opCodeName, op.signature));
                        } catch (NoSuchMethodException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    
                }
            } catch (SecurityException | ClassNotFoundException e) {
                // TODO Auto-generated catch block
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
    
    public void instrumentMethod(MethodVisitor mv,String owner, String name, String signature) { 
        //System.out.println("instrumenting method: "+name);
        methods.get(owner+name+signature).invokeStatic(mv);
    }
    public boolean wantsToInstrumentMethod(int opcode,String owner, String name, String signature) {
        //System.out.println("Wants to instrument method: "+name+signature+" from "+owner+" "+methods.containsKey(owner+name+signature));
        return (opcode == Opcodes.INVOKESTATIC) && methods.containsKey(owner+name+signature);
        
    }
    public void instrumentConstLoading(MethodVisitor mv, Class<?> cst){
        /*if(cst instanceof Double){
            double c = (double) cst;
            float inf = Math.nextDown((float)c);
            float sup = Math.nextUp((float)c);
            mv.visitLdcInsn(DoubleIntervalBehaviour.embedValues(new FloatInterval(inf,sup)));
        }else
            mv.visitLdcInsn(cst);*/
        ConstTransform methodToUse = ConstTransform.getMethodForClass(cst);
        methods.get(methodToUse.operation.opCodeName).invoke(mv);
    }
    public boolean wantsToInstrumentConstLoading(Class<?> o){
        ConstTransform methodToUse = ConstTransform.getMethodForClass(o);
        //System.out.println("wantsToInstrumentConstLoading? "+o+( methodToUse != null && methods.containsKey(methodToUse.operation.opCodeName)));
       return methodToUse != null && methods.containsKey(methodToUse.operation.opCodeName);
        
     
    }
}
