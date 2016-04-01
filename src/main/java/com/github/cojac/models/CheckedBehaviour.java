package com.github.cojac.models;


public class CheckedBehaviour {
    
    public static final String PRECISION_MSG = "Smearing: ";
    public static final String RESULT_IS_POS_INF_MSG = "Result is +Infinity: ";
    public static final String RESULT_IS_NEG_INF_MSG = "Result is -Infinity: ";
    public static final String UNDERFLOW_MSG = "Underflow: ";
    public static final String RESULT_IS_NAN_MSG = "Result is NaN: ";
    public static final String VERY_CLOSE_MSG = "Comparing very close: ";
    public static final String CANCELLATION_MSG = "Cancellation: ";

    public static final double CLOSENESS_ULP_FACTOR_DOUBLE = 32.0;
    public static final float CLOSENESS_ULP_FACTOR_FLOAT = 16.0f;
    public static final double CANCELLATION_ULP_FACTOR_DOUBLE = 32.0;
    public static final float CANCELLATION_ULP_FACTOR_FLOAT = 16.0f;
    
    public static final double NEGATIVE_INFINITY_DOUBLE = Double.NEGATIVE_INFINITY;
    public static final double NEGATIVE_INFINITY_FLOAT = Float.NEGATIVE_INFINITY;
    
    public static final double POSITIVE_INFINITY_DOUBLE = Double.POSITIVE_INFINITY;
    public static final double POSITIVE_INFINITY_FLOAT = Float.POSITIVE_INFINITY;
    public CheckedBehaviour() {
        throw new AssertionError();
    }
    
    /*--------------------------CASTING--------------------------*/
    
    public static int L2I(long a) {
        if (a < Integer.MIN_VALUE || a > Integer.MAX_VALUE) {
            Reactions.react( "Overflow : L2I");
        }

        return (int) a;
    }

    public static int I2S(int a) {
        if (a < Short.MIN_VALUE || a > Short.MAX_VALUE) {
            Reactions.react( "Overflow : I2S");
        }

        return (short) a;
    }

    public static int I2C(int a) {
        if (a < Character.MIN_VALUE || a > Character.MAX_VALUE) {
            Reactions.react( "Overflow : I2C");
        }

        return (char) a;
    }

    public static int I2B(int a) {
        if (a < Byte.MIN_VALUE || a > Byte.MAX_VALUE) {
            Reactions.react( "Overflow : I2B");
        }

        return (byte) a;
    }

    public static int D2I(double a) {
        if (!(a >= Integer.MIN_VALUE && a <= Integer.MAX_VALUE)) {  // NaN too...
            Reactions.react( "Overflow : D2I");
        }

        return (int) a;
    }

    public static long D2L(double a) {
        if (!(a >= Long.MIN_VALUE && a <= Long.MAX_VALUE)) {  // NaN too...
            Reactions.react( "Overflow : D2L");
        }

        return (long) a;
    }

    public static int F2I(float a) {
        if (!(a >= Integer.MIN_VALUE && a <= Integer.MAX_VALUE)) {  // NaN too...
            Reactions.react( "Overflow : F2I");
        }

        return (int) a;
    }

    public static long F2L(float a) {
        if (!(a >= Long.MIN_VALUE && a <= Long.MAX_VALUE)) {  // NaN too...
            Reactions.react( "Overflow : F2L");
        }

        return (long) a;
    }

    public static float D2F(double a) {
        if (a < -Float.MAX_VALUE || a > Float.MAX_VALUE) {
            Reactions.react( "Overflow : D2F");
        }

        return (float) a;
    }
    
    public static float I2F(int a) {
        if ((int)((float)a) != a) {
            Reactions.react( "Loss of precision : I2F");
        }

        return (float) a;
    }
    
    public static double L2D(long a) {
        if ((long)((double)a) != a) {
            Reactions.react( "Loss of precision : L2D");
        }

        return (double) a;
    }
    
    /*--------------------------DOUBLE--------------------------*/
    public static double DADD(double a, double b) {
        double r = a + b;

        if (b != 0 && r == a || a != 0 && r == b) {
            if (r != 2.0 * r)  // means here: isInfinite(r) (can't be 0 on NaN)
            {
                Reactions.react( PRECISION_MSG + "DADD");
            }
        } else if (a != POSITIVE_INFINITY_DOUBLE && a != NEGATIVE_INFINITY_DOUBLE &&
            b != POSITIVE_INFINITY_DOUBLE && b != NEGATIVE_INFINITY_DOUBLE &&
            r != 0.0) {
            if (r == POSITIVE_INFINITY_DOUBLE) {
                Reactions.react( RESULT_IS_POS_INF_MSG + "DADD");
            } else if (r == NEGATIVE_INFINITY_DOUBLE) {
                Reactions.react( RESULT_IS_NEG_INF_MSG + "DADD");
            } else if (Math.abs(r) <= CANCELLATION_ULP_FACTOR_DOUBLE * Math.ulp(a)) {
                Reactions.react( CANCELLATION_MSG + "DADD");
            }
        }

        return r;
    }
   
    public static double DSUB(double a, double b) {
        double r = a - b;

        if (b != 0.0 && r == a || a != 0.0 && r == -b) {
            if (r != 2.0 * r)  // means here: isInfinite(r) (can't be 0 on NaN)
            {
                Reactions.react( PRECISION_MSG + "DSUB");
            }
        } else if (a != POSITIVE_INFINITY_DOUBLE && a != NEGATIVE_INFINITY_DOUBLE &&
            b != POSITIVE_INFINITY_DOUBLE && b != NEGATIVE_INFINITY_DOUBLE &&
            r != 0.0) {
            if (r == POSITIVE_INFINITY_DOUBLE) {
                Reactions.react( RESULT_IS_POS_INF_MSG + "DSUB");
            } else if (r == NEGATIVE_INFINITY_DOUBLE) {
                Reactions.react( RESULT_IS_NEG_INF_MSG + "DSUB");
            } else if (Math.abs(r) <= CANCELLATION_ULP_FACTOR_DOUBLE * Math.ulp(a)) {
                Reactions.react( CANCELLATION_MSG + "DSUB");
            }
        }

        return r;
    }

    public static double DMUL(double a, double b) {
        double r = a * b;

        if (a != POSITIVE_INFINITY_DOUBLE && a != NEGATIVE_INFINITY_DOUBLE && b != POSITIVE_INFINITY_DOUBLE && b != NEGATIVE_INFINITY_DOUBLE) {
            if (r == POSITIVE_INFINITY_DOUBLE) {
                Reactions.react( RESULT_IS_POS_INF_MSG + "DMUL");
            } else if (r == NEGATIVE_INFINITY_DOUBLE) {
                Reactions.react( RESULT_IS_NEG_INF_MSG + "DMUL");
            } else if (r == 0.0 && a != 0.0 && b != 0.0) {
                Reactions.react( UNDERFLOW_MSG + "DMUL");
            }
        }

        return r;
    }

    public static double DDIV(double a, double b) {
        double r = a / b;

        if (a == a && b == b && r != r) {
            Reactions.react( RESULT_IS_NAN_MSG + "DDIV");
        } else if (a != POSITIVE_INFINITY_DOUBLE && a != NEGATIVE_INFINITY_DOUBLE && b != POSITIVE_INFINITY_DOUBLE && b != NEGATIVE_INFINITY_DOUBLE) {
            if (r == POSITIVE_INFINITY_DOUBLE) {
                Reactions.react( RESULT_IS_POS_INF_MSG + "DDIV");
            } else if (r == NEGATIVE_INFINITY_DOUBLE) {
                Reactions.react( RESULT_IS_NEG_INF_MSG + "DDIV");
            } else if (r == 0.0 && a != 0.0) {
                Reactions.react( UNDERFLOW_MSG + "DDIV");
            }
        }

        return r;
    }

    public static double DREM(double a, double b) {
        double r = a % b;

        if (a == a && b == b && r != r) {
            Reactions.react( RESULT_IS_NAN_MSG + "DREM");
        }
        if (Math.ulp(a) > Math.abs(b)) {
            Reactions.react( PRECISION_MSG + "DREM");
        }
        return r;
    }

    public static int DCMPG(double a, double b) {
        if (Double.isNaN(a) || Double.isNaN(b)) return +1;
        int r = a == b ? 0 : a < b ? -1 : 1;
        if (a == 2.0 * a || b == 2.0 * b)  // means here: isInfinite(r) (can't be 0 on NaN)
            return r;
        if (r != 0 && Math.abs(a - b) <= CLOSENESS_ULP_FACTOR_DOUBLE * Math.ulp(a)) {
            Reactions.react( VERY_CLOSE_MSG + "DCMP");
        }
        return r;
    }

    public static int DCMPL(double a, double b) {
        if (Double.isNaN(a) || Double.isNaN(b)) return -1;
        int r = a == b ? 0 : a < b ? -1 : 1;
        if (a == 2.0 * a || b == 2.0 * b)  // means here: isInfinite(r) (can't be 0 on NaN)
            return r;
        if (r != 0 && Math.abs(a - b) <= CLOSENESS_ULP_FACTOR_DOUBLE * Math.ulp(a)) {
            Reactions.react( VERY_CLOSE_MSG + "DCMP");
        }
        return r;
    }
    
    /*--------------------------FLOAT--------------------------*/
    
    public static float FADD(float a, float b) {
        float r = a + b;

        if (b != 0 && r == a || a != 0 && r == b) {
            if (r != 2.0 * r)  // means here: isInfinite(r) (can't be 0 on NaN)
            {
                Reactions.react( PRECISION_MSG + "FADD");
            }
        } else if (a != POSITIVE_INFINITY_FLOAT && a != NEGATIVE_INFINITY_FLOAT
            && b != POSITIVE_INFINITY_FLOAT && b != NEGATIVE_INFINITY_FLOAT
            && r != 0.0f) {
            if (r == POSITIVE_INFINITY_FLOAT) {
                Reactions.react( RESULT_IS_POS_INF_MSG + "FADD");
            } else if (r == NEGATIVE_INFINITY_FLOAT) {
                Reactions.react( RESULT_IS_NEG_INF_MSG + "FADD");
            } else if (Math.abs(r) <= CANCELLATION_ULP_FACTOR_FLOAT * Math.ulp(a)) {
                Reactions.react( CANCELLATION_MSG + "FADD");
            }
        }
        return r;
    }

    public static float FSUB(float a, float b) {
        float r = a - b;

        if (b != 0 && r == a || a != 0 && r == -b) {
            if (r != 2.0 * r)  // means here: isInfinite(r) (can't be 0 on NaN)
            {
                Reactions.react( PRECISION_MSG + "FSUB");
            }
        } else if (a != POSITIVE_INFINITY_FLOAT && a != NEGATIVE_INFINITY_FLOAT
            && b != POSITIVE_INFINITY_FLOAT && b != NEGATIVE_INFINITY_FLOAT
            && r != 0.0f) {
            if (r == POSITIVE_INFINITY_FLOAT) {
                Reactions.react( RESULT_IS_POS_INF_MSG + "FSUB");
            } else if (r == NEGATIVE_INFINITY_FLOAT) {
                Reactions.react( RESULT_IS_NEG_INF_MSG + "FSUB");
            } else if (Math.abs(r) <= CANCELLATION_ULP_FACTOR_FLOAT * Math.ulp(a)) {
                Reactions.react( CANCELLATION_MSG + "FSUB");
            }
        }

        return r;
    }

    public static float FMUL(float a, float b) {
        float r = a * b;

        if (a != POSITIVE_INFINITY_FLOAT && a != NEGATIVE_INFINITY_FLOAT && b != POSITIVE_INFINITY_FLOAT && b != NEGATIVE_INFINITY_FLOAT) {
            if (r == POSITIVE_INFINITY_FLOAT) {
                Reactions.react( RESULT_IS_POS_INF_MSG + "FMUL");
            } else if (r == NEGATIVE_INFINITY_FLOAT) {
                Reactions.react( RESULT_IS_NEG_INF_MSG + "FMUL");
            } else if (r == 0.0f && a != 0.0f && b != 0.0f) {
                Reactions.react( UNDERFLOW_MSG + "FMUL");
            }
        }

        return r;
    }

    public static float FDIV(float a, float b) {
        float r = a / b;

        if (a == a && b == b && r != r) {
            Reactions.react( RESULT_IS_NAN_MSG + "FDIV");
        } else if (a != POSITIVE_INFINITY_FLOAT && a != NEGATIVE_INFINITY_FLOAT && b != POSITIVE_INFINITY_FLOAT && b != NEGATIVE_INFINITY_FLOAT) {
            if (r == POSITIVE_INFINITY_FLOAT) {
                Reactions.react( RESULT_IS_POS_INF_MSG + "FDIV");
            } else if (r == NEGATIVE_INFINITY_FLOAT) {
                Reactions.react( RESULT_IS_NEG_INF_MSG + "FDIV");
            } else if (r == 0.0f && a != 0.0f) {
                Reactions.react( UNDERFLOW_MSG + "FDIV");
            }
        }

        return r;
    }

    public static float FREM(float a, float b) {
        float r = a % b;

        if (a == a && b == b && r != r) {
            Reactions.react( RESULT_IS_NAN_MSG + "FREM");
        }
        if (Math.ulp(a) > Math.abs(b)) {
            Reactions.react( PRECISION_MSG + "FREM");
        }

        return r;
    }

    public static int FCMPG(float a, float b) {
        if (Float.isNaN(a) || Float.isNaN(b)) return +1;
        int r = a == b ? 0 : a < b ? -1 : 1;
        if (a == 2.0f * a || b == 2.0f * b)  // means here: isInfinite(a) (can't be 0 on NaN)
            return r;
        if (r != 0 && Math.abs(a - b) <= CLOSENESS_ULP_FACTOR_FLOAT * Math.ulp(a)) {
            Reactions.react( VERY_CLOSE_MSG + "FCMP");
        }
        return r;
    }

    public static int FCMPL(float a, float b) {
        if (Float.isNaN(a) || Float.isNaN(b)) return -1;
        int r = a == b ? 0 : a < b ? -1 : 1;
        if (a == 2.0f * a || b == 2.0f * b)  // means here: isInfinite(a) (can't be 0 on NaN)
            return r;
        if (r != 0 && Math.abs(a - b) <= CLOSENESS_ULP_FACTOR_FLOAT * Math.ulp(a)) {
            Reactions.react( VERY_CLOSE_MSG + "FCMP");
        }
        return r;
    }
    
    /*--------------------------INT--------------------------*/
    public static int IADD(int a, int b) {
        int r = a + b;

        if (((a ^ r) & (b ^ r)) < 0) {
            Reactions.react( "Overflow : IADD");
        }

        return r;
    }

    public static int ISUB(int a, int b) {
        int r = a - b;

        if (((a ^ b) & (a ^ r)) < 0) {
            Reactions.react( "Overflow : ISUB");
        }

        return r;
    }

    public static int IDIV(int a, int b) {
        int r = a / b;

        if (a == Integer.MIN_VALUE && b == -1) {
            Reactions.react( "Overflow : IDIV");
        }

        return r;
    }

    public static int IMUL(int a, int b) {
        int r = a * b;

        if (b == 0) {
            return r;
        }

        if (b == -1 && a == Integer.MIN_VALUE || r / b != a) {
            Reactions.react( "Overflow : IMUL");
        }

        return r;
    }

    public static int IINC(int a, int b) {
        int r = a + b;

        if (((a ^ r) & (b ^ r)) < 0) {
            Reactions.react( "Overflow : IINC");
        }

        return r;
    }

    public static int INEG(int a) {
        if (a == Integer.MIN_VALUE) {
            Reactions.react( "Overflow : INEG");
        }

        return -a;
    }
    /*--------------------------LONG--------------------------*/
    
    public static long LADD(long a, long b) {
        long r = a + b;

        if (((a ^ r) & (b ^ r)) < 0) {
            Reactions.react( "Overflow : LADD");
        }

        return r;
    }

    public static long LSUB(long a, long b) {
        long r = a - b;

        if (((a ^ b) & (a ^ r)) < 0) {
            Reactions.react( "Overflow : LSUB");
        }

        return r;
    }

    public static long LDIV(long a, long b) {
        long r = a / b;

        if (a == Long.MIN_VALUE && b == -1) {
            Reactions.react( "Overflow : LDIV");
        }

        return r;
    }

    public static long LMUL(long a, long b) {
        long r = a * b;

        if (b == 0) {
            return r;
        }

        if (b == -1 && a == Long.MIN_VALUE || r / b != a) {
            Reactions.react( "Overflow : LMUL");
        }

        return r;
    }

    public static long LNEG(long a) {
        if (a == Long.MIN_VALUE) {
            Reactions.react( "Overflow : LNEG");
        }

        return -a;
    }
    /*--------------------------MATH--------------------------*/
    public static double pow(double a, double b) {
        double r = StrictMath.pow(a, b);

        if (r != r) {
            Reactions.react( RESULT_IS_NAN_MSG + "Math.pow()");
        } else if (r == Double.POSITIVE_INFINITY) {
            Reactions.react( RESULT_IS_POS_INF_MSG + "Math.pow()");
        } else if (r == Double.NEGATIVE_INFINITY) {
            Reactions.react( RESULT_IS_NEG_INF_MSG + "Math.pow()");
        }

        return r;
    }

    public static double asin(double a) {
        double r = StrictMath.asin(a);

        if (r != r) {
            Reactions.react( RESULT_IS_NAN_MSG + "Math.asin()");
        } else if (r == Double.POSITIVE_INFINITY) {
            Reactions.react( RESULT_IS_POS_INF_MSG + "Math.asin()");
        } else if (r == Double.NEGATIVE_INFINITY) {
            Reactions.react( RESULT_IS_NEG_INF_MSG + "Math.asin()");
        }

        return r;
    }

    public static double exp(double a) {
        double r = StrictMath.exp(a);

        if (r != r) {
            Reactions.react( RESULT_IS_NAN_MSG + "Math.exp()");
        } else if (r == Double.POSITIVE_INFINITY) {
            Reactions.react( RESULT_IS_POS_INF_MSG + "Math.exp()");
        } else if (r == Double.NEGATIVE_INFINITY) {
            Reactions.react( RESULT_IS_NEG_INF_MSG + "Math.exp()");
        }

        return r;
    }

    public static double log(double a) {
        double r = StrictMath.log(a);

        if (r != r) {
            Reactions.react( RESULT_IS_NAN_MSG + "Math.log()");
        } else if (r == Double.POSITIVE_INFINITY) {
            Reactions.react( RESULT_IS_POS_INF_MSG + "Math.log()");
        } else if (r == Double.NEGATIVE_INFINITY) {
            Reactions.react( RESULT_IS_NEG_INF_MSG + "Math.log()");
        }

        return r;
    }
}
