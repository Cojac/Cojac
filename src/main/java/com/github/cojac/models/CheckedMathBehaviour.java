package com.github.cojac.models;

import static com.github.cojac.models.CheckedBehaviourConstants.*;
public class CheckedMathBehaviour {
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
