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
import ch.eiafr.cojac.Signatures;
import ch.eiafr.cojac.models.ReactionType;
import ch.eiafr.cojac.reactions.Reaction;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.LocalVariablesSorter;

import java.util.HashMap;
import java.util.Map;

import static org.objectweb.asm.Opcodes.*;

final class DirectInstrumenter implements OpCodeInstrumenter {
    private final ReactionType reaction;
    private final InstrumentationStats stats;
    private final String logFileName;

    private final Map<Integer, MethodDescriptor> invocations = new HashMap<Integer, MethodDescriptor>(50);

    private static final String CHECKED_INTS = "ch/eiafr/cojac/models/CheckedInts";
    private static final String CHECKED_CASTS = "ch/eiafr/cojac/models/CheckedCasts";
    private static final String CHECKED_LONGS = "ch/eiafr/cojac/models/CheckedLongs";
    private static final String CHECKED_FLOATS = "ch/eiafr/cojac/models/CheckedFloats";
    private static final String CHECKED_DOUBLES = "ch/eiafr/cojac/models/CheckedDoubles";

    DirectInstrumenter(Args args, InstrumentationStats stats) {
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
        invocations.put(IADD, new MethodDescriptor(CHECKED_INTS, "checkedIADD", Signatures.RAW_INTEGER_BINARY));
        invocations.put(ISUB, new MethodDescriptor(CHECKED_INTS, "checkedISUB", Signatures.RAW_INTEGER_BINARY));
        invocations.put(IMUL, new MethodDescriptor(CHECKED_INTS, "checkedIMUL", Signatures.RAW_INTEGER_BINARY));
        invocations.put(IDIV, new MethodDescriptor(CHECKED_INTS, "checkedIDIV", Signatures.RAW_INTEGER_BINARY));

        invocations.put(INEG, new MethodDescriptor(CHECKED_INTS, "checkedINEG", Signatures.RAW_INTEGER_UNARY));
        invocations.put(IINC, new MethodDescriptor(CHECKED_INTS, "checkedIINC", Signatures.RAW_INTEGER_BINARY));

        invocations.put(LADD, new MethodDescriptor(CHECKED_LONGS, "checkedLADD", Signatures.RAW_LONG_BINARY));
        invocations.put(LSUB, new MethodDescriptor(CHECKED_LONGS, "checkedLSUB", Signatures.RAW_LONG_BINARY));
        invocations.put(LMUL, new MethodDescriptor(CHECKED_LONGS, "checkedLMUL", Signatures.RAW_LONG_BINARY));
        invocations.put(LDIV, new MethodDescriptor(CHECKED_LONGS, "checkedLDIV", Signatures.RAW_LONG_BINARY));

        invocations.put(LNEG, new MethodDescriptor(CHECKED_LONGS, "checkedLNEG", Signatures.RAW_LONG_UNARY));

        invocations.put(DADD, new MethodDescriptor(CHECKED_DOUBLES, "checkedDADD", Signatures.RAW_DOUBLE_BINARY));
        invocations.put(DSUB, new MethodDescriptor(CHECKED_DOUBLES, "checkedDSUB", Signatures.RAW_DOUBLE_BINARY));
        invocations.put(DMUL, new MethodDescriptor(CHECKED_DOUBLES, "checkedDMUL", Signatures.RAW_DOUBLE_BINARY));
        invocations.put(DDIV, new MethodDescriptor(CHECKED_DOUBLES, "checkedDDIV", Signatures.RAW_DOUBLE_BINARY));
        invocations.put(DREM, new MethodDescriptor(CHECKED_DOUBLES, "checkedDREM", Signatures.RAW_DOUBLE_BINARY));
        invocations.put(DCMPL, new MethodDescriptor(CHECKED_DOUBLES, "checkedDCMPL", Signatures.RAW_DOUBLE_CMP));
        invocations.put(DCMPG, new MethodDescriptor(CHECKED_DOUBLES, "checkedDCMPG", Signatures.RAW_DOUBLE_CMP));

        invocations.put(FADD, new MethodDescriptor(CHECKED_FLOATS, "checkedFADD", Signatures.RAW_FLOAT_BINARY));
        invocations.put(FSUB, new MethodDescriptor(CHECKED_FLOATS, "checkedFSUB", Signatures.RAW_FLOAT_BINARY));
        invocations.put(FMUL, new MethodDescriptor(CHECKED_FLOATS, "checkedFMUL", Signatures.RAW_FLOAT_BINARY));
        invocations.put(FREM, new MethodDescriptor(CHECKED_FLOATS, "checkedFREM", Signatures.RAW_FLOAT_BINARY));
        invocations.put(FDIV, new MethodDescriptor(CHECKED_FLOATS, "checkedFDIV", Signatures.RAW_FLOAT_BINARY));
        invocations.put(FCMPL, new MethodDescriptor(CHECKED_FLOATS, "checkedFCMPL", Signatures.RAW_FLOAT_CMP));
        invocations.put(FCMPG, new MethodDescriptor(CHECKED_FLOATS, "checkedFCMPG", Signatures.RAW_FLOAT_CMP));

        invocations.put(L2I, new MethodDescriptor(CHECKED_CASTS, "checkedL2I", Signatures.RAW_L2I));
        invocations.put(I2S, new MethodDescriptor(CHECKED_CASTS, "checkedI2S", Signatures.RAW_I2S));
        invocations.put(I2C, new MethodDescriptor(CHECKED_CASTS, "checkedI2C", Signatures.RAW_I2C));
        invocations.put(I2B, new MethodDescriptor(CHECKED_CASTS, "checkedI2B", Signatures.RAW_I2B));
        invocations.put(D2F, new MethodDescriptor(CHECKED_CASTS, "checkedD2F", Signatures.RAW_D2F));
        invocations.put(D2I, new MethodDescriptor(CHECKED_CASTS, "checkedD2I", Signatures.RAW_D2I));
        invocations.put(D2L, new MethodDescriptor(CHECKED_CASTS, "checkedD2L", Signatures.RAW_D2L));
        invocations.put(F2I, new MethodDescriptor(CHECKED_CASTS, "checkedF2I", Signatures.RAW_F2I));
        invocations.put(F2L, new MethodDescriptor(CHECKED_CASTS, "checkedF2L", Signatures.RAW_F2L));
    }

    @Override
    public void instrument(MethodVisitor mv, int opCode, String classPath, Methods methods, Reaction r, LocalVariablesSorter src) {
        mv.visitLdcInsn(reaction.value());
        mv.visitLdcInsn(logFileName);

        Arg arg = Arg.fromOpCode(opCode);

        if (arg != null) {
            stats.incrementCounterValue(arg);
            invocations.get(opCode).invokeStatic(mv);
        }
    }

}
