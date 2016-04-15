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

package com.github.cojac;

import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.objectweb.asm.Opcodes;

public enum Arg {
    HELP("h"),
    VERBOSE("v"),

    PRINT("Sc"),
    EXCEPTION("Se"),
    CALL_BACK("Sk"),
    LOG_FILE("Sl"),
    DETAILED_LOG("Sd"),
    
    BYPASS("Xb"),
    FILTER("Xf"),
    RUNTIME_STATS("Xs"),
    INSTRUMENTATION_STATS("Xt"),
    
    JMX_ENABLE("jmxenable"),
    JMX_HOST("jmxhost"),
    JMX_PORT("jmxport"),
    JMX_NAME("jmxname"),
    
    REPLACE_FLOATS("R"), // used internally, but no more appears in the usage
    FLOAT_WRAPPER("Wf"),
    DOUBLE_WRAPPER("Wd"),
    NG_WRAPPER("W"),
    
    BIG_DECIMAL_PRECISION("Rb"),  // -RbigDecimal nbOfDigits
    INTERVAL("Ri"),                 // -Rinterval
    STOCHASTIC("Rs"),             // -RstochasticArithmetic
    AUTOMATIC_DERIVATION("Ra"),    // -Rautodiff
    DISABLE_UNSTABLE_COMPARISONS_CHECK("R_noUnstableComparisons"),
    STABILITY_THRESHOLD("R_unstableAt"),

    ALL("Ca"),
    NONE("Cn"),
    OPCODES("Copcodes"),
    INTS("Cints"),  // warning: its ordinal value is used (individual opcodes must be below)
    FLOATS("Cfloats"),
    DOUBLES("Cdoubles"),
    LONGS("Clongs"),
    CASTS("Ccasts"),
    MATHS("Cmath"),
    
    /*V.Gazzola*/
    
    DOUBLE2FLOAT("BD2F"),
    CHECKB("BC"),
    ROUND_BIASED_UP("Rbu"),
    ROUND_BIASED_DOWN("Rbd"),
    ROUND_BIASED_RANDOM("Rbr"),
    ARBITRARY_PRECISION("Ap"),
    DOUBLE_INTERVAL("Di"),
    ROUNDING("Rn"),
    
    // Those below are used internally, but no more appear in the usage.
    IADD("iadd", Opcodes.IADD, INTS),
    IDIV("idiv", Opcodes.IDIV, INTS),
    IINC("iinc", Opcodes.IINC, INTS),
    ISUB("isub", Opcodes.ISUB, INTS),
    IMUL("imul", Opcodes.IMUL, INTS),
    INEG("ineg", Opcodes.INEG, INTS),

    LADD("ladd", Opcodes.LADD, LONGS),
    LSUB("lsub", Opcodes.LSUB, LONGS),
    LMUL("lmul", Opcodes.LMUL, LONGS),
    LDIV("ldiv", Opcodes.LDIV, LONGS),
    LNEG("lneg", Opcodes.LNEG, LONGS),

    DADD("dadd", Opcodes.DADD, DOUBLES),
    DSUB("dsub", Opcodes.DSUB, DOUBLES),
    DMUL("dmul", Opcodes.DMUL, DOUBLES),
    DDIV("ddiv", Opcodes.DDIV, DOUBLES),
    DREM("drem", Opcodes.DREM, DOUBLES),
    DCMP("dcmp", Opcodes.DCMPL, DOUBLES),

    FADD("fadd", Opcodes.FADD, FLOATS),
    FSUB("fsub", Opcodes.FSUB, FLOATS),
    FMUL("fmul", Opcodes.FMUL, FLOATS),
    FDIV("fdiv", Opcodes.FDIV, FLOATS),
    FREM("frem", Opcodes.FREM, FLOATS),
    FCMP("fcmp", Opcodes.FCMPL, FLOATS),

    L2I("l2i", Opcodes.L2I, CASTS),
    I2S("i2s", Opcodes.I2S, CASTS),
    I2C("i2c", Opcodes.I2C, CASTS),
    I2B("i2b", Opcodes.I2B, CASTS),
    D2F("d2f", Opcodes.D2F, CASTS),
    D2I("d2i", Opcodes.D2I, CASTS),
    D2L("d2l", Opcodes.D2L, CASTS),
    F2I("f2i", Opcodes.F2I, CASTS),
    F2L("f2l", Opcodes.F2L, CASTS),
    I2F("i2f", Opcodes.I2F, CASTS),
    L2D("l2d", Opcodes.L2D, CASTS);

    private final int opCode;
    private final String name;
    private final Arg parent;

    static String allOpcodes="";

    static {
        for (Arg arg : Arg.values())
            if (arg.isOperator()) allOpcodes+=arg.name+",";
        // options.addOption(arg.shortOpt(), false, "Instrument the " + arg.shortOpt() + " operation");
        allOpcodes=allOpcodes.substring(0, allOpcodes.length()-1);
    }
    Arg(String name) {
        this.name = name;
        opCode = -1;
        parent = null;
    }

    Arg(String name, int opCode, Arg parent) {
        this.name = name;
        this.opCode = opCode;
        this.parent = parent;
    }

    public String shortOpt() {
        return name;
    }

    public boolean isOperator() {
        return opCode > 0;
    }

    public int opCode() {
        return opCode;
    }

    public Arg getParent() {
        return parent;
    }

    public static Arg fromOpCode(int opCode) {
        for (Arg arg : values()) {
            if (arg.opCode == opCode) {
                return arg;
            }
        }

        if (opCode == Opcodes.FCMPG) {
            return Arg.FCMP;
        } else if (opCode == Opcodes.DCMPG) {
            return Arg.DCMP;
        }

        return null;
    }
    
    public static Arg fromName(String name) {
        for (Arg arg : values()) {
            if (arg.name.equals(name)) {
                return arg;
            }
        }
        return null;
    }

    @SuppressWarnings("static-access")
    static Options createOptions() {
        Options options = new Options();

        options.addOption(Arg.HELP.shortOpt(),
                "help", false, "Print the help of the program and exit");
        options.addOption(Arg.VERBOSE.shortOpt(),
                "verbose", false, "Display some internal traces");
        options.addOption(Arg.PRINT.shortOpt(),
                "console", false, "Signal problems with console messages to stderr (default signaling policy)");
        options.addOption(Arg.EXCEPTION.shortOpt(),
                "exception", false, "Signal problems by throwing an ArithmeticException");
        options.addOption(OptionBuilder.
                withLongOpt("callback").
                withArgName("meth").
                hasArg().
                withDescription("Signal problems by calling " +
                    "a user-supplied method matching this signature:" +
                    "\n...public static void f(String msg) \n" +
                    "(Give a fully qualified identifier in the form: \n" +
                    "pkgA/myPkg/myClass/myMethod)").
                create(Arg.CALL_BACK.shortOpt()));
        options.addOption(OptionBuilder.
                withLongOpt("logfile").
                withArgName("path").
                hasOptionalArg().
                withDescription("Signal problems by writing to a log file.\n" +
                    "Default filename is: " + Args.DEFAULT_LOG_FILE_NAME + '.').
                create(Arg.LOG_FILE.shortOpt()));
        options.addOption(Arg.DETAILED_LOG.shortOpt(),
                "detailed", false, "Log the full stack trace (combined with -Cc or -Cl)");
        options.addOption(OptionBuilder.
                withLongOpt("bypass").
                withArgName("prefixes").
                hasOptionalArg().
                withDescription("Bypass classes starting with one of these prefixes (semi-colon separated list). " +
                        "\nExample: -Xb foo;bar.util\n will skip classes with name foo* or bar.util*").
                create(Arg.BYPASS.shortOpt()));
        options.addOption(Arg.FILTER.shortOpt(),
                "filter", false, "Report each problem only once per faulty line");
        options.addOption(Arg.RUNTIME_STATS.shortOpt(),
                "summary", false, "Print runtime statistics");
        options.addOption(Arg.INSTRUMENTATION_STATS.shortOpt(),
                "stats", false, "Print instrumentation statistics");
        
        options.addOption(Arg.JMX_ENABLE.shortOpt(), false, "Enable JMX feature");
        options.addOption(OptionBuilder.
            withArgName("host").
            hasArg().
            withDescription("Set remote JMX connection host (default: localhost)").
            create(JMX_HOST.shortOpt()));
        options.addOption(OptionBuilder.
            withArgName("port").
            hasArg().
            withDescription("Set remote JMX connection port (default: 5017)").
            create(JMX_PORT.shortOpt()));
        options.addOption(OptionBuilder.
            withArgName("MBean-id").
            hasArg().
            withDescription("Set remote MBean name (default: COJAC)").
            create(JMX_NAME.shortOpt()));
        
//        options.addOption(Arg.REPLACE_FLOATS.shortOpt(),
//            "replacefloats", false, "Replace floats by Cojac-wrapper objects ");
        
        options.addOption(OptionBuilder.
                withArgName("class").
                hasArg().
                withDescription("Select the double container (don't use it!).\n" +
                        "Example: -Wd cojac.BigDecimalDouble will use com.github.cojac.models.wrappers.BigDecimalDouble").
                create(DOUBLE_WRAPPER.shortOpt()));
        options.addOption(OptionBuilder.
                withArgName("class").
                hasArg().
                withDescription("Select the float container. See -Wd.").
                create(FLOAT_WRAPPER.shortOpt()));
        options.addOption(OptionBuilder.
                withArgName("class").
                hasArg().
                withDescription("Select the wrapper (don't use it!).\n" +
                        "Example: -W cojac.WrapperBasic will use com.github.cojac.models.wrappers.WrapperBasic").
                create(NG_WRAPPER.shortOpt()));
		
        options.addOption(OptionBuilder.
                withLongOpt("bigdecimal").
                withArgName("digits").
                hasArg().
                withDescription("Use BigDecimal wrapping with a certain precision (number of digits).\n" +
                        "Example: -Rb 100 will wrap with 100-significant-digit BigDecimals").
                create(BIG_DECIMAL_PRECISION.shortOpt()));
		
        options.addOption(Arg.INTERVAL.shortOpt(),
                "interval",false,"Use interval computation wrapping");
        options.addOption(Arg.STOCHASTIC.shortOpt(),
                "stochastic",false,"Use discrete stochastic arithmetic wrapping");
        options.addOption(Arg.AUTOMATIC_DERIVATION.shortOpt(),
                "autodiff",false,"Use automatic differentiation wrapping");
        
        options.addOption(Arg.DISABLE_UNSTABLE_COMPARISONS_CHECK.shortOpt(),
                false,"Disable unstability checks in comparisons, for the Interval or Stochastic wrappers");
        options.addOption(OptionBuilder.
                withArgName("epsilon").
                hasArg().
                withDescription("Relative precision considered unstable, for Interval/Stochastic wrappers (default 0.00001)").
                create(STABILITY_THRESHOLD.shortOpt()));

        options.addOption(Arg.ALL.shortOpt(),
                "all", false, "Sniff everywhere (this is the default behavior)");
        options.addOption(Arg.NONE.shortOpt(),
                "none", false, "Don't sniff at all");
        options.addOption(Arg.OPCODES.shortOpt(),
                true, "Sniff in those (comma separated) opcodes; eg: "+allOpcodes);
        options.addOption(Arg.MATHS.shortOpt(),
            false, "Sniff in (Strict)Math.xyz() methods");
        options.addOption(Arg.INTS.shortOpt(),
            false, "Sniff in ints opcodes");
        options.addOption(Arg.LONGS.shortOpt(),
            false, "Sniff in longs opcodes");
        options.addOption(Arg.CASTS.shortOpt(),
            false, "Sniff in casts opcodes");
        options.addOption(Arg.DOUBLES.shortOpt(),
            false, "Sniff in doubles opcodes");
        options.addOption(Arg.FLOATS.shortOpt(),
            false, "Sniff in floats opcodes");
        /*V.Gazzola*/
        options.addOption(Arg.DOUBLE2FLOAT.shortOpt(),
                false, "Cast Doubles into Floats");
        options.addOption(Arg.CHECKB.shortOpt(),
                false, "Sniff Numerical problems");
        options.addOption(Arg.ROUND_BIASED_UP.shortOpt(),
                false, "\"Round\" (Biased) up");
        options.addOption(Arg.ROUND_BIASED_DOWN.shortOpt(),
                false, "\"Round\" (Biased) down");
        options.addOption(Arg.ROUND_BIASED_RANDOM.shortOpt(),
                false, "\"Round\" (Biased) randomly up or down");
        options.addOption(Arg.DOUBLE_INTERVAL.shortOpt(),
                false, "Perform interval computation on Double");
        options.addOption(Arg.ROUNDING.shortOpt(),
                false, "Change the CPU's rounding mode (alpha)");
        options.addOption(OptionBuilder.
                withLongOpt("arbitraryPrecisionBits").
                withArgName("bits").
                hasArg().
                withDescription("Use Arbitrary precision with a certain number of bits in the mantissa.\n" +
                        "Example: -"+ARBITRARY_PRECISION.shortOpt()+" 2 will drop the precision of floats and double to 2 bits").
                create(ARBITRARY_PRECISION.shortOpt()));
        return options;
    }

}