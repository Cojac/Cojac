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

import ch.eiafr.cojac.Arg;
import ch.eiafr.cojac.Args;
import ch.eiafr.cojac.InstrumentationStats;
import ch.eiafr.cojac.Methods;
//import ch.eiafr.cojac.Signatures;
import ch.eiafr.cojac.models.FloatWrapper;
import ch.eiafr.cojac.models.ReactionType;
import ch.eiafr.cojac.reactions.Reaction;
import static ch.eiafr.cojac.instrumenters.InvokableMethod.*;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.LocalVariablesSorter;
import org.objectweb.asm.Type;

import java.util.HashMap;
import java.util.Map;

import static org.objectweb.asm.Opcodes.*;

final class ReplaceFloatsInstrumenter implements OpCodeInstrumenter {
    private final ReactionType reaction;
    private final InstrumentationStats stats;
    private final String logFileName;

    private final Map<Integer, InvokableMethod> invocations = new HashMap<Integer, InvokableMethod>(50);
    private final Map<Integer, InvokableMethod> conversions = new HashMap<Integer, InvokableMethod>(50);
    
   // public static final 

    ReplaceFloatsInstrumenter(Args args, InstrumentationStats stats) {
        super();
        this.stats = stats;
        reaction = args.getReactionType();

        if (args.isSpecified(Arg.CALL_BACK))
            logFileName = args.getValue(Arg.CALL_BACK); // No, I'm not proud of that trick...
        else
            logFileName = args.getValue(Arg.LOG_FILE);

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
        
        conversions.put(FCONST_0, new InvokableMethod(COJAC_FLOAT_WRAPPER_INTERNAL_NAME, "fromFloat", REPLACED_FROM_FLOAT));
        conversions.put(FCONST_1, new InvokableMethod(COJAC_FLOAT_WRAPPER_INTERNAL_NAME, "fromFloat", REPLACED_FROM_FLOAT));
        conversions.put(FCONST_2, new InvokableMethod(COJAC_FLOAT_WRAPPER_INTERNAL_NAME, "fromFloat", REPLACED_FROM_FLOAT));
        
    }

    @Override
    public void instrument(MethodVisitor mv, int opCode, String classPath, Methods methods, Reaction r, LocalVariablesSorter src) {
        InvokableMethod replacementMethod = invocations.get(opCode);
        InvokableMethod conversionMethod = conversions.get(opCode);
        if (replacementMethod != null) {
            replacementMethod.invokeStatic(mv);
        } else if (conversionMethod != null) {
            mv.visitInsn(opCode);
            conversionMethod.invokeStatic(mv);
        } else if (opCode==FRETURN) {
            System.out.println("ohoho");
            mv.visitInsn(ARETURN);
        } else {
            mv.visitInsn(opCode);
        }
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
    public static final String REPLACED_FROM_FLOAT   = "(F)"+RFL;
}
