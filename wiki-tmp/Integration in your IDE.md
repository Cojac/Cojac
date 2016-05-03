# Integration in your IDE

To use COJAC in your IDE, you essentially follow those steps: 

- download cojac.jar, and store it somewhere

- add a JVM option to the "Run Configuration" of your program: 

  ```
  -javaagent:your/path/to/cojac.jar
  -javaagent:your/path/to/cojac.jar="-Xs -Xf"
  -javaagent:your/path/to/cojac.jar="-Rb 50"
  ```

Warnings go to the console (stderr), where probably file locations like `HelloWorld.java:12` can be clicked to get redirected to an edition buffer.

In Eclipse for instance, it is also possible to define "variables" in run configurations to hold the JVM option, if you want to name your favorite flags. See also [this section](https://github.com/Cojac/Cojac/wiki#12---launching-an-application-with-cojac) of the User Guide.

The COJAC Team.