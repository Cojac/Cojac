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
    /*
    private final Map<Integer, Integer> varMap = new HashMap<>();
    private final Set<Integer> floatVars = new HashSet<>();
    private final Set<Integer> nonFloatVars = new HashSet<>();
    */

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
    public void visitIincInsn(int var, int increment) {
        if (DONT_INSTRUMENT) {
            mv.visitIincInsn(var, increment); return; 
        }
        int replacedVar=var;
        /*
        if (floatVars.contains(var)) {
            if (varMap.containsKey(var)) {
                replacedVar=varMap.get(var);
            } else {
                replacedVar=lvs.newLocal(Type.INT_TYPE);
                varMap.put(var, replacedVar);
            }
            System.out.println("COJJJ inst'ing iinc: "+var+"->"+replacedVar);
        } else {
            nonFloatVars.add(var);
        }
        */
        mv.visitIincInsn(replacedVar, increment);
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
        mv.visitFrame(type, nLocal, newLocal.toArray(), nStack, stack);
    }
        
    @Override
    public void visitInsn(int opCode) {
        if (DONT_INSTRUMENT) {
            mv.visitInsn(opCode); return; 
        }

        // Replace instructions on doubles by instruction on objects when necessary
        if(opCode == DUP2 || opCode == DUP2_X1 || opCode == DUP2_X2 || opCode == POP2){
            int size = aa.stack.size();
            Object topStack = aa.stack.get(size-1);
            if(topStack instanceof String && topStack.equals(COJAC_DOUBLE_WRAPPER_INTERNAL_NAME)){
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
        if(opCode == FALOAD){
            opCode = AALOAD;
        }
        if(opCode == FASTORE){
            opCode = AASTORE;
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
        String descAfter=replaceFloatMethodDescription(desc);
        if (!desc.equals(descAfter)) 
            stats.incrementCounterValue(opcode);
        // TODO: something smarter, taking into account the method call kinds ?
        mv.visitMethodInsn(opcode, owner, name, descAfter);
    }

    @Override
    public void visitVarInsn(int opcode, int var) {
        if (DONT_INSTRUMENT) {
            mv.visitVarInsn(opcode, var); return; 
        }
        
        if (opcode==DLOAD || opcode==DSTORE) {
            int replacedOpcode = (opcode==DLOAD) ? ALOAD:ASTORE;
            int replacedVar=var;
            /*
            if (nonFloatVars.contains(var)) {
                if (varMap.containsKey(var)) {
                    replacedVar=varMap.get(var);
                } else {
                    replacedVar=lvs.newLocal(COJAC_DOUBLE_WRAPPER_TYPE);
                    varMap.put(var, replacedVar);
                }
                stats.incrementCounterValue(opcode);
                System.out.println("USED???");
            } else {
                floatVars.add(var);
            }
            */
            mv.visitVarInsn(replacedOpcode, replacedVar); // Whats is the different between super and mv ???
        }
        else if (opcode==FLOAD || opcode==FSTORE) {
            int replacedOpcode = (opcode==FLOAD) ? ALOAD:ASTORE;
            int replacedVar=var;
            /*
            if (nonFloatVars.contains(var)) {
                if (varMap.containsKey(var)) {
                    replacedVar=varMap.get(var);
                } else {
                    replacedVar=lvs.newLocal(COJAC_FLOAT_WRAPPER_TYPE);
                    varMap.put(var, replacedVar);
                }
                stats.incrementCounterValue(opcode);
                if(mainMethod)
                    System.out.println("REPLACE ?? BY FLOAT");
            } else {
                if(mainMethod)
                    System.out.println("ADD VAR FLOAT "+var);
                floatVars.add(var);
            }
            */
            mv.visitVarInsn(replacedOpcode, replacedVar);
        } else {
            int replacedVar=var;
            /*
            if (floatVars.contains(var)) {
                if (varMap.containsKey(var)) {
                    replacedVar=varMap.get(var);
                } else {
                    replacedVar=lvs.newLocal(typeFromVarInsn(opcode));
                    varMap.put(var, replacedVar);
                }
                stats.incrementCounterValue(opcode);
                if(mainMethod)
                    System.out.println("REPLACE FLOAT BY ??");
            } else {
                if(mainMethod)
                    System.out.println("ADD VAR NONFLOAT "+var);
                nonFloatVars.add(var);
            }
            */
            mv.visitVarInsn(opcode, replacedVar); // CAUTION: do not use mv.visitVarInsn
        }
    }

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
        if (instrumenter != null && opcode == NEWARRAY && operand == Opcodes.T_FLOAT) { // instrument only if it's an array of floats
            stats.incrementCounterValue(opcode);
            instrumenter.instrument(mv, opcode, classPath, methods, reaction, null);
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
        
        int opcode = MULTIANEWARRAY;
        IOpcodeInstrumenter instrumenter = factory.getInstrumenter(opcode);
        if (instrumenter != null && desc.endsWith("F")){ // instrument if it's an array of floats
            
            // Get the dimensions sizes on the stack and create an array with it (for method calling in instrumenter)
            mv.visitIntInsn(BIPUSH, dims);
            mv.visitIntInsn(NEWARRAY, Opcodes.T_INT);

            for(int i=0 ; i<dims ; i++){
                mv.visitInsn(DUP_X1);
                mv.visitInsn(SWAP);
                mv.visitLdcInsn(i);
                mv.visitInsn(SWAP);
                mv.visitInsn(IASTORE);
            }
            
            mv.visitLdcInsn(dims);
            
            stats.incrementCounterValue(opcode);
            instrumenter.instrument(mv, opcode, classPath, methods, reaction, null);
            // Cast the object returned to the multi array of cojac float wrapper
            String type = "";
            for (int i = 0; i < dims; i++) {
                type += "[";
            }
            type += COJAC_FLOAT_WRAPPER_TYPE;
            mv.visitTypeInsn(CHECKCAST, type);
            
        } else { // Delegate to parent
            mv.visitMultiANewArrayInsn(desc, dims);
        }
        
    }
    
    @Override
    public void visitMaxs(int maxStack, int maxLocals){
        // TODO - verify max values
        //System.out.println("VISIT MAXS maxStack="+maxStack+" | maxLocals="+maxLocals);
        mv.visitMaxs(maxStack, maxLocals);
    }
    
    private void printStack(){
        printStack("STACK", aa.stack);
    }
    
    private void printLocals(){
        printStack("LOCALS", aa.locals);
    }
    
    
    private void printStack(String title, List stack){
        String str = "";
        for (Object object : stack) {
            
            if(object instanceof Integer){
                if(object == Opcodes.TOP){
                    str += "TOP ";
                }
                else if(object == Opcodes.INTEGER){
                    str += "INTEGER ";
                }
                else if(object == Opcodes.FLOAT){
                    str += "FLOAT ";
                }
                else if(object == Opcodes.DOUBLE){
                    str += "DOUBLE ";
                }
                else if(object == Opcodes.LONG){
                    str += "LONG ";
                }
                else if(object == Opcodes.NULL){
                    str += "NULL ";
                }
                else if(object == Opcodes.UNINITIALIZED_THIS){
                    str += "UNINITIALIZED_THIS ";
                }
            }
            else{
                str += object+" ";
            }
        }
        System.out.println(title+"=> "+str);
    }
    
    private void printStack(String title, Object[] stack){
        String str = "";
        for (Object object : stack) {
            
            if(object instanceof Integer){
                if(object == Opcodes.TOP){
                    str += "TOP ";
                }
                else if(object == Opcodes.INTEGER){
                    str += "INTEGER ";
                }
                else if(object == Opcodes.FLOAT){
                    str += "FLOAT ";
                }
                else if(object == Opcodes.DOUBLE){
                    str += "DOUBLE ";
                }
                else if(object == Opcodes.LONG){
                    str += "LONG ";
                }
                else if(object == Opcodes.NULL){
                    str += "NULL ";
                }
                else if(object == Opcodes.UNINITIALIZED_THIS){
                    str += "UNINITIALIZED_THIS ";
                }
            }
            else{
                str += object+" ";
            }
        }
        System.out.println(title+"=> "+str);
    }
    
}