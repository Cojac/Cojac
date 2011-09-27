-------------
INTRODUCTION
-------------

Cojac instruments Java bytecode so that integer overflows will be signaled
at runtime. 

For example, consider the following file Abc.java :

    public class Abc {
      public static void main(String[] args) {
        int a=2, b=Integer.MAX_VALUE;
        System.out.println(a*b);
      } 
    }

Compile it, then instrument the Abc.class file. Executing the instrumented
Abc.class file will generate at runtime a log file Abc_COJAC_Report.log saying:

  COJAC: Overflow: IMUL Abc.main(Abc.java:4)

-------------
USAGE
-------------

java -jar cojac-0.91.jar [options] ClassOrJarFilename

-------------
BASIC OPTIONS
-------------

-l [fileName.log] : signal overflows by writing to a log file.
                    Default filename is: <classname>_COJAC_Report.log
                    This is the default signaling policy
-p                : signal overflows with console messages (to stderr)
-e                : signal overflows by throwing an ArithmeticException
-all              : instruments every operation (int, long, cast)
-path pathName    : path where the instrumented code will be written.
                    Default is .\COJAC_Output\
-stats            : print statistics during instrumentation

-------------
OTHER OPTIONS
-------------

-iadd -isub -imul 
-idiv -iinc -ineg   : instruments that particular int operation (default)
-ladd -lsub -lmul 
-ldiv -lneg         : instruments that particular long operation
-l2i -i2s -i2c -i2b : instruments that particular cast operation						

-call method        : signals overflows by calling a particular user-defined 
                      method whose definition must match the following:
                      
                      public static void f(String instructionName)
                                           
                      Give the fully qualified method identifier, in the form:
                        pkgA/pkgB/Class/myMethod
                                           
-method identifier
        signature   : instruments only the specified method.
                      Give the fully qualified method identifier, in the form:
                        pkgA/myPkg/myClass/myMethod
                      Signature conforms to Java format (as given by javap -s)
                        eg: (I[I)S for short a(int b, int [] c)

-opsize : instruments so that the bytecode size overhead is limited
          (but the instrumented code will be slower...)

-detailedLog : logs the full stack trace (combined with -p or -l)

---------------
ACKNOWLEDGMENTS
---------------

Thanks to Ruggero Botteon and Diego Cavadini for the enthousiasm and
efforts during their diploma project (november 2007).

-------------
NOTES
-------------

- No warranty... Use at your own risks
- Use only one signaling policy (either -p, -e, or -l)
- You can mix checked operations (eg -iadd -lmul -isub -i2b)
- Log file must use the .log extension
- Processed files must end with .jar or .class
- this is a first implementation. The slowdown is quite big. The whole can be
  improved. The source code is getting better but still needs refactoring.
- Problems are expected with logfile signaling for a multi-thread application.
- Compile-time evaluated expression (eg (7*MAX_VALUE) ) won't be signaled... 
- Don't instrument java1.6 bytecode from within an older jvm.
- Happy Cojacing !





