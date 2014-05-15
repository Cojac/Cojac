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

package ch.eiafr.cojac;

import ch.eiafr.cojac.instrumenters.IOpcodeInstrumenter;
import ch.eiafr.cojac.instrumenters.IOpcodeInstrumenterFactory;
import ch.eiafr.cojac.instrumenters.InvokableMethod;
import static ch.eiafr.cojac.instrumenters.InvokableMethod.*;
import ch.eiafr.cojac.instrumenters.ReplaceFloatsMethods;
import ch.eiafr.cojac.reactions.IReaction;

import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.LocalVariablesSorter;


import static org.objectweb.asm.Opcodes.*;
import org.objectweb.asm.commons.AnalyzerAdapter;


final class FloatReplacerMethodVisitor extends MethodVisitor {
	
	private final CojacReferences references;
	
    private final IOpcodeInstrumenterFactory factory;
    private final InstrumentationStats stats;
    private final Args args;
    private final Methods methods;
    private final IReaction reaction;
    private final String classPath;
    public static final boolean DONT_INSTRUMENT = false;
   
    private final LocalVariablesSorter lvs;
    private final AnalyzerAdapter aa;
    private final ReplaceFloatsMethods rfm;
	
	private final String methodName; // Only for de bug

    // Must Link the lvs to the aa and the aa to the parent method visitor before call!
    FloatReplacerMethodVisitor(int access, String desc, AnalyzerAdapter aa, LocalVariablesSorter lvs, ReplaceFloatsMethods rfm, InstrumentationStats stats, Args args, Methods methods, IReaction reaction, String classPath, IOpcodeInstrumenterFactory factory, String name, CojacReferences references) {
        super(Opcodes.ASM4, lvs);
        
        this.aa = aa;
        this.lvs = lvs;
        this.rfm = rfm;

		this.references = references;
		
        this.stats = stats;
        this.args = args;
        this.factory = factory;

        this.methods = methods;
        this.reaction = reaction;
        this.classPath = classPath;
		
		this.methodName = name;
    }
	
    @Override
    public void visitInsn(int opCode) {
        if (DONT_INSTRUMENT) {
            mv.visitInsn(opCode); return; 
        }
        
        // TODO - check instruction DUP_X2
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
            instrumenter.instrument(mv, opCode, classPath, methods, reaction, null);
        } else { // Delegate to parent
            mv.visitInsn(opCode);
        }
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        if (DONT_INSTRUMENT) {
            mv.visitMethodInsn(opcode, owner, name, desc, itf); return; 
        }

        if(rfm.instrument(mv, opcode, owner, name, desc, stackTop())){
            return;
        }
        
        String descAfter=replaceFloatMethodDescription(desc);
        if (!desc.equals(descAfter)) 
            stats.incrementCounterValue(opcode);
        // TODO: something smarter, taking into account the method call kinds ?

        mv.visitMethodInsn(opcode, owner, name, descAfter, itf);
    }

    @Override
    public void visitVarInsn(int opcode, int var) {
        if (DONT_INSTRUMENT) {
            mv.visitVarInsn(opcode, var); return; 
        }
        
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
        if (DONT_INSTRUMENT) {
            mv.visitLocalVariable(name, desc, signature, start, end, index);
            return;
        }
        
        //TODO something coherent, even if it is only for the benefit of the debuggers...
        desc=afterFloatReplacement(desc);
        mv.visitLocalVariable(name, desc, signature, start, end, index);
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
        if (DONT_INSTRUMENT) {
            mv.visitFieldInsn(opcode, owner, name, desc);
            return;
        }
        
		if(references.hasToBeInstrumented(owner) == false){ // proxy for fields
			Type type = Type.getType(desc);
			Type cojacType = afterFloatReplacement(type);
			if(type.equals(cojacType) == false){
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
		
		// TODO handle GETSTATIC & handle std_lib and already loaded classes
		if(opcode == GETFIELD){
			if(descAfter.equals(COJAC_DOUBLE_WRAPPER_TYPE_DESCR)){
				mv.visitMethodInsn(INVOKESTATIC, COJAC_DOUBLE_WRAPPER_INTERNAL_NAME, "initialize", "("+COJAC_DOUBLE_WRAPPER_TYPE_DESCR+")"+COJAC_DOUBLE_WRAPPER_TYPE_DESCR, false);
			}
			if(descAfter.equals(COJAC_FLOAT_WRAPPER_TYPE_DESCR)){
				mv.visitMethodInsn(INVOKESTATIC, COJAC_FLOAT_WRAPPER_INTERNAL_NAME, "initialize", "("+COJAC_FLOAT_WRAPPER_TYPE_DESCR+")"+COJAC_FLOAT_WRAPPER_TYPE_DESCR, false);
			}
		}
		
    }

    @Override
    public void visitLdcInsn(Object cst) {
        mv.visitLdcInsn(cst);
        if (DONT_INSTRUMENT) {
            return;
        }
        if (cst instanceof Float) {
            stats.incrementCounterValue(Opcodes.LDC);
            InvokableMethod.FROM_FLOAT.invoke(mv);
        }
        if (cst instanceof Double) {
            stats.incrementCounterValue(Opcodes.LDC);
            InvokableMethod.FROM_DOUBLE.invoke(mv);
        }
    }
    
    @Override
    public void visitIntInsn(int opcode, int operand){
        if (DONT_INSTRUMENT) {
            mv.visitIntInsn(opcode, operand);
            return;
        }
        
        IOpcodeInstrumenter instrumenter = factory.getInstrumenter(opcode);
        if (instrumenter != null && opcode == NEWARRAY && (operand == Opcodes.T_FLOAT || operand == Opcodes.T_DOUBLE)) { // instrument only if it's an array of floats
            stats.incrementCounterValue(opcode);
            instrumenter.instrument(mv, opcode, operand, classPath, methods, reaction, null);
        } else { // Delegate to parent
            mv.visitIntInsn(opcode, operand);
        }
    }
    
    
    @Override
    public void visitMultiANewArrayInsn(String desc, int dims){
        if (DONT_INSTRUMENT) {
            mv.visitMultiANewArrayInsn(desc, dims);
            return;
        }
        if(desc.endsWith("F") || desc.endsWith("D")){
            String objDescr = Type.getType(Object.class).getDescriptor();
            String wrapper = COJAC_FLOAT_WRAPPER_INTERNAL_NAME;
            if(desc.endsWith("D"))
                wrapper = COJAC_DOUBLE_WRAPPER_INTERNAL_NAME;
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
        if (DONT_INSTRUMENT) {
            mv.visitTypeInsn(opcode, type);
            return;
        }
        Type myType = Type.getObjectType(type); // get type from internal name
		
		Type cojacType = afterFloatReplacement(myType);
		
		// TODO - handle arrays casted into object...
		if(opcode == CHECKCAST && myType.equals(cojacType) == false){
			if(stackTop().equals(Type.getType(Object.class).getInternalName())){
				if(cojacType.equals(COJAC_FLOAT_WRAPPER_TYPE)){
					 mv.visitMethodInsn(INVOKESTATIC, COJAC_FLOAT_WRAPPER_INTERNAL_NAME, "castFromObject", "("+Type.getType(Object.class).getDescriptor()+")"+COJAC_FLOAT_WRAPPER_TYPE_DESCR, false);
					 return;
				}
				if(cojacType.equals(COJAC_DOUBLE_WRAPPER_TYPE)){
					 mv.visitMethodInsn(INVOKESTATIC, COJAC_DOUBLE_WRAPPER_INTERNAL_NAME, "castFromObject", "("+Type.getType(Object.class).getDescriptor()+")"+COJAC_DOUBLE_WRAPPER_TYPE_DESCR, false);
					 return;
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