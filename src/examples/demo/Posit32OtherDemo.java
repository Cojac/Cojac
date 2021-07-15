package demo;

public class Posit32OtherDemo {
    // From [BeatingFloatingPoint] p. 15 (§4.5.3)
    static float f1() {
        return (-100 + (float) Math.sqrt(9976.0f)) / 6.0f;
        // correct: −0:020012014  (from paper)
        // float32: -0.020011902  (from paper)
        // posit32: −0:02001206   (from paper)
    }

    // From [BeatingFloatingPoint] p. 14 (§4.5.1)
    static float f2() {
        float exp = 67 / 16f;
        float num = (float) (27 / 10f - (float) Math.E);
        float den = (float) ((float) Math.PI - ((float) Math.sqrt(2) + (float) Math.sqrt(3)));
        return (float) Math.pow(num / den, exp);
        // correct: 302:8827196   (from paper)
        // float32: 302.9124      (from paper)
        // posit32: 302.88231     (from paper)
    }

    public static void main(String[] args) {
        System.out.println(f1());
        System.out.println(f2());
        // normal: -0.020011902 OK
        //          302.9124    Ok
        // -Rp:    -0.02001206  OK
        //          302.89627   Slight distortion (due to intermediate casting?)
    }
}
