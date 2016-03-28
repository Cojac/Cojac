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

package com.github.cojac.instrumenters;

import com.github.cojac.Arg;
import com.github.cojac.Args;
import com.github.cojac.InstrumentationStats;

public final class ClassLoaderInstrumenterFactory implements IOpcodeInstrumenterFactory {
    private final IOpcodeInstrumenter opCodeInstrumenter;

    public ClassLoaderInstrumenterFactory(Args args, InstrumentationStats stats) {
        super();

        if (args.isSpecified(Arg.REPLACE_FLOATS))
            opCodeInstrumenter = new ReplaceFloatsInstrumenter(args, stats);
        else if(args.isSpecified(Arg.DOUBLE2FLOAT)){
            //System.out.println("d2f specked");
            opCodeInstrumenter = new NewInstrumenter(args, stats);
        }else{
            //System.out.println("default Behaviour(checker)");
            opCodeInstrumenter = new DirectInstrumenter(args, stats);
        }
            
    }

    @Override
    public IOpcodeInstrumenter getInstrumenter(int opcode) {
        if (opCodeInstrumenter.wantsToInstrument(opcode))
            return opCodeInstrumenter;
        return null;
    }
}
