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

package ch.eiafr.cojac.instrumenters;

import ch.eiafr.cojac.Args;
import ch.eiafr.cojac.InstrumentationStats;
import ch.eiafr.cojac.Methods;
import ch.eiafr.cojac.reactions.IReaction;
import static ch.eiafr.cojac.instrumenters.InvokableMethod.*;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.LocalVariablesSorter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.objectweb.asm.Opcodes.*;

// TODO: instrument float arrays instructions...

final class ReplaceFloatsInstrumenter implements IOpcodeInstrumenter {
    private final InstrumentationStats stats;

    private final Map<Integer, InvokableMethod> invocations = new HashMap<Integer, InvokableMethod>(50);
    private final Map<Integer, InvokableMethod> conversions = new HashMap<Integer, InvokableMethod>(50);
    
    private static final Set<Integer> replaceFloatsOpcodes = new HashSet<>();
    static {
        int [] t = {
                FRETURN, 
                FCONST_0, FCONST_1, FCONST_2, 
                FLOAD, FSTORE, 
                I2F, L2F, D2F, F2D, F2I, F2L,
                FMUL, FADD, FDIV, FSUB, FREM, FNEG, 
                FCMPG, FCMPL, 
                IINC,
                NEWARRAY
        };
        for(int e:t) {
            replaceFloatsOpcodes.add(e);
        }
    }


   // public static final 

    ReplaceFloatsInstrumenter(Args args, InstrumentationStats stats) {
        super();
        this.stats = stats;
        fillMethods();
    }

    private void fillMethods() {
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
        
        invocations.put(NEWARRAY, new InvokableMethod(COJAC_FLOAT_WRAPPER_INTERNAL_NAME, "newarray", REPLACED_NEWARRAY));
        
        conversions.put(FCONST_0, new InvokableMethod(COJAC_FLOAT_WRAPPER_INTERNAL_NAME, "fromFloat", REPLACED_FROM_FLOAT));
        conversions.put(FCONST_1, new InvokableMethod(COJAC_FLOAT_WRAPPER_INTERNAL_NAME, "fromFloat", REPLACED_FROM_FLOAT));
        conversions.put(FCONST_2, new InvokableMethod(COJAC_FLOAT_WRAPPER_INTERNAL_NAME, "fromFloat", REPLACED_FROM_FLOAT));
        
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
        } else if (opCode==FRETURN) {
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
    private static final String RFL=COJAC_FLOAT_WRAPPER_TYPE_DESCR;//Type.getType(FloatWrapper.class).getDescriptor();
    
    public static final String REPLACED_FLOAT_BINARY = "("+RFL+RFL+")"+RFL;
    public static final String REPLACED_FLOAT_UNARY  = "("+RFL+")"+RFL;
    public static final String REPLACED_FLOAT_CMP    = "("+RFL+RFL+")I";
    public static final String REPLACED_I2F          = "(I)"+RFL;
    public static final String REPLACED_L2F          = "(J)"+RFL;
    public static final String REPLACED_D2F          = "(D)"+RFL;
    public static final String REPLACED_F2I          = "("+RFL+")I";
    public static final String REPLACED_F2L          = "("+RFL+")J";
    public static final String REPLACED_F2D          = "("+RFL+")D";
    public static final String REPLACED_FROM_FLOAT   = "(F)"+RFL;
    
    public static final String REPLACED_NEWARRAY          = "(I)["+RFL;
    
}
