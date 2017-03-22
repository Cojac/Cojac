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
    HELP   ("h"),
    VERBOSE("v"),

    PRINT       ("Sc"),
    EXCEPTION   ("Se"),
    CALL_BACK   ("Sk"),
    LOG_FILE    ("Sl"),
    DETAILED_LOG("Sd"),
    
    BYPASS               ("Xb"),
    FILTER               ("Xf"),
    RUNTIME_STATS        ("Xs"),
    INSTRUMENTATION_STATS("Xt"),
    
    JMX_ENABLE("jmxenable"),
    JMX_HOST  ("jmxhost"),
    JMX_PORT  ("jmxport"),
    JMX_NAME  ("jmxname"),
    
    REPLACE_FLOATS("R"), // used internally, but no more appears in the usage
    FLOAT_WRAPPER ("Wf"),
    DOUBLE_WRAPPER("Wd"),
    NG_WRAPPER    ("W"),
    
    BIG_DECIMAL_WR("Rb"),    // BigDecimal nbOfDigits
    INTERVAL_WR   ("Ri"),    // Interval computation
    STOCHASTIC_WR ("Rs"),    // StochasticArithmetic
    AUTODIFF_WR   ("Ra"),    // AutoDiff "forward mode"
    AUTODIFFREV_WR("Rar"),   // AutoDiff "reverse mode"
    SYMBOLIC_WR   ("Rsymb"), // was: Rsy
    CHEBFUN_WR    ("Rcheb"),
    DISABLE_UNSTABLE_CMP_CHECK("RnoUnstableCmp"),
    STABILITY_THRESHOLD       ("RunstableAt"),
    /* Badoud */
    LISTING_INSTRUCTIONS    ("Bddwrite"),  // was: -Li path/to/file (badoud) 
    LOAD_BEHAVIOUR_MAP      ("Bddread"),   // was: Lbm
    POLY_BEHAVIOURAL_LOGGING("Rddwrite"),  // was: Rpbl
    POLY_BEHAVIOURAL_LOAD   ("Rddread"),   // was: Rpbload
    /* V.Gazzola */
    DOUBLE2FLOAT("Bdaf"),               // was: BD2F
    ROUND_BIASED_UP       ("Beroundu"), // was: Rbu
    ROUND_BIASED_DOWN     ("Beroundd"), // was: Rbd
    ROUND_BIASED_RANDOM   ("Beroundr"), // was: Rbr
    ROUND_NATIVELY_UP     ("Bnroundu"), // was: Rnu
    ROUND_NATIVELY_DOWN   ("Bnroundd"), // was: Rnd
    ROUND_NATIVELY_TO_ZERO("Bnroundz"), // was: Rnz
    ARBITRARY_PRECISION   ("Bpr"),      // was: Ap
    DOUBLE_INTERVAL       ("Bdai"),     // was: Di
    CMPFUZZER             ("Bfuz"), 
    INSTRUMENT_SELECTIVELY("Only"),     // was: Oi
    
    ALL    ("Ca"),
    NONE   ("Cn"),
    OPCODES("Copcodes"),
    INTS   ("Cints"),  // warning: its ordinal value is used (individual opcodes must be below)
    FLOATS ("Cfloats"),
    DOUBLES("Cdoubles"),
    LONGS  ("Clongs"),
    CASTS  ("Ccasts"),
    MATHS  ("Cmath"),
    
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
                false, "Signal problems with console messages to stderr (default signaling policy)");
        options.addOption(Arg.EXCEPTION.shortOpt(),
                false, "Signal problems by throwing an ArithmeticException");
        options.addOption(OptionBuilder
                .withArgName("meth")
                .hasArg()
                .withDescription("Signal problems by calling " +
                    "a user-supplied method matching this signature:" +
                    "...public static void f(String msg) \n" +
                    "(Give a fully qualified identifier in the form: \n" +
                    "pkgA/myPkg/myClass/myMethod)")
                .create(Arg.CALL_BACK.shortOpt()));
        options.addOption(OptionBuilder
                .withArgName("file")
                .hasOptionalArg()
                .withDescription("Signal problems by writing to a log file.\n" +
                    "Default filename is: " + Args.DEFAULT_LOG_FILE_NAME + '.')
                .create(Arg.LOG_FILE.shortOpt()));
        options.addOption(Arg.DETAILED_LOG.shortOpt(),
                false, "Log the full stack trace (combined with -Cc or -Cl)");
        options.addOption(OptionBuilder
                .withArgName("prefixes")
                .hasOptionalArg()
                .withDescription("Bypass classes starting with one of these prefixes (semi-colon separated list). " +
                        "\nExample: -Xb foo;bar.util\n will skip classes with name foo* or bar.util*")
                .create(Arg.BYPASS.shortOpt()));
        options.addOption(Arg.FILTER.shortOpt(),
                false, "Report each problem only once per faulty line");
        options.addOption(Arg.RUNTIME_STATS.shortOpt(),
                false, "Print runtime statistics");
        options.addOption(Arg.INSTRUMENTATION_STATS.shortOpt(),
                false, "Print instrumentation statistics");
        
        options.addOption(Arg.JMX_ENABLE.shortOpt(), false, "Enable JMX feature");
        options.addOption(OptionBuilder
            .withArgName("host")
            .hasArg()
            .withDescription("Set remote JMX connection host (default: localhost)")
            .create(JMX_HOST.shortOpt()));
        options.addOption(OptionBuilder
            .withArgName("port")
            .hasArg()
            .withDescription("Set remote JMX connection port (default: 5017)")
            .create(JMX_PORT.shortOpt()));
        options.addOption(OptionBuilder
                .withArgName("ID")
                .hasArg()
                .withDescription("Set remote MBean name (default: COJAC)")
                .create(JMX_NAME.shortOpt()));
        
//        options.addOption(Arg.REPLACE_FLOATS.shortOpt(),
//            "replacefloats", false, "Replace floats by Cojac-wrapper objects ");
        
        options.addOption(OptionBuilder
                .withArgName("class")
                .hasArg()
                .withDescription("Select the double container (not for regular users!).\n" +
                        "Example: -"+DOUBLE_WRAPPER.shortOpt()+" cojac.MyDouble will use com.github.cojac.models.wrappers.MyDouble")
                .create(DOUBLE_WRAPPER.shortOpt()));
        options.addOption(OptionBuilder
                .withArgName("class")
                .hasArg()
                .withDescription("Select the float container. See "+DOUBLE_WRAPPER.shortOpt())
                .create(FLOAT_WRAPPER.shortOpt()));
        options.addOption(OptionBuilder
                .withArgName("class")
                .hasArg()
                .withDescription("Select the wrapper (not for regular users!).\n" +
                        "Example: -"+NG_WRAPPER.shortOpt()+" cojac.WrapperBasic will use com.github.cojac.models.wrappers.WrapperBasic")
                .create(NG_WRAPPER.shortOpt()));
		
        options.addOption(OptionBuilder
                .withArgName("digits")
                .hasArg()
                .withDescription("Use BigDecimal wrapping with arbitrarily high precision." +
                        "Example: -"+BIG_DECIMAL_WR.shortOpt()+" 100 will wrap with 100-significant-digit BigDecimals")
                .create(BIG_DECIMAL_WR.shortOpt()));
		
        options.addOption(Arg.INTERVAL_WR.shortOpt(),
                false,"Use interval computation wrapping");
        options.addOption(Arg.STOCHASTIC_WR.shortOpt(),
                false,"Use discrete stochastic arithmetic wrapping");
        options.addOption(Arg.AUTODIFF_WR.shortOpt(),
                false,"Use automatic differentiation (forward mode) wrapping");
        options.addOption(Arg.AUTODIFFREV_WR.shortOpt(),
                false,"Use automatic differentiation (reverse mode) wrapping");
        options.addOption(Arg.SYMBOLIC_WR.shortOpt(),
                false,"Use symbolic wrapping");
        options.addOption(Arg.CHEBFUN_WR.shortOpt(),
                false,"Use chefun wrapping");
        options.addOption(Arg.DISABLE_UNSTABLE_CMP_CHECK.shortOpt(),
                false,"Disable unstability checks in comparisons, for the Interval or Stochastic wrappers");
        options.addOption(OptionBuilder
                .withArgName("e")
                .hasArg()
                .withDescription("Relative precision considered unstable, eg for Interval/Stochastic wrappers (default 0.00001)")
                .create(STABILITY_THRESHOLD.shortOpt()));
        // badoud
        options.addOption(OptionBuilder
                .withArgName("file")
                .hasArg()
                .withDescription("Write the located effect of the Wrapper to an XML file (used for Delta-Debugging)")
                .create(Arg.POLY_BEHAVIOURAL_LOGGING.shortOpt()));
        options.addOption(OptionBuilder
                .withArgName("file")
                .hasArg()
                .withDescription("Read an XML file to tune how the Wrapper behaves (used for Delta-Debugging)")
                .create(Arg.POLY_BEHAVIOURAL_LOAD.shortOpt())); 
        
        options.addOption(Arg.ALL.shortOpt(),
                false, "Sniff everywhere (this is the default behavior)");
        options.addOption(Arg.NONE.shortOpt(),
                false, "Don't sniff at all");
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
                false, "Use doubles as if they were single-precision floats");
        options.addOption(Arg.ROUND_BIASED_UP.shortOpt(),
                false, "Emulate \"round\" (biased) up");
        options.addOption(Arg.ROUND_BIASED_DOWN.shortOpt(),
                false, "Emulate \"round\" (biased) down");
        options.addOption(Arg.ROUND_BIASED_RANDOM.shortOpt(),
                false, "Emulate \"round\" (biased) randomly");
        options.addOption(Arg.DOUBLE_INTERVAL.shortOpt(),
                false, "Hijack doubles as low-precision intervals");
        options.addOption(Arg.ROUND_NATIVELY_UP.shortOpt(),
                false, "Change the CPU rounding mode toward plus infinity");
        options.addOption(Arg.ROUND_NATIVELY_DOWN.shortOpt(),
                false, "Change the CPU rounding mode toward minus infinity");
        options.addOption(Arg.ROUND_NATIVELY_TO_ZERO.shortOpt(),
                false, "Change the CPU rounding mode toward zero");
        options.addOption(Arg.CMPFUZZER.shortOpt(), 
                false, "toggle CMP results for operands too close together");
        options.addOption(OptionBuilder
                .withArgName("nbOfBits")
                .hasArg()
                .withDescription("limit the precision of a double's mantissa." +
                        "Example: -"+ARBITRARY_PRECISION.shortOpt()+" 8 emulates eight-significant bits floats and doubles")
                .create(ARBITRARY_PRECISION.shortOpt()));
        options.addOption(OptionBuilder
                .withArgName("code")
                .hasOptionalArg()
                .withDescription("Select precisely which portions of code will be instrumented. The syntax is sketched in this "+
                        "example: -"+Arg.INSTRUMENT_SELECTIVELY.shortOpt()+" \"pkg.Foo{m1(II)I,m3(),1,12,112,25};pkg.Bar{10-354}\" "+
                        "Will instrument fully methods m1(II)I, m3() and lines [1,12,112,25] from Class pkg.Foo "+
                        "and lines 10 to 354 (inclusive) from Class pkg.Bar")
                .create(Arg.INSTRUMENT_SELECTIVELY.shortOpt()));
        //Badoud
        options.addOption(OptionBuilder
                .withArgName("file")
                .hasArg()
                .withDescription("Write the located effect of an ArithmeticBehavior to an XML file (used for Delta-Debugging)")
                .create(Arg.LISTING_INSTRUCTIONS.shortOpt()));      
        options.addOption(OptionBuilder
                .withArgName("file")
                .hasArg()
                .withDescription("Read an XML file to tune how an ArithmeticBehavior behaves (used for Delta-Debugging)")
                .create(Arg.LOAD_BEHAVIOUR_MAP.shortOpt()));
        return options;
    }

}