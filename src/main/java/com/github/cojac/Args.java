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

package com.github.cojac;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.github.cojac.models.ReactionType;

public final class Args {
    private final Options options = Arg.createOptions();
    private final Map<Arg, ArgValue> values = new EnumMap<>(Arg.class);
    
    static final String DEFAULT_LOG_FILE_NAME = "COJAC_Report.log";
    static final String DEFAULT_JMX_HOST = "localhost";
    static final String DEFAULT_JMX_PORT = "5217";
    static final String DEFAULT_JMX_NAME = "COJAC";
    static final String DEFAULT_STABILITY_THRESHOLD = "0.0001";
    static final String BEHAVIOUR_PACKAGE = "com/github/cojac/models/behaviours/";
    
    private final static Map<Arg, String> behaviours = new EnumMap<>(Arg.class);

    static{
        behaviours.put(Arg.INTS,    "CheckedIntBehaviour");
        behaviours.put(Arg.DOUBLES, "CheckedDoubleBehaviour");
        behaviours.put(Arg.FLOATS,  "CheckedFloatBehaviour");
        behaviours.put(Arg.LONGS,   "CheckedLongBehaviour");
        behaviours.put(Arg.CASTS,   "CheckedCastBehaviour");
        behaviours.put(Arg.MATHS,   "CheckedMathBehaviour");
        behaviours.put(Arg.ROUND_BIASED_UP,     "PseudoRoundingBehaviour");
        behaviours.put(Arg.ROUND_BIASED_DOWN,   "PseudoRoundingBehaviour");
        behaviours.put(Arg.ROUND_BIASED_RANDOM, "PseudoRoundingBehaviour");
        behaviours.put(Arg.ARBITRARY_PRECISION,    "ConversionBehaviour");
        behaviours.put(Arg.ROUND_NATIVELY_UP,      "ConversionBehaviour");
        behaviours.put(Arg.ROUND_NATIVELY_DOWN,    "ConversionBehaviour");
        behaviours.put(Arg.ROUND_NATIVELY_TO_ZERO, "ConversionBehaviour");
        behaviours.put(Arg.DOUBLE2FLOAT,           "ConversionBehaviour");
        behaviours.put(Arg.DOUBLE_INTERVAL,    "DoubleIntervalBehaviour");
        behaviours.put(Arg.CMPFUZZER,               "CmpFuzzerBehaviour");
    }
    
    private static final String USAGE =
             "java -javaagent:cojac.jar=\"[OPTIONS]\" YourApp [appArgs]\n"
           + "(version 1.5.1 - 2019 Nov 7)";
    private static final String HEADER =
            "\nNice tools to enrich Java arithmetic capabilities, on-the-fly:"
            +"\n - Numerical Problem Sniffer: detects and signals arithmetic poisons like "
            +"integer overflows, aborption, catastrophic cancellation, NaN or infinite results."
            +"\n - New float/double behaviors: double-as-floats, change rounding mode... Some known "
            +"tricks to track possible numerical instabilities."
            +"\n - Enriching Wrapper for float/double: wraps every double/float in richer objects. Current "
            +"models include BigDecimal (you choose the precision!), "
            +"interval computation, automatic differentiation, symbolic processing, chebfun and more."
            +"\n----------------- OPTIONS -----------------\n";
    private static final String FOOTER =
            "\n------> https://github.com/Cojac/Cojac <------";
    
    // private String behaviour = behaviours.get(Arg.ALL);
    
    public Args() {
        super();

        for (Arg arg : Arg.values()) {
            values.put(arg, new ArgValue(false, ""));
        }
    }

    public boolean parse(String[] args) {
        // in an older version, we parsed <options> <appsOrFiles> <appArgs>...
        String[] cojacArgs = args; 
        try {
            CommandLine commandLine = new GnuParser().parse(options, cojacArgs);
            for (Arg arg : Arg.values()) {
                if (commandLine.hasOption(arg.shortOpt())) {
                    values.get(arg).setSpecified();
                    values.get(arg).setValue(commandLine.getOptionValue(arg.shortOpt()));
                    verifyOptionFormat(arg);
                }
            }
        } catch (ParseException e) {
            System.out.println("Invalid command line.  Reason: " + e.getMessage());
            return false;
        }
        setDefaults();
        return true;
    }

    private void verifyOptionFormat(Arg arg) throws ParseException {
        String val=values.get(arg).getValue();
        String msg="bad format for option "+arg;
        try {
            switch(arg) {
            case BIG_DECIMAL_WR:
            case ARBITRARY_PRECISION:
            case JMX_PORT: Integer.parseInt(val); break;
            case STABILITY_THRESHOLD: Double.parseDouble(val); break;
            case OPCODES: processOpcodeList(val); break;
            }
        } catch(Exception e) {
            throw new ParseException(msg);
        }
    }

    private void processOpcodeList(String val) throws Exception {
        String[] t=val.split(",");
        for(String s:t) {
            Arg a=Arg.fromName(s);
            if (a==null || !a.isOperator()) 
                throw new Exception("bad opcode name");
            specify(a);
        }
    }

    private void setDefaults() {
        if (isSpecified(Arg.NONE)) {
            disableAll();
        } else if (!areSomeCategoriesSelected() && !areSomeOpcodesSelected()) {
            specifyDefaultsOpCode();
        }

        if (isSpecified(Arg.LOG_FILE)) {
            if (values.get(Arg.LOG_FILE).getValue() == null) {
                values.get(Arg.LOG_FILE).setValue(DEFAULT_LOG_FILE_NAME);
            }
        }

        if (!(isSpecified(Arg.LOG_FILE) || isSpecified(Arg.EXCEPTION) || isSpecified(Arg.CALL_BACK))) {
            specify(Arg.PRINT);
        }

        if (isSpecified(Arg.JMX_ENABLE)) {
            if (values.get(Arg.JMX_HOST).getValue().equals("")) {
                values.get(Arg.JMX_HOST).setValue(DEFAULT_JMX_HOST);
            }
            if (values.get(Arg.JMX_PORT).getValue().equals("")) {
                values.get(Arg.JMX_PORT).setValue(DEFAULT_JMX_PORT);
            }
            if (values.get(Arg.JMX_NAME).getValue().equals("")) {
                values.get(Arg.JMX_NAME).setValue(DEFAULT_JMX_NAME);
            }
        }
        if(!isSpecified(Arg.STABILITY_THRESHOLD))
            values.get(Arg.STABILITY_THRESHOLD).setValue(DEFAULT_STABILITY_THRESHOLD);
        
        if (isSpecified(Arg.FLOAT_WRAPPER)) {
            specify(Arg.REPLACE_FLOATS);
        }
    }

    private void disableAll() {
        for (Arg arg : Arg.values()) {
            if (arg.isOperator()) {
                unspecify(arg);
            }
        }
        unspecify(Arg.ALL);
        unspecify(Arg.INTS);
        unspecify(Arg.DOUBLES);
        unspecify(Arg.FLOATS);
        unspecify(Arg.LONGS);
        unspecify(Arg.MATHS);
        unspecify(Arg.CASTS);
    }

    private void specifyDefaultsOpCode() {
        specify(Arg.INTS);
        specify(Arg.LONGS);
        specify(Arg.CASTS);
        specify(Arg.FLOATS);
        specify(Arg.DOUBLES);
        specify(Arg.MATHS);
        specify(Arg.CASTS);
        //behaviour = behaviours.get(Arg.ALL);
    }

    private boolean areSomeCategoriesSelected() {
        return isSpecified(Arg.INTS) || isSpecified(Arg.DOUBLES) || isSpecified(Arg.FLOATS) ||
            isSpecified(Arg.LONGS) || isSpecified(Arg.MATHS) || isSpecified(Arg.CASTS) 
            || isSpecified(Arg.DOUBLE2FLOAT)|| isSpecified(Arg.ROUND_BIASED_UP)
            || isSpecified(Arg.ROUND_BIASED_DOWN)|| isSpecified(Arg.ROUND_BIASED_RANDOM)
            || isSpecified(Arg.ARBITRARY_PRECISION)||isSpecified(Arg.DOUBLE_INTERVAL)
            ||isSpecified(Arg.ROUND_NATIVELY_UP)||isSpecified(Arg.ROUND_NATIVELY_DOWN)
            ||isSpecified(Arg.ROUND_NATIVELY_TO_ZERO)||isSpecified(Arg.CMPFUZZER);
    }

    private boolean areSomeOpcodesSelected() {
        for (Arg arg : Arg.values()) {
            if (arg.isOperator() && isSpecified(arg)) {
                return true;
            }
        }
        return false;
    }

    public void printHelpAndExit() {
        HelpFormatter f=new HelpFormatter();
        f.setWidth(80);
        f.printHelp(USAGE, HEADER, options, FOOTER);
        System.exit(0);
    }

    public boolean isSpecified(Arg arg) {
       /* if(behaviours.containsKey(arg)){
            behaviour = behaviours.get(arg);
        }*/
        return values.get(arg).isSpecified();
    }

    public boolean specify(Arg arg) {
//        if(behaviours.containsKey(arg)){
//            behaviour = behaviours.get(arg);
//        }
        return values.get(arg).setSpecified();
    }

    public boolean unspecify(Arg arg) {
//        if(behaviours.containsKey(arg)){
//            behaviour = null;
//        }
        return values.get(arg).setSpecified(false);
    }

    public String getValue(Arg arg) {
        return values.get(arg).getValue();
    }

    public void setValue(Arg arg, String value) {
        specify(arg);

        values.get(arg).setValue(value);
    }
    
    public boolean isOperationEnabled(Arg arg) {
        boolean res=isSpecified(arg) || arg.getParent() != null && isSpecified(arg.getParent());
        if (arg.ordinal() >= Arg.INTS.ordinal()) {
            return res || isSpecified(Arg.ALL);
        } 
        return res;
    }
    
    public ReactionType getReactionType() {
        if (isSpecified(Arg.PRINT)) {
            return isSpecified(Arg.DETAILED_LOG) ? ReactionType.PRINT : ReactionType.PRINT_SMALLER;
        } else if (isSpecified(Arg.LOG_FILE)) {
            return isSpecified(Arg.DETAILED_LOG) ? ReactionType.LOG : ReactionType.LOG_SMALLER;
        } else if (isSpecified(Arg.EXCEPTION)) {
            return ReactionType.EXCEPTION;
        } else if (isSpecified(Arg.CALL_BACK)) {
            return ReactionType.CALLBACK;
        } else if (isSpecified(Arg.NUMERICAL_PROFILER)) {
            return ReactionType.PRINT_PROFILER;
        }
        throw new RuntimeException("no reaction is defined!");
    }
    
    public String getBehaviour() {
        Set<Arg> args=EnumSet.noneOf(Arg.class);
        if(isSpecified(Arg.ALL) || isSpecified(Arg.OPCODES)) {
            args.add(Arg.CASTS);
            args.add(Arg.INTS);
            args.add(Arg.LONGS);
            args.add(Arg.DOUBLES);
            args.add(Arg.FLOATS);
        }
        if(isSpecified(Arg.ALL)) {
            args.add(Arg.MATHS);
        }
        for (Arg arg : Arg.values()) {
            if (!arg.isOperator() && isSpecified(arg) &&  
                    behaviours.containsKey(arg)) {
                args.add(arg);
            }
        }
        
        String result = args.stream()
                .map(a -> behaviours.get(a))
                .map(s -> BEHAVIOUR_PACKAGE + s)
                .collect(Collectors.joining(";"));
        return result;
    }

    //========================================================================
    private static final class ArgValue {
        private boolean specified;
        private String value;

        private ArgValue(boolean specified, String value) {
            super();

            this.specified = specified;
            this.value = value;
        }

        public boolean isSpecified() {
            return specified;
        }

        public String getValue() {
            return value;
        }

        public boolean setSpecified() {
            return setSpecified(true);
        }

        public boolean setSpecified(boolean specified) {
            this.specified = specified;

            return this.specified;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}