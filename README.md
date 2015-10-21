# COJAC - Numerical Sniffer and Enriching Wrapper for Java

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
  - interval computation (or discrete stochastic arithmetic), to keep the rounding error under control
  - automatic differentiation, to bring you for free the derivative of any encoded function
  
- No recompilation needed, easy to use (you want 100 significant digits? `java -javaagent:cojac.jar="-Rb 100" ...`)


### Java numbers with a fresh eye and a cool tool...

... Don't miss the opportunity: read the [User Guide](https://github.com/frederic-bapst/Cojac/wiki)!