<p align="center">
<img src="https://github.com/Cojac/Cojac/wiki/images/logo-cojac-512.png"
   width=196" />
<h1 align="center">******* COJAC User Guide *******  <a href="https://github.com/Cojac/Cojac/wiki/CojacWiki.pdf">(PDF)</a> </h1>
</p>

1. [Introduction](home#1---introduction)  
    1.1 [Overview](home#11---overview)  
    1.2 [Launching an application with COJAC](home#12---launching-an-application-with-cojac)  
    1.3 [Links and notes](home#13---links-and-notes)
  
2. [COJAC: the numerical sniffer](home#2---cojac-the-numerical-sniffer)  
    2.1 [What COJAC considers suspicious](home#21---what-cojac-considers-suspicious)  
    2.2 [Configuring what COJAC will detect](home#22---configuring-what-cojac-will-detect)    
    2.3 [Configuring where COJAC will be applied](home#23---configuring-where-cojac-will-be-applied)      
    2.4 [Configuring how COJAC will signal the detected problems](home#24---configuring-how-cojac-will-signal-the-detected-problems)      
    2.5 [Instrumenting without executing](home#25---instrumenting-without-executing)    
    2.6 [Example](home#26---example)    
    2.7 [JMX feature](home#27---jmx-feature)
  
3. [COJAC: the arithmetic behavior toolkit ](home#3---cojac-the-arithmetic-behavior-toolkit)  
    3.1 ["Double-as-float" behavior](home#31---double-as-float-behavior)  
    3.2 ["Rounding mode" behavior](home#32---rounding-mode-behavior)    
    3.3 ["Epsilon-fuzzer" behavior](home#33---epsilon-fuzzer-behavior)      

4. [COJAC: the enriching wrapper](home#4---cojac-the-enriching-wrapper)  
    4.1 [Our wrapping mechanism](home#41---our-wrapping-mechanism)  
    4.2 [Wrapper "BigDecimal"](home#42---wrapper-bigdecimal)  
    4.3 [Wrapper "Interval computation"](home#43---wrapper-interval-computation)  
    4.4 [Wrapper "Discrete stochastic arithmetic"](home#44---wrapper-discrete-stochastic-arithmetic)  
    4.5 [Wrapper "Automatic differentiation (forward mode)"](home#45---wrapper-automatic-differentiation-forward-mode)  
    4.6 [Wrapper "Automatic differentiation (reverse mode)"](home#46---wrapper-automatic-differentiation-reverse-mode)  
    4.7 [Wrapper "Symbolic expressions" (and functions)](home#47---wrapper-symbolic-expressions-and-functions)  
    4.8 [Wrapper "Chebfun"](home#48---wrapper-chebfun)  
    4.9 [Wrapper "ComplexNumber"](#49-wrapper-complex-number)  

5. [Detailed usage](home#5---detailed-usage)  

6. [Limitations and known issues](home#6---limitations-and-known-issues)    
    6.1 [Issues with the sniffer](home#61---issues-with-the-sniffer)  
    6.2 [Issues with the wrapper](home#62---issues-with-the-wrapper)

7. [And now...](home#7---and-now)  

--------------------------------------------------
# 1 - Introduction

Welcome to COJAC, a tool that leverages in new ways the arithmetic 
capabilities of the Java programming language. The idea is summarized in 
small demos 
on [YouTube](https://youtu.be/eAy71M34U_I?list=PLHLKWUtT0B7kNos1e48vKhFlGAXR1AAkF).

If you need a definition for the acronym "COJAC", feel free to choose one of 
following:

- `C`limbing `O`ver `J`ava `A`lgebraic `C`omputation
- `C`reating `O`ther `J`uicy `A`rithmetic `C`apabilities
- `C`hecking `O`verflows in `JA`va `C`ode

## 1.1 - Overview
COJAC is in fact a many-fold tool: 

- a **_"numerical sniffer"_** that detects anomalies arising in arithmetic operations, 
on both integers (eg overflow) and floating point numbers (eg 
cancellation or NaN outcome). This can be of great help to detect vicious bugs 
involving annoying events that normally happen silently in the Java Virtual 
Machine. See [§2](home#2---cojac-the-numerical-sniffer). This tool is pretty 
stable and efficient (but can't handle constant expressions evaluated at compile-time).

- a collection of **_new arithmetic behaviors_** that mimic well-known tricks
used to increase the confidence against numerical instabilities. This includes: 
computing as if every `double` were declared as a `float`, changing the 
default rounding mode, or inversing the result of comparisons on operands very 
close together.

- an **_"enriching wrapper"_** that automatically converts every float/double data 
into richer number types, so that you can experiment, at a very low programming cost,
and in the most elegant way, some beautiful models such as arbitrary precision 
numbers, interval computation, the marvelous automatic differentiation, and even 
symbolic expressions or Chebfun. 
See [§4](home#4---cojac-the-enriching-wrapper). This enriching wrapper is fun 
and a bit experimental (some
[limitations](home#62---issues-with-the-wrapper) such as quite naive models 
implementation, 
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
_wrapping feature_ has not yet been discussed in a scientific paper.

Any comment/feedback is welcome!

--------------------------------------------------
# 2 - COJAC: the numerical sniffer

Originally, the root idea behind COJAC was a sigh: it is definitely a pity that 
Java is not able to signal integer overflows at runtime. Soon afterwards we 
generalized the idea towards a full *numerical sniffer*, as discussed in 
this [article](http://drdobbs.com/testing/232601564).

The COJAC sniffer is highly configurable, you can specify which operations are to be 
watched, where it should avoid inspecting, and how you want COJAC to warn you about 
the problems. 


## 2.1 - What COJAC considers suspicious

The COJAC sniffer tracks the following kinds of event:

 * *Integer overflow*: the result is out-of-bounds for a `long` or `int` 
 operation (an arithmetic operation, not the bit-shift operations). 
 Examples: `3*Integer.MAX_VALUE, Integer.MIN_VALUE/-1`
 * *Offending typecasting*: a value loses its essence after a type conversion. 
 Examples: `(short) Long.MAX_VALUE, (int) Float.NaN`. We also detect 
 loss of precision when converting int-to-float or long-to-double (since 
 release v1.4.1). Example: `(float)Integer.MAX_VALUE`
 * *Absorption*:  adding/subtracting a non-zero floating point number (float or 
 double) has no effect because the two operands have excessively different 
 orders of magnitude. Examples: `(342.0 + 1.0E-43), (342.0 - 1.0E+43)`
 * *Cancellation*: two floating point numbers almost cancel each other after 
 an addition or subtraction, so that the least significant bits (often noise) 
 are promoted to highest significance. Example:  `(3.000001f - 3.0f)`
 * *Questionable comparisons*: two floating point numbers are being compared, 
 but they are very close together. Example: `if (3.000001f >= 3.0f)...`
 * *Underflow*: the result of a division is so small that it gets rounded to 
 zero. Example: `(2.5E-233 / 7.2E+233)`
 * *NaN or Infinite results*: an operations leads to NaN or Infinity, from 
 finite non-NaN operands. Example: `Math.acos(1.00001)`
 
Note that nearly all these example expressions, when written verbatim in Java, 
are evaluated at compile-time because they involve only literal values; so in 
that (uninteresting) case, the problems won't be detected by COJAC (see 
[known issues](home#61---issues-with-the-sniffer)).

--------------------------------------------------
## 2.2 - Configuring what COJAC will detect

COJAC offers a fine-grained configuration of what must be instrumented in the 
application; the options let you select which category (or even which bytecode 
instructions) to watch:

 * `-Ca` to activates all possible detectors (this is the default behaviour)
 * `-Cints` to watch the "int" operations: `IADD`,`IDIV`,`IINC`,`ISUB`,`IMUL`,`INEG`
 * `-Clongs` to watch the "long" operations: `LADD`,`LDIV`,`LSUB`,`LMUL`,`LNEG`
 * `-Cdoubles` to watch the "double" operations: `DADD`,`DDIV`,`DSUB`,`DMUL`,`DREM`
 * `-Cfloats` to watch the "float" operations: `FADD`,`FDIV`,`FSUB`,`FMUL`,`FREM`
 * `-Ccasts`  to watch the typecasting operations: `L2I`,`I2S`,`I2C`,`I2B`,`D2F`,`D2I`,`D2L`,`F2I`,`F2L`
 * `-Cmath`  to watch the invocations of java.lang.Math methods, like `Math.sqrt` etc.

--------------------------------------------------
## 2.3 - Configuring where COJAC will be applied

COJAC instruments the code everywhere, except in classes from the standard library 
(`java.*`, `javax.*`, ...), and possibly what has been requested to be skipped 
with one of the mechanisms described below. Alternatively, there is an experimental
option when you want to explicitly limit the scope of instrumentation to particular
portions of your source code.


### 2.3.1 - Excluding some code with -b option

One way of preventing the instrumentation of a class is to use the `-Xb` 
bypass option :  
  `java -javaagent:cojac.jar="-Xb ch.eif.;com.foo.Bar"`

This will discard the instrumentation of any class whose fully qualified name
starts with "ch.eif." or "com.foo.Bar". 

### 2.3.2 - Excluding some code with annotations

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

### 2.3.3 - Instrumenting in selected parts only

As an example of this experimental feature, here is how you can specify that 
you want to restrict the scope of COJAC to two classes, the complete class Foo, 
and part of the class Bar (the whole method fct(), plus lines 1 to 25, plus lines 
34 and 99, plus bytecode instructions 22 to 27 and instruction 100:  
  `-Only com.pkg.Foo{fct()I,1-25,34,99_22-27,100};com.pkg.bar`

Another experimental feature (well, everything in COJAC is experimental, right?)
is to handle XML files as a means to represent what COJAC is changing in your 
bytecode. An option can generate such a file, another option can read such a 
(possibly modified) file to drive the instrumentation. We used that mechanism 
for our Delta-Debugging experiments.

--------------------------------------------------
## 2.4 - Configuring how COJAC will signal the detected problems

You can  choose the way COJAC will inform you when a problem occurs: 

 * `-Sc`   This is the default behaviour, all the messages are printed to the 
 standard error stream

 * `-Sl file`  All warning messages are appended to a file, the 'file' 
 argument is optional, by default the file is named `COJAC_Report.log`

 * `-Se`  Each problem will throw an `ArithmeticException`

 * `-Sk myMethod`   Each problem will invoke a method of your choice. The 
 method must have that 
 signature: `public static void myMethod(String opcodeName)` 
 and you have to provide its complete qualified path, in the 
 format `pkgA/myPkg/myClass/myMethod`.

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
## 2.5 - Instrumenting without executing

Prehistoric versions of COJAC were able to create "offline" an instrumented 
version of the application code (that can later be executed). We no longer 
provide this feature: COJAC is designed to apply on-the-fly instrumentation, we 
advocate its use as a diagnostic-enabled java launching means. 

--------------------------------------------------
## 2.6 - Example

Here is a small example of what COJAC can do. Let's take a simple class like that: 

```java
public class Demo {
  public static void main(String[] args) {
    int a = 2_000_000_000, b = 2_000_000_000;
    int res1 = a + b;
    System.out.println("Adding billions: " + res1);
    double c = 1E22, d = 321;
    double res2 = c + d;
    System.out.println("Adding units to a large number : " + res2);
    double sqrt = Math.sqrt(-1);
    System.out.println("Square root of -1: " + sqrt);
  }
}
```

Compile it with something like `javac Demo.java`. Then, use COJAC to launch 
this application:  

```
$ java -javaagent:cojac.jar Demo

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
## 2.7 - JMX feature

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
pattern:    service:jmx:rmi:///jndi/rmi://<host>:<port>/<MBean-name>
default:    service:jmx:rmi:///jndi/rmi://localhost:5017/COJAC
```

You can access the management interface programmatically or using a tool such 
as `jconsole`. This can be great to watch long-term running applications or 
web services.

--------------------------------------------------
# 3 - COJAC: the arithmetic behavior toolkit

In fact, the "Numerical Sniffer" is one example of a more general mechanism in
which COJAC somehow redefines the standard operations on `int`/`long`/`float`/`double`. 
Shouting a warning message when an operation suffers from a numerical artifact is 
not the only possible change in the arithmetic behavior we have implemented.

In the domain of scientific computing, there is a set of tricks that are 
commonly used to track potential numerical instabilities (see e.g. 
[that paper](http://www.davidhbailey.com/dhbpapers/numerical-bugs.pdf)
from Bailey). We have equipped COJAC with a couple of those techniques that help 
to estimate the stability of some floating point procedure. The rough idea is 
to change a little bit the computation, just to see if the result varies slightly 
or dramatically. 


--------------------------------------------------
## 3.1 - "Double-as-float" behavior

In this mode, every `double` in your code acts as if it were represented in 
single-precision (like a `float`). COJAC offers the most elegant way to experiment
this well-known trick. 

Research papers have discussed how Delta-Debugging can be applied in that context,
to detect which data needs to be represented in double-precision and which can safely
be kept in single-precision (in order to optimize memory footprint and CPU time).
We have reproduced the experiment with COJAC, equipping it with a general 
Delta-Debugging infrastructure. Ask us if you are interested, or have a look at 
the source code of class `com.github.cojac.deltadebugging.App`.

By the way, just for fun, we have extended the idea of choosing the actual precision, 
so that it is no more a binary choice: you can choose any number of
significant bits up to 52 (if under 23 it will apply on `float` too).

--------------------------------------------------
## 3.2 - "Rounding-mode" behavior

In this mode, the user can override the rounding mode of the computations, a setting 
that is normally not accessible at all in Java (this has been strongly criticized). 
We offer this feature in two flavors: 

* for a common CPU architecture, we have implemented (via JNI) a native way to 
set the rounding mode of the CPU in an OS-specific way. So instead of the default 
ROUND-TO-NEAREST, you can now specify ROUND-UP, ROUND-DOWN, or ROUND-TOWARDS-ZERO.

* a more naive/sloppy realization in pure Java consists in simulating another 
rounding mode by simply adding an ulp in the corresponding direction. You have 
the choice between ROUND-UP, ROUND-DOWN, or ROUND-RANDOMLY.

--------------------------------------------------
## 3.3 - "Epsilon-fuzzer" behavior

In this mode, the comparison operators like `<` or `>=` are redefined so that
the relation is reversed when the two operands are too close together. 

--------------------------------------------------
# 4 - COJAC: the enriching wrapper

To expand further what new powers could be given to the traditional floating
point numbers, we have been working on the automatic replacement of the 
primitive types `float`/`double` (as well as their "standard wrapper" 
counterpart Float/Double) by objects that realize richer number models.

Now our poor old floating point numbers can be enriched in various ways, 
that we only start to study. There are still limitations, but... yes we did it!

## 4.1 - Our wrapping mechanism

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

The magic methods are model-specific (see below), but every number wrapper will 
at least provide those two methods: 
```java
public static String COJAC_MAGIC_wrapperName();
public static String COJAC_MAGIC_toString(double n); // internal representation
```

## 4.2 - Wrapper "BigDecimal"

Our first "enriched number" type provides arbitrary precision numbers, where
the number of significant digits is specified at runtime. The implementation 
relies on `java.math.BigDecimal`. 

This wrapper is activated with the option `-Rb nDigits`, and it does not 
provide additional magic methods.

Isn't it a killer feature? Your code uses standard `float`/`double` and at 
runtime you decide to compute everything with 150 significant digits if you like! 
Here is a small demo program. 

```java
public class HelloBigDecimal {
    static double mullerRecurrence(int n) {
        double[] u = new double[n+1];
        u[0] = +2.0; 
        u[1] = -4.0;
        for(int i=2; i<=n; i++)
            u[i] = 111 - 1130/u[i-1] + 3000/(u[i-1]*u[i-2]);
        return u[n];
    }

    public static void main(String[] args) {
        double m = mullerRecurrence(20);
        double r = 6.04;
        System.out.print  (m);
        System.out.println("  ... should be: ~ "+r);
    }
}
```

Compare those executions (standard doubles, 3- or 50-digit BigDecimals): 

```
$ java demo.HelloBigDecimal
99.8985692661829  ... should be: ~ 6.04

$ java -javaagent:cojac.jar="-Rb 3" demo.HelloBigDecimal
100.0  ... should be: ~ 6.04

$ java -javaagent:cojac.jar="-Rb 50" demo.HelloBigDecimal
6.036031881081857  ... should be: ~ 6.04
```


## 4.3 - Wrapper "Interval computation"

The traditional float/double types compute approximate results (due to possible 
rounding), but they can give no information at all about how far we get from 
the "true" mathematical result. The idea behind the *interval computation* 
technique of *reliable computing*  is to represent numbers with an interval 
that is guaranteed to hold the true result, thus keeping track of the possible 
error margin. 

Our Interval wrapper is a toy realization of this technique. Internally, we emit
a warning whenever the relative error of a number goes beyond a certain threshold 
(that can be adjusted via an option).

This wrapper is activated with the option `-Ri`. Two magic methods 
are defined for this model:
```java
public static double COJAC_MAGIC_relativeError(double n);
public static double COJAC_MAGIC_width(double n); // interval width max-min
```

For now, we are yet far from the quality of a good "interval computation" library. For 
instance, we systematically round up/down instead of adjusting the 
rounding mode. Nevertheless, the strength and simplicity of the automatic wrapping 
should not be underestimated.

## 4.4 - Wrapper "Discrete stochastic arithmetic"

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

This wrapper is activated with the option `-Rs`. It offers the same magic 
methods as our interval computation model, and is similar in its way of 
signaling high relative errors.

## 4.5 - Wrapper "Automatic differentiation" ("forward" mode)

*Automatic differentiation* is an awesome idea (never heard of it? You _should_ have 
a look!). One reason the technique is not in widespread use lies in the lack 
of *simple* tools that can show its power. We pretend that the 
situation dramatically changes thanks to COJAC. 

In the "forward mode" Autodiff model, you somehow "mark" one of your input data as 
the independent variable (say X) of interest; then whatever computation that data is
injected in will be equipped with differentiation: all those downstream results 
somehow store as a second component the derivative in X. 

This wrapper is activated with the option `-Ra`. Let's show the wrapper and its
magic methods in action with a small example.

```java
public class AutoDiffForward {
  public static double COJAC_MAGIC_derivative(double a) { return 0; }
  public static double COJAC_MAGIC_asDerivativeVar(double a) { return a; }
  
  static double someFunction(double x, double a, double b) {
    double res=a*x*x;     // the computation can be complex,
    res = res + b*x;      // with loops, calls, recursion etc.
    res = res + 1;
    return res;     // f: a X^2 + b X + 1        f': 2aX + b
  }
  
  public static void main(String[] args) {
    double x=2;
    x=COJAC_MAGIC_asDerivativeVar(x);  // we'll compute df/dx
    double y=someFunction(x, 3, 4);
    System.out.println("f(2):  "+y);
    System.out.println("f'(2): "+COJAC_MAGIC_derivative(y));
  }
}

$ java -javaagent:cojac.jar="-Ra" AutoDiffForward
f(x):  21.0
f'(x): 16.0
```

## 4.6 - Wrapper "Automatic differentiation" ("reverse" mode)

Just to insist: Autodiff is an awesome idea. In the "reverse mode" Autodiff 
model, you perform your various computations, and then you tell one of the results 
that you are interested in every possible partial derivative. Suddenly, each 
number variable is getting aware of its role as an independent variable and is
happy to give you the corresponding partial derivative. Here is a small demo. 

```java
public class HelloAutodiffReverse {
    public static void   COJAC_MAGIC_computePartialDerivatives(double a) {}
    public static double COJAC_MAGIC_partialDerivativeIn(double a) { return 0;}

    public static double somePolynomial(double x, double y) {
        return 3*x*x + 2*y;
    }

    public static void main(String[] args) {
        double r, x=2.0, y=3.0;
        r=somePolynomial(x, y);
        System.out.println("f(x,y): "+r);
        COJAC_MAGIC_computePartialDerivatives(r);
        System.out.println("df/dx: "+COJAC_MAGIC_partialDerivativeIn(x));
        System.out.println("df/dy: "+COJAC_MAGIC_partialDerivativeIn(y));
    }
}

$ java -javaagent:cojac.jar="-Rar" HelloAutodiffReverse
f(x,y): 18.0
df/dx: 12.0
df/dy: 2.0

```

## 4.7 - Wrapper "Symbolic" expressions (and functions!)

Symbolic computation is the jewel concept at the heart of brilliant environments 
like Mathematica or WolframAlpha. Apparently, the way C or Java processes `float`
or `double` numbers is light-years away from that. Before COJAC, the only means 
to play with symbolic expressions in Java was to find/develop a library that
defines new classes for the numbers, along with methods to combine them with math 
operations. 

But, although Java processes standard types purely numerically, there is still a 
trace of the corresponding formulas in the bytecode. With the "Symbolic" wrapper, 
COJAC is able to rebuild the full expression trees at runtime. It could be the
starting point of amazing features, and we illustrate this by showing how to 
embed simplification rules that apply only at the symbolic level.

Here is an example. Normally in Java, the computation of `c` is applied 
numerically, and in this particular case there will be a tiny rounding error: 

```java
public class HelloSymbolicExpressions {
    public static String COJAC_MAGIC_toString(double n) { return "-"; }
    
    static float sum(float[] t) {
        float r=0.0f;
        for(float e:t) r += e;
        return r;
    }

    public static void main(String[] args) {
        double a = 3.1, b = a - 0.1;
        double c = b * (a / (a - 0.1));
        System.out.println(c);
        System.out.println(COJAC_MAGIC_toString(c));
        float[] t={23.0f, 0.0000008f, 0.0000009f};
        System.out.println(sum(t));
    }
}

$ java demo.HelloSymbolic
3.1000000000000005
-
23.0
```

Just activate the "Symbolic" wrapper to radically change the way the computation 
is done: 

```
$ java -javaagent:cojac.jar="-Rsymb" demo.HelloSymbolic
3.1
MUL(SUB(3.1,0.1),DIV(3.1,SUB(3.1,0.1)))
23.000002
```

You can see how the `double c` is indeed stored internally; it is only when 
trying to output the result that it gets evaluated, 
here applying the simplification rule `F*(E/F)==E`. And for the sum, in fact 
COJAC has indeed changed the algorithm, by reordering the terms and applying
Kahan summation.

We have walked a step further by introducing the concept of an "unknown". So it
is also possible to define "symbolic functions" :

```java
public class HelloSymbolicFunctions {
    public static String COJAC_MAGIC_toString(double n) { return ""; }
    public static double COJAC_MAGIC_identityFct() { return 0; }
    public static double COJAC_MAGIC_evaluateAt(double fct, double x) { return 0; }

    public static void main(String[] args) {
        double x=COJAC_MAGIC_identityFct();  // x is the "unknown"
        double a = 3.0, b = a + 1;
        double c = b * (a / (a - x));
        System.out.println(COJAC_MAGIC_toString(c));
        System.out.println(COJAC_MAGIC_evaluateAt(c, 5.0));
    }
}
```

Now `c` is no more a `double` value at all, it is an abstract function 
`f(x)`. 

```
$ java -javaagent:cojac.jar="-Rsymb" demo.HelloSymbolicFunctions
MUL(ADD(3.0,1.0),DIV(3.0,SUB(3.0,x)))
-6.0
```

Beside evaluating the function on a point or taking the derivative, many 
advanced features could be added: 
solving equations such as `f(x)=0`, computing an integral, etc. An elegant 
bridge from standard Java code to Mathematica-like processing!

## 4.8 - Wrapper "Chebfun"

The previous section has just shown how our plain old `double` data are promoted
to symbolic functions `f(x)`. It happens that mathematicians have invented 
many ways to represent a real function. Instead of a direct formula, one can 
also decide to represent it as a polynomial approximation. In this context, 
Chebyshev polynomials are especially powerful, and Trefethen et al. have studied 
the approach in breadth and depth to develop the Matlab package named *Chebfun*. 

We have ported the idea within COJAC. 
Again, our contribution is not on the detailed
features set (improving that is a matter of rewriting in Java the many numerical 
recipes that Chebfun embeds in Matlab). The COJAC port is nevertheless a 
breakthrough in that it shows how existing code that uses simple `double` 
numbers can be leveraged to be manipulated in a completely new world where
we have smart math weapons. All this by the magic of a runtime option!

```java
public class HelloChebfun {
    public static String COJAC_MAGIC_toString(double n) { return ""; }
    public static double COJAC_MAGIC_identityFct() { return 0.5; }
    public static double COJAC_MAGIC_evaluateAt(double d, double x) { return 0; }
    public static double COJAC_MAGIC_derivative(double d) { return d; }
    
    public static void main(String[] args) {
        double x=COJAC_MAGIC_identityFct(); // f(x)=x
        double a = 3.0, b = a + 1;
        double c = b * (a / (a - x));
        System.out.println(COJAC_MAGIC_toString(c));
        System.out.println(COJAC_MAGIC_evaluateAt(c, 0.5));
    }
}

$ java -javaagent:cojac.jar="-Rcheb" demo.HelloChebfun
Chebfun(degree:32 (effective:19)), [6.0, 5.98, 5.94, ... 3.003, 3.0], 
                              fft: [4.24, 1.45, 0.24 ... -4.4E-16]
4.8
```

## 4.9 - Wrapper "Complex Number"

The standard `float`/`double` in Java are limited to real numbers. In most cases,
this isn't a problem. However there are some situations situations where the
calculation has to be done with complex numbers even if the answer is real.

You can enable this wrapper with the option `-Rc`. This will replace each
`float`/`double` with a wrapper which can handle complex numbers. In the normal
mode, the comparison is done by comparing the real part first and only after the
imaginary part is compared. When the complex numbers is casted back to an
`int`/`double`, the imaginary part will be lost without any warning.

You can enable the strict mode with the option `-Rc strict`. In this case, the
comparison with a number which has an imaginary part will result in an
`ArithmeticException`.  When the complex numbers is casted back to a
`int`/`double` or any other types, a `ClassCastException` will be thrown.

In both modes, there are three specific magic methods for the complex numbers:
* `public static double COJAC_MAGIC_getReal(double d)`. This method returns only
the real part of the complex number
* `public static double COJAC_MAGIC_getImaginary(double d)`. This method returns
only the imaginary part of the complex. The resulting number will only have a real
part which is equals to the imaginary part of the source number.
* `public static boolean COJAC_MAGIC_equals(double a, double b)`. This method
returns a boolean which indicates if both numbers are equal. This method will
never throw any exception.

```java
public class HelloComplexNumber {

    // Find a root of a cubic equation of the form ax^3 + bx^2 + cx + d = 0 with the general cubic formula
    // This formula can be found on wikipedia: https://en.wikipedia.org/wiki/Cubic_equation#General_cubic_formula
    static double solveCubicEquation(double a, double b, double c, double d) {
        double det0 = b * b - 3 * a * c;
        double det1 = 2 * b * b * b - 9 * a * b * c + 27 * a * a * d;

        double sqrt = Math.sqrt(det1 * det1 - 4 * det0 * det0 * det0);
        if (Double.isNaN(sqrt))
            // the root can't be calculated if this value is not available.
            return Double.NaN;
        double coef = Math.cbrt((det1 + sqrt) / 2);

        if (coef == 0) {
            coef = Math.cbrt((det1 - sqrt) / 2);
        }
        if (coef == 0) {
            return -b / (3 * a);
        }
        return -(b + coef + det0 / coef) / (3 * a);
    }

    public static void main(String[] args) {
        System.out.println(solveCubicEquation(2, 1, 3, 1) + " should be ≈ -0.34563");
        System.out.println(solveCubicEquation(1, -2, -13, -10) + " should be -1, -2 or 5");
    }
}

$ java -javaagent:cojac.jar="-Rc" demo.HelloComplexNumber

Output:
-0.34562739226941647 should be ≈ -0.34563
-2.0 + 2.9605947323337506E-16i should be -1, -2 or 5
```



--------------------------------------------------
# 5 - Detailed usage


Here is a manpage-like description of COJAC, as produced by the --help 
flag. 

```
usage: java -javaagent:cojac.jar="[OPTIONS]" YourApp [appArgs]
            (version 1.5.0 - 2017 Mar 19)

Nice tools to enrich Java arithmetic capabilities, on-the-fly:
- Numerical Problem Sniffer: detects and signals arithmetic poisons like integer
overflows, aborption, catastrophic cancellation, NaN or infinite results.
- New float/double behaviors: double-as-floats, change rounding mode... Some
known tricks to track possible numerical instabilities.
- Enriching Wrapper for float/double: wraps every double/float in richer
objects. Current models include BigDecimal (you choose the precision!), interval
computation, automatic differentiation, symbolic processing, chebfun and more.
----------------- OPTIONS -----------------
 -Bdaf              Use doubles as if they were single-precision floats
 -Bdai              Hijack doubles as low-precision intervals
 -Bddread <file>    Read an XML file to tune how an ArithmeticBehavior behaves
                    (used for Delta-Debugging)
 -Bddwrite <file>   Write the located effect of an ArithmeticBehavior to an XML
                    file (used for Delta-Debugging)
 -Beroundd          Emulate "round" (biased) randomly
 -Beroundu          Emulate "round" (biased) up
 -Bfuz              toggle CMP results for operands too close together
 -Bnroundd          Change the CPU rounding mode toward minus infinity
 -Bnroundu          Change the CPU rounding mode toward plus infinity
 -Bnroundz          Change the CPU rounding mode toward zero
 -Bpr <nbOfBits>    limit the precision of a double's mantissa.Example: -Bpr 8
                    emulates eight-significant bits floats and doubles
 -Ca                Sniff everywhere (this is the default behavior)
 -Ccasts            Sniff in casts opcodes
 -Cdoubles          Sniff in doubles opcodes
 -Cfloats           Sniff in floats opcodes
 -Cints             Sniff in ints opcodes
 -Clongs            Sniff in longs opcodes
 -Cmath             Sniff in (Strict)Math.xyz() methods
 -Cn                Don't sniff at all
 -Copcodes <arg>    Sniff in those (comma separated) opcodes; eg:
                    iadd,idiv,iinc,isub,imul,ineg,ladd,lsub,lmul,ldiv,lneg,dadd,
                    dsub,dmul,ddiv,drem,dcmp,fadd,fsub,fmul,fdiv,frem,fcmp,l2i,i
                    2s,i2c,i2b,d2f,d2i,d2l,f2i,f2l,i2f,l2d
 -h,--help          Print the help of the program and exit
 -jmxenable         Enable JMX feature
 -jmxhost <host>    Set remote JMX connection host (default: localhost)
 -jmxname <ID>      Set remote MBean name (default: COJAC)
 -jmxport <port>    Set remote JMX connection port (default: 5017)
 -Only <code>       Select precisely which portions of code will be
                    instrumented. The syntax is sketched in this example: -Only
                    "pkg.Foo{m1(II)I,m3(),1,12,112,25};pkg.Bar{10-354}" Will
                    instrument fully methods m1(II)I, m3() and lines
                    [1,12,112,25] from Class pkg.Foo and lines 10 to 354
                    (inclusive) from Class pkg.Bar
 -Ra                Use automatic differentiation (forward mode) wrapping
 -Rar               Use automatic differentiation (reverse mode) wrapping
 -Rb <digits>       Use BigDecimal wrapping with arbitrarily high
                    precision.Example: -Rb 100 will wrap with
                    100-significant-digit BigDecimals
 -Rc [strict]       Use complex number wrapping. Strict mode generates an
                    exception when the imaginary part is lost or when a
                    comparison between two different complex numbers is made.
 -Rcheb             Use chefun wrapping
 -Rddread <file>    Read an XML file to tune how the Wrapper behaves (used for
                    Delta-Debugging)
 -Rddwrite <file>   Write the located effect of the Wrapper to an XML file (used
                    for Delta-Debugging)
 -Ri                Use interval computation wrapping
 -RnoUnstableCmp    Disable unstability checks in comparisons, eg for the Interval
                    or Stochastic wrappers
 -Rs                Use discrete stochastic arithmetic wrapping
 -Rsymb             Use symbolic wrapping
 -RunstableAt <e>   Relative precision considered unstable, for
                    Interval/Stochastic wrappers (default 0.00001)
 -Sc                Signal problems with console messages to stderr (default
                    signaling policy)
 -Sd                Log the full stack trace (combined with -Cc or -Cl)
 -Se                Signal problems by throwing an ArithmeticException
 -Sk <meth>         Signal problems by calling a user-supplied method matching
                    this signature:...public static void f(String msg)
                    (Give a fully qualified identifier in the form:
                    pkgA/myPkg/myClass/myMethod)
 -Sl <file>         Signal problems by writing to a log file.
                    Default filename is: COJAC_Report.log.
 -v,--verbose       Display some internal traces
 -W <class>         Select the wrapper (not for regular users!).
                    Example: -W cojac.WrapperBasic will use
                    com.github.cojac.models.wrappers.WrapperBasic
 -Wd <class>        Select the double container (not for regular users!).
                    Example: -Wd cojac.MyDouble will use
                    com.github.cojac.models.wrappers.MyDouble
 -Wf <class>        Select the float container. See Wd
 -Xb <prefixes>     Bypass classes starting with one of these prefixes
                    (semi-colon separated list).
                    Example: -Xb foo;bar.util
                    will skip classes with name foo* or bar.util*
 -Xf                Report each problem only once per faulty line
 -Xs                Print runtime statistics
 -Xt                Print instrumentation statistics

------> https://github.com/Cojac/Cojac <------
```

--------------------------------------------------
# 6 - Limitations and known issues

This section discusses a couple of issues for the current version of COJAC.

## 6.1 - Issues with the sniffer

The sniffer part of COJAC is rather stable. Here are some limitations:

 * Of course a suspicious operation is not always the manifestation of a software 
 defect. For instance, you can rely on integer overflows to compute a hash function, 
 or maybe the cancellation phenomenon is not a problem because the floating point 
 numbers you deal with do not suffer from imprecision.

 * That's inherent to the approach: compile-time expressions can't be processed: 
 don't complain that a "hello world" use case like
`System.out.println(3*Integer.MAX_VALUE);` does not log the overflow!
 
 * The tool targets the JVM only. We focus on Java, but it might be interesting 
 to try it on other JVM-equipped languages (Scala, Jython...). By the way, if 
 you dream of a numerical sniffer not limited to the Java world, have a look at 
 [cojac-grind](https://github.com/Cojac/cojac-grind)...

## 6.2 - Issues with the wrapper

The wrapper part of COJAC should be considered an experimental prototype. Here 
are some limitations:

* The frontier between "user code" and "java library" has some serious defects, eg 
when arrays of numbers are involved.

* We don't handle the "callbacks" from java library to user code when floating 
point numbers are being passed around.

* We have tried to handle `invokedynamic` (at least how Java8 compilers 
use it), but it has not been thoroughly  tested yet, so we expect some problems 
with Java8 lambdas (version 1.4.1 has fixed some problems).

* We don't handle the possible use of Java reflection (in case of method 
invocations via reflection, we don't apply the necessary transformations). 

* The decision of converting both the primitive types float/double and their
original wrapper Float/Double brings several problems, eg signature conflicts, or 
comparison (compareTo/equals) misbehavior.

* The implementation of the models is really naive. For instance, we do not compute
every Math.* operations with the required precision in the BigDecimal model.

* Of course, the slow-down is very important.

--------------------------------------------------
# 7 - And now...

...well, have fun with numbers juggling! Please give us feedback :smiley: via an issue on github, or `the.google.tool@gmail.com`.  
<tt>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</tt>  The COJAC Team. 

--------------------------------------------------
### Acknowledgements 

Several Computer Science students (and professors) from [HEIA-FR](https://www.heia-fr.ch) 
have been involved in some way in the COJAC project: 

- Ruggiero Botteon and Diego Cavadini (for the very first pre-prototype)
- Baptiste Wicht (major early contributor, strong refactoring/testing - many thanks!)
- Maxime Reymond (for an Eclipse Plugin, now (temporarily?) discontinued)
- Vincent Pasquier (for switching to the "Java agent" technology)
- Luis Domingues (for improving the Valgrind cousin Cojac-grind)
- Romain Monnard (without whom the "wrapping" would still be an unimplemented dream - many thanks!)
- Prof. Richard Baltensperger (for patiently explaining what Chebfun is and how it works)
- Sylvain Julmy (for populating 3 "rich number" types)
- Lucy Linder (for improving the whole, preparing the release on GitHub, designing the first two videos, drawing the logo, and more - many thanks!)
- Valentin Gazzola (for adding new ways to modify the arithmetic behavior, eg rounding mode changing and double-as-float)
- Rémi Badoud (for working on the Symbolic+Chebfun wrappers, and on the Delta-Debugging infrastructure)
- Yael Iseli (for studying how COJAC behaves on six other JVM-based programming languages)
- Emerald Cottet (for investigating how to integrate Unum/Posit number format)
- Martin Spoto (for adding a "numerical profiler" feature)
- Cédric Tâche (for working on the integration of complex numbers and Unum/Posit numbers)


