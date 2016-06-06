/*01*/package demo;
/*02*/public class SimpleExample {
/*03*/  public static void main(String[] args) {
/*04*/    double a = 1e8;
/*05*/    double b = 1;
/*06*/    double c = b + 2;
/*07*/    double d = a + b - a;
/*08*/    System.out.println(" d -> " + a +" should be 3.0 ");
/*09*/    System.out.println(" d -> " + d +" should be 1.0 ");
/*10*/  }
/*11*/}

//// double e = com.github.cojac.models.behaviours.ConversionBehaviour.DSUB(c, a);