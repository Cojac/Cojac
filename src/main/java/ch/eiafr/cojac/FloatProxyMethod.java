/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.eiafr.cojac;

import ch.eiafr.cojac.instrumenters.MethodSignature;
import java.util.HashMap;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import static ch.eiafr.cojac.instrumenters.InvokableMethod.*;
import static org.objectweb.asm.Opcodes.*;

/**
 *
 * @author romain
 */
public class FloatProxyMethod {
    private final CojacClassVisitor ccv;
    
    private static final String CFW_N=COJAC_FLOAT_WRAPPER_INTERNAL_NAME;
    private static final String CFW=COJAC_FLOAT_WRAPPER_TYPE_DESCR;
    private static final String CDW_N=COJAC_DOUBLE_WRAPPER_INTERNAL_NAME;
    private static final String CDW=COJAC_DOUBLE_WRAPPER_TYPE_DESCR;
    
    private static final String FL_NAME = Type.getType(Float.class).getInternalName();
    private static final String FL_DESCR = Type.getType(Float.class).getDescriptor();
    
    private static final String DL_NAME = Type.getType(Double.class).getInternalName();
    private static final String DL_DESCR = Type.getType(Double.class).getDescriptor();
    
    private static final String COJAC_PROXY_METHODS_PREFIX = "COJAC_PROXY_METHOD_";
    
    private final String classPath;
    
    public FloatProxyMethod(CojacClassVisitor ccv, String classPath){
        this.ccv = ccv;
        this.classPath = classPath;
    }
    
    public void proxyCall(MethodVisitor mv, int opcode, String owner, String name, String desc){

        
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
            Type cojacType = afterFloatReplacement(type);
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
        String proxyName = COJAC_PROXY_METHODS_PREFIX+name;
        if(name.equals("<init>")){
            proxyName = COJAC_PROXY_METHODS_PREFIX+"constructor";
        }
        
        MethodSignature ms = new MethodSignature(owner, proxyName, newDesc);
        if(!ccv.isProxyMerhod(proxyName, newDesc)){
            MethodVisitor newMv = ccv.addProxyMethod(ACC_STATIC, proxyName, newDesc, null, null);
            
            int varIndex = 0; // Static method
            for (int i = 0; i < args.length; i++) {
                Type type = args[i];
                newMv.visitVarInsn(getLoadOpcode(type), varIndex);
                varIndex += type.getSize();
                if(typeConversions.get(i) != null){
                    convertCojacToRealType(typeConversions.get(i), newMv);
                }
            }
            newMv.visitMethodInsn(opcode, owner, name, desc);
            newMv.visitInsn(Type.getReturnType(desc).getOpcode(IRETURN));
            newMv.visitMaxs(0, 0);
        }
        mv.visitMethodInsn(INVOKESTATIC, classPath, proxyName, newDesc);
//        System.out.println("CALL => "+proxyName+" => "+newDesc);
        convertReturnType(desc, mv);
        
    }
    
    public void nativeCall(MethodVisitor mv, int access, String owner, String name, String desc){
        
        if(name.equals("nativeGetBounds")){
            System.out.println("nativeGetBounds "+owner+" "+desc);
        }
        
        HashMap<Integer, Type> typeConversions = new HashMap<>();
        
        Type args[] = Type.getArgumentTypes(desc);
        for (int i=0 ; i < args.length; i++) {
            Type type = args[i];
            Type cojacType = afterFloatReplacement(type);
            if(!type.equals(cojacType)){
                typeConversions.put(i, type);
                args[i] = cojacType;
            }
        }

        
        String newDesc = Type.getMethodDescriptor(afterFloatReplacement(Type.getReturnType(desc)), args);
        
        if(desc.equals(newDesc)){
            return;
        }
        
        boolean isStatic = (access & ACC_STATIC) > 0;
        
        MethodVisitor newMv = ccv.addProxyMethod(access & ~ACC_NATIVE, name, newDesc, null, null);
        
        int varIndex = 1;
        if(isStatic)
            varIndex = 0;
        else
            newMv.visitVarInsn(ALOAD, 0);
        
        for (int i = 0; i < args.length; i++) {
            Type type = args[i];
            if(type == null){
                System.out.println("WHAT???");
            }
            newMv.visitVarInsn(getLoadOpcode(type), varIndex);
            varIndex += type.getSize();
            if(typeConversions.get(i) != null){
                convertCojacToRealType(typeConversions.get(i), newMv);
            }
        }
        
        if(name.equals("nativeGetBounds")){
            System.out.println("call to "+owner+" $$$COJAC_NATIVE_METHOD$$$_"+name+" "+desc);
        }
        if(isStatic)
            newMv.visitMethodInsn(INVOKESTATIC, owner, name, desc);
        else
            newMv.visitMethodInsn(INVOKEVIRTUAL, owner, name, desc);
        convertReturnType(desc, newMv);
        
        newMv.visitInsn(afterFloatReplacement(Type.getReturnType(desc)).getOpcode(IRETURN));
        newMv.visitMaxs(0, 0);
        
    }
    
    public void proxyNative(MethodVisitor mv, int access, String owner, String name, String desc){
        
        HashMap<Integer, Type> typeConversions = new HashMap<>();
        
        Type args[] = Type.getArgumentTypes(desc);
        Type newDescArgs[] = new Type[args.length];
        for (int i=0 ; i < args.length; i++) {
            Type type = args[i];
            Type cojacType = afterFloatReplacement(type);
            newDescArgs[i] = cojacType;
            if(!type.equals(cojacType)){
                typeConversions.put(i, type);
                args[i] = type;
            }
        }

        
        String newDesc = Type.getMethodDescriptor(afterFloatReplacement(Type.getReturnType(desc)), newDescArgs);
        
        boolean isStatic = (access & ACC_STATIC) > 0;
        
        MethodVisitor newMv = ccv.addProxyMethod(access, name, desc, null, null);
        
        int varIndex = 1;
        if(isStatic)
            varIndex = 0;
        else{
            newMv.visitVarInsn(ALOAD, 0);
        }
        
        for (int i = 0; i < args.length; i++) {
            Type type = args[i];
            newMv.visitVarInsn(getLoadOpcode(type), varIndex);
            varIndex += type.getSize();
            if(typeConversions.get(i) != null){
                convertRealToCojacType(typeConversions.get(i), newMv);
            }
        }
        if(isStatic)
            newMv.visitMethodInsn(INVOKESTATIC, owner, name, newDesc);
        else
            newMv.visitMethodInsn(INVOKESPECIAL, owner, name, newDesc);
        convertCojacToRealType(Type.getReturnType(desc), newMv);
        
        newMv.visitInsn(Type.getReturnType(desc).getOpcode(IRETURN));
        newMv.visitMaxs(0, 0);
        
    }
    
    private void convertReturnType(String desc, MethodVisitor mv){
        Type returnType = Type.getReturnType(desc);
        Type cojacType = afterFloatReplacement(returnType);
        if(!returnType.equals(cojacType)){
            convertRealToCojacType(returnType, mv);
        }
    }
    
    public static void convertRealToCojacType(Type realType, MethodVisitor mv){
        if(realType.equals(Type.FLOAT_TYPE)){
            mv.visitMethodInsn(INVOKESTATIC, CFW_N, "fromFloat", "(F)"+CFW);
        }
        else if(realType.equals(Type.getType(Float.class))){
            mv.visitMethodInsn(INVOKESTATIC, CFW_N, "fromFloat", "("+Type.getType(Float.class).getDescriptor()+")"+CFW);
        }
        else if(realType.equals(Type.DOUBLE_TYPE)){
            mv.visitMethodInsn(INVOKESTATIC, CDW_N, "fromDouble", "(D)"+CDW);
        }
        else if(realType.equals(Type.getType(Double.class))){
            mv.visitMethodInsn(INVOKESTATIC, CDW_N, "fromDouble", "("+Type.getType(Double.class).getDescriptor()+")"+CDW);
        }
        else if(realType.getSort() == Type.ARRAY){
            if(realType.getDimensions() > 1){
                System.out.println("ALERT ARRAY DIMENSION HIGHER THAN 1 => "+realType.getDimensions());
                String objDesc = Type.getType(Object.class).getDescriptor();
                if(realType.getElementType().equals(Type.FLOAT_TYPE)){
                    mv.visitLdcInsn(realType.getDimensions());
                    mv.visitMethodInsn(INVOKESTATIC, CFW_N, "convertArrayToCojac", "("+objDesc+"I)"+objDesc);
                    mv.visitTypeInsn(CHECKCAST, afterFloatReplacement(realType).getDescriptor());
                }
                else if(realType.getElementType().equals(Type.DOUBLE_TYPE)){
                    mv.visitLdcInsn(realType.getDimensions());
                    mv.visitMethodInsn(INVOKESTATIC, CDW_N, "convertArrayToCojac", "("+objDesc+"I)"+objDesc);
                    mv.visitTypeInsn(CHECKCAST, afterFloatReplacement(realType).getDescriptor());
                }
                return;
            }
            if(realType.getElementType().equals(Type.FLOAT_TYPE)){
                mv.visitMethodInsn(INVOKESTATIC, CFW_N, "convertArray", "("+realType.getDescriptor()+")"+afterFloatReplacement(realType).getDescriptor());
            }
            else if(realType.getElementType().equals(Type.DOUBLE_TYPE)){
                mv.visitMethodInsn(INVOKESTATIC, CDW_N, "convertArray", "("+realType.getDescriptor()+")"+afterFloatReplacement(realType).getDescriptor());
            }
        }
    }
    
    public static void convertCojacToRealType(Type realType, MethodVisitor mv){
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
        else if(realType.getSort() == Type.ARRAY){
            if(realType.getDimensions() > 1){
                System.out.println("ALERT ARRAY DIMENSION HIGHER THAN 1 => "+realType.getDimensions());
                String objDesc = Type.getType(Object.class).getDescriptor();
                if(realType.getElementType().equals(Type.FLOAT_TYPE)){
                    mv.visitLdcInsn(realType.getDimensions());
                    mv.visitMethodInsn(INVOKESTATIC, CFW_N, "convertArrayToReal", "("+objDesc+"I)"+objDesc);
                    mv.visitTypeInsn(CHECKCAST, realType.getDescriptor());
                }
                else if(realType.getElementType().equals(Type.DOUBLE_TYPE)){
                    mv.visitLdcInsn(realType.getDimensions());
                    mv.visitMethodInsn(INVOKESTATIC, CDW_N, "convertArrayToReal", "("+objDesc+"I)"+objDesc);
                    mv.visitTypeInsn(CHECKCAST, realType.getDescriptor());
                }
                return;
            }
            if(realType.getElementType().equals(Type.FLOAT_TYPE)){
                mv.visitMethodInsn(INVOKESTATIC, CFW_N, "convertArray", "("+afterFloatReplacement(realType).getDescriptor()+")"+realType.getDescriptor());
            }
            else if(realType.getElementType().equals(Type.DOUBLE_TYPE)){
                mv.visitMethodInsn(INVOKESTATIC, CDW_N, "convertArray", "("+afterFloatReplacement(realType).getDescriptor()+")"+realType.getDescriptor());
            }
        }
    }
    

    private int getLoadOpcode(Type type){
        switch(type.getSort()){
            case Type.ARRAY:
            case Type.OBJECT:
                return ALOAD;
            case Type.CHAR:
                return CALOAD;
            case Type.DOUBLE:
                return DLOAD;
            case Type.FLOAT:
                return FLOAD;
			case Type.BOOLEAN:
            case Type.BYTE:
            case Type.SHORT:
            case Type.INT:
                return ILOAD;
            case Type.LONG:
                return LLOAD;
        }
        return -1;
    }
    
}
