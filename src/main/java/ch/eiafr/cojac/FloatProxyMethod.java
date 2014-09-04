/*
 * *
 *    Copyright 2014 Frédéric Bapst & Romain Monnard
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

import static ch.eiafr.cojac.models.FloatReplacerClasses.*;
import static ch.eiafr.cojac.FloatReplacerMethodVisitor.DN_NAME;
import static ch.eiafr.cojac.FloatReplacerMethodVisitor.FN_NAME;
import static ch.eiafr.cojac.instrumenters.InvokableMethod.*;
import java.util.HashMap;
import org.objectweb.asm.MethodVisitor;
import static org.objectweb.asm.Opcodes.*;
import org.objectweb.asm.Type;

public class FloatProxyMethod {
    private final CojacClassVisitor ccv;
    
    private static final String FL_NAME = Type.getType(Float.class).getInternalName();
    private static final String FL_DESCR = Type.getType(Float.class).getDescriptor();
    
    private static final String DL_NAME = Type.getType(Double.class).getInternalName();
    private static final String DL_DESCR = Type.getType(Double.class).getDescriptor();
    
	private static final String COJAC_TYPE_CONVERT_NAME = "COJAC_TYPE_CONVERT";
    
    private final String classPath;
    
    public FloatProxyMethod(CojacClassVisitor ccv, String classPath){
        this.ccv = ccv;
        this.classPath = classPath;
    }
    
    public void proxyCall(MethodVisitor mv, int opcode, String owner, String name, String desc){
		HashMap<Integer, Type> convertedArrays = convertArgumentsToReal(mv, desc, opcode, owner);
		
		mv.visitMethodInsn(opcode, owner, name, desc, (opcode == INVOKEINTERFACE));
		
		checkArraysAfterCall(mv, convertedArrays, desc);
		
		convertReturnType(mv, desc);
    }
    
    public void nativeCall(MethodVisitor mv, int access, String owner, String name, String desc){
		HashMap<Integer, Type> convertedArrays;
        boolean isStatic = (access & ACC_STATIC) > 0;
		
		String newDesc = replaceFloatMethodDescription(desc);
		
		MethodVisitor newMv = ccv.addProxyMethod(access & ~ACC_NATIVE, name, newDesc, null, null);

		int varIndex = 0;
		int opcode = INVOKESTATIC;
		if(!isStatic){
			newMv.visitVarInsn(ALOAD, 0);
			varIndex = 1;
			opcode = INVOKEVIRTUAL;
		}
		
		Type args[] = Type.getArgumentTypes(newDesc);
		for (Type type : args) {
			newMv.visitVarInsn(getLoadOpcode(type), varIndex);
			varIndex += type.getSize();
		}
		
		
		convertedArrays = convertArgumentsToReal(newMv, desc, opcode, owner);

		
		newMv.visitMethodInsn(opcode, owner, name, desc, false);
		
        
		checkArraysAfterCall(newMv, convertedArrays, desc);
		
		convertReturnType(newMv, desc);
		
		newMv.visitInsn(afterFloatReplacement(Type.getReturnType(desc)).getOpcode(IRETURN));
        newMv.visitMaxs(0, 0);
    }
	
	private HashMap<Integer, Type> convertArgumentsToReal(MethodVisitor mv, String desc, int opcode, String owner){
		HashMap<Integer, Type> typeConversions = new HashMap<>();
		HashMap<Integer, Type> convertedArrays;
		
		Type outArgs[] = Type.getArgumentTypes(desc);
		Type inArgs[] = new Type[outArgs.length];
		for (int i = 0; i < outArgs.length; i++) {
			inArgs[i] = afterFloatReplacement(outArgs[i]);
			if(inArgs[i].equals(outArgs[i]) == false){
				typeConversions.put(i, outArgs[i]);
			}
		}
		
		/*
		// TODO - if no convertion, do not call the convertion to improve a
		// little bit the performance but do not forget to convert the owner in
		// case of invokevirtual...
		if(Arrays.equals(inArgs, outArgs)){
			return;
		}
		*/
		
		String convertDesc = Type.getMethodDescriptor(Type.getType("[Ljava/lang/Object;"), inArgs);
		
		convertedArrays = createConvertMethod(convertDesc, inArgs, outArgs, typeConversions);
		mv.visitMethodInsn(INVOKESTATIC, classPath, COJAC_TYPE_CONVERT_NAME, convertDesc, false);
		
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
		
		// Keep the reference of converted arrays
		if(opcode == INVOKEVIRTUAL || opcode == INVOKEINTERFACE || opcode == INVOKESPECIAL){
			mv.visitInsn(DUP_X1);
		}
		else{
			mv.visitInsn(DUP);
		}
		
		
		// Explode the object array to put arguments on stack
		for(int i=0 ; i < outArgs.length ; i++){
			mv.visitInsn(DUP);
			mv.visitLdcInsn(i);
			mv.visitInsn(AALOAD);
			
			if(outArgs[i].getSort() == Type.ARRAY){
				mv.visitTypeInsn(CHECKCAST, "["+Type.getType(Object.class).getDescriptor());
				mv.visitLdcInsn(1);
				mv.visitInsn(AALOAD);
			}
			
			if(outArgs[i].getSort() == Type.ARRAY || outArgs[i].getSort() == Type.OBJECT){ // else: primitive type
				mv.visitTypeInsn(CHECKCAST, outArgs[i].getInternalName());
			}
			else{
				mv.visitTypeInsn(CHECKCAST, getPrimitiveWrapper(outArgs[i]).getInternalName());
				mv.visitMethodInsn(INVOKEVIRTUAL, getPrimitiveWrapper(outArgs[i]).getInternalName(), getWrapperToPrimitiveMethod(outArgs[i]), "()"+outArgs[i].getDescriptor(), false);
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
		
		return convertedArrays;
	}
	
	private void convertReturnType(MethodVisitor mv, String desc){
		Type returnType = Type.getReturnType(desc);
		Type cojacType = afterFloatReplacement(returnType);
		if(returnType.equals(cojacType) == false){
			convertRealToCojacType(returnType, mv);
		}
		
		if(returnType.equals(Type.getType(Object.class))){
			convertObjectToCojac(mv, returnType);
		}
		else if(returnType.getSort() == Type.ARRAY && returnType.getElementType().equals(Type.getType(Object.class))){
			convertObjectToCojac(mv, returnType);
		}
	}
	
	private HashMap<Integer, Type> createConvertMethod(String convertDesc, Type[] inArgs, Type[] outArgs, HashMap<Integer, Type> typeConversions){
		
		HashMap<Integer, Type> convertedArrays = new HashMap<>();
		
		 for (int i=0 ; i < outArgs.length; i++) {
			 if(inArgs[i].getSort() == Type.ARRAY){ // If it is an array, keep the two arrays references (cojac & original)
				convertedArrays.put(i, inArgs[i]);
			 }
		 }
		
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
				
				if(inArgs[i].getSort() == Type.ARRAY){ // If it is an array, keep the two arrays references (cojac & original)
					newMv.visitLdcInsn(2);
					newMv.visitTypeInsn(ANEWARRAY, Type.getType(Object.class).getInternalName());
					newMv.visitInsn(DUP_X1);
					newMv.visitInsn(SWAP);
					newMv.visitInsn(DUP_X1);
					newMv.visitLdcInsn(0);
					newMv.visitInsn(SWAP);
					newMv.visitInsn(AASTORE);
				}
				
				
                if(typeConversions.get(i) != null){
                    convertCojacToRealType(typeConversions.get(i), newMv);
                }
				else if(inArgs[i].equals(Type.getType(Object.class))){
					convertObjectToReal(newMv, inArgs[i]);
				}
				else if(inArgs[i].getSort() == Type.ARRAY && inArgs[i].getElementType().equals(Type.getType(Object.class))){
					convertObjectToReal(newMv, inArgs[i]);
				}
				convertPrimitiveToObject(newMv, type);
				
				if(inArgs[i].getSort() == Type.ARRAY){ // If it is an array, keep the two arrays references (cojac & original)
					newMv.visitInsn(SWAP);
					newMv.visitInsn(DUP_X1);
					newMv.visitInsn(SWAP);
					newMv.visitLdcInsn(1);
					newMv.visitInsn(SWAP);
					newMv.visitInsn(AASTORE);
				}
				
				newMv.visitInsn(AASTORE);
            }
			newMv.visitInsn(ARETURN);
            newMv.visitMaxs(0, 0);
		}
		return convertedArrays;
	}
    
	private void checkArraysAfterCall(MethodVisitor mv, HashMap<Integer, Type> convertedArrays, String desc){
		Type returnType = Type.getReturnType(desc);
		int returnTypeSize = returnType.getSize();
		if(returnTypeSize == 1){
			mv.visitInsn(SWAP);
		}
		else if(returnTypeSize == 2){
			mv.visitInsn(DUP2_X1); // D D Object D D
			mv.visitInsn(POP2); // D D Object
		}
		for(int pos: convertedArrays.keySet()){
			Type type = convertedArrays.get(pos);
			mv.visitInsn(DUP);
			mv.visitLdcInsn(pos);
			mv.visitInsn(AALOAD);
			mv.visitTypeInsn(CHECKCAST, "["+Type.getType(Object.class).getDescriptor());
			
			mv.visitInsn(DUP);
			mv.visitLdcInsn(0);
			mv.visitInsn(AALOAD);
			mv.visitInsn(SWAP);
			mv.visitLdcInsn(1);
			mv.visitInsn(AALOAD);
			// mergeOriginalArrayIntoCojac
			String objDesc = Type.getType(Object.class).getDescriptor();
			mv.visitMethodInsn(INVOKESTATIC, DN_NAME, "mergeOriginalArrayIntoCojac", "("+objDesc+objDesc+")V", false);
			
		}
		mv.visitInsn(POP);
	}

	public static void convertRealToCojacType(Type realType, MethodVisitor mv){
        if(realType.equals(Type.FLOAT_TYPE)){
            mv.visitMethodInsn(INVOKESTATIC, COJAC_FLOAT_WRAPPER_INTERNAL_NAME, "fromFloat", "(F)"+COJAC_FLOAT_WRAPPER_TYPE_DESCR, false);
        }
        else if(realType.equals(Type.getType(Float.class))){
            mv.visitMethodInsn(INVOKESTATIC, COJAC_FLOAT_WRAPPER_INTERNAL_NAME, "fromFloat", "("+Type.getType(Float.class).getDescriptor()+")"+COJAC_FLOAT_WRAPPER_TYPE_DESCR, false);
        }
        else if(realType.equals(Type.DOUBLE_TYPE)){
            mv.visitMethodInsn(INVOKESTATIC, COJAC_DOUBLE_WRAPPER_INTERNAL_NAME, "fromDouble", "(D)"+COJAC_DOUBLE_WRAPPER_TYPE_DESCR, false);
        }
        else if(realType.equals(Type.getType(Double.class))){
            mv.visitMethodInsn(INVOKESTATIC, COJAC_DOUBLE_WRAPPER_INTERNAL_NAME, "fromDouble", "("+Type.getType(Double.class).getDescriptor()+")"+COJAC_DOUBLE_WRAPPER_TYPE_DESCR, false);
        }
        else if(realType.getSort() == Type.ARRAY){
            if(realType.getElementType().equals(Type.FLOAT_TYPE) || realType.getElementType().equals(Type.DOUBLE_TYPE)){ // TODO better code
                String objDesc = Type.getType(Object.class).getDescriptor();
				mv.visitTypeInsn(CHECKCAST, Type.getType(Object.class).getInternalName());
				mv.visitLdcInsn(realType.getDimensions());
                if(realType.getElementType().equals(Type.FLOAT_TYPE)){
                    mv.visitMethodInsn(INVOKESTATIC, FN_NAME, "convertArrayToCojac", "("+objDesc+"I)"+objDesc, false);
                }
                else if(realType.getElementType().equals(Type.DOUBLE_TYPE)){
                    mv.visitMethodInsn(INVOKESTATIC, DN_NAME, "convertArrayToCojac", "("+objDesc+"I)"+objDesc, false);
                }
				mv.visitTypeInsn(CHECKCAST, afterFloatReplacement(realType).getInternalName());
            }
        }
    }
    
    public static void convertCojacToRealType(Type realType, MethodVisitor mv){
        if(realType.equals(Type.FLOAT_TYPE)){
            mv.visitMethodInsn(INVOKESTATIC, COJAC_FLOAT_WRAPPER_INTERNAL_NAME, "toFloat", "("+COJAC_FLOAT_WRAPPER_TYPE_DESCR+")F", false);
        }
        else if(realType.equals(Type.getType(Float.class))){
            mv.visitMethodInsn(INVOKESTATIC, COJAC_FLOAT_WRAPPER_INTERNAL_NAME, "toRealFloatWrapper", "("+COJAC_FLOAT_WRAPPER_TYPE_DESCR+")"+FL_DESCR, false);
        }
        else if(realType.equals(Type.DOUBLE_TYPE)){
            mv.visitMethodInsn(INVOKESTATIC, COJAC_DOUBLE_WRAPPER_INTERNAL_NAME, "toDouble", "("+COJAC_DOUBLE_WRAPPER_TYPE_DESCR+")D", false);
        }
        else if(realType.equals(Type.getType(Double.class))){
            mv.visitMethodInsn(INVOKESTATIC, COJAC_DOUBLE_WRAPPER_INTERNAL_NAME, "toRealDoubleWrapper", "("+COJAC_DOUBLE_WRAPPER_TYPE_DESCR+")"+DL_DESCR, false);
        }
        else if(realType.getSort() == Type.ARRAY){
			if(realType.getElementType().equals(Type.FLOAT_TYPE) || realType.getElementType().equals(Type.DOUBLE_TYPE)){
				String objDesc = Type.getType(Object.class).getDescriptor();
				mv.visitTypeInsn(CHECKCAST, Type.getType(Object.class).getInternalName());
				mv.visitLdcInsn(realType.getDimensions());
				if(realType.getElementType().equals(Type.FLOAT_TYPE)){
					mv.visitMethodInsn(INVOKESTATIC, FN_NAME, "convertArrayToReal", "("+objDesc+"I)"+objDesc, false);
				}
				else if(realType.getElementType().equals(Type.DOUBLE_TYPE)){
					mv.visitMethodInsn(INVOKESTATIC, DN_NAME, "convertArrayToReal", "("+objDesc+"I)"+objDesc, false);
				}
				mv.visitTypeInsn(CHECKCAST, realType.getInternalName());
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
		newMv.visitMethodInsn(INVOKESTATIC, wrapper.getInternalName(), "valueOf", "("+type.getDescriptor()+")"+wrapper.getDescriptor(), false);
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
	
	private void convertObjectToReal(MethodVisitor mv, Type objType){
		String objDesc = Type.getType(Object.class).getDescriptor();
		mv.visitTypeInsn(CHECKCAST, Type.getType(Object.class).getInternalName());
		mv.visitMethodInsn(INVOKESTATIC, DN_NAME, "convertFromObjectToReal", "("+objDesc+")"+objDesc, false);
		mv.visitTypeInsn(CHECKCAST, objType.getInternalName());
	}
	
	private void convertObjectToCojac(MethodVisitor mv, Type objType){
		String objDesc = Type.getType(Object.class).getDescriptor();
		mv.visitTypeInsn(CHECKCAST, Type.getType(Object.class).getInternalName());
		mv.visitMethodInsn(INVOKESTATIC, DN_NAME, "convertFromObjectToCojac", "("+objDesc+")"+objDesc, false);
		mv.visitTypeInsn(CHECKCAST, afterFloatReplacement(objType).getInternalName());
	}
	
}
