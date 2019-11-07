## COJAC - Boosting arithmetic capabilities of Java numbers

### Java numbers with a fresh eye and a cool tool...

... Don't miss the opportunity:  
  - watch our small demos on [YouTube](https://youtu.be/eAy71M34U_I?list=PLHLKWUtT0B7kNos1e48vKhFlGAXR1AAkF)
  - read the [User Guide](https://github.com/Cojac/Cojac/wiki)
  - download [cojac.jar](https://github.com/Cojac/Cojac/releases/download/v1.5.1/cojac.jar)

### Numerical Sniffer, and other floating point gadgets

- Automatic detection of all those poisonous problems with numbers, such as: 
  - integer overflow
  - absorption and cancellation with float/double
  - NaN or Infinite results
  - offending type casting
  
- New behaviors for floating point numbers, such as doubles-are-floats, or redefinition of the default rounding mode.

- No recompilation needed, easy to use (`java -javaagent:cojac.jar ...`)

### Enriching Wrapper

- Automatic replacement of every float/double with more powerful number types, such as:
  - high precision numbers: *you* choose the number of significant digits for the standard float/double types! What a super-power for a runtime flag!
  - interval computation, to keep rounding errors under control and signal when it grows too much. Again, without any source code modification!
  - automatic differentiation, to bring you (for free!) the derivative of any encoded function. No other port of AutoDiff is that elegant!
  - symbolic processing, and even Chebfun-like representation
  
- No recompilation needed, easy to use (you want 100 significant digits? `java -javaagent:cojac.jar="-Rb 100" ...`)



### Recent improvements

The following features have been implemented and recently included in the current release:

- symbolic expressions: we rebuild the formulas from the bytecode, and then apply simplification rules, just like Mathematica and similar great tools are able to do. Hard to believe, but yes we did it!

- symbolic functions: a step further, where you can tag a number as being the *unknown*.

- Chebfun representation: a completely different way to represent symbolic functions, with a good compromise of accuracy/efficiency (see [Matlab's Chebfun](http://www.chebfun.org/)). But here completely transparently: with COJAC the same computation code can be run numerically, symbolically, or as Chebfuns.

- a couple of well-known *debugging tools* useful to track possible instabilities: forcing doubles to act like floats, changing the rounding mode, negating the comparisons when the numbers are too close together. 

- a Delta-Debugging infrastructure for the debugging features above, so that you can automatically find the portion of code that causes the instabilities.

--------------------------

Developed at the [School of Engineering of Fribourg](https://www.heia-fr.ch), 
Switzerland.

Distributed under the "Apache License, v2.0".
