<p align="center">
<img src="https://github.com/Cojac/Cojac/wiki/images/logo-cojac-512.png"
   width=256" />
<h1 align="center">********** User Guide **********</h1>
</p>

1. [Introduction](home#1---introduction)

  1. [Overview](home#11---overview)
  2. [Launching an application with COJAC](home#12---launching-an-application-with-cojac)
  3. [Links and notes](home#13---links-and-notes)
  
2. [COJAC: the numerical sniffer](home#2---cojac-the-numerical-sniffer)
  
  1. [What COJAC considers suspicious](home#21---what-cojac-considers-suspicious)
  2. [Configuring what COJAC will detect](home#22---configuring-what-cojac-will-detect)
  3. [Configuring how COJAC will signal the detected problems](home#23---configuring-how-cojac-will-signal-the-detected-problems)
  4. [Instrumenting without executing](home#24---instrumenting-without-executing)
  5. [Example](home#25---example)
  6. [JMX feature](home#26---jmx-feature)
  
3. [COJAC: the enriching wrapper](home#3---cojac-the-enriching-wrapper)

  1. [Our wrapping mechanism](home#31---our-wrapping-mechanism)
  2. [Number model "BigDecimal"](home#32---number-model-bigdecimal)
  3. [Number model "Interval computation"](home#33---number-model-interval-computation)
  4. [Number model "Discrete stochastic arithmetic"](home#34---number-model-discrete-stochastic-arithmetic)
  5. [Number model "Automatic differentiation"](home#35---number-model-automatic-differentiation)

4. [Detailed usage](home#4---detailed-usage)

5. [Limitations and known issues](home#5---limitations-and-known-issues)

  1. [Issues with the sniffer](home#51---issues-with-the-sniffer)
  2. [Issues with the wrapper](home#52---issues-with-the-wrapper)

6. [And now...](home#6---and-now)

--------------------------------------------------
# 1 - Introduction

Welcome to COJAC, a tool that leverages in new ways the arithmetic 
capabilities of the Java programming language. The idea is summarized in 
small demos on [YouTube](https://youtu.be/DqAFQfbWZOU?list=PLHLKWUtT0B7kNos1e48vKhFlGAXR1AAkF).

If you need a definition for the acronym "COJAC", feel free to choose one of 
following:

- `C`limbing `O`ver `J`ava `A`lgebraic `C`omputation
- `C`reating `O`ther `J`uicy `A`rithmetic `C`apabilities
- `C`hecking `O`verflows in `JA`va `C`ode

## 1.1 - Overview
COJAC is in fact a two-fold tool: 

- a **_"numerical sniffer"_** that detects anomalies arising in arithmetic operations, 
on both integers (eg overflow) and floating point numbers (eg 
cancellation or NaN outcome). This can be of great help to detect vicious bugs 
involving annoying events that normally happen silently in the Java Virtual 
Machine. See [§2](home#2---cojac-the-numerical-sniffer). This tool is pretty 
stable and efficient (but can't handle constant expressions evaluated at compile-time).

- an **_"enriching wrapper"_** that automatically converts every float/double data 
into richer number types, so that you can experiment, at a very low programming cost,
and in a very elegant way, some
beautiful models such as arbitrary precision numbers, interval computation,
the marvelous automatic differentiation, even symbolic expressions and functions 
or Chebfun. 
See [§3](home#3---cojac-the-enriching-wrapper). This enriching wrapper is fun and a bit experimental (some
[limitations](home#52---issues-with-the-wrapper) such as quite naive models implementation, 
strong slowdown, poorly tested support for Java8 lambdas, problem when user-code 
is "called back" from Java library...).

With COJAC you don't have to modify your source code or even recompile. All 
the work is done at runtime, when your application gets instrumented on-the-fly. 

This software is distributed under the "Apache License, v2.0", without any warranty.


## 1.2 - Launching an application with COJAC

COJAC relies on the Java Agent mechanism; our tool is activated via the mere 
insertion of this option in the JVM command line: 
`-javaagent:cojac.jar[=cojac_options]`. 

This mechanism lets you seamlessly activate COJAC at runtime. 
For instance:

```
java -javaagent:cojac.jar -jar MyApplication.jar
java -javaagent:cojac.jar="-Ca -Xf -Xs" -jar MyApplication.jar
java -javaagent:cojac.jar="Rb 100" -cp myFolder my.pkg.MyMain
```

This way it should be widely applicable (applications, servlets, junit test 
case, run from within an IDE or from anywhere else...). 

The integration with existing IDEs should be easy. In Eclipse for instance, 
you could define once, in the context of the "Run Configurations", a new "Variable" 
named `COJAC` with the value `-javaagent:/path/to/cojac.jar="-Xf -Xs"`, 
then you just have to insert `{COJAC}` in the VM arguments to enable the tool 
with your favorite options in any run configurations.

## 1.3 - Links and notes
COJAC is offered (without warranty of any kind) in the hope it can be useful 
for educational purposes, as well as in the software industry. 

You might be interested in the 
[valgrind-based cousin](https://github.com/Cojac/cojac-grind), a 
similar tool not limited to the Java world (it acts on any Linux binary).

The concept of _numerical problem sniffer_ is presented in our 
[article](http://drdobbs.com/testing/232601564) published by Dr Dobb's 
(remember how cool this journal was?). The revolutionary (but still experimental) 
_wrapping feature_ is brand new and has not yet been discussed in any paper.

Any comment/feedback is welcome!

--------------------------------------------------
# 2 - COJAC: the numerical sniffer

The root idea behind COJAC was that it is definitely a pity that Java is not 
able to signal integer overflows at runtime. Then we generalized the idea towards
a full *numerical sniffer*, as discussed in this [article](http://drdobbs.com/testing/232601564).

COJAC is highly configurable, you can specify which operations are to be 
watched, where it should avoid inspecting, and how you want COJAC to warn you about 
the problems. 


## 2.1 - What COJAC considers suspicious

The COJAC sniffer tracks the following kinds of event:

 * *Integer overflow*: the result is out-of-bounds for a `long` or `int` operation (an arithmetic operation, not the bit-shift operations). Examples: `3*Integer.MAX_VALUE, Integer.MIN_VALUE/-1`
 * *Offending typecasting*: a value loses its essence after a type conversion. Examples: `(short) Long.MAX_VALUE, (int) Float.NaN`. We also detect loss
 of precision when converting int-to-float or long-to-double (since release v1.4.1). Example: `(float)Integer.MAX_VALUE`
 * *Absorption*:  adding/subtracting a non-zero floating point number (float or double) has no effect because the two operands have excessively different orders of magnitude. Examples: `(342.0 + 1.0E-43), (342.0 - 1.0E+43)`
 * *Cancellation*: two floating point numbers almost cancel each other after an addition or subtraction, so that the least significant bits (often noise) are promoted to highest significance. Example:  `(3.000001f - 3.0f)`
 * *Questionable comparisons*: two floating point numbers are being compared, but they are very close together. Example: `if (3.000001f >= 3.0f)...`
 * *Underflow*: the result of a division is so small that it gets rounded to zero. Example: `(2.5E-233 / 7.2E+233)`
 * *NaN or Infinite results*: an operations leads to NaN or Infinity, from finite non-NaN operands. Example: `Math.acos(1.00001)`
 
Note that all these example expressions, when written verbatim in Java, 
are evaluated at compile-time because they involve only literal values; so in 
that (uninteresting) case, the problems won't be detected by COJAC (see 
[known issues](home#51---issues-with-the-sniffer)).

--------------------------------------------------
## 2.2 - Configuring what COJAC will detect

COJAC offers a fine-grained configuration of what must be instrumented in the application; the options let you select which category (or even which bytecode instructions) to watch:

 * `-Ca` to activates all possible detectors (this is the default behaviour)
 * `-Cints` to watch the "int" operations: `IADD`,`IDIV`,`IINC`,`ISUB`,`IMUL`,`INEG`
 * `-Clongs` to watch the "long" operations: `LADD`,`LDIV`,`LSUB`,`LMUL`,`LNEG`
 * `-Cdoubles` to watch the "double" operations: `DADD`,`DDIV`,`DSUB`,`DMUL`,`DREM`
 * `-Cfloats` to watch the "float" operations: `FADD`,`FDIV`,`FSUB`,`FMUL`,`FREM`
 * `-Ccasts`  to watch the typecasting operations: `L2I`,`I2S`,`I2C`,`I2B`,`D2F`,`D2I`,`D2L`,`F2I`,`F2L`
 * `-Cmath`  to watch the invocations of java.lang.Math methods, like `Math.sqrt` etc.

By default, all detectors are activated.

COJAC instruments the code everywhere, except in classes from the standard library 
(`java.*`, `javax.*`, ...), and possibly what has been requested to be skipped 
with one of the mechanisms described below.

### 2.2.1 - Excluding some code with -b option

One way of preventing the instrumentation of a class is to use the `-Xb` 
bypass option : 
```
java -javaagent:cojac.jar="-Xb ch.eif.;com.foo.Bar"
```

This will discard the instrumentation of any class whose fully qualified name
starts with "ch.eif." or "com.foo.Bar". 

### 2.2.2 - Excluding some code with annotations

It is possible to exclude particular classes or methods with an annotation
in the source code. The only requirement is to add the following interface
somewhere in your code (in whatever package, but the interface identifier 
has to be strictly respected):

```java
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
@Retention(RUNTIME) public @interface NoCojacInstrumentation {}
```

The annotation is to be placed just before the class or method definition; 
its scope is local.

--------------------------------------------------
## 2.3 - Configuring how COJAC will signal the detected problems

You can  choose the way COJAC will inform you when a problem occurs: 

 * `-Sc`   This is the default behaviour, all the messages are printed to the 
 standard error stream

 * `-Sl file`  All warning messages are appended to a file, the 'file' 
 argument is optional, by default the file is named `COJAC_Report.log`

 * `-Se`  Each problem will throw an `ArithmeticException`

 * `-Sk myMethod`   Each problem will invoke a method of your choice. The method must have that signature: `public static void myMethod(String opcodeName)` and you have to provide its complete qualified path, in the format `pkgA/myPkg/myClass/myMethod`.

You can also configure what information COJAC will give you. Use the `-Sd` 
option to have the detailed stack trace for each warning. You can also use 
the `-Xt` to display the statistics about the instrumentation, namely some 
information about how many bytecode instructions have been instrumented by COJAC. 

COJAC provides a useful `-Xf` option which filters the warnings. For 
instance, if you have the same line that causes the same error many times, 
it's convenient to see only the first occurrence, which is the role of this 
option. If you have enabled the `-Xs` option, all the filtered events will 
be finally synthesized (with occurrences count).  


--------------------------------------------------
## 2.4 - Instrumenting without executing

Early versions of COJAC were able to create "offline" an instrumented version of the 
application code (that can later be executed). We no longer provide this feature: 
COJAC is designed to apply on-the-fly instrumentation, we advocate its use
as a diagnostic-enabled java launching means. 

--------------------------------------------------
## 2.5 - Example

Here is a small example of what COJAC can do. Let's take a simple class like that: 

```java
/*01*/ public class Demo {
/*02*/   public static void main(String[] args) {
/*03*/    int a = 2_000_000_000, b = 2_000_000_000;
/*04*/    int res1 = a + b;
/*05*/    System.out.println("Adding billions: " + res1);
/*06*/    double c = 1E22, d = 321;
/*07*/    double res2 = c + d;
/*08*/    System.out.println("Adding units to a large number : " + res2);
/*09*/    double sqrt = Math.sqrt(-1);
/*10*/    System.out.println("Square root of -1: " + sqrt);
/*11*/  }
/*12*/}
```

Compile it with something like `javac Demo.java`. Then, use COJAC to launch 
this application: `java -javaagent:cojac.jar Demo`.
And you'll get the following output: 

```
COJAC: Overflow : IADD Demo.main(Demo.java:4)
Adding billions: -294967296
COJAC: Absorption: DADD Demo.main(Demo.java:7)
Adding units to a large number : 1.0E22
Square root of -1: NaN
COJAC: Result is NaN: java/lang/Math.sqrt(D)D Demo.main(Demo.java:9)
```

You can see the warning messages in the console. All the COJAC logs start 
with ''COJAC: '', followed by the kind of anomaly and the location in 
the source code (so that in an Eclipse console for instance, you can easily 
get redirected to the edited file).

--------------------------------------------------
## 2.6 - JMX feature

COJAC gives the developer access to detection management through JMX. 
The management interface is the following:

```java
// gives every opcode instrumented with statistics
Map<String, Integer> getCountersMBean(); 
// offers the developer a means to know which methods are subject to annotation
List<String> getBlacklist(); 
// gives every detected event that occurred since the beginning of the application
Map<String, Long> getEvent();
// let you dynamically enable or disable the processing of the detected events.
void start(); 
void stop(); 
```

The RMI service URL is parameterized by a couple of COJAC options. Here is 
the pattern and an example of default configuration:

```
pattern:
service:jmx:rmi:///jndi/rmi://<host>:<port>/<MBean-name>

default: 
service:jmx:rmi:///jndi/rmi://localhost:5017/COJAC
```

You can access the management interface programmatically or using a tool such 
as jconsole. This can be great to watch long-term running applications or 
web services.

--------------------------------------------------
# 3 - COJAC: the enriching wrapper

We have been working on the automatic replacement of the primitive types 
float/double (as well as their "standard wrapper" counterpart Float/Double) by 
objects that realize richer number models.

Now our poor old floating point numbers can be enriched in various ways, 
that we only start to study. There are still limitations, but... yes we did it!

## 3.1 - Our wrapping mechanism

Our wrapping mechanism... is automatic! Just tell us which number model you want to 
activate, and there you go. Everywhere there are float/Float/double/Double data 
in user code (local variables, parameters, attributes...), they will be replaced
by an object redefining the arithmetic behavior. As the code inside the Java library 
cannot be transformed in such a way, we designed a mechanism that automatically 
deals with the necessary conversions when the user code invokes a method of the
Java library (with the sad consequence that the "enrichment" is then lost).

The "arithmetic behavior" that is embedded in our richer number types includes: 

* the basic operations (addition, multiplication...) corresponding to the
 bytecode instructions `dadd`, `dsub`, `dmul`, `ddiv`, `drem`, `dneg`

* the most common operations defined in `java.lang.Math`: `sqrt`, `abs`, 
`sin`, `cos`, `tan`, `asin`, `acos`, `atan`, `sinh`, `cosh`, `tanh`, 
`exp`, `log`, `log10`, `toRadians`, `toDegrees`.

COJAC features a mechanism of *Magic Methods* that can be used to interact with 
the new enriched numbers. A magic method has an identifier starting with 
the prefix `COJAC_MAGIC_` and a specific signature, to be declared in user 
code; at runtime, COJAC will redirect the call to such methods 
towards the implementation inside the wrapper,
thus giving the user code access to the "richer number" power.

The magic methods are model-specific (see below), but every number wrapper will at least provide those two methods: 
```java
public static String COJAC_MAGIC_wrapperName();
public static String COJAC_MAGIC_toString(double n); // internal representation
  
```

## 3.2 - Number model "BigDecimal"

Our first "enriched number" type provides arbitrary precision numbers, where
the number of significant digits is specified at runtime. The implementation 
relies on `java.math.BigDecimal`. 

This wrapper is activated with the option `-Rb nDigits`, and it does not 
provide additional magic methods.

Isn't it a killing feature? Your code uses standard `float`/`double` and at runtime you decide to compute everything with 150 significant digits if you like!

Here is a small demo program. 

```java
public class HelloMrCojac {  
  public static String COJAC_MAGIC_toString(double n) { return ""; }
  public static void main(String[] args) {
    double p1=approxPi(10);
    double p2=approxPi(1000);
    double p3=approxPi(1000000);
    System.out.println(p1+" "+COJAC_MAGIC_toString(p1));
    System.out.println(p2+" "+COJAC_MAGIC_toString(p2));
    System.out.println(p3+" "+COJAC_MAGIC_toString(p3));
  }
  public static double approxPi(long m) {
    double r = 1.0, dm, dp, k;
    for (long i=2; i<=m; i=i+2) {
      k = 2*i;
      dm = 1.0/(k-1);
      dp = 1.0/(k+1);
      r  = r - dm + dp;
    }
    return 4*r;
  }
}
```

A normal run will produce that output: 

```
prompt> java HelloMrCojac
3.232315809405594
3.1425916543395442
3.1415936535887745
```

Now, let's compute with twice as much significant digits:

```
prompt> java -javaagent:cojac.jar="-Rb 30" HelloMrCojac
3.2323158094055926 3.23231580940559268732643345647
3.142591654339543  3.14259165433954305090112773730
3.141593653588793  3.14159365358879323921264313231
```


Just for fun, let's show that we can also decrease the internal precision 
down to only 2 significant digits:

```
prompt> java -javaagent:cojac.jar="-Rb 2" HelloMrCojac
3.3 3.3
3.3 3.3
3.3 3.3
```

## 3.3 - Number model "Interval computation"

The traditional float/double types compute approximate results (due to possible 
rounding), but they can give no information at all about how far we get from 
the "true" mathematical result. The idea behind the *interval computation* technique of *reliable computing*  is to represent numbers with an interval that is guaranteed to hold the true result, thus keeping track of the possible error margin. 

Our Interval wrapper is a toy realization of this technique. Internally, we emit
a warning whenever the relative error of a number goes beyond a certain threshold 
(that can be adjusted via an option).

This wrapper is activated with the option `-Ri`. Two additional magic methods are defined for this model:
```java
public static double COJAC_MAGIC_relativeError(double n);
public static double COJAC_MAGIC_width(double n); // interval width max-min
```

For now, we are yet far from the quality of a good "interval computation" library. For 
instance, we systematically round up/down instead of adjusting the 
rounding mode. Nevertheless, the strength and simplicity of the automatic wrapping 
should not be underestimated.

## 3.4 - Number model "Discrete stochastic arithmetic"

Interval computation is known to offer strong guarantees, but also to be overly 
pessimistic in the estimation of the relative error (the interval is often far 
wider than needed). _Discrete stochastic arithmetic_ is an interesting 
alternative: roughly, the idea is to keep 3 versions of every arithmetic
result, each corresponding to the application of a rounding mode chosen _randomly_. 
An unstable computation has a high probability of producing 3 manifestly divergent 
results, and the relative error can be estimated.

For the moment, we don't follow the full-strength theory behind stochastic
arithmetic, and simply round up/down randomly. We claim again that the 
real breakthrough lies in the ease of our tool, which helps experimenting
with the model.

This wrapper is activated with the option `-Rs`. 
That wrapper offers the same magic methods as our interval computation model, 
and is similar in its way of signaling high relative errors.

## 3.5 - Number model "Automatic differentiation" ("forward" mode)

*Automatic differentiation* is an awesome idea (never heard of it? You _should_ have 
a look!). One reason the technique is not in widespread use lies in the fact that 
there is a lack of *simple* tools that can show its power. We pretend that the 
situation dramatically changes thanks to COJAC. 

In the "forward mode" Autodiff model, you somehow "mark" one of your input data as 
the dependent variable, then compute any functions you want from that value, and
the various results somehow stores as a second component the derivative against that variable. 

This wrapper is activated with the option `-Ra`. 
Here are the magic methods that are provided:
```java
public static double COJAC_MAGIC_asDerivationTarget(double a);
public static double COJAC_MAGIC_getDerivation(double a);
```

Let's show them in action with a small example.

```java
public class AutoDiffForward {
  public static double COJAC_MAGIC_getDerivation(double a)      { return 0; }
  public static double COJAC_MAGIC_asDerivationTarget(double a) { return a; }
  static double someFunction(double x, double a, double b) {
    double res=a*x*x;     // the computation can be complex,
    res = res + b*x;      // with loops, calls, recursion etc.
    res = res + 1;
    return res;     // f: a X^2 + b X + 1        f': 2aX + b
  }
  public static void main(String[] args) {
    double x=2;
    x=COJAC_MAGIC_asDerivationTarget(x);  // we'll compute df/dx
    double y=someFunction(x, 3, 4);
    System.out.println("f(2):  "+y);
    System.out.println("f'(2): "+COJAC_MAGIC_getDerivation(y));
  }
```

And we run it with the appropriate Derivation wrapper:

```
prompt> java -javaagent:cojac.jar="-Ra" AutoDiffForward
f(x):  21.0
f'(x): 16.0
```

As you can check, the results are those expected!

## 3.6 - Number model "Automatic differentiation" ("backward" mode)

Just to insist: Autodiff is an awesome idea. In the "backward mode" Autodiff 
model, you perform your various computations, and then you tell one of the results 
that you are interested in every possible partial derivative. Suddenly, each 
number variables is getting aware of its role as a dependent variable and is
happy to give you the corresponding partial derivative. 

Here is a small demo. 

```
public class AutodiffBackwardDemo {
    public static double COJAC_MAGIC_partialDerivativeAgainst(double a) { return 0;}
    public static void COJAC_MAGIC_computePartialDerivatives(double a) {}

    public static double somePolynomial0(double x, double y) {
        return 3*x*x + 2*y;
    }

    public static void main(String[] args) {
        double r, x, y;
        x=2.0; y=3.0; 
        r=somePolynomial(x, y);
        System.out.println("f(x,y): "+r);
        COJAC_MAGIC_computePartialDerivatives(r);
        
        System.out.println("df/dx: "+COJAC_MAGIC_partialDerivativeAgainst(x));
        System.out.println("df/dy: "+COJAC_MAGIC_partialDerivativeAgainst(y));
    }
}

```

## 3.7 - Number model "Symbolic" expressions (and functions!)

Symbolic computation is the jewel concept at the heart of brilliant environments 
like Mathematica or WolframAlpha. Apparently, the way C or Java processes `float`
or `double` numbers light-years away from that. Before COJAC, the only means 
to play with symbolic expressions in Java was to find/develop a library that
defines new classes for the numbers and methods to combine them with various 
operations. 

But, although Java processes standard types purely numerically, there is still a 
trace of the corresponding formulas in the bytecode. With the "Symbolic" wrapper, 
COJAC is able to rebuild the full expression trees at runtime. It could be the
starting point of amazing features, and we illustrate this by showing how to 
embed simplification rules that apply only at the symbolic level.

Here is an example.

```
public class HelloSymbolic {
    public static String COJAC_MAGIC_toString(double n) { return ""; }

    public static void main(String[] args) {
        double a = 3.1, b = a - 0.1;
        double c = b * (a / (a - 0.1));
        System.out.println(c);
        System.out.println(COJAC_MAGIC_toString(c));
    }
}
```

Normally in Java, the computation of `c` is applied numerically, and in this
particular case there will be a tiny rounding error: 

```
$ java demo.HelloSymbolic
3.1000000000000005

```

Just activate the "Symbolic" wrapper to radically change the way the computation 
is done: 

------------------------------------------
-------
# 4 - Detail


usage


Here is the full manpage-like description of COJAC, as produced by the help argument (`java -javaagent:cojac.jar=

h"`
: 

```
usage: java -javaagent:cojac.jar="[OPTIONS]" YourApp [
ppArgs]
            (version 1.4 - 2015

ct 21)

Two nice tools to enrich Java arithmetic capabilities, on-
he-fly:
- Numerical Problem Sniffer: detects and signals arithmetic poisons like
integer
  overflows, smearing and catastrophic cancellation, NaN or infinite 
esults.
- Enriching Wrapper for float/double: wraps every double/float in richer o
jects. 
  Current models include BigDecimal (you choose the precision), interval comp
tation,
  discrete stochastic arithmetic, and even automatic differen
iat
on.
```
```
----------------- OPTIONS ---------
-------
 -Ca,--all                   Sniff everywhere (this is the default b
havior)
 -Ccasts                     Sniff in casts
opcodes
 -Cdoubles                   Sniff in doubles
opcodes
 -Cfloats                    Sniff in floats
opcodes
 -Cints                      Sniff in ints
opcodes
 -Clongs                     Sniff in longs
opcodes
 -Cmath                      Sniff in (Strict)Math.xyz()
methods
 -Cn,--none                  Don't snif
 at all
 -Copcodes <arg>             Sniff in those (comma separated) opco
es; eg:
                             iadd,idiv,iinc,isub,imul,ineg,ladd,lsub,lmu
,ldiv,l
                             neg,dadd,dsub,dmul,ddiv,drem,dcmp,fadd,fsub
fmul,fd
                             iv,frem,fcmp,l2i,i2s,i2c,i2b,d2f,d2i,d2l
f2i,f2l
 -h,--help                   Print the help of the program 
nd exit
 -jmxenable                  Enable JMX
feature
 -jmxhost <host>             Set remote JMX connection host (default: lo
alhost)
 -jmxname <MBean-id>         Set remote MBean name (default
 COJAC)
 -jmxport <port>             Set remote JMX connection port (defaul
: 5017)
 -R_noUnstableComparisons    Disable unstability checks in comparisons,
for the
                             Interval or Stochastic 
rappers
 -R_unstableAt <epsilon>     Relative precision considered unsta
le, for
                             Interval/Stochastic wrappers (default 
.00001)
 -Ra,--autodiff              Use automatic differentiation 
rapping
 -Rb,--bigdecimal <digits>   Use BigDecimal wrapping with a certain p
ecision
                             (number of 
igits).
                             Example: -Rb 100 will w
ap with
                             100-significant-digit Big
ecimals
 -Ri,--interval              Use interval computation 
rapping
 -Rs,--stochastic            Use discrete stochastic arithmetic 
rapping
 -Sc,--console               Signal problems with console messages t
 stderr
                             (default signaling
policy)
 -Sd,--detailed              Log the full stack trace (combined with -Cc
or -Cl)
 -Se,--exception             Signal problems by throwing an ArithmeticE
ception
 -Sk,--callback <meth>       Signal problems by calling a user-supplie
 method
                             matching this si
nature:
                             ...public static void f(Str
ng msg)
                             (Give a fully qualified identifier in t
e form:
                             pkgA/myPkg/myClass/m
Method)
 -Sl,--logfile <path>        Signal problems by writing to a l
g file.
                             Default filename is: COJAC_Rep
rt.log.
 -v,--verbose                Display some interna
 traces
 -Xb,--bypass <prefixes>     Bypass classes starting with one of these 
refixes
                             (semi-colon separate
 list).
                             Example: -Xb foo;
ar.util
                             will skip classes with name foo* or b
r.util*
 -Xf,--filter                Report each problem only once per fau
ty line
 -Xs,--summary               Print runtime st
tistics
 -Xt,--stats                 Print instrumentation st

istics

------> https://github.com/Cojac/Cojac 
---

- 
```

------------------------------------------
-------
# 5 - Limitations and know

issues

This section discusses a couple of issues for the current version o

Cojac.

## 5.1 - Issues with the

niffer

The sniffer part of COJAC is rather stable. Here are some limi

tions:

 * Of course a suspicious operation is not always the manifestation of a software defect. For instance, you can rely on integer overflows to compute a hash function, or maybe the cancellation phenomenon is not a problem because the floating point numbers you deal with do not suffer from impr

ision.

 * That's inherent to the approach: compile-time expressions can't be pro
essed: 
 don't complain that a "hello world" use c
se like
`System.out.println(3*Integer.MAX_VALUE);` does not log the o
e
flow!
 
 * The tool targets the JVM only. We focus on Java, but it might be inte
esting 
 to try it on other JVM-equipped languages (Scala, Jython...). By the 
ay, if 
 you dream of a numerical sniffer not limited to the Java world, have a 
ook at 
 [cojac-grind](https://github.com/Cojac/cojac-g

nd)...

## 5.2 - Issues with the

rapper

The wrapper part of COJAC should be considered an experimental prototyp
. Here 
are some limi

tions:

* The frontier between "user code" and "java library" has some serious defe
ts, eg 
when arrays of numbers are i

olved.

* We don't handle the "callbacks" from java library to user code when f
oating 
point numbers are being passed

round.

* We have tried to handle `invokedynamic` (at least how Java8 co
pilers 
use it), but it has not been thoroughly  tested yet, so we expect some p
oblems 
with Java8 lambdas (Cojac 1.4.1 has fixed some pr

lems).

* In case of `class A extends J` where `J` is from the java libr
ry, and
offers a method `f()` (with floating point parameters/result) that 
A` does
not redefine : suppose the declaration `A a`, then the call `a.f(...)
 fails,
whereas `((J)a).f(...)` is OK. This should be fixed in Coja

1.4.1.

* We don't handle the possible use of Java reflection (in case of
method 
invocations via reflection, we don't apply the necessary transforma

ons). 

* The decision of converting both the primitive types float/double a
d their
original wrapper Float/Double brings several problems, eg signature confli
ts, or 
comparison (compareTo/equals) misb

avior.

* The implementation of the models is really naive. For instance, we do not
compute
every Math.* operations with the required precision in the BigDecima

model.

* Of course, the slow-down is very im

rtant.

------------------------------------------
-------
# 6 - An

now...

...well, happy problem sniffing, and happy number wra

ing!  

<tt>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</tt>  ...please give us feedback :smiley: via an issue on github, or `the.google.tool@gma

.com`.

<tt>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</tt>  The COJA

Team. 

------------------------------------------
-------
### Acknowled

ments 

Several Computer Science students from [HEIA-FR](https://www.heia
fr.ch) 
have been involved in some way in the COJAC p

ject: 

- Ruggiero Botteon and Diego Cavadini (for the very first pre-pr
totype)
- Baptiste Wicht (the major contributor, strong refactoring, testing - many 
hanks!)
- Maxime Reymond (for an Eclipse Plugin, now (temporarily?) disco
tinued)
- Vincent Pasquier (for switching to the "Java agent" tec
nology)
- Luis Domingues (for improving the Valgrind cousin Coja
-grind)
- Romain Monnard (without whom the "wrapping" would still be an unimplemented dream - many 
hanks!)
- Sylvain Julmy (for populating 3 "rich number
 types)
- Lucy Linder (for improving the whole, preparing the release on GitHub, design the first two videos, and much more - many 
thanks!)
- Valentin Gazzola (for adding new ways to modify the artithmetic behavior, including changing the rounding mode and forcing double to act like
floats)
- Rémi Badoud (for adding a mode where we rebuild genuine symbolic expressions that we can symbolically simplify, and abstract functions that can be stored either symbolically or like Matlab's Chebfun; and also for adding a Delta-Debugging infrastructure t
