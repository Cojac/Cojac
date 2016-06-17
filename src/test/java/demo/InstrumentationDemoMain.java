package demo;

public class InstrumentationDemoMain {
    
    
    public static void main(String[] args) {

        InstrumentationDemoClass1 i1 = new InstrumentationDemoClass1(1, 1);
        InstrumentationDemoClass1 i2 = new InstrumentationDemoClass1(1e8, 1e5);
        i1.add(i2);
        i1.sub(i2);
    }

}
