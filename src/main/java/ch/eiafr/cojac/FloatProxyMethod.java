/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.eiafr.cojac;

import static ch.eiafr.cojac.FloatProxyMethod_old.convertCojacToRealType;
import static ch.eiafr.cojac.FloatProxyMethod_old.convertRealToCojacType;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import static ch.eiafr.cojac.instrumenters.InvokableMethod.*;
import java.util.HashMap;
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
	private static final String COJAC_TYPE_CONVERT_NAME = "COJAC_TYPE_CONVERT";
    
    private final String classPath;
    
    public FloatProxyMethod(CojacClassVisitor ccv, String classPath){
        this.ccv = ccv;
        this.classPath = classPath;
    }
    
    public void proxyCall(MethodVisitor mv, int opcode, String owner, String name, String desc){
		convertArgumentsToReal(mv, desc, opcode, owner);
		
		mv.visitMethodInsn(opcode, owner, name, desc);
		
		Type returnType = Type.getReturnType(desc);
		Type cojacType = afterFloatReplacement(returnType);
		if(returnType.equals(cojacType) == false){
			convertRealToCojacType(returnType, mv);
		}
    }
    
    public void nativeCall(MethodVisitor mv, int access, String owner, String name, String desc){
        boolean isStatic = (access & ACC_STATIC) > 0;
		
		String newDesc = replaceFloatMethodDescription(desc);
		
		MethodVisitor newMv = ccv.addProxyMethod(access & ~ACC_NATIVE, name, newDesc, null, null);

		convertArgumentsToReal(newMv, desc, 0, owner);
		
		if(isStatic)
            newMv.visitMethodInsn(INVOKESTATIC, owner, name, desc);
        else
            newMv.visitMethodInsn(INVOKEVIRTUAL, owner, name, desc);
        
		newMv.visitInsn(afterFloatReplacement(Type.getReturnType(desc)).getOpcode(IRETURN));
        newMv.visitMaxs(0, 0);
    }
    
    public void proxyNative(MethodVisitor mv, int access, String owner, String name, String desc){
		System.out.println("PROXY NATIVER NOT IMPLEMENTED");
        /*
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
        */
    }
	
	private void convertArgumentsToReal(MethodVisitor mv, String desc, int opcode, String owner){
		HashMap<Integer, Type> typeConversions = new HashMap<>();
		Type outArgs[] = Type.getArgumentTypes(desc);
		Type inArgs[] = new Type[outArgs.length];
		for (int i = 0; i < outArgs.length; i++) {
			inArgs[i] = afterFloatReplacement(outArgs[i]);
			if(inArgs[i].equals(outArgs[i]) == false){
				typeConversions.put(i, outArgs[i]);
			}
		}
		
		//if(Arrays.equals(inArgs, outArgs)){
		//	System.out.println("no convertions");
		//	return;
		//}
		
		String convertDesc = Type.getMethodDescriptor(Type.getType("[Ljava/lang/Object;"), inArgs);
		
		
		if(!ccv.isProxyMerhod(COJAC_TYPE_CONVERT_NAME, convertDesc)){
			 MethodVisitor newMv = ccv.addProxyMethod(ACC_STATIC, COJAC_TYPE_CONVERT_NAME, convertDesc, null, null);
			 int varIndex = 0;
			 
			 newMv.visitLdcInsn(outArgs.length);
			 newMv.visitTypeInsn(ANEWARRAY, Type.getType(Object.class).getInternalName());
			 
			 for (int i=0 ; i < outArgs.length; i++) {
				newMv.visitInsn(DUP);
				newMv.visitLdcInsn(i);
                Type type = outArgs[i];
                newMv.visitVarInsn(getLoadOpcode(inArgs[i]), varIndex);
                varIndex += inArgs[i].getSize();
                if(typeConversions.get(i) != null){
                    convertCojacToRealType(typeConversions.get(i), newMv);
                }
				if(inArgs[i].equals(Type.getType(Object.class))){
					convetObject(newMv, inArgs[i]);
				}
				if(inArgs[i].getSort() == Type.ARRAY && inArgs[i].getElementType().equals(Type.getType(Object.class))){
					convetObjectArray(newMv, inArgs[i]);
				}
				convertPrimitiveToObject(newMv, type);
				newMv.visitInsn(AASTORE);
            }
			newMv.visitInsn(ARETURN);
            newMv.visitMaxs(0, 0);
		}
		mv.visitMethodInsn(INVOKESTATIC, classPath, COJAC_TYPE_CONVERT_NAME, convertDesc);
		
		// convertion of owner if invokevirtual
		if(opcode == INVOKEVIRTUAL){
			if(owner.equals(Type.getType(Float.class).getInternalName())){
				mv.visitInsn(SWAP);
				convertCojacToRealType(Type.getType(Float.class), mv);
				mv.visitInsn(SWAP);
			}
			if(owner.equals(Type.getType(Double.class).getInternalName())){
				mv.visitInsn(SWAP);
				convertCojacToRealType(Type.getType(Double.class), mv);
				mv.visitInsn(SWAP);
			}
		}
		
		// Explode the object array to put arguments on stack
		for(int i=0 ; i < outArgs.length ; i++){
			mv.visitInsn(DUP);
			mv.visitLdcInsn(i);
			mv.visitInsn(AALOAD);
			if(outArgs[i].getSort() == Type.ARRAY || outArgs[i].getSort() == Type.OBJECT){ // else: primitive type
				mv.visitTypeInsn(CHECKCAST, outArgs[i].getInternalName());
			}
			else{
				mv.visitTypeInsn(CHECKCAST, getPrimitiveWrapper(outArgs[i]).getInternalName());
				mv.visitMethodInsn(INVOKEVIRTUAL, getPrimitiveWrapper(outArgs[i]).getInternalName(), getWrapperToPrimitiveMethod(outArgs[i]), "()"+outArgs[i].getDescriptor());
			}
			
			if(outArgs[i].getSize() == 2){ // Swap when double or long: Object D D
				mv.visitInsn(DUP2_X1); // D D Object D D
				mv.visitInsn(POP2); // D D Object
			}
			else{
				mv.visitInsn(SWAP);
			}
		}
		mv.visitInsn(POP);
		
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
            if(realType.getDimensions() > 1){ // TODO better code
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
            if(realType.getDimensions() > 1){ // TODO better code
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
            case Type.DOUBLE:
                return DLOAD;
            case Type.FLOAT:
                return FLOAD;
			case Type.CHAR:
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

	private void convertPrimitiveToObject(MethodVisitor newMv, Type type) {
		if(type.getSort() == Type.ARRAY || type.getSort() == Type.OBJECT)
			return;
		Type wrapper = getPrimitiveWrapper(type);
		newMv.visitMethodInsn(INVOKESTATIC, wrapper.getInternalName(), "valueOf", "("+type.getDescriptor()+")"+wrapper.getDescriptor());
	}
	
	private Type getPrimitiveWrapper(Type primitiveType){
		switch(primitiveType.getSort()){
			case Type.BOOLEAN: return Type.getType(Boolean.class);
			case Type.BYTE: return Type.getType(Byte.class);
			case Type.CHAR: return Type.getType(Character.class);
			case Type.DOUBLE: return Type.getType(Double.class);
			case Type.FLOAT: return Type.getType(Float.class);
			case Type.INT: return Type.getType(Integer.class);
			case Type.LONG: return Type.getType(Long.class);
			case Type.SHORT: return Type.getType(Short.class);
		}
		return null;
	}
	
	private String getWrapperToPrimitiveMethod(Type primitiveType){
		switch(primitiveType.getSort()){
			case Type.BOOLEAN:return "booleanValue";
			case Type.BYTE: return "byteValue";
			case Type.CHAR: return "charValue";
			case Type.DOUBLE: return "doubleValue";
			case Type.FLOAT: return "floatValue";
			case Type.INT: return "intValue";
			case Type.LONG: return "longValue";
			case Type.SHORT: return "shortValue";
		}
		return null;
	}
	
	private void convetObject(MethodVisitor mv, Type objType){
		String objDesc = Type.getType(Object.class).getDescriptor();
		mv.visitMethodInsn(INVOKESTATIC, CDW_N, "convertFromObject", "("+objDesc+")"+objDesc);
	}
	
	private void convetObjectArray(MethodVisitor mv, Type objType){
		String objDesc = Type.getType(Object.class).getDescriptor();
		mv.visitTypeInsn(CHECKCAST, Type.getType(Object.class).getInternalName());
		mv.visitMethodInsn(INVOKESTATIC, CDW_N, "convertFromObjectArray", "("+objDesc+")"+objDesc);
		mv.visitTypeInsn(CHECKCAST, objType.getInternalName());
	}
	
}
