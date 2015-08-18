See http://code.google.com/p/cojac/

Happy numerical problem sniffing!

usage: java -javaagent:cojac.jar="[OPTIONS]" YourApp [appArgs]
            (version 1.4 - 2015 Aug 16)
 -a,--all                    Instrument every operation (int, long, cast)
 -AD,--autodiff              procede automatic differentiation
 -b,--bypass <arg>           Bypass classes starting with one of these
                             prefixes (semi-colon separated list).
                             Example: -b foo;bar.util skips classes with
                             name foo* or bar.util*
 -BDP,--BDPrecision <arg>    Select the BigDecimal wrapper precision
                             (number of decimal)Example: -BDP 100 set 100
                             decimals for BigDecimal wrapper
 -c,--console                Signal overflows with console messages to
                             stderr (default signaling policy)
 -casts                      Activate all the casts opcodes
 -d,--detailed               logs the full stack trace (combined with -c
                             or -l)
 -d2f                        Instrument the d2f operation
 -d2i                        Instrument the d2i operation
 -d2l                        Instrument the d2l operation
 -dadd                       Instrument the dadd operation
 -dcmp                       Instrument the dcmp operation
 -ddiv                       Instrument the ddiv operation
 -dmul                       Instrument the dmul operation
 -doubles                    Activate all the doubles opcodes
 -drem                       Instrument the drem operation
 -dsub                       Instrument the dsub operation
 -DW,--doublewrapper <arg>   Select the DoubleWrapper wanted (Must be in
                             COJAC or in classpath)Example: -DW
                             cojac.BigDecimalDouble use the class as
                             doubles replacement object
 -e,--exception              Signal overflows by throwing an
                             ArithmeticException
 -f,--filter                 Report each problem only once per faulty line
 -f2i                        Instrument the f2i operation
 -f2l                        Instrument the f2l operation
 -fadd                       Instrument the fadd operation
 -fcmp                       Instrument the fcmp operation
 -fdiv                       Instrument the fdiv operation
 -floats                     Activate all the floats opcodes
 -fmul                       Instrument the fmul operation
 -frem                       Instrument the frem operation
 -fsub                       Instrument the fsub operation
 -FW,--floatwrapper <arg>    Select the FloatWrapper wanted (Must be in
                             COJAC or in classpath)Example: -FW
                             cojac.BigDecimalFloat use the class as floats
                             replacement object
 -h,--help                   Print the help of the program
 -I,--interval               Use Interval computation float/double wrapper
 -i2b                        Instrument the i2b operation
 -i2c                        Instrument the i2c operation
 -i2s                        Instrument the i2s operation
 -iadd                       Instrument the iadd operation
 -idiv                       Instrument the idiv operation
 -iinc                       Instrument the iinc operation
 -imul                       Instrument the imul operation
 -ineg                       Instrument the ineg operation
 -ints                       Activate all the ints opcodes
 -isub                       Instrument the isub operation
 -jmxenable                  Enable JMX feature.
 -jmxhost <host>             Set the host for remote JMX connection
                             (Default is localhost).
 -jmxname <MBean-id>         Set the name of the remote MBean (Default is
                             COJAC).
 -jmxport <port>             Set the port for remote JMX connection
                             (Default is 5017).
 -k,--callback <meth>        Signal overflows by calling a particular
                             user-defined method whose definition matches
                             the following:
                             public static void f(String msg)
                             Give the fully qualified method identifier,
                             in the form:
                             pkgA/myPkg/myClass/myMethod
 -l,--logfile <path>         Signal overflows by writing to a log file.
                             Default filename is: COJAC_Report.log.
 -l2i                        Instrument the l2i operation
 -ladd                       Instrument the ladd operation
 -ldiv                       Instrument the ldiv operation
 -lmul                       Instrument the lmul operation
 -lneg                       Instrument the lneg operation
 -longs                      Activate all the longs opcodes
 -lsub                       Instrument the lsub operation
 -maths                      Detect problems in (Strict)Math.XXX() methods
 -n,--none                   Don't instrument any instruction
 -noUnstableComparisons      disable unstability checks in comparisons,
                             for the Interval or Stochastic wrappers
 -R,--replacefloats          Replaces floats by CojacFloat objects
 -s,--summary                Print runtime statistics
 -STO,--stochastic           Use discrete stochastic arithmetic
                             float/double wrapper
 -t,--stats                  Print instrumentation statistics
 -unstableUnder <arg>        Relative precision considered unstable, for
                             the Interval or Stochastic wrappers (eg
                             0.0001)
 -v,--verbose                display some internal traces
