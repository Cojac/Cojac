/*
 * *
 *    Copyright 2011-2014 Baptiste Wicht, Frédéric Bapst & Romain Monnard
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

import static ch.eiafr.cojac.instrumenters.InvokableMethod.afterFloatReplacement;
import static ch.eiafr.cojac.instrumenters.InvokableMethod.replaceFloatMethodDescription;
import static ch.eiafr.cojac.models.FloatReplacerClasses.COJAC_DOUBLE_WRAPPER_INTERNAL_NAME;
import static ch.eiafr.cojac.models.FloatReplacerClasses.COJAC_DOUBLE_WRAPPER_TYPE;
import static ch.eiafr.cojac.models.FloatReplacerClasses.COJAC_DOUBLE_WRAPPER_TYPE_DESCR;
import static ch.eiafr.cojac.models.FloatReplacerClasses.COJAC_FLOAT_WRAPPER_INTERNAL_NAME;
import static ch.eiafr.cojac.models.FloatReplacerClasses.COJAC_FLOAT_WRAPPER_TYPE;
import static ch.eiafr.cojac.models.FloatReplacerClasses.COJAC_FLOAT_WRAPPER_TYPE_DESCR;
import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AnalyzerAdapter;

import ch.eiafr.cojac.instrumenters.IOpcodeInstrumenter;
import ch.eiafr.cojac.instrumenters.IOpcodeInstrumenterFactory;
import ch.eiafr.cojac.instrumenters.ReplaceFloatsMethods;
import ch.eiafr.cojac.models.DoubleNumbers;
import ch.eiafr.cojac.models.FloatNumbers;

/**
 * There is a delegation chain in place for MethodVisitors:
 * MyLocalAdder                    adds two local variables
 *  -> FloatReplacerMethodVisitor  main bytecode transformation (with helper class FloatProxyMethod)
 *   -> FloatVariableSorter        remaps parameters index (due to the replacement of double (2 slots) as objects (1 slot))
 *    -> AnalyzerAdapter           keeps track of effective stack, so that we know the type of the top
 *     -> parentMv                 typically the final Writer
 * (the -> means "delegates to")
 * With maybe some intermediate TraceMethodVisitor to help debugging...
 * The AnalyzerAdapter keep the representation of the operand stack and the local variables
 * Only the operand stack can be used in that class.
 * The load and store operation indices (for the slot of each local variable) are
 * remapped in the FloatVariableSorter.
 */
final class FloatReplacerMethodVisitor extends MethodVisitor {
	
	private final CojacReferences references;
	
    private final IOpcodeInstrumenterFactory factory;
    private final InstrumentationStats stats;
   
    private final AnalyzerAdapter aa;
    private final ReplaceFloatsMethods rfm;
    //private final FloatVariablesSorter lvs;

	public static final String FN_NAME = Type.getType(FloatNumbers.class).getInternalName();
	public static final String DN_NAME = Type.getType(DoubleNumbers.class).getInternalName();
	
    FloatReplacerMethodVisitor(AnalyzerAdapter aa, 
            MethodVisitor lvs, 
            ReplaceFloatsMethods rfm, 
            InstrumentationStats stats, 
            IOpcodeInstrumenterFactory factory, 
            CojacReferences references) {
        super(Opcodes.ASM5, lvs);
        
        //this.lvs=lvs;
        this.aa = aa;
        this.rfm = rfm;
		this.references = references;
        this.stats = stats;
        this.factory = factory;
    }
	
    @Override
    public void visitInsn(int opCode) {
        // TODO - make a junit test to check the DUP_X2 instruction...
        // Replace instructions on doubles by instruction on objects when necessary
        if(opCode == DUP2 || opCode == DUP2_X1 || opCode == DUP2_X2 || opCode == POP2){
            if(stackTop().equals(COJAC_DOUBLE_WRAPPER_INTERNAL_NAME)){
                stats.incrementCounterValue(opCode);
                switch(opCode){
                    case DUP2: opCode = DUP; break;
                    case DUP2_X1: opCode = DUP_X1; break;
                    case DUP2_X2: opCode = DUP_X2; break;
                    case POP2: opCode = POP; break;
                    default: break;
                }
            }
        }
        // replace FALOAD by AALOAD
        switch(opCode){
            case DALOAD:
            case FALOAD: opCode = AALOAD; break;
            case DASTORE:
            case FASTORE: opCode = AASTORE; break;
        }
        
        IOpcodeInstrumenter instrumenter = factory.getInstrumenter(opCode);
        if (instrumenter != null) {
            stats.incrementCounterValue(opCode);
            instrumenter.instrument(mv, opCode); //, classPath, methods, reaction, null);
        } else { // Delegate to parent
            mv.visitInsn(opCode);
        }
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        if(rfm.instrumentCall(mv, opcode, owner, name, desc, stackTop())){
            return;
        }
        
        String descAfter=replaceFloatMethodDescription(desc);
        if (!desc.equals(descAfter)) 
            stats.incrementCounterValue(opcode);

        mv.visitMethodInsn(opcode, owner, name, descAfter, itf);
    }

    @Override
    public void visitInvokeDynamicInsn(String name, String desc,
                                       Handle bsm, Object... bsmArgs) {
        //TODO: pretty sure we have to somehow instrument invokeDynamic... maybe ask Lucy?
        
        if(!bsm.getOwner().equals("java/lang/invoke/LambdaMetafactory")) {
            mv.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
            return; // TODO: handle any bootstrap method in invokedynamic
        }
        if (!(bsmArgs[0] instanceof Type) ||
                !(bsmArgs[1] instanceof Handle) || 
                !(bsmArgs[2] instanceof Type)) {
            mv.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
            return;
        }
        Type a0=(Type)(bsmArgs[0]);
        Type a2=(Type)(bsmArgs[2]);
        Handle target=(Handle)(bsmArgs[1]);
        String handleOwner=target.getOwner();
        if (!references.hasToBeInstrumented(handleOwner)) {
            mv.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
            return;
        }
        Object[] bsmArgsAfter=new Object[bsmArgs.length];
        for(int i=0; i<bsmArgs.length; i++) {
            bsmArgsAfter[i]=bsmArgs[i];
        }
        String a0Before=a0.getInternalName(), a2Before=a2.getInternalName();
        String a0After=replaceFloatMethodDescription(a0Before), a2After=replaceFloatMethodDescription(a2Before);
        bsmArgsAfter[0]=Type.getType(a0After);
        bsmArgsAfter[2]=Type.getType(a2After);
        bsmArgsAfter[1]=new Handle(target.getTag(), target.getOwner(), target.getName(),
                replaceFloatMethodDescription(target.getDesc()));
        mv.visitInvokeDynamicInsn(name, desc, bsm, bsmArgsAfter);
    }
    
    @Override
    public void visitVarInsn(int opcode, int var) {
        int replacedOpcode = opcode;
        switch(opcode){
            case FLOAD:
            case DLOAD: replacedOpcode = ALOAD; break;
            case FSTORE:
            case DSTORE: replacedOpcode = ASTORE; break;
        }
        mv.visitVarInsn(replacedOpcode, var);
    }

	@Override
    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
        desc=afterFloatReplacement(desc);
        mv.visitLocalVariable(name, desc, signature, start, end, index);
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
		if(references.hasToBeInstrumented(owner) == false){ // proxy for fields
			Type type = Type.getType(desc);
			Type cojacType = afterFloatReplacement(type);
			if(type.equals(cojacType) == false) {  // the type is being changed
				if(opcode == GETFIELD || opcode == GETSTATIC){
					mv.visitFieldInsn(opcode, owner, name, desc);
					FloatProxyMethod.convertRealToCojacType(type, mv);
					return;
				}
				if(opcode == PUTFIELD || opcode == PUTSTATIC){
					FloatProxyMethod.convertCojacToRealType(type, mv);
					mv.visitFieldInsn(opcode, owner, name, desc);
					return;
				}
			}
		}
		
        String descAfter=afterFloatReplacement(desc);
        if (!desc.equals(descAfter)) 
            stats.incrementCounterValue(opcode);
        mv.visitFieldInsn(opcode, owner, name, descAfter);
		
		// TODO - handle GETSTATIC & handle std_lib and already loaded classes
		if(opcode == GETFIELD){
			String objDesc = Type.getType(Object.class).getDescriptor();
			if(descAfter.equals(COJAC_DOUBLE_WRAPPER_TYPE_DESCR)){
				mv.visitMethodInsn(INVOKESTATIC, DN_NAME, "initialize", "("+objDesc+")"+objDesc, false);
				mv.visitTypeInsn(CHECKCAST, COJAC_DOUBLE_WRAPPER_INTERNAL_NAME);
			}
			if(descAfter.equals(COJAC_FLOAT_WRAPPER_TYPE_DESCR)){
				mv.visitMethodInsn(INVOKESTATIC, FN_NAME, "initialize", "("+objDesc+")"+objDesc, false);
				mv.visitTypeInsn(CHECKCAST, COJAC_FLOAT_WRAPPER_INTERNAL_NAME);
			}
		}
    }

    @Override
    public void visitLdcInsn(Object cst) {
        mv.visitLdcInsn(cst);
        if (cst instanceof Float) {
            stats.incrementCounterValue(Opcodes.LDC);
			mv.visitMethodInsn(INVOKESTATIC, COJAC_FLOAT_WRAPPER_INTERNAL_NAME, "fromFloat", "(F)"+COJAC_FLOAT_WRAPPER_TYPE_DESCR, false);
        }
        if (cst instanceof Double) {
            stats.incrementCounterValue(Opcodes.LDC);
			mv.visitMethodInsn(INVOKESTATIC, COJAC_DOUBLE_WRAPPER_INTERNAL_NAME, "fromDouble", "(D)"+COJAC_DOUBLE_WRAPPER_TYPE_DESCR, false);
        }
    }
    
    @Override
    public void visitIntInsn(int opcode, int operand){		
		if(opcode == NEWARRAY){
			if(operand == Opcodes.T_FLOAT){
				mv.visitMethodInsn(INVOKESTATIC, FN_NAME, "newarray", "(I)["+Type.getType(Object.class).getDescriptor(), false);
				mv.visitTypeInsn(CHECKCAST, "["+COJAC_FLOAT_WRAPPER_TYPE_DESCR);
				return;
			}
			if(operand == Opcodes.T_DOUBLE){
				mv.visitMethodInsn(INVOKESTATIC, DN_NAME, "newarray", "(I)["+Type.getType(Object.class).getDescriptor(), false);
				mv.visitTypeInsn(CHECKCAST, "["+COJAC_DOUBLE_WRAPPER_TYPE_DESCR);
				return;
			}
		}
		mv.visitIntInsn(opcode, operand);
    }
    
    
    @Override
    public void visitMultiANewArrayInsn(String desc, int dims){
        if(desc.endsWith("F") || desc.endsWith("D")){
            String objDescr = Type.getType(Object.class).getDescriptor();
            String wrapper = FN_NAME;
            if(desc.endsWith("D"))
                wrapper = DN_NAME;
            String replacedDesc = desc;
            replacedDesc = replacedDesc.replaceAll("F", COJAC_FLOAT_WRAPPER_TYPE_DESCR);
            replacedDesc = replacedDesc.replaceAll("D", COJAC_DOUBLE_WRAPPER_TYPE_DESCR);
            mv.visitMultiANewArrayInsn(replacedDesc, dims);
            mv.visitLdcInsn(dims);
            mv.visitMethodInsn(INVOKESTATIC, wrapper, "initializeMultiArray", "("+objDescr+"I)"+objDescr, false);
            mv.visitTypeInsn(CHECKCAST, replacedDesc);
        }else{
            mv.visitMultiANewArrayInsn(desc, dims);
        }
    }
    
    @Override
    public void visitTypeInsn(int opcode, String type){
        // BAPST: that's where we instrument NEW Double/Float...
        Type myType = Type.getObjectType(type); // get type from internal name
		
		Type cojacType = afterFloatReplacement(myType);
		
		if(opcode == CHECKCAST && myType.equals(cojacType) == false){
			if(stackTop().equals(Type.getType(Object.class).getInternalName())){
				if(cojacType.equals(COJAC_FLOAT_WRAPPER_TYPE)){
					 mv.visitMethodInsn(INVOKESTATIC, FN_NAME, "castFromObject", "("+Type.getType(Object.class).getDescriptor()+")"+Type.getType(Object.class).getDescriptor(), false);
				}
				if(cojacType.equals(COJAC_DOUBLE_WRAPPER_TYPE)){
					 mv.visitMethodInsn(INVOKESTATIC, DN_NAME, "castFromObject", "("+Type.getType(Object.class).getDescriptor()+")"+Type.getType(Object.class).getDescriptor(), false);
				}
			}
		}

        mv.visitTypeInsn(opcode, cojacType.getInternalName());
    }
    
    private Object stackTop(){
        if(aa.stack == null)
			return null;
        if(aa.stack.isEmpty())
            return null;
        return aa.stack.get(aa.stack.size()-1);
    }
    
}