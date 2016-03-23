package defects;

// doesn't work with Wrapping... 
// Maybe because of the "captured data" of the closure...

public class Buggy01 {

    private static void fffff(double x) {

      runit(() -> {
        System.out.println("aa "+x);
      });
    }

    private static void runit(Runnable r) {
        r.run();
    }
    
    public static void main(String... args) {
      fffff(5.0);
    }
}
