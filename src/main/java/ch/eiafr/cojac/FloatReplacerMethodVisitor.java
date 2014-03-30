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
import ch.eiafr.cojac.reactions.IReaction;
import java.util.ArrayList;

import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.LocalVariablesSorter;

import java.util.List;

import static org.objectweb.asm.Opcodes.*;
import org.objectweb.asm.commons.AnalyzerAdapter;


final class FloatReplacerMethodVisitor extends MethodVisitor {
    private final IOpcodeInstrumenterFactory factory;
    private final InstrumentationStats stats;
    private final Args args;
    private final Methods methods;
    private final IReaction reaction;
    private final String classPath;
    public static final boolean DONT_INSTRUMENT = false;
   
    private final LocalVariablesSorter lvs;
    private final AnalyzerAdapter aa;

    // Must Link the lvs to the aa and the aa to the parent method visitor before call!
    FloatReplacerMethodVisitor(int access, String desc, AnalyzerAdapter aa, LocalVariablesSorter lvs, InstrumentationStats stats, Args args, Methods methods, IReaction reaction, String classPath, IOpcodeInstrumenterFactory factory) {
        super(Opcodes.ASM4, lvs);
        
        this.aa = aa;
        this.lvs = lvs;
        
        this.stats = stats;
        this.args = args;
        this.factory = factory;

        this.methods = methods;
        this.reaction = reaction;
        this.classPath = classPath;
    }

    @Override
    public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
        if(DONT_INSTRUMENT){
            mv.visitFrame(type, nLocal, local, nStack, stack);
            return;
        }
        ArrayList<Object> newLocal = new ArrayList<>();
        for (Object object : local) {
            if(object == Opcodes.DOUBLE){
                newLocal.add(COJAC_DOUBLE_WRAPPER_INTERNAL_NAME);
                newLocal.add(Opcodes.TOP);
                nLocal++;
            }
            else if(object == Opcodes.FLOAT){
                newLocal.add(COJAC_FLOAT_WRAPPER_INTERNAL_NAME);
            }
            else{
                newLocal.add(object);
            }
        }
        /*
        String str = "LOCALS BEFORE: [ ";
        for (Object object : local) {
            if(object instanceof String)
                str += (String) object + " ";
            else if(object == Opcodes.TOP)
                str += "TOP ";
            else if(object == Opcodes.DOUBLE)
                str += "D ";
            else if(object == Opcodes.LONG)
                str += "J ";
            else if(object == Opcodes.INTEGER)
                str += "I ";
            else if(object == Opcodes.FLOAT)
                str += "F ";
            else
                str += ". ";
        }
        str += "]";
        System.out.println("VISIT FRAME => "+str);
        
        str = "LOCALS AFTER: [ ";
        for (Object object : newLocal) {
            if(object instanceof String)
                str += (String) object + " ";
            else if(object == Opcodes.TOP)
                str += "TOP ";
            else if(object == Opcodes.DOUBLE)
                str += "D ";
            else if(object == Opcodes.LONG)
                str += "J ";
            else if(object == Opcodes.INTEGER)
                str += "I ";
            else if(object == Opcodes.FLOAT)
                str += "F ";
            else
                str += ". ";
        }
        str += "]";
        System.out.println("VISIT FRAME => "+str);
        */
        mv.visitFrame(type, nLocal, newLocal.toArray(), nStack, stack);
    }
        
    @Override
    public void visitInsn(int opCode) {
        if (DONT_INSTRUMENT) {
            mv.visitInsn(opCode); return; 
        }

        // Replace instructions on doubles by instruction on objects when necessary
        if(opCode == DUP2 || opCode == DUP2_X1 || opCode == DUP2_X2 || opCode == POP2){
            if(stackTop().equals(COJAC_DOUBLE_WRAPPER_INTERNAL_NAME)){
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
    public void visitMethodInsn(int opcode, String owner, String name, String desc) {
        if (DONT_INSTRUMENT) {
            mv.visitMethodInsn(opcode, owner, name, desc); return; 
        }
        
        
        if(owner.equals("java/lang/String") && name.equals("valueOf") && desc.startsWith("(F)")){
            mv.visitMethodInsn(INVOKESTATIC, COJAC_FLOAT_WRAPPER_INTERNAL_NAME, "toFloat", "("+COJAC_FLOAT_WRAPPER_TYPE_DESCR+")F");
            mv.visitMethodInsn(opcode, owner, name, desc);
            return;
        }
        
        if(owner.equals("java/io/PrintStream") && name.startsWith("print") && desc.startsWith("(F)")){
            mv.visitMethodInsn(INVOKESTATIC, COJAC_FLOAT_WRAPPER_INTERNAL_NAME, "toFloat", "("+COJAC_FLOAT_WRAPPER_TYPE_DESCR+")F");
            mv.visitMethodInsn(opcode, owner, name, desc);
            return;
        }
        
        if(owner.equals("java/lang/StringBuilder") && name.equals("append") && desc.startsWith("(F)")){
            mv.visitMethodInsn(INVOKESTATIC, COJAC_FLOAT_WRAPPER_INTERNAL_NAME, "toFloat", "("+COJAC_FLOAT_WRAPPER_TYPE_DESCR+")F");
            mv.visitMethodInsn(opcode, owner, name, desc);
            return;
        }
        
        if(owner.equals("java/lang/Float") && name.equals("valueOf") && desc.startsWith("(F)")){
            if(stackTop().equals(Opcodes.FLOAT)){
                // Only if this is a real float
                mv.visitMethodInsn(INVOKESTATIC, COJAC_FLOAT_WRAPPER_INTERNAL_NAME, "fromFloat", "(F)"+COJAC_FLOAT_WRAPPER_TYPE_DESCR);
            }
            return;
        }
        
        if(owner.equals("java/lang/Float") && name.equals("floatValue")){
            // Do nothing, this is already the FloatWrapper of COJAC
            return;
        }
        
        String descAfter=replaceFloatMethodDescription(desc);
        if (!desc.equals(descAfter)) 
            stats.incrementCounterValue(opcode);
        // TODO: something smarter, taking into account the method call kinds ?
        
        if(opcode == INVOKESPECIAL){
            if(owner.equals("java/lang/Float")){
                System.out.println("desc => "+descAfter);
                mv.visitMethodInsn(opcode, COJAC_FLOAT_WRAPPER_INTERNAL_NAME, name, descAfter);
                return;
            }
        }
        
        mv.visitMethodInsn(opcode, owner, name, descAfter);
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
/*
    private Type typeFromVarInsn(int opcode) {
        Type objt = Type.getObjectType("java/lang/Object");
        switch(opcode) {
        case RET: // TODO: verify RET uses an int variable
        case ISTORE:
        case ILOAD: return Type.INT_TYPE;
        case LSTORE:
        case LLOAD: return Type.LONG_TYPE;
        case DSTORE:
        case DLOAD: return Type.DOUBLE_TYPE;
        case ASTORE:
        case ALOAD: return objt;  // TODO: verify if ALOAD can use an java.lang.Object variable
        }
        return null;
    }
*/
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
        
        String descAfter=afterFloatReplacement(desc);
        if (!desc.equals(descAfter)) 
            stats.incrementCounterValue(opcode);
        mv.visitFieldInsn(opcode, owner, name, descAfter);
    }

    @Override
    public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
        mv.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
    }

    @Override
    public void visitLdcInsn(Object cst) {
        mv.visitLdcInsn(cst);
        if (DONT_INSTRUMENT) {
            return;
        }
        if (cst instanceof Float) {
            stats.incrementCounterValue(Opcodes.LDC);
            InvokableMethod.FROM_FLOAT.invokeStatic(mv);
        }
        if (cst instanceof Double) {
            stats.incrementCounterValue(Opcodes.LDC);
            InvokableMethod.FROM_DOUBLE.invokeStatic(mv);
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
            String objDescr = Type.getObjectType("java/lang/Object").getDescriptor();
            String wrapper = COJAC_FLOAT_WRAPPER_INTERNAL_NAME;
            if(desc.endsWith("D"))
                wrapper = COJAC_DOUBLE_WRAPPER_INTERNAL_NAME;
            String replacedDesc = desc;
            replacedDesc = replacedDesc.replaceAll("F", COJAC_FLOAT_WRAPPER_TYPE_DESCR);
            replacedDesc = replacedDesc.replaceAll("D", COJAC_DOUBLE_WRAPPER_TYPE_DESCR);
            mv.visitMultiANewArrayInsn(replacedDesc, dims);
            mv.visitLdcInsn(dims);
            mv.visitMethodInsn(INVOKESTATIC, wrapper, "initializeMultiArray", "("+objDescr+"I)"+objDescr);
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
        
        
        if(type.equals("java/lang/Float")){
            mv.visitTypeInsn(opcode, COJAC_FLOAT_WRAPPER_INTERNAL_NAME);
            return;
        }
        
        mv.visitTypeInsn(opcode, type);
    }
    
    private Object stackTop(){
        return aa.stack.get(aa.stack.size()-1);
    }
    
}