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
import static ch.eiafr.cojac.instrumenters.ReplaceFloatsMethods.FL_DESCR;
import static ch.eiafr.cojac.instrumenters.ReplaceFloatsMethods.DL_DESCR;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AnalyzerAdapter;

import ch.eiafr.cojac.FloatReplaceClassVisitor.MyLocalAdder;

public class FloatProxyMethod {
    private static final Type JWRAPPER_FLOAT_TYPE  = Type.getType(Float.class);
    private static final Type JWRAPPER_DOUBLE_TYPE = Type.getType(Double.class);
    private static final Type OBJ_ARRAY_TYPE=Type.getType("[Ljava/lang/Object;");
    private static final Type   OBJ_TYPE = Type.getType(Object.class);
    private static final String OBJ_DESC = OBJ_TYPE.getDescriptor();
    
	private static final String COJAC_TYPE_CONVERT_NAME = "COJAC_TYPE_CONVERT";
    
    private final FloatReplaceClassVisitor ccv;
    private AnalyzerAdapter aaAfter;
    private MyLocalAdder mla;
    private final String crtClassName;
    
    public FloatProxyMethod(FloatReplaceClassVisitor ccv, String classPath){
        this.ccv = ccv;
        this.crtClassName = classPath;
    }
    
    public void nativeCall(MethodVisitor mv, int access, String owner, String name, String desc){
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
        ConversionContext cc=new ConversionContext(opcode, owner, name, desc);
		Type args[] = Type.getArgumentTypes(newDesc);
		for (Type type : args) {
			newMv.visitVarInsn(getLoadOpcode(type), varIndex);
			varIndex += type.getSize();
		}
        convertArgumentsToReal(mv, cc);           
        // stack >> [target] allParamsArr [target] allParamsArr
        maybeConvertTarget(mv, cc.opcode, cc.owner);
        // stack >> [target] allParamsArr [newTarget] allParamsArr
        explodeOnStack(mv, cc, true); 
        // stack >> [target] allParamsArr [newTarget] nprm0 nprm1 nprm2...
		newMv.visitMethodInsn(opcode, owner, name, desc, false);       
        // stack >> [target] allParamsArr [possibleResult]
		checkArraysAfterCall(newMv, cc.convertedArrays, desc);		
        // stack >> [target] [possibleResult]
		convertReturnType(newMv, desc);		
        // stack >> [target] [newPossibleResult]
		newMv.visitInsn(afterFloatReplacement(Type.getReturnType(desc)).getOpcode(IRETURN));
        // stack >> [target]       // left on the stack... not a problem!
        newMv.visitMaxs(0, 0);
    }
    
    public void proxyCall(MethodVisitor mv, int opcode, String owner, 
            String name, String desc, boolean tryUnproxied){
        if (tryUnproxied && opcode==INVOKEVIRTUAL ) { //|| opcode==INVOKEINTERFACE  false && 
            //proxyCallAndStupidVars(mv, opcode, owner, name, desc);
            proxyCallBetterWithVars(mv, opcode, owner, name, desc);
            return;
        }
        ConversionContext cc=new ConversionContext(opcode, owner, name, desc);
        // stack >> [target] nprm0 nprm1 nprm2...
        convertArgumentsToReal(mv, cc);           
        // stack >> [target] allParamsArr [target] allParamsArr
        maybeConvertTarget(mv, cc.opcode, cc.owner);
        // stack >> [target] allParamsArr [newTarget] allParamsArr
        explodeOnStack(mv, cc, true); 
        // stack >> [target] allParamsArr [newTarget] nprm0 nprm1 nprm2...
        mv.visitMethodInsn(opcode, owner, name, desc, (opcode == INVOKEINTERFACE));
        // stack >> [target] allParamsArr [possibleResult]
        checkArraysAfterCall(mv, cc.convertedArrays, desc);
        // stack >> [target] [possibleResult]
        int resultWidth=Type.getReturnType(desc).getSize();
        if(hasTarget(opcode)) {
            if(resultWidth==0) {
                mv.visitInsn(POP);
            } else if (resultWidth==1) {
                mv.visitInsn(SWAP);
                mv.visitInsn(POP);
            } else { // resultWidth==2) 
                mv.visitInsn(DUP2_X1);
                // stack >> possibleResult target possibleResult
                mv.visitInsn(POP2);
                // stack >> possibleResult target
                mv.visitInsn(POP);
                // stack >> possibleResult
            }
        }
        // stack >> [possibleResult]
        convertReturnType(mv, desc);
        // stack >> [newPossibleResult]
    }

    
    // it does not work at all...  GOSH !
    // OK, try-catch was not the right way to detect if the call will be reasonable
    // it's the bytecode verifier that can't accept bad parameters...
    // AND above all, the stack is emptied when jumping to the catch() section,
    // so all the slots before <target> are lost!!
    // Maybe try to call a util function isCallable(obj, owner, name, descAfter)
    // that will check that via reflection... Well, we'll have to invoke the method
    // by reflection (the target cannot be cast to something known at runtime!
   public void proxyCallBetterWithVars(MethodVisitor mv, int opcode, String owner, String name, String desc){
        final String pm = "possibleMethod";
        final String pmDesc = "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/reflect/Method;";
        final String myInvoke = "myInvoke";
        final String myInvokeDesc = "(Ljava/lang/reflect/Method;Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;";
        int paramArrayVar=-1;
        int targetVar=-1;
        //checkNotNullStack();
        Object[] stackEInFrame = aaAfter.stack.toArray();
        paramArrayVar = mla.paramArrayVar();
        targetVar = mla.targetVar();
        //Label lEndTry = new Label(); 
        Label lBeginHandler = new Label(), lEndHandler = new Label();
        String descAfter=replaceFloatMethodDescription(desc);
        Type cojReturnType=Type.getReturnType(descAfter);
        ConversionContext cc=new ConversionContext(opcode, owner, name, desc);
        // stack >> target prm0 prm1 prm2...
        convertArgumentsToReal(mv, cc);
        // stack >> target allParamsArr target allParamsArr
        mv.visitVarInsn(ASTORE, paramArrayVar);
        // stack >> target allParamsArr target
        String targetType=stackTopClass();
        mv.visitVarInsn(ASTORE, targetVar);
        // stack >> target allParamsArr
        Object[] localsInFrame = aaAfter.locals.toArray();
        mv.visitInsn(SWAP);
        // stack >> allParamsArr target
        mv.visitInsn(DUP_X1);
        // stack >> target allParamsArr target
        mv.visitLdcInsn(name);
        // stack >> target allParamsArr target methName
        mv.visitLdcInsn(descAfter);
        // stack >> target allParamsArr target methName methDesc
        mv.visitMethodInsn(INVOKESTATIC, DN_NAME, pm, pmDesc, false);
        // stack >> target allParamsArr nullOrMeth
        mv.visitInsn(DUP);
        // stack >> target allParamsArr nullOrMeth nullOrMeth
        
        ArrayList<Object> scl=new ArrayList<Object>(aaAfter.stack);
        scl.remove(scl.size()-1); // remove the last nullOrMeth
        Object[] stackContent=scl.toArray(); //target allParamsArr nullOrMeth
        
        mv.visitJumpInsn(IFNULL, lBeginHandler);
        aaAfter.visitFrame(F_NEW, localsInFrame.length, localsInFrame, 
                                  stackContent.length, stackContent);
        // stack >> target allParamsArr meth
        mv.visitInsn(DUP_X2);
        // stack >> meth target allParamsArr meth
        mv.visitInsn(POP);
        // stack >> meth target allParamsArr
        mv.visitLdcInsn(cc.inArgs.length);
        // stack >> meth target allParamsArr n
        mv.visitInsn(AALOAD);
        // stack >> meth target asObjPrm
        mv.visitTypeInsn(CHECKCAST, "["+OBJ_TYPE.getDescriptor());
        // stack >> meth target appropriatePrmArr 
        mv.visitMethodInsn(INVOKESTATIC, DN_NAME, myInvoke, myInvokeDesc, false);
        // stack >> [ResultOrNull]
        if (cc.hasReturn()) {
            if (cc.hasPrimitiveResultInCojacVersion()) {
                String jWrapperName=getJWrapper(cojReturnType).getInternalName();
                mv.visitTypeInsn(CHECKCAST, jWrapperName);
                mv.visitMethodInsn(INVOKEVIRTUAL, jWrapperName, getWrapperToPrimitiveMethod(cojReturnType), "()"+cojReturnType.getDescriptor(), false);
            } else {
                mv.visitTypeInsn(CHECKCAST, cojReturnType.getInternalName());
            }
        } else {
            mv.visitInsn(POP); // discard the dummy null result
        }
        // stack >> [possibleResult]
        //;; mv.visitLabel(lEndTry);
        mv.visitJumpInsn(GOTO, lEndHandler);  // and we're done !

        ;; mv.visitLabel(lBeginHandler);
        // stack >> target allParamsArr null
        // CAUTION: we bypass 'mv' and directly talk to the AnalyzerAdapter!
        aaAfter.visitFrame(F_NEW, localsInFrame.length, localsInFrame, 
                                   stackContent.length, stackContent);
        checkNotNullStack();  //TODO: remove
        mv.visitInsn(POP); // we drop the null (no-method) slot
        maybeConvertTarget(mv, cc.opcode, cc.owner);
        // stack >> newTarget allParamsArr
        mv.visitInsn(DUP_X1);
        // stack >> allParamsArr newTarget allParamsArr
        explodeOnStack(mv, cc, true); 
        // stack >> allParamsArr newTarget nprm0 nprm1 nprm2...
        mv.visitMethodInsn(opcode, owner, name, desc, (opcode == INVOKEINTERFACE));
        // stack >> allParamsArr [possibleResult]
        checkArraysAfterCall(mv, cc.convertedArrays, desc);
        // stack >> [possibleResult]
        convertReturnType(mv, desc);
        // stack >> [newPossibleResult]
        ;; mv.visitLabel(lEndHandler);
        scl.remove(scl.size()-1); // remove 3 slots: target allParamsArr nullOrMeth
        scl.remove(scl.size()-1); 
        scl.remove(scl.size()-1); 
        stackContent=scl.toArray();
        stackContent=addReturnTypeTo(stackContent, cojReturnType);
        aaAfter.visitFrame(F_NEW, localsInFrame.length, localsInFrame, 
                                   stackContent.length, stackContent);
    }
// if(crtClassName.contains("RunWindow")) System.out.println("E: "+aaAfter.stack);
// if(crtClassName.contains("RunWindow")) System.out.println("F: "+java.util.Arrays.toString(stackContent));

    // CAUTION/WRONG: according to ASM User Guide, "...the stack is cleared,
    // the exception is pushed on this empty stack, and execution continues at catch"
    // public void proxyCallBetterWithoutVars(...){

    private Object[] removeTargetAndParams(Object[] stackEInFrame, int nParam) {
        Object[] res=new Object[stackEInFrame.length-nParam-1]; // -1 for the target
        for(int i=0; i<res.length; i++)
            res[i]=stackEInFrame[i];
        return res;
    }

    private Object[] addReturnTypeTo(Object[] stackContent, Type returnType) {
        int sort=returnType.getSort();
        if (sort==Type.VOID) return stackContent;
        if (sort==Type.ARRAY || sort==Type.OBJECT) 
            return addTo(stackContent, returnType.getInternalName());
        // else primitive type
        switch (sort) {
        case Type.BOOLEAN:
        case Type.BYTE:
        case Type.CHAR:
        case Type.SHORT:
        case Type.INT:
            return addTo(stackContent, Opcodes.INTEGER);
        case Type.LONG:
            return addTo(stackContent, Opcodes.LONG);
            //return addTo(addTo(stackContent, Opcodes.LONG), Opcodes.TOP);
        }
        return stackContent;
    }

    private Object[] addTo(Object[] s, Object e) {
        Object[] t=new Object[s.length+1];
        System.arraycopy(s, 0, t, 0, s.length);
        t[t.length-1]=e;
        return t;
    }

    private String stackTopClass() {
        Object o = this.stackTop(); // that's taken from aaAfter indeed...
        if (o instanceof String) return (String) o;
        return null;
    }
    
    private void checkNotNullStack() {
        if (aaAfter.stack != null) return; 
        throw new RuntimeException("STUPID NULL stack!!!");
    }
    
    public boolean needsConversion(String owner, String name, String desc) {
        if(owner.equals(JWRAPPER_FLOAT_TYPE .getInternalName())) return true;
        if(owner.equals(JWRAPPER_DOUBLE_TYPE.getInternalName())) return true;
        if(name.equals("clone") && desc.equals("()Ljava/lang/Object;")) return true;
        for (Type t:Type.getArgumentTypes(desc))
            if (needsConversion(t)) return true;
        return needsConversion(Type.getReturnType(desc));
    }
    
    private static boolean needsConversion(Type t) {
        return ! afterFloatReplacement(t).equals(t);
    }
	
    private void convertArgumentsToReal(MethodVisitor mv, ConversionContext cc){        
        String convertDesc = Type.getMethodDescriptor(OBJ_ARRAY_TYPE, cc.inArgs);
        createConvertMethod(convertDesc, cc);
        // stack >> [target] prm0 prm1 prm2...
        mv.visitMethodInsn(INVOKESTATIC, crtClassName, COJAC_TYPE_CONVERT_NAME, convertDesc, false);
        // stack >> [target] allParamsArr
        // We'll need a reference to converted arrays (for merging purposes)
        // and a copy of the target (trying first without proxy)...
        if(hasTarget(cc.opcode)){
            mv.visitInsn(DUP2);
        } else {
            mv.visitInsn(DUP);
        }
        // stack >> [target] allParamsArr [target] allParamsArr
    }

    private static boolean hasTarget(int opcode) {
        return opcode == INVOKEVIRTUAL || opcode == INVOKEINTERFACE || opcode == INVOKESPECIAL;
    }
    
    private static void explodeOnStack(MethodVisitor mv, ConversionContext cc, boolean wantTheConversion) {
        Type[] args = wantTheConversion ? cc.outArgs : cc.inArgs;
        // stack >> ... allParamsArr
        for(int i=0 ; i < args.length ; i++) {
            // stack >> ... allParamsArr
            Type oa=args[i];
            int oaSort=oa.getSort();
            mv.visitInsn(DUP);
            mv.visitLdcInsn(i);
            mv.visitInsn(AALOAD);
            boolean keepBothVersions = (oa.getSort() == Type.ARRAY);
            if(keepBothVersions){
                mv.visitTypeInsn(CHECKCAST, "["+OBJ_TYPE.getDescriptor());
                mv.visitLdcInsn(wantTheConversion?1:0);
                mv.visitInsn(AALOAD);
            }
            
            if(oaSort == Type.ARRAY || oaSort == Type.OBJECT) { // else: primitive type
                mv.visitTypeInsn(CHECKCAST, oa.getInternalName());
            } else {
                String jWrapperName=getJWrapper(oa).getInternalName();
                mv.visitTypeInsn(CHECKCAST, jWrapperName);
                mv.visitMethodInsn(INVOKEVIRTUAL, jWrapperName, getWrapperToPrimitiveMethod(oa), "()"+oa.getDescriptor(), false);
            }
            
            if(oa.getSize() == 2){     //    Object DD  Swap when double or long
                mv.visitInsn(DUP2_X1); // DD Object DD
                mv.visitInsn(POP2);    // DD Object
            } else {
                mv.visitInsn(SWAP);
            }
            // stack >> ... nprmI allParamsArr
        }
        mv.visitInsn(POP);
        // stack >> ... nprm0 nprm1 nprm2...
    }

    private static void maybeConvertTarget(MethodVisitor mv, int opcode, String owner) {
        // stack >> [target] allParamsArr
        if(!hasTarget(opcode)) return;
        // stack >> target allParamsArr
        if(opcode != INVOKEVIRTUAL) return;   // not sure for invokeinterface or invokedynamic...
        if(owner.equals(JWRAPPER_FLOAT_TYPE.getInternalName())) {
            mv.visitInsn(SWAP);
            convertCojacToRealType(JWRAPPER_FLOAT_TYPE, mv);
            mv.visitInsn(SWAP);
        } else if(owner.equals(JWRAPPER_DOUBLE_TYPE.getInternalName())) {
            mv.visitInsn(SWAP);
            convertCojacToRealType(JWRAPPER_DOUBLE_TYPE, mv);
            mv.visitInsn(SWAP);
        }
        Type ownerType=Type.getType(owner); // that case doesn't contain the preceding two
        Type afterType = afterFloatReplacement(ownerType);
        if (!ownerType.equals(afterType)) {
            mv.visitInsn(SWAP);
            convertCojacToRealType(ownerType, mv);
            mv.visitInsn(SWAP);
        }
        // stack >> [newTarget] allParamsArr
    }
	
	private static void convertReturnType(MethodVisitor mv, String desc) {
		Type returnType = Type.getReturnType(desc);
		Type cojacType = afterFloatReplacement(returnType);
		if(returnType.equals(cojacType) == false) {
			convertRealToCojacType(returnType, mv);
		}
		if(returnType.equals(OBJ_TYPE)){
			convertObjectToCojac(mv, returnType);
		} else if(returnType.getSort() == Type.ARRAY && returnType.getElementType().equals(OBJ_TYPE)){
			convertObjectToCojac(mv, returnType);
		}
	}
	
    private void createConvertMethod(String convertDesc, ConversionContext cc) {
        if(ccv.isProxyMethod(COJAC_TYPE_CONVERT_NAME, convertDesc))
            return; // the method already exists
        MethodVisitor newMv = ccv.addProxyMethod(ACC_STATIC, COJAC_TYPE_CONVERT_NAME, convertDesc, null, null);
        int varIndex = 0;
        newMv.visitLdcInsn(cc.outArgs.length+1); // additional cell for the AllAsObjects array
        newMv.visitTypeInsn(ANEWARRAY, OBJ_TYPE.getInternalName());
        // stack >> prmsArr
        newMv.visitInsn(DUP);
        newMv.visitLdcInsn(cc.outArgs.length);
        newMv.visitInsn(DUP);
        newMv.visitTypeInsn(ANEWARRAY, OBJ_TYPE.getInternalName());
        // stack >> prmsArr prmsArr n asObjArr
        newMv.visitInsn(AASTORE);
        // stack >> prmsArr
        
        for (int i=0 ; i < cc.outArgs.length; i++) {
            Type ia = cc.inArgs[i];
            newMv.visitInsn(DUP);
            newMv.visitInsn(DUP);
            newMv.visitLdcInsn(cc.outArgs.length);
            // stack >> prmsArr prmsArr prmsArr n
            newMv.visitInsn(AALOAD);
            newMv.visitTypeInsn(CHECKCAST, "["+OBJ_TYPE.getDescriptor());
            // stack >> prmsArr prmsArr asObjArr
            newMv.visitLdcInsn(i);
            // stack >> prmsArr prmsArr asObjArr i
            newMv.visitVarInsn(getLoadOpcode(ia), varIndex);
            convertPrimitiveToObject(newMv, cc.inArgs[i]);
            // stack >> prmsArr prmsArr asObjArr i pi
            newMv.visitInsn(AASTORE);
            // stack >> prmsArr prmsArr
            newMv.visitLdcInsn(i);
            // stack >> prmsArr prmsArr i
            newMv.visitVarInsn(getLoadOpcode(ia), varIndex);
            varIndex += cc.inArgs[i].getSize();
            // stack >> prmsArr prmsArr i pi
            boolean keepBothVersions = (ia.getSort() == Type.ARRAY);
            if(keepBothVersions){ // keep the old reference (the Cojac one)
                newMv.visitLdcInsn(2);
                newMv.visitTypeInsn(ANEWARRAY, OBJ_TYPE.getInternalName());
                // stack >> prmsArr prmsArr i pi piArr
                newMv.visitInsn(DUP_X1);
                // stack >> prmsArr prmsArr i piArr pi piArr
                newMv.visitInsn(SWAP);
                // stack >> prmsArr prmsArr i piArr piArr pi
                newMv.visitInsn(DUP_X1);
                newMv.visitLdcInsn(0);
                newMv.visitInsn(SWAP);
                // stack >> prmsArr prmsArr i piArr pi piArr pi 0
                newMv.visitInsn(AASTORE);
                // stack >> prmsArr prmsArr i piArr
            }
            // stack >> prmsArr prmsArr i pi
            if(cc.needsConversion(i)) {
                convertCojacToRealType(cc.asOriginalJavaType(i), newMv);
            } 
            convertPrimitiveToObject(newMv, cc.outArgs[i]);
            // stack >> prmsArr prmsArr i pi
            if(keepBothVersions) { // keep the new reference (the java one)
                newMv.visitInsn(SWAP);
                newMv.visitInsn(DUP_X1);
                newMv.visitInsn(SWAP);
                newMv.visitLdcInsn(1);
                newMv.visitInsn(SWAP);
                newMv.visitInsn(AASTORE);
            }
            // stack >> prmsArr prmsArr i pi
            newMv.visitInsn(AASTORE);
            // stack >> prmsArr
        }
        newMv.visitInsn(ARETURN);
        newMv.visitMaxs(0, 0);
    }

    
	/* just an idea - useless for the moment...
	private static boolean isPrimitiveFloatOrDoubleMultiArray(Type ia) {
	    if(ia.getSort() != Type.ARRAY) return false;
	    Type eltType=ia.getElementType();
	    if (eltType.equals(JWRAPPER_FLOAT_TYPE) || eltType.equals(JWRAPPER_DOUBLE_TYPE))
	        return true;
	    return isPrimitiveFloatOrDoubleMultiArray(eltType);
	}
	*/
    
	private static void checkArraysAfterCall(MethodVisitor mv, Map<Integer, Type> convertedArrays, String desc){
        // stack >> allParamsArr [possibleResult]
		Type returnType = Type.getReturnType(desc);
		int returnTypeSize = returnType.getSize();
		if(returnTypeSize == 1){
			mv.visitInsn(SWAP);
		} else if(returnTypeSize == 2) {
		    // mv.visitInsn(NOP); just a marker as a debugging helper
			mv.visitInsn(DUP2_X1); // D D Object D D
			mv.visitInsn(POP2); // D D Object
		}
        // stack >> [possibleResult] allParamsArr
		for(int pos: convertedArrays.keySet()) {
			// Type type = convertedArrays.get(pos);
			mv.visitInsn(DUP);
	        // stack >> [possibleResult] allParamsArr allParamsArr
			mv.visitLdcInsn(pos);
            // stack >> [possibleResult] allParamsArr allParamsArr i
			mv.visitInsn(AALOAD);
            // stack >> [possibleResult] allParamsArr allParamsArr[i]
			mv.visitTypeInsn(CHECKCAST, "["+OBJ_TYPE.getDescriptor());
            // stack >> [possibleResult] allParamsArr allParamsArr[i]  (of type Object[])
			mv.visitInsn(DUP);
            // stack >> [possibleResult] allParamsArr allParamsArr[i]  allParamsArr[i]
			mv.visitLdcInsn(0);
            // stack >> [possibleResult] allParamsArr allParamsArr[i]  allParamsArr[i] 0  
			mv.visitInsn(AALOAD);
            // stack >> [possibleResult] allParamsArr allParamsArr[i]  allParamsArr[i][0]    
			mv.visitInsn(SWAP);
            // stack >> [possibleResult] allParamsArr allParamsArr[i][0] allParamsArr[i]
			mv.visitLdcInsn(1);
            // stack >> [possibleResult] allParamsArr allParamsArr[i][0] allParamsArr[i] 1
			mv.visitInsn(AALOAD);
            // stack >> [possibleResult] allParamsArr allParamsArr[i][0] allParamsArr[i][1]
			// mergeOriginalArrayIntoCojac
			mv.visitMethodInsn(INVOKESTATIC, DN_NAME, "mergeOriginalArrayIntoCojac", "("+OBJ_DESC+OBJ_DESC+")V", false);
	        // stack >> [possibleResult] allParamsArr
		}
        // stack >> [possibleResult] allParamsArr
		mv.visitInsn(POP);
        // stack >> [possibleResult]
	}

	public static void convertRealToCojacType(Type realType, MethodVisitor mv){
        // WRAPPER SPEC: FW.fromFloat(float) -> FW,  FW.fromRealFloatWrapper(Float) -> FW
        // WRAPPER SPEC: DW.fromDouble(double) -> DW, DW.fromRealDoubleWrapper(Double) -> DW
        if(realType.equals(Type.FLOAT_TYPE)){
            mv.visitMethodInsn(INVOKESTATIC, COJAC_FLOAT_WRAPPER_INTERNAL_NAME, "fromFloat", "(F)"+COJAC_FLOAT_WRAPPER_TYPE_DESCR, false);
        } else if(realType.equals(JWRAPPER_FLOAT_TYPE)){
            mv.visitMethodInsn(INVOKESTATIC, COJAC_FLOAT_WRAPPER_INTERNAL_NAME, "fromRealFloatWrapper", "("+JWRAPPER_FLOAT_TYPE.getDescriptor()+")"+COJAC_FLOAT_WRAPPER_TYPE_DESCR, false);
        } else if(realType.equals(Type.DOUBLE_TYPE)){
            mv.visitMethodInsn(INVOKESTATIC, COJAC_DOUBLE_WRAPPER_INTERNAL_NAME, "fromDouble", "(D)"+COJAC_DOUBLE_WRAPPER_TYPE_DESCR, false);
        } else if(realType.equals(JWRAPPER_DOUBLE_TYPE)){
            mv.visitMethodInsn(INVOKESTATIC, COJAC_DOUBLE_WRAPPER_INTERNAL_NAME, "fromRealDoubleWrapper", "("+JWRAPPER_DOUBLE_TYPE.getDescriptor()+")"+COJAC_DOUBLE_WRAPPER_TYPE_DESCR, false);
        } else if(isPrimitiveFloatOrDoubleArray(realType)) {
            mv.visitTypeInsn(CHECKCAST, OBJ_TYPE.getInternalName());
            mv.visitLdcInsn(realType.getDimensions());
            if(realType.getElementType().equals(Type.FLOAT_TYPE)){
                mv.visitMethodInsn(INVOKESTATIC, FN_NAME, "convertPrimitiveArrayToCojac", "("+OBJ_DESC+"I)"+OBJ_DESC, false);
            } else if(realType.getElementType().equals(Type.DOUBLE_TYPE)){
                mv.visitMethodInsn(INVOKESTATIC, DN_NAME, "convertPrimitiveArrayToCojac", "("+OBJ_DESC+"I)"+OBJ_DESC, false);
            }
            mv.visitTypeInsn(CHECKCAST, afterFloatReplacement(realType).getInternalName());
        }
    }
	
	private static boolean isPrimitiveFloatOrDoubleArray(Type realType) {
	    if(realType.getSort() != Type.ARRAY) return false;
	    Type t=realType.getElementType();
	    if(t.equals(Type.FLOAT_TYPE) || t.equals(Type.DOUBLE_TYPE)) return true;
	    return false;
	}
    
    private static boolean isJWrapperFloatOrDoubleArray(Type realType) {
        if(realType.getSort() != Type.ARRAY) return false;
        Type t=realType.getElementType();
        if(t.equals(JWRAPPER_DOUBLE_TYPE) || t.equals(JWRAPPER_FLOAT_TYPE)) return true;
        return false;
    }

    public static void convertCojacToRealType(Type realType, MethodVisitor mv){
        if(realType.equals(Type.FLOAT_TYPE)){
            mv.visitMethodInsn(INVOKESTATIC, COJAC_FLOAT_WRAPPER_INTERNAL_NAME, "toFloat", "("+COJAC_FLOAT_WRAPPER_TYPE_DESCR+")F", false);
        } else if(realType.equals(JWRAPPER_FLOAT_TYPE)){
            mv.visitMethodInsn(INVOKESTATIC, COJAC_FLOAT_WRAPPER_INTERNAL_NAME, "toRealFloatWrapper", "("+COJAC_FLOAT_WRAPPER_TYPE_DESCR+")"+FL_DESCR, false);
        } else if(realType.equals(Type.DOUBLE_TYPE)){
            mv.visitMethodInsn(INVOKESTATIC, COJAC_DOUBLE_WRAPPER_INTERNAL_NAME, "toDouble", "("+COJAC_DOUBLE_WRAPPER_TYPE_DESCR+")D", false);
        } else if(realType.equals(JWRAPPER_DOUBLE_TYPE)){
            mv.visitMethodInsn(INVOKESTATIC, COJAC_DOUBLE_WRAPPER_INTERNAL_NAME, "toRealDoubleWrapper", "("+COJAC_DOUBLE_WRAPPER_TYPE_DESCR+")"+DL_DESCR, false);
        } else if(isPrimitiveFloatOrDoubleArray(realType) || isJWrapperFloatOrDoubleArray(realType)) {
            mv.visitTypeInsn(CHECKCAST, OBJ_TYPE.getInternalName());
            mv.visitLdcInsn(realType.getDimensions());
            Type eType=realType.getElementType();
            if(eType.equals(Type.FLOAT_TYPE)) {
                mv.visitMethodInsn(INVOKESTATIC, FN_NAME, "convertArrayToPrimitive", "("+OBJ_DESC+"I)"+OBJ_DESC, false);
            } else if(eType.equals(Type.DOUBLE_TYPE)) {
                mv.visitMethodInsn(INVOKESTATIC, DN_NAME, "convertArrayToPrimitive", "("+OBJ_DESC+"I)"+OBJ_DESC, false);
            } else if(eType.equals(JWRAPPER_FLOAT_TYPE)) {
                mv.visitMethodInsn(INVOKESTATIC, FN_NAME, "convertArrayToJWrapper", "("+OBJ_DESC+"I)"+OBJ_DESC, false);
            } else if(eType.equals(JWRAPPER_DOUBLE_TYPE)) {
                mv.visitMethodInsn(INVOKESTATIC, DN_NAME, "convertArrayToJWrapper", "("+OBJ_DESC+"I)"+OBJ_DESC, false);
            }
            mv.visitTypeInsn(CHECKCAST, realType.getInternalName());
        }
    }

    private static int getLoadOpcode(Type type) {
        switch(type.getSort()) {
            case Type.ARRAY:
            case Type.OBJECT:  return ALOAD;
            case Type.DOUBLE:  return DLOAD;
            case Type.FLOAT:   return FLOAD;
			case Type.CHAR:
			case Type.BOOLEAN:
            case Type.BYTE:
            case Type.SHORT:
            case Type.INT:     return ILOAD;
            case Type.LONG:    return LLOAD;
        }
        return -1;
    }

	private static void convertPrimitiveToObject(MethodVisitor newMv, Type type) {
		if(type.getSort() == Type.ARRAY || type.getSort() == Type.OBJECT)
			return;
		Type wrapper = getJWrapper(type);
		newMv.visitMethodInsn(INVOKESTATIC, wrapper.getInternalName(), "valueOf", "("+type.getDescriptor()+")"+wrapper.getDescriptor(), false);
	}
	
	private static Type getJWrapper(Type primitiveType){
		switch(primitiveType.getSort()){
			case Type.BOOLEAN: return Type.getType(Boolean.class);
			case Type.BYTE:    return Type.getType(Byte.class);
			case Type.CHAR:    return Type.getType(Character.class);
			case Type.DOUBLE:  return Type.getType(Double.class);
			case Type.FLOAT:   return Type.getType(Float.class);
			case Type.INT:     return Type.getType(Integer.class);
			case Type.LONG:    return Type.getType(Long.class);
			case Type.SHORT:   return Type.getType(Short.class);
		}
		return null;
	}
	
	private static String getWrapperToPrimitiveMethod(Type primitiveType){
		switch(primitiveType.getSort()){
			case Type.BOOLEAN:return "booleanValue";
			case Type.BYTE:   return "byteValue";
			case Type.CHAR:   return "charValue";
			case Type.DOUBLE: return "doubleValue";
			case Type.FLOAT:  return "floatValue";
			case Type.INT:    return "intValue";
			case Type.LONG:   return "longValue";
			case Type.SHORT:  return "shortValue";
		}
		return null;
	}
	
	/*
	 private void convertObjectToReal(MethodVisitor mv, Type aType){
		mv.visitTypeInsn(CHECKCAST, OBJ_TYPE.getInternalName());
		mv.visitMethodInsn(INVOKESTATIC, DN_NAME, "convertFromObjectToReal", "("+OBJ_DESC+")"+OBJ_DESC, false);
		mv.visitTypeInsn(CHECKCAST, aType.getInternalName());
	}
	*/
	
	private static void convertObjectToCojac(MethodVisitor mv, Type aType) {
		mv.visitTypeInsn(CHECKCAST, OBJ_TYPE.getInternalName());
		mv.visitMethodInsn(INVOKESTATIC, DN_NAME, "convertFromObjectToCojac", "("+OBJ_DESC+")"+OBJ_DESC, false);
		mv.visitTypeInsn(CHECKCAST, afterFloatReplacement(aType).getInternalName());
	}
	
    public void setUsefulPartners(AnalyzerAdapter aaAfter, MyLocalAdder mla) {
        this.aaAfter=aaAfter;
        this.mla=mla;
    }

    private Object stackTop(){
        AnalyzerAdapter aa = aaAfter;
        if(aa.stack == null)
            return null;
        if(aa.stack.isEmpty())
            return null;
        return aa.stack.get(aa.stack.size()-1);
    }


    //========================================================================
    static class ConversionContext {
        private final int opcode; 
        private final String owner, name, jDesc, cDesc;
        /** with double, Float etc... */
        private final Type[] outArgs;
        /** with DW, FW etc... */
        private final Type[] inArgs;
        private final Map<Integer, Type> typeConversions = new HashMap<>();
        private final Map<Integer, Type> convertedArrays = new HashMap<>();   
        
        public ConversionContext(int opcode, String owner, String name, String desc) {
            this.opcode=opcode;
            this.owner=owner;
            this.name=name;
            this.jDesc=desc;
            this.cDesc=replaceFloatMethodDescription(desc);
            outArgs = Type.getArgumentTypes(desc);
            inArgs = typesAfterReplacement(outArgs);
            rememberArrays();
        }
        
        public boolean needsConversion(int i) {
            return typeConversions.containsKey(i);
        }
        
        public Type asOriginalJavaType(int i) {
            return outArgs[i];
        }

        private Type[] typesAfterReplacement(Type[] javArgs) {
            Type cojArgs[] = new Type[javArgs.length];
            for (int i = 0; i < javArgs.length; i++) {
                cojArgs[i] = afterFloatReplacement(javArgs[i]);
                if(cojArgs[i].equals(javArgs[i]) == false){
                    typeConversions.put(i, javArgs[i]);
                }
            }
            return cojArgs;
        }
        
        private void rememberArrays() {
            // If it is an array, keep both arrays references (cojac & original)
            for (int i=0 ; i < inArgs.length; i++) {
                if(inArgs[i].getSort() == Type.ARRAY) { 
                    convertedArrays.put(i, inArgs[i]);
                }
            }
        }
        
        public boolean hasReturn() {
            return !Type.getReturnType(jDesc).equals(Type.VOID_TYPE);
        }
        
        public boolean hasPrimitiveResultInCojacVersion() {
            return hasReturn() && 
                    Type.getReturnType(cDesc).getSort() != Type.ARRAY && 
                    Type.getReturnType(cDesc).getSort() != Type.OBJECT;
        }
    }
    //========================================================================
}

/*============================================================================

-----------------------------------------------
(1) Existing mechanism, from [Monnard14, p. 19]:
-----------------------------------------------

...public void unInstrumentedCallee(float f, int i, double d);

class MyClass {
  public static void instrumentedCaller(){
    float f; int i, double d;
    ... 
    ref.unInstrumentedCallee(f, i, d);
  }
}

class MyClass { // after instrumentation
  public static void instrumentedCaller(){
    FloatWrapper f; int i; DoubleWrapper d;
    Object[] obj = COJAC_TYPE_CONVERT(f, i, d);  // but only on stack, no additional local var
    ref.unInstrumentedCallee((float)obj[0], (int)obj[1], (double)obj[2]);
  }
 
  public static Object[] COJAC_TYPE_CONVERT(FloatWrapper f, int i, DoubleWrapper d){
    Object[] obj = new Object[3];
    obj[0] = FloatWrapper.toFloat(f);
    obj[1] = i;
    obj[2] = DoubleWrapper.toDouble(d);
    return obj;
  }
}

-----------------------------------------------
(1) Idea of a new mechanism, for invokevirtual/invokeinterface 
    of a possibly overridden instrumented method
-----------------------------------------------

class MyClass { // after instrumentation
  public static void instrumentedCaller(){
    FloatWrapper f; int i; DoubleWrapper d;
    Object[] obj = COJAC_TYPE_CONVERT(f, i, d);
    try {
      ref.possiblyUninstrumentedCallee((FloatWrapper)obj[0], (int)obj[2], (DoubleWrapper)obj[4]); // signature 
    } catch (NoSuchMethodError e) {
      ref.possiblyUninstrumentedCallee((float)obj[1], (int)obj[3], (double)obj[5]);
      // and convert back return value and maybe post-process arrays
    } 
  }
 
  public static Object[] COJAC_TYPE_CONVERT(FloatWrapper f, int i, DoubleWrapper d){
    Object[] obj = new Object[6];
    obj[0] = FloatWrapper.toFloat(f);
    obj[1] = f;
    obj[1] = i;
    obj[2] = i;
    obj[2] = DoubleWrapper.toDouble(d);
    obj[3] = d;
    // if needed add a first parameter for 'ref' and put it in the array
    return obj;
  }
}


============================================================================*/



