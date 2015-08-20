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
import ch.eiafr.cojac.reactions.IReaction;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.LocalVariablesSorter;

import java.util.HashMap;
import java.util.Map;

import static org.objectweb.asm.Opcodes.*;

final class DirectInstrumenter implements IOpcodeInstrumenter {
    private final ReactionType reaction;
    private final InstrumentationStats stats;
    private final String logFileName;
    private final Args args;

    private final Map<Integer, InvokableMethod> invocations = new HashMap<Integer, InvokableMethod>(50);

    private static final String CHECKED_INTS = "ch/eiafr/cojac/models/CheckedInts";
    private static final String CHECKED_CASTS = "ch/eiafr/cojac/models/CheckedCasts";
    private static final String CHECKED_LONGS = "ch/eiafr/cojac/models/CheckedLongs";
    private static final String CHECKED_FLOATS = "ch/eiafr/cojac/models/CheckedFloats";
    private static final String CHECKED_DOUBLES = "ch/eiafr/cojac/models/CheckedDoubles";

    DirectInstrumenter(Args args, InstrumentationStats stats) {
        super();
        this.args=args;
        this.stats = stats;

        reaction = args.getReactionType();


        if (args.isSpecified(Arg.CALL_BACK))
            logFileName = args.getValue(Arg.CALL_BACK); // No, I'm not proud of that trick...
        else
            logFileName = args.getValue(Arg.LOG_FILE);

        fillMethods();
    }

    private void fillMethods() {
        invocations.put(IADD, new InvokableMethod(CHECKED_INTS, "checkedIADD", Signatures.RAW_INTEGER_BINARY));
        invocations.put(ISUB, new InvokableMethod(CHECKED_INTS, "checkedISUB", Signatures.RAW_INTEGER_BINARY));
        invocations.put(IMUL, new InvokableMethod(CHECKED_INTS, "checkedIMUL", Signatures.RAW_INTEGER_BINARY));
        invocations.put(IDIV, new InvokableMethod(CHECKED_INTS, "checkedIDIV", Signatures.RAW_INTEGER_BINARY));

        invocations.put(INEG, new InvokableMethod(CHECKED_INTS, "checkedINEG", Signatures.RAW_INTEGER_UNARY));
        invocations.put(IINC, new InvokableMethod(CHECKED_INTS, "checkedIINC", Signatures.RAW_INTEGER_BINARY));

        invocations.put(LADD, new InvokableMethod(CHECKED_LONGS, "checkedLADD", Signatures.RAW_LONG_BINARY));
        invocations.put(LSUB, new InvokableMethod(CHECKED_LONGS, "checkedLSUB", Signatures.RAW_LONG_BINARY));
        invocations.put(LMUL, new InvokableMethod(CHECKED_LONGS, "checkedLMUL", Signatures.RAW_LONG_BINARY));
        invocations.put(LDIV, new InvokableMethod(CHECKED_LONGS, "checkedLDIV", Signatures.RAW_LONG_BINARY));

        invocations.put(LNEG, new InvokableMethod(CHECKED_LONGS, "checkedLNEG", Signatures.RAW_LONG_UNARY));

        invocations.put(DADD, new InvokableMethod(CHECKED_DOUBLES, "checkedDADD", Signatures.RAW_DOUBLE_BINARY));
        invocations.put(DSUB, new InvokableMethod(CHECKED_DOUBLES, "checkedDSUB", Signatures.RAW_DOUBLE_BINARY));
        invocations.put(DMUL, new InvokableMethod(CHECKED_DOUBLES, "checkedDMUL", Signatures.RAW_DOUBLE_BINARY));
        invocations.put(DDIV, new InvokableMethod(CHECKED_DOUBLES, "checkedDDIV", Signatures.RAW_DOUBLE_BINARY));
        invocations.put(DREM, new InvokableMethod(CHECKED_DOUBLES, "checkedDREM", Signatures.RAW_DOUBLE_BINARY));
        invocations.put(DCMPL, new InvokableMethod(CHECKED_DOUBLES, "checkedDCMPL", Signatures.RAW_DOUBLE_CMP));
        invocations.put(DCMPG, new InvokableMethod(CHECKED_DOUBLES, "checkedDCMPG", Signatures.RAW_DOUBLE_CMP));

        invocations.put(FADD, new InvokableMethod(CHECKED_FLOATS, "checkedFADD", Signatures.RAW_FLOAT_BINARY));
        invocations.put(FSUB, new InvokableMethod(CHECKED_FLOATS, "checkedFSUB", Signatures.RAW_FLOAT_BINARY));
        invocations.put(FMUL, new InvokableMethod(CHECKED_FLOATS, "checkedFMUL", Signatures.RAW_FLOAT_BINARY));
        invocations.put(FREM, new InvokableMethod(CHECKED_FLOATS, "checkedFREM", Signatures.RAW_FLOAT_BINARY));
        invocations.put(FDIV, new InvokableMethod(CHECKED_FLOATS, "checkedFDIV", Signatures.RAW_FLOAT_BINARY));
        invocations.put(FCMPL, new InvokableMethod(CHECKED_FLOATS, "checkedFCMPL", Signatures.RAW_FLOAT_CMP));
        invocations.put(FCMPG, new InvokableMethod(CHECKED_FLOATS, "checkedFCMPG", Signatures.RAW_FLOAT_CMP));

        invocations.put(L2I, new InvokableMethod(CHECKED_CASTS, "checkedL2I", Signatures.RAW_L2I));
        invocations.put(I2S, new InvokableMethod(CHECKED_CASTS, "checkedI2S", Signatures.RAW_I2S));
        invocations.put(I2C, new InvokableMethod(CHECKED_CASTS, "checkedI2C", Signatures.RAW_I2C));
        invocations.put(I2B, new InvokableMethod(CHECKED_CASTS, "checkedI2B", Signatures.RAW_I2B));
        invocations.put(D2F, new InvokableMethod(CHECKED_CASTS, "checkedD2F", Signatures.RAW_D2F));
        invocations.put(D2I, new InvokableMethod(CHECKED_CASTS, "checkedD2I", Signatures.RAW_D2I));
        invocations.put(D2L, new InvokableMethod(CHECKED_CASTS, "checkedD2L", Signatures.RAW_D2L));
        invocations.put(F2I, new InvokableMethod(CHECKED_CASTS, "checkedF2I", Signatures.RAW_F2I));
        invocations.put(F2L, new InvokableMethod(CHECKED_CASTS, "checkedF2L", Signatures.RAW_F2L));
    }

    @Override
    public void instrument(MethodVisitor mv, int opCode) { //, String classPath, Methods methods, IReaction r, LocalVariablesSorter src) {
        mv.visitLdcInsn(reaction.value());
        mv.visitLdcInsn(logFileName);

        Arg arg = Arg.fromOpCode(opCode);

        if (arg != null) {
            stats.incrementCounterValue(opCode);// arg
            invocations.get(opCode).invokeStatic(mv);
        }
    }
    
    @Override
    public boolean wantsToInstrument(int opcode) {
        Arg arg = Arg.fromOpCode(opcode);
        if(arg==null) return false;
        return args.isOperationEnabled(arg);
    }

}
