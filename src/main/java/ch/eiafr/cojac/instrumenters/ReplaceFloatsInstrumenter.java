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

package ch.eiafr.cojac.instrumenters;

import ch.eiafr.cojac.Args;
import static ch.eiafr.cojac.models.FloatReplacerClasses.*;
import ch.eiafr.cojac.InstrumentationStats;
import ch.eiafr.cojac.Methods;
import ch.eiafr.cojac.models.DoubleNumbers;
import ch.eiafr.cojac.models.FloatNumbers;
import ch.eiafr.cojac.reactions.IReaction;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.LocalVariablesSorter;


final class ReplaceFloatsInstrumenter implements IOpcodeInstrumenter {
    private final InstrumentationStats stats;
	
	public static final String FN_NAME = Type.getType(FloatNumbers.class).getInternalName();
	public static final String DN_NAME = Type.getType(DoubleNumbers.class).getInternalName();

    private final Map<Integer, InvokableMethod> invocations = new HashMap<>(50);
    private final Map<Integer, InvokableMethod> conversions = new HashMap<>(50);
    
    private static final Set<Integer> replaceFloatsOpcodes = new HashSet<>();
    static {
        int [] t = {
                // FLOATS
                FRETURN, 
                FCONST_0, FCONST_1, FCONST_2, 
                // FLOAD, FSTORE, // TODO: nothing to do here?
                I2F, L2F, D2F, F2D, F2I, F2L,
                FMUL, FADD, FDIV, FSUB, FREM, FNEG, 
                FCMPG, FCMPL, 
                IINC,
                // DOUBLES
                DRETURN,
                DCONST_0, DCONST_1,
                I2D, L2D, D2I, D2L, 
                DMUL, DADD, DDIV, DSUB, DREM, DNEG,
                DCMPG, DCMPL,
        };
        for(int e:t) {
            replaceFloatsOpcodes.add(e);
        }
    }


   // public static final 

    ReplaceFloatsInstrumenter(Args args, InstrumentationStats stats) {
        super();
		fillDescriptors();
        this.stats = stats;
        fillMethods();
    }

    private void fillMethods() {
        // Floats
        // WRAPPER SPEC: FW.fadd/fsub/fmul/frem/fdiv(FW,FW) -> FW
        // WRAPPER SPEC: FW.fneg(FW) -> FW
        // WRAPPER SPEC: FW.cmpl(FW,FW) -> boolean
        // WRAPPER SPEC: FW.l2f/i2f/d2f/f2i/f2l/f2d
        // WRAPPER SPEC: FW.fromFloat(float) -> FW

        invocations.put(FADD, new InvokableMethod(COJAC_FLOAT_WRAPPER_INTERNAL_NAME, "fadd", REPLACED_FLOAT_BINARY));
        invocations.put(FSUB, new InvokableMethod(COJAC_FLOAT_WRAPPER_INTERNAL_NAME, "fsub", REPLACED_FLOAT_BINARY));
        invocations.put(FMUL, new InvokableMethod(COJAC_FLOAT_WRAPPER_INTERNAL_NAME, "fmul", REPLACED_FLOAT_BINARY));
        invocations.put(FREM, new InvokableMethod(COJAC_FLOAT_WRAPPER_INTERNAL_NAME, "frem", REPLACED_FLOAT_BINARY));
        invocations.put(FDIV, new InvokableMethod(COJAC_FLOAT_WRAPPER_INTERNAL_NAME, "fdiv", REPLACED_FLOAT_BINARY));

        invocations.put(FNEG, new InvokableMethod(COJAC_FLOAT_WRAPPER_INTERNAL_NAME, "fneg", REPLACED_FLOAT_UNARY));

        invocations.put(FCMPL, new InvokableMethod(COJAC_FLOAT_WRAPPER_INTERNAL_NAME, "fcmpl", REPLACED_FLOAT_CMP));
        invocations.put(FCMPG, new InvokableMethod(COJAC_FLOAT_WRAPPER_INTERNAL_NAME, "fcmpg", REPLACED_FLOAT_CMP));

        invocations.put(L2F, new InvokableMethod(COJAC_FLOAT_WRAPPER_INTERNAL_NAME, "l2f", REPLACED_L2F));
        invocations.put(I2F, new InvokableMethod(COJAC_FLOAT_WRAPPER_INTERNAL_NAME, "i2f", REPLACED_I2F));
        invocations.put(D2F, new InvokableMethod(COJAC_FLOAT_WRAPPER_INTERNAL_NAME, "d2f", REPLACED_D2F));
        invocations.put(F2I, new InvokableMethod(COJAC_FLOAT_WRAPPER_INTERNAL_NAME, "f2i", REPLACED_F2I));
        invocations.put(F2L, new InvokableMethod(COJAC_FLOAT_WRAPPER_INTERNAL_NAME, "f2l", REPLACED_F2L));
        invocations.put(F2D, new InvokableMethod(COJAC_FLOAT_WRAPPER_INTERNAL_NAME, "f2d", REPLACED_F2D));
        
        conversions.put(FCONST_0, new InvokableMethod(COJAC_FLOAT_WRAPPER_INTERNAL_NAME, "fromFloat", REPLACED_FROM_FLOAT));
        conversions.put(FCONST_1, new InvokableMethod(COJAC_FLOAT_WRAPPER_INTERNAL_NAME, "fromFloat", REPLACED_FROM_FLOAT));
        conversions.put(FCONST_2, new InvokableMethod(COJAC_FLOAT_WRAPPER_INTERNAL_NAME, "fromFloat", REPLACED_FROM_FLOAT));
        
        // Doubles
        // WRAPPER SPEC: DW.dadd/dsub/dmul/drem/ddiv(DW,DW) -> DW
        // WRAPPER SPEC: DW.dneg(DW) -> DW
        // WRAPPER SPEC: DW.cmpl(DW,DW) -> boolean
        // WRAPPER SPEC: DW.l2d/i2d/d2i/d2l
        // WRAPPER SPEC: DW.fromDouble(double) -> DW

        invocations.put(DADD, new InvokableMethod(COJAC_DOUBLE_WRAPPER_INTERNAL_NAME, "dadd", REPLACED_DOUBLE_BINARY));
        invocations.put(DSUB, new InvokableMethod(COJAC_DOUBLE_WRAPPER_INTERNAL_NAME, "dsub", REPLACED_DOUBLE_BINARY));
        invocations.put(DMUL, new InvokableMethod(COJAC_DOUBLE_WRAPPER_INTERNAL_NAME, "dmul", REPLACED_DOUBLE_BINARY));
        invocations.put(DREM, new InvokableMethod(COJAC_DOUBLE_WRAPPER_INTERNAL_NAME, "drem", REPLACED_DOUBLE_BINARY));
        invocations.put(DDIV, new InvokableMethod(COJAC_DOUBLE_WRAPPER_INTERNAL_NAME, "ddiv", REPLACED_DOUBLE_BINARY));
        
        invocations.put(DNEG, new InvokableMethod(COJAC_DOUBLE_WRAPPER_INTERNAL_NAME, "dneg", REPLACED_DOUBLE_UNARY));
        
        invocations.put(DCMPL, new InvokableMethod(COJAC_DOUBLE_WRAPPER_INTERNAL_NAME, "dcmpl", REPLACED_DOUBLE_CMP));
        invocations.put(DCMPG, new InvokableMethod(COJAC_DOUBLE_WRAPPER_INTERNAL_NAME, "dcmpg", REPLACED_DOUBLE_CMP));
        
        invocations.put(I2D, new InvokableMethod(COJAC_DOUBLE_WRAPPER_INTERNAL_NAME, "i2d", REPLACED_I2D));
        invocations.put(L2D, new InvokableMethod(COJAC_DOUBLE_WRAPPER_INTERNAL_NAME, "l2d", REPLACED_L2D));
        invocations.put(D2I, new InvokableMethod(COJAC_DOUBLE_WRAPPER_INTERNAL_NAME, "d2i", REPLACED_D2I));
        invocations.put(D2L, new InvokableMethod(COJAC_DOUBLE_WRAPPER_INTERNAL_NAME, "d2l", REPLACED_D2L));
        
        conversions.put(DCONST_0, new InvokableMethod(COJAC_DOUBLE_WRAPPER_INTERNAL_NAME, "fromDouble", REPLACED_FROM_DOUBLE));
        conversions.put(DCONST_1, new InvokableMethod(COJAC_DOUBLE_WRAPPER_INTERNAL_NAME, "fromDouble", REPLACED_FROM_DOUBLE));
    }

    @Override
    public void instrument(MethodVisitor mv, int opCode, String classPath, Methods methods, IReaction r, LocalVariablesSorter src) {
        InvokableMethod replacementMethod = invocations.get(opCode);
        InvokableMethod conversionMethod = conversions.get(opCode);
        if (replacementMethod != null) {
            replacementMethod.invokeStatic(mv);
        } else if (conversionMethod != null) {
            mv.visitInsn(opCode);
            conversionMethod.invokeStatic(mv);
        } else if (opCode==FRETURN || opCode==DRETURN) {
            //stats.incrementCounterValue(arg);
            mv.visitInsn(ARETURN);
        } else {
            mv.visitInsn(opCode);
        }
    }
    
    @Override
    public boolean wantsToInstrument(int opcode) {
        return replaceFloatsOpcodes.contains(opcode);
    }

    //==========================================
    private static String RFL;
    private static String RDL;
    private static String REPLACED_FLOAT_BINARY;
    private static String REPLACED_FLOAT_UNARY;
    private static String REPLACED_FLOAT_CMP;
    private static String REPLACED_I2F;
    private static String REPLACED_L2F;
    private static String REPLACED_D2F;
    private static String REPLACED_F2I;
    private static String REPLACED_F2L;
    private static String REPLACED_F2D;
    private static String REPLACED_FROM_FLOAT;
	
    private static String REPLACED_DOUBLE_BINARY;
    private static String REPLACED_DOUBLE_UNARY;
    private static String REPLACED_DOUBLE_CMP;
    private static String REPLACED_I2D;
    private static String REPLACED_L2D;
    private static String REPLACED_D2I;
    private static String REPLACED_D2L;
	private static String REPLACED_FROM_DOUBLE;
	
	public void fillDescriptors(){
		RFL=COJAC_FLOAT_WRAPPER_TYPE_DESCR;
		RDL=COJAC_DOUBLE_WRAPPER_TYPE_DESCR;
		REPLACED_FLOAT_BINARY = "("+RFL+RFL+")"+RFL;
		REPLACED_FLOAT_UNARY  = "("+RFL+")"+RFL;
		REPLACED_FLOAT_CMP    = "("+RFL+RFL+")I";
		REPLACED_I2F          = "(I)"+RFL;
		REPLACED_L2F          = "(J)"+RFL;
		REPLACED_D2F          = "("+RDL+")"+RFL;
		REPLACED_F2I          = "("+RFL+")I";
		REPLACED_F2L          = "("+RFL+")J";
		REPLACED_F2D          = "("+RFL+")"+RDL;
		REPLACED_FROM_FLOAT   = "(F)"+RFL;

		REPLACED_DOUBLE_BINARY= "("+RDL+RDL+")"+RDL;
		REPLACED_DOUBLE_UNARY  = "("+RDL+")"+RDL;
		REPLACED_DOUBLE_CMP    = "("+RDL+RDL+")I";
		REPLACED_I2D          = "(I)"+RDL;
		REPLACED_L2D          = "(J)"+RDL;
		REPLACED_D2I          = "("+RDL+")I";
		REPLACED_D2L          = "("+RDL+")J";
		REPLACED_FROM_DOUBLE  = "(D)"+RDL;
	}
	
}
