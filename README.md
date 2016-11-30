## COJAC - Numerical Sniffer and Enriching Wrapper for Java

### Numerical Sniffer

- Automatic detection of all those poisonous problems with numbers, such as: 
  - integer overflow
  - smearing and cancellation with float/double
  - NaN or Infinite results
  - offending type casting

- No recompilation needed, easy to use (`java -javaagent:cojac.jar ...`)

### Enriching Wrapper

- Automatic replacement of every float/double with more powerful number types, such as:
  - high precision numbers (*you* choose the number of significant digits!)
  - interval computation, to keep rounding errors under control
  - automatic differentiation, to bring you for free the derivative of any encoded function
  
- No recompilation needed, easy to use (you want 100 significant digits? `java -javaagent:cojac.jar="-Rb 100" ...`)

### Java numbers with a fresh eye and a cool tool...

... Don't miss the opportunity: 
- watch our small demos on [YouTube](https://youtu.be/DqAFQfbWZOU?list=PLHLKWUtT0B7kNos1e48vKhFlGAXR1AAkF)
- read the [User Guide](https://github.com/Cojac/Cojac/wiki)
- download [cojac.jar](https://github.com/Cojac/Cojac/releases/download/v1.4.1/cojac.jar)

Developed at the [School of Engineering of Fribourg](https://www.heia-fr.ch), 
Switzerland.

### Recent improvements

The following features have been implemented, and are impatient to appear in the next release:

- symbolic expressions: we rebuild the formulas from the bytecode, and then apply simplification rules, just like Mathematica and other great tools are able to do. Hard to believe, but yes we did it!

- symbolic functions: a step further, where you can tag a number as being the *unknown*.

- Chebfun representation: a completely different way to represent symbolic functions that provides a good compromise of accuracy/efficiency (see [Matlab's Chebfun](http://www.chebfun.org/). But here completely transparently: with COJAC the same computation code can be run numerically, symbolically, or as Chebfuns

- a couple of well-known *debugging tools* useful to track possible instabilities: forcing doubles to act like floats, changing the rounding mode, negating the comparisons when the numbers are too close together. 

- a Delta-Debugging infrastructure for the debugging features above, so that you can automatically find the portion of code that causes the instabilities.

--------------------------

Distributed under the "Apache License, v2.0".
