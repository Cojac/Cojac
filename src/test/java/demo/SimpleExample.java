/*01*/package demo;
/*02*/public class SimpleExample {
/*03*/  public static void main(String[] args) {
/*04*/    double a = 1e8;
/*05*/    double b = 1;
/*06*/    double c = b + 2;
/*07*/    double d = a + b;
/*08*/    double e = d - a;
lala ();
lala ();
/*09*/    System.out.println(" c -> " + c +" should be 3.0 ");
/*10*/    System.out.println(" e -> " + e +" should be 1.0 ");
/*11*/  }
    public static void lala() {
        int i =1;
    }
/*12*/}

///*01*/package demo;
///*02*/public class SimpleExample {
///*03*/  public static void main(String[] args) {
///*04*/    float  a = 1e8f;                          //op. 3
///*05*/    float  b = 1;                             //op. 4
///*06*/    float  c = b + 2;                         //op. 5, 6
///*07*/    double d = (double)a + (double)b;         //op. 7
///*08*/    float  e = (float)((double)d - (double)a);//op. 8
///*09*/    System.out.println(" c -> " + c +" should be 3.0 ");
///*10*/    System.out.println(" d -> " + e +" should be 1.0 ");
///*11*/  }
///*12*/}

//// double e = com.github.cojac.models.behaviours.ConversionBehaviour.DSUB(c, a);