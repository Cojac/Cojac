package com.github.cojac.misctests;

public class InstrumentationDemoClass1 {

    private double myDouble1;
    private double myDouble2;

    public InstrumentationDemoClass1(double myDouble1, double myDouble2) {
        this.myDouble1 = myDouble1;
        this.myDouble2 = myDouble2;
    }

    public void add(InstrumentationDemoClass1 i) {
        this.myDouble1 += i.myDouble1;
        this.myDouble2 += i.myDouble2;
    }
    
    public void sub(InstrumentationDemoClass1 i) {
        this.myDouble1 -= i.myDouble1;
        this.myDouble2 -= i.myDouble2;
    }
    
    public double getFirstDouble() {
        return myDouble1;
    }
    
    public double getSecondDouble() {
        return myDouble2;
    }

}
