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
import ch.eiafr.cojac.instrumenters.stack.*;
import ch.eiafr.cojac.instrumenters.variables.*;

import java.util.HashMap;
import java.util.Map;

import static org.objectweb.asm.Opcodes.*;

public final class SimpleOpCodeFactory implements OpCodeInstrumenterFactory {
    private final Map<Integer, OpCodeInstrumenter> instrumenters = new HashMap<Integer, OpCodeInstrumenter>(15);
    private final OpCodeInstrumenter opSizeInstrumenter;

    private final Args args;
    private final InstrumentationStats stats;

    public SimpleOpCodeFactory(Args args, InstrumentationStats stats) {
        super();

        this.args = args;
        this.stats = stats;

        opSizeInstrumenter = new OpSizeInstrumenter();

        instrumenters.put(IADD, new IADDInstrumenter());
        instrumenters.put(ISUB, new ISUBInstrumenter());
        instrumenters.put(IDIV, new IDIVInstrumenter());
        instrumenters.put(INEG, new INegInstrumenter());

        if (args.isSpecified(Arg.VARIABLES)) {
            instrumenters.put(IMUL, new IMULVariablesInstrumenter());
        } else {
            instrumenters.put(IMUL, new IMULInstrumenter());
        }

        instrumenters.put(LDIV, new LDIVInstrumenter());
        instrumenters.put(LNEG, new LNEGInstrumenter());

        if (args.isSpecified(Arg.VARIABLES)) {
            instrumenters.put(LADD, new LADDVariablesInstrumenter());
            instrumenters.put(LSUB, new LSUBVariablesInstrumenter());
            instrumenters.put(LMUL, new LMULVariablesInstrumenter());
        } else {
            instrumenters.put(LADD, new LADDInstrumenter());
            instrumenters.put(LSUB, new LSUBInstrumenter());
            instrumenters.put(LMUL, new LMULInstrumenter());
        }

        instrumenters.put(L2I, new L2IInstrumenter());
        instrumenters.put(I2S, new I2SInstrumenter());
        instrumenters.put(I2B, new I2BInstrumenter());
        instrumenters.put(I2C, new I2CInstrumenter());
        instrumenters.put(D2I, new D2IInstrumenter());
        instrumenters.put(D2F, new D2FInstrumenter());
        instrumenters.put(D2L, new D2LInstrumenter());
        instrumenters.put(F2I, new F2IInstrumenter());
        instrumenters.put(F2L, new F2LInstrumenter());

        if (args.isSpecified(Arg.VARIABLES)) {
            instrumenters.put(DADD, new DADDVariablesInstrumenter());
            instrumenters.put(DSUB, new DSUBVariablesInstrumenter());
            instrumenters.put(DDIV, new DDIVVariablesInstrumenter());
            instrumenters.put(DMUL, new DMULVariablesInstrumenter());
            instrumenters.put(DREM, new DREMVariablesInstrumenter());
            instrumenters.put(DCMPL, new DCMPVariablesInstrumenter());
            instrumenters.put(DCMPG, new DCMPVariablesInstrumenter());

            instrumenters.put(FADD, new FADDVariablesInstrumenter());
            instrumenters.put(FSUB, new FSUBVariablesInstrumenter());
            instrumenters.put(FDIV, new FDIVVariablesInstrumenter());
            instrumenters.put(FMUL, new FMULVariablesInstrumenter());
            instrumenters.put(FREM, new FREMVariablesInstrumenter());
            instrumenters.put(FCMPL, new FCMPVariablesInstrumenter());
            instrumenters.put(FCMPG, new FCMPVariablesInstrumenter());
        } else {
            instrumenters.put(DADD, new DADDInstrumenter());
            instrumenters.put(DSUB, new DSUBInstrumenter());
            instrumenters.put(DDIV, new DDIVInstrumenter());
            instrumenters.put(DMUL, new DMULInstrumenter());
            instrumenters.put(DREM, new DREMInstrumenter());
            instrumenters.put(DCMPL, new DCMPLInstrumenter());
            instrumenters.put(DCMPG, new DCMPGInstrumenter());

            instrumenters.put(FADD, new FADDInstrumenter());
            instrumenters.put(FSUB, new FSUBInstrumenter());
            instrumenters.put(FDIV, new FDIVInstrumenter());
            instrumenters.put(FMUL, new FMULInstrumenter());
            instrumenters.put(FREM, new FREMInstrumenter());
            instrumenters.put(FCMPL, new FCMPLInstrumenter());
            instrumenters.put(FCMPG, new FCMPGInstrumenter());
        }
    }

    @Override
    public OpCodeInstrumenter getInstrumenter(int opCode, Arg arg) {
        if (arg != null && args.isOperationEnabled(arg)) {
            if (!args.isSpecified(Arg.WASTE_SIZE)) {
                return opSizeInstrumenter;
            }

            if (instrumenters.containsKey(opCode)) {
                stats.incrementCounterValue(arg);

                return instrumenters.get(opCode);
            }
        }

        return null;
    }
}