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

package ch.eiafr.cojac;

import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.objectweb.asm.Opcodes;

public enum Arg {
    REPLACE_FLOATS("R"),
	FLOAT_WRAPPER("FW"),
	DOUBLE_WRAPPER("DW"),
	BIG_DECIMAL_PRECISION("BDP"),
    HELP("h"),
    ALL("a"),
    PRINT("c"),
    PATH("p"),
    EXCEPTION("e"),
    DETAILED_LOG("d"),
    RUNTIME_STATS("s"),
    CALL_BACK("k"),
    LOG_FILE("l"),
    FILTER("f"),
    NONE("n"),
    BYPASS("b"),
    INSTRUMENTATION_STATS("t"),
    NO_CANCELLATION("XnoCancellation"), //not yet ready
    JMX_ENABLE("jmxenable"),
    JMX_HOST("jmxhost"),
    JMX_PORT("jmxport"),
    JMX_NAME("jmxname"),
    VERBOSE("v"),
    INTERVAL("I"),
    INTERVAL_COMP("IC"),

    INTS("ints"),
    FLOATS("floats"),
    DOUBLES("doubles"),
    LONGS("longs"),
    CASTS("casts"),
    MATHS("maths"),

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
    F2L("f2l", Opcodes.F2L, CASTS);

    private final int opCode;
    private final String name;
    private final Arg parent;

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

    @SuppressWarnings("static-access")
    static Options createOptions() {
        Options options = new Options();

        options.addOption(Arg.REPLACE_FLOATS.shortOpt(),
            "replacefloats", false, "Replaces floats by CojacFloat objects ");
		options.addOption(Arg.FLOAT_WRAPPER.shortOpt(),
            "floatwrapper", true, "Select the FloatWrapper wanted (Must be in COJAC or in classpath)" +
            "Example: -FW cojac.BigDecimalFloat use the class as floats replacement object");
		options.addOption(Arg.DOUBLE_WRAPPER.shortOpt(),
            "doublewrapper", true, "Select the DoubleWrapper wanted (Must be in COJAC or in classpath)" +
            "Example: -DW cojac.BigDecimalDouble use the class as doubles replacement object");
		options.addOption(Arg.BIG_DECIMAL_PRECISION.shortOpt(),
            "BDPrecision", true, "Select the BigDecimal wrapper precision (number of decimal)" +
            "Example: -BDP 100 set 100 decimals for BigDecimal wrapper");
        options.addOption(Arg.NONE.shortOpt(),
            "none", false, "Don't instrument any instruction");
        options.addOption(Arg.BYPASS.shortOpt(),
            "bypass", true, "Bypass classes starting with one of these prefixes (semi-colon separated list). " +
            "Example: -b foo;bar.util skips classes with name foo* or bar.util*");
        options.addOption(Arg.HELP.shortOpt(),
            "help", false, "Print the help of the program");
        options.addOption(Arg.PRINT.shortOpt(),
            "console", false, "Signal overflows with console messages to stderr (default signaling policy)");
        options.addOption(Arg.EXCEPTION.shortOpt(),
            "exception", false, "Signal overflows by throwing an ArithmeticException");
        options.addOption(Arg.ALL.shortOpt(),
            "all", false, "Instrument every operation (int, long, cast)");
        options.addOption(Arg.FILTER.shortOpt(),
            "filter", false, "Report each problem only once per faulty line");
        options.addOption(Arg.INSTRUMENTATION_STATS.shortOpt(),
            "stats", false, "Print instrumentation statistics");
        options.addOption(Arg.RUNTIME_STATS.shortOpt(),
            "summary", false, "Print runtime statistics");
        options.addOption(Arg.INTERVAL.shortOpt(),
                "interval",true,"Replaces double and float by Cojac's Interval Object");
        options.addOption(Arg.INTERVAL_COMP.shortOpt(),
                "interval",false,"Replaces double and float by Cojac's Interval Object and check only the comparaison");

        options.addOption(Arg.MATHS.shortOpt(),
            false, "Detect problems in (Strict)Math.XXX() methods");
        options.addOption(Arg.INTS.shortOpt(),
            false, "Activate all the ints opcodes");
        options.addOption(Arg.LONGS.shortOpt(),
            false, "Activate all the longs opcodes");
        options.addOption(Arg.CASTS.shortOpt(),
            false, "Activate all the casts opcodes");
        options.addOption(Arg.DOUBLES.shortOpt(),
            false, "Activate all the doubles opcodes");
        options.addOption(Arg.FLOATS.shortOpt(),
            false, "Activate all the floats opcodes");

        options.addOption(Arg.DETAILED_LOG.shortOpt(),
            "detailed", false, "logs the full stack trace (combined with -c or -l)");

        options.addOption(Arg.VERBOSE.shortOpt(),
                "verbose", false, "display some internal traces");

        //feature not ready!
//      options.addOption(Arg.NO_CANCELLATION.shortOpt(),
//          false, "Disable cancellation test in DADD/DSUB/FADD/FSUB");

        //Disabled temporarily, well... definitely!

//      options.addOption("v", false, "Enable COJAC to add variables in instrumented bytecode");

        options.addOption(Arg.JMX_ENABLE.shortOpt(), false, "Enable JMX feature.");

//      options.addOption(OptionBuilder.
//          withLongOpt("frames").
//          withDescription("Compute Java Frames in bytecode (instrumentation will be slower)").
//          create());  // no short opt


        options.addOption(OptionBuilder.
            withLongOpt("path").
            withArgName("path").
            hasArg().
            withDescription("Path where the instrumented code will be written. Default is " + Args.DEFAULT_OUTPUT_DIR).
            create(Arg.PATH.shortOpt()));

        options.addOption(OptionBuilder.
            withLongOpt("callback").
            withArgName("meth").
            hasArg().
            withDescription("Signal overflows by calling " +
                "a particular user-defined method whose definition " +
                "matches the following:\n public static void f(String msg) \n" +
                "Give the fully qualified method identifier, in the form: \n" +
                "pkgA/myPkg/myClass/myMethod").
            create(Arg.CALL_BACK.shortOpt()));

        options.addOption(OptionBuilder.
            withLongOpt("logfile").
            withArgName("path").
            hasOptionalArg().
            withDescription("Signal overflows by writing to a log file. " +
                "Default filename is: " + Args.DEFAULT_LOG_FILE_NAME + '.').
            create(Arg.LOG_FILE.shortOpt()));

        options.addOption(OptionBuilder.
            withArgName("host").
            hasArg().
            withDescription("Set the host for remote JMX connection (Default is localhost).").
            create(JMX_HOST.shortOpt()));

        options.addOption(OptionBuilder.
            withArgName("port").
            hasArg().
            withDescription("Set the port for remote JMX connection (Default is 5017).").
            create(JMX_PORT.shortOpt()));

        options.addOption(OptionBuilder.
            withArgName("MBean-id").
            hasArg().
            withDescription("Set the name of the remote MBean (Default is COJAC).").
            create(JMX_NAME.shortOpt()));

        for (Arg arg : Arg.values()) {
            if (arg.isOperator()) {
                options.addOption(arg.shortOpt(), false, "Instrument the " + arg.shortOpt() + " operation");
            }
        }

        return options;
    }

}