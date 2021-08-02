package com.github.cojac.defects;

// didn't work with Wrapping...
// Fixed 24.03.16

public class Buggy01 {

    private static void fffff(double x) {

      runit(() -> System.out.println("aa "+x));
    }

    private static void runit(Runnable r) {
        r.run();
    }
    
    public static void main(String... args) {
      fffff(5.0);
    }
}

/*
    DLOAD 0: x
    INVOKEDYNAMIC run(double) : Runnable [
      // handle kind 0x6 : INVOKESTATIC
      LambdaMetafactory.metafactory(MethodHandles$Lookup, String, MethodType, MethodType, MethodHandle, MethodType) : CallSite
      // arguments:
        () : void, 
        // handle kind 0x6 : INVOKESTATIC
        Buggy01.lambda$0(double) : void, 
        () : void
    ]

*/