/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.eiafr.cojac.instrumenters;

import java.util.HashMap;
import java.util.Map;
import org.objectweb.asm.MethodVisitor;

import static ch.eiafr.cojac.instrumenters.InvokableMethod.*;
import java.util.ArrayList;
import org.objectweb.asm.ClassVisitor;
import static org.objectweb.asm.Opcodes.*;
import org.objectweb.asm.Type;

/**
 *
 * @author romain
 */
public class ReplaceFloatsMethods {
    
    private final Map<MethodSignature, Object> suppressions = new HashMap<>(50);
    private final Map<MethodSignature, InvokableMethod> invocations = new HashMap<>(50);
    private final ArrayList<String> allMethodsConversions = new ArrayList<>(50);

    private static final String FL_NAME = Type.getType(Float.class).getInternalName();
    private static final String FL_DESCR = Type.getType(Float.class).getDescriptor();
    
    private static final String DL_NAME = Type.getType(Double.class).getInternalName();
    private static final String DL_DESCR = Type.getType(Double.class).getDescriptor();
    
    
    private static final String MATH_NAME = Type.getType(Math.class).getInternalName();
    private static final String MATH_DESCR = Type.getType(Math.class).getDescriptor();

    private static final String CFW_N=COJAC_FLOAT_WRAPPER_INTERNAL_NAME;
    private static final String CFW=COJAC_FLOAT_WRAPPER_TYPE_DESCR;
    private static final String CDW_N=COJAC_DOUBLE_WRAPPER_INTERNAL_NAME;
    private static final String CDW=COJAC_DOUBLE_WRAPPER_TYPE_DESCR;
    
    private final ArrayList<MethodSignature> addedMethods = new ArrayList<>();
    private final ClassVisitor cv;
    private final String classPath;
    private static final String COJAC_PROXY_METHODS_PREFIX = "COJAC_PROXY_METHOD_";
    
    public ReplaceFloatsMethods(ClassVisitor cv, String classPath) {
        fillMethods();
        this.cv = cv;
        this.classPath = classPath;
    }

    private void fillMethods() {
        
        // Floats replacements
        suppressions.put(new MethodSignature(FL_NAME, "valueOf", "(F)"+FL_DESCR), CFW_N); // delete if the value is already a FloatWrapper
        suppressions.put(new MethodSignature(FL_NAME, "floatValue", "()F"), null); // delete in every case (keep FloatWrapper)
        suppressions.put(new MethodSignature(FL_NAME, "<init>", "(F)V"), CFW_N); // Delete the new Float(float) is the float is already a FloatWrapper
        
        invocations.put(new MethodSignature(FL_NAME, "valueOf", "(F)"+FL_DESCR), InvokableMethod.FROM_FLOAT);
        
        invocations.put(new MethodSignature(FL_NAME, "<init>", "(F)V"), new InvokableMethod(CFW_N, "<init>", "(F)V", INVOKESPECIAL));
        invocations.put(new MethodSignature(FL_NAME, "<init>", "(Ljava/lang/String;)V"), new InvokableMethod(CFW_N, "fromString", "(Ljava/lang/String;)"+CFW, INVOKESTATIC));
        invocations.put(new MethodSignature(FL_NAME, "<init>", "(D)V"), new InvokableMethod(CFW_N, "fromDouble", "("+CDW+")"+CFW, INVOKESTATIC));
        
        invocations.put(new MethodSignature(FL_NAME, "doubleValue", "()D"), new InvokableMethod(CFW_N, "f2d", "("+CFW+")"+CDW, INVOKESTATIC));
        invocations.put(new MethodSignature(FL_NAME, "intValue", "()I"), new InvokableMethod(CFW_N, "f2i", "("+CFW+")I", INVOKESTATIC));
        invocations.put(new MethodSignature(FL_NAME, "longValue", "()J"), new InvokableMethod(CFW_N, "f2l", "("+CFW+")J", INVOKESTATIC));

        invocations.put(new MethodSignature(FL_NAME, "parseFloat", "(Ljava/lang/String;)F"), new InvokableMethod(CFW_N, "fromString", "(Ljava/lang/String;)"+CFW, INVOKESTATIC));
        
        allMethodsConversions.add(FL_NAME); // use proxy to call every other methods from Float
        
        
        // Doubles replacements
        suppressions.put(new MethodSignature(DL_NAME, "valueOf", "(D)"+DL_DESCR), CDW_N); // delete if the value is already a DoubleWrapper
        suppressions.put(new MethodSignature(DL_NAME, "doubleValue", "()D"), null); // delete in every case (keep DoubleWrapper)
        suppressions.put(new MethodSignature(DL_NAME, "<init>", "(D)V"), CDW_N); // Delete the new Double(double) is the double is already a DoubleWrapper
        
        invocations.put(new MethodSignature(DL_NAME, "valueOf", "(D)"+DL_DESCR), InvokableMethod.FROM_DOUBLE);
        
        invocations.put(new MethodSignature(DL_NAME, "<init>", "(D)V"), new InvokableMethod(CDW_N, "<init>", "(D)V", INVOKESPECIAL));
        invocations.put(new MethodSignature(DL_NAME, "<init>", "(Ljava/lang/String;)V"), new InvokableMethod(CDW_N, "fromString", "(Ljava/lang/String;)"+CDW, INVOKESTATIC));
        invocations.put(new MethodSignature(DL_NAME, "<init>", "(F)V"), new InvokableMethod(CDW_N, "fromFloat", "("+CFW+")"+CDW, INVOKESTATIC));
        
        invocations.put(new MethodSignature(DL_NAME, "floatValue", "()F"), new InvokableMethod(CDW_N, "d2f", "("+CDW+")"+CFW, INVOKESTATIC));
        invocations.put(new MethodSignature(DL_NAME, "intValue", "()I"), new InvokableMethod(CDW_N, "d2i", "("+CDW+")I", INVOKESTATIC));
        invocations.put(new MethodSignature(DL_NAME, "longValue", "()J"), new InvokableMethod(CDW_N, "d2l", "("+CDW+")J", INVOKESTATIC));

        invocations.put(new MethodSignature(DL_NAME, "parseFloat", "(Ljava/lang/String;)D"), new InvokableMethod(CDW_N, "fromString", "(Ljava/lang/String;)"+CDW, INVOKESTATIC));
        
        allMethodsConversions.add(DL_NAME); // use proxy to call every other methods from Double
        
        // Math Library
        invocations.put(new MethodSignature(MATH_NAME, "sqrt", "(D)D"), new InvokableMethod(CDW_N, "math_sqrt", "("+CDW+")"+CDW, INVOKESTATIC));
        
        allMethodsConversions.add(MATH_NAME);
        
    }
    
    
    public boolean instrument(MethodVisitor mv, int opcode, String owner, String name, String desc, Object stackTop){
        MethodSignature ms = new MethodSignature(owner, name, desc);
        
        InvokableMethod replacementMethod = invocations.get(ms);
        
        if(suppressions.containsKey(ms)){
            Object supressionMethod = suppressions.get(ms);
            if(supressionMethod == null)
                return true;
            if(stackTop.equals(supressionMethod))
                return true;
        }
        
        if(replacementMethod != null){
            replacementMethod.invoke(mv);
            return true;
        }
        else if(allMethodsConversions.contains(owner)){
            transformTypesAndInvoke(mv, opcode, owner, name, desc);
            return true;
        }
        return false;
    }
    
    
    public void transformTypesAndInvoke(MethodVisitor mv, int opcode, String owner, String name, String desc){
        HashMap<Integer, Type> typeConversions = new HashMap<>();
        
        Type args[] = Type.getArgumentTypes(desc);
        if(opcode == INVOKEVIRTUAL || opcode == INVOKEINTERFACE || opcode == INVOKESPECIAL || opcode == INVOKEVIRTUAL){
            Type newArgs[] = new Type[args.length+1];
            System.arraycopy(args, 0, newArgs, 1, args.length);
            newArgs[0] = Type.getObjectType(owner);
            args = newArgs;
        }
        for (int i=0 ; i < args.length; i++) {
            Type type = args[i];
            Type cojacType = getCojacType(type);
            if(!type.equals(cojacType)){
                typeConversions.put(i, type);
                args[i] = cojacType;
            }
        }
        
        if(typeConversions.isEmpty()){
            mv.visitMethodInsn(opcode, owner, name, desc);
            convertReturnType(desc, mv);
            return;
        }
        
        String newDesc = Type.getMethodDescriptor(Type.getReturnType(desc), args);
        
        MethodSignature ms = new MethodSignature(owner, COJAC_PROXY_METHODS_PREFIX+name, newDesc);
        if(!addedMethods.contains(ms)){
            addedMethods.add(ms);

            MethodVisitor newMv = cv.visitMethod(ACC_STATIC , COJAC_PROXY_METHODS_PREFIX+name, newDesc, null, null);
            for (int i = 0; i < args.length; i++) {
                Type type = args[i];
                newMv.visitVarInsn(getLoadStoreOpcode(type), i);
                if(typeConversions.get(i) != null){
                    convertCojacToRealType(typeConversions.get(i), newMv);
                }
            }
            newMv.visitMethodInsn(opcode, owner, name, desc);
            newMv.visitInsn(Type.getReturnType(desc).getOpcode(IRETURN));
            newMv.visitMaxs(0, 0);
        }
        mv.visitMethodInsn(INVOKESTATIC, classPath, COJAC_PROXY_METHODS_PREFIX+name, newDesc);
        convertReturnType(desc, mv);
        
    }
    
    private int getLoadStoreOpcode(Type type){
        switch(type.getSort()){
            case Type.ARRAY:
            case Type.OBJECT:
                return ALOAD;
            case Type.BOOLEAN:
                return BALOAD;
            case Type.CHAR:
                return CALOAD;
            case Type.DOUBLE:
                return DALOAD;
            case Type.FLOAT:
                return FALOAD;
            case Type.INT:
                return IALOAD;
            case Type.LONG:
                return LALOAD;
        }
        return -1;
    }
    
    private void convertReturnType(String desc, MethodVisitor mv){
        Type returnType = Type.getReturnType(desc);
        Type cojacType = getCojacType(returnType);
        if(!returnType.equals(cojacType)){
            convertRealToCojacType(returnType, mv);
        }
    }
    
    private Type getCojacType(Type type){
        if(type.equals(Type.getType(Float.class))){
            return COJAC_FLOAT_WRAPPER_TYPE;
        }
        if(type.equals(Type.FLOAT_TYPE)){
            return COJAC_FLOAT_WRAPPER_TYPE;
        }
        if(type.equals(Type.getType(Double.class))){
            return COJAC_DOUBLE_WRAPPER_TYPE;
        }
        if(type.equals(Type.DOUBLE_TYPE)){
            return COJAC_DOUBLE_WRAPPER_TYPE;
        }
        return type; 
    }


    private void convertCojacToRealType(Type realType, MethodVisitor mv){
        if(realType.equals(Type.FLOAT_TYPE)){
            mv.visitMethodInsn(INVOKESTATIC, CFW_N, "toFloat", "("+CFW+")F");
        }
        else if(realType.equals(Type.getType(Float.class))){
            mv.visitMethodInsn(INVOKESTATIC, CFW_N, "toRealFloatWrapper", "("+CFW+")"+FL_DESCR);
        }
        else if(realType.equals(Type.DOUBLE_TYPE)){
            mv.visitMethodInsn(INVOKESTATIC, CDW_N, "toDouble", "("+CDW+")D");
        }
        else if(realType.equals(Type.getType(Double.class))){
            mv.visitMethodInsn(INVOKESTATIC, CDW_N, "toRealDoubleWrapper", "("+CDW+")"+DL_DESCR);
        }
    }
    
    private void convertRealToCojacType(Type realType, MethodVisitor mv){
        if(realType.equals(Type.FLOAT_TYPE)){
            mv.visitMethodInsn(INVOKESTATIC, CFW_N, "fromFloat", "(F)"+CFW);
        }
        if(realType.equals(Type.DOUBLE_TYPE)){
            mv.visitMethodInsn(INVOKESTATIC, CDW_N, "fromDouble", "(D)"+CDW);
        }
    }
    
}
