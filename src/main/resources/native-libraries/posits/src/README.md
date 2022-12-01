# Bridge JNI for posit32 from "SofPosit"

This library contains a bridge with the posit32 from the library SoftPosit.

## Build

First you have to build SoftPosit with the following commands:
```
cd libraries/SoftPosit/build/Linux-x86_64-GCC
make
cd ../../../..
```
Then you can build this library with the following command
```
cd ..
cmake src
make
```

The .dll has been built with Windows 10 and mingw64.
The .so has been built with Ubuntu 20.04 (from Windows 10 Store) and gcc.