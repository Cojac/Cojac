package com.github.cojac.misctests;

import java.math.BigDecimal;

public class InstrumentationDemoMain {

    public static void main(String[] args) {
        InstrumentationDemoClass1 i1 = new InstrumentationDemoClass1(1, 1);
        InstrumentationDemoClass1 i2 = new InstrumentationDemoClass1(1e8, 1e5);
        i1.add(i2);
        i1.sub(i2);
        System.out.println(i1.getFirstDouble());
        System.out.println(i1.getSecondDouble());
        boolean fail = false;
        try {
            if (!BigDecimal.valueOf(i1.getFirstDouble()).equals(BigDecimal.valueOf(1.0)))
                fail = true;
            if (!BigDecimal.valueOf(i1.getSecondDouble()).equals(BigDecimal.valueOf(1.0)))
                fail = true;
            if (fail) {
                System.exit(1);
            }
        } catch (Exception e) {
            System.exit(1);
        }
    }

}
