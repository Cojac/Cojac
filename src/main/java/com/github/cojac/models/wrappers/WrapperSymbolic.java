/*
 * *
 *    Copyright 2011-2014 Frédéric Bapst
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.github.cojac.models.wrappers;

public class WrapperSymbolic extends ACojacWrapper {

    private final SymbolicExpression expr;

    // -------------------------------------------------------------------------
    private WrapperSymbolic() {
        this.expr = new SymbolicExpression();
    }

    private WrapperSymbolic(double value) {
        this.expr = new SymbolicExpression(value);
    }

    private WrapperSymbolic(SymbolicExpression expr) {
        this.expr = expr;
    }

    private WrapperSymbolic(OP oper, SymbolicExpression left) {
        this.expr = new SymbolicExpression(oper, left);
    }

    private WrapperSymbolic(OP oper, SymbolicExpression left, SymbolicExpression right) {
        this.expr = new SymbolicExpression(oper, left, right);
    }

    // -------------------------------------------------------------------------
    // ----------------- Necessary constructor ---------------------------------
    // -------------------------------------------------------------------------
    public WrapperSymbolic(ACojacWrapper w) {
        this(w == null ? null : symb(w).expr);
    }
    // -------------------------------------------------------------------------

    @Override
    public ACojacWrapper dadd(ACojacWrapper w) {
        return new WrapperSymbolic(OP.ADD, this.expr, symb(w).expr);
    }

    @Override
    public ACojacWrapper dsub(ACojacWrapper w) {
        return new WrapperSymbolic(OP.SUB, this.expr, symb(w).expr);
    }

    @Override
    public ACojacWrapper dmul(ACojacWrapper w) {
        return new WrapperSymbolic(OP.MUL, this.expr, symb(w).expr);
    }

    @Override
    public ACojacWrapper ddiv(ACojacWrapper w) {
        return new WrapperSymbolic(OP.DIV, this.expr, symb(w).expr);
    }

    @Override
    public ACojacWrapper drem(ACojacWrapper w) {
        return new WrapperSymbolic(OP.REM, this.expr, symb(w).expr);
    }

    @Override
    public ACojacWrapper dneg() {
        return new WrapperSymbolic(OP.NEG, this.expr);
    }

    @Override
    public ACojacWrapper math_sqrt() {
        return new WrapperSymbolic(OP.SQRT, this.expr);
    }

    @Override
    public ACojacWrapper math_abs() {
        return new WrapperSymbolic(OP.ABS, this.expr);
    }

    @Override
    public ACojacWrapper math_sin() {
        return new WrapperSymbolic(OP.SIN, this.expr);
    }

    @Override
    public ACojacWrapper math_cos() {
        return new WrapperSymbolic(OP.COS, this.expr);
    }

    @Override
    public ACojacWrapper math_tan() {
        return new WrapperSymbolic(OP.TAN, this.expr);
    }

    @Override
    public ACojacWrapper math_asin() {
        return new WrapperSymbolic(OP.ASIN, this.expr);
    }

    @Override
    public ACojacWrapper math_acos() {
        return new WrapperSymbolic(OP.ACOS, this.expr);
    }

    @Override
    public ACojacWrapper math_atan() {
        return new WrapperSymbolic(OP.ATAN, this.expr);
    }

    @Override
    public ACojacWrapper math_sinh() {
        return new WrapperSymbolic(OP.SINH, this.expr);
    }

    @Override
    public ACojacWrapper math_cosh() {
        return new WrapperSymbolic(OP.COSH, this.expr);
    }

    @Override
    public ACojacWrapper math_tanh() {
        return new WrapperSymbolic(OP.TANH, this.expr);
    }

    @Override
    public ACojacWrapper math_exp() {
        return new WrapperSymbolic(OP.EXP, this.expr);
    }

    @Override
    public ACojacWrapper math_log() {
        return new WrapperSymbolic(OP.LOG, this.expr);
    }

    @Override
    public ACojacWrapper math_log10() {
        return new WrapperSymbolic(OP.LOG10, this.expr);
    }

    @Override
    public ACojacWrapper math_toRadians() {
        return new WrapperSymbolic(OP.RAD, this.expr);
    }

    @Override
    public ACojacWrapper math_toDegrees() {
        return new WrapperSymbolic(OP.DEG, this.expr);
    }

    @Override
    public ACojacWrapper math_pow(ACojacWrapper w) {
        return new WrapperSymbolic(OP.POW, this.expr, symb(w).expr);
    }

    // -------------------------------------------------------------------------
    @Override
    public double toDouble() {
        return expr.value;
    }

    @Override
    public ACojacWrapper fromDouble(double a, boolean wasFromFloat) {
        return new WrapperSymbolic(new SymbolicExpression(a));
    }

    @Override
    public String asInternalString() {
        return expr + "";
    }

    @Override
    public String wrapperName() {
        return "Symbolic";
    }

    // ------------------------------------------------------------------------
    public static boolean COJAC_MAGIC_isSymbolicUnknown(CommonDouble d) {
        return symb(d.val).expr.isUnknown;
    }

    public static boolean COJAC_MAGIC_isSymbolicUnknown(CommonFloat d) {
        return symb(d.val).expr.isUnknown;
    }

    public static CommonDouble COJAC_MAGIC_asSymbolicUnknown(CommonDouble d) {
        WrapperSymbolic res = new WrapperSymbolic();
        return new CommonDouble(res);
    }

    public static CommonFloat COJAC_MAGIC_asSymbolicUnknown(CommonFloat d) {
        WrapperSymbolic res = new WrapperSymbolic();
        return new CommonFloat(res);
    }

    public static CommonDouble COJAC_MAGIC_evaluateSymbolicAt(CommonDouble d, CommonDouble x) {
        double result = symb(d.val).expr.evaluate(symb(x.val).expr.value);
        WrapperSymbolic res = new WrapperSymbolic(result);
        return new CommonDouble(res);
    }

    public static CommonFloat COJAC_MAGIC_evaluateSymbolicAt(CommonFloat d, CommonFloat x) {
        double result = symb(d.val).expr.evaluate(symb(x.val).expr.value);
        WrapperSymbolic res = new WrapperSymbolic(result);
        return new CommonFloat(res);
    }

    // -------------------------------------------------------------------------
    private static WrapperSymbolic symb(ACojacWrapper w) {
        return (WrapperSymbolic) w;
    }

    // -------------------------------------------------------------------------
    private class SymbolicExpression {

        private double value;
        private boolean isUnknown;
        private OP oper;
        private SymbolicExpression left;
        private SymbolicExpression right;

        public SymbolicExpression() {
            this.value = Double.NaN;
            this.isUnknown = true;
            this.oper = OP.NOP;
            this.right = null;
            this.left = null;
        }

        public SymbolicExpression(double value) {
            this.value = value;
            this.isUnknown = false;
            this.oper = OP.NOP;
            this.left = null;
            this.right = null;
        }

        public SymbolicExpression(OP oper, SymbolicExpression left, SymbolicExpression right) {
            this.value = Double.NaN;
            this.isUnknown = false;
            this.oper = oper;
            this.left = left;
            this.right = right;
        }

        public SymbolicExpression(OP oper, SymbolicExpression left) {
            this.value = Double.NaN;
            this.isUnknown = false;
            this.oper = oper;
            this.left = left;
            this.right = null;
        }

        public double evaluate(double x) {
            if (isUnknown)
                return x;
            if (oper == OP.NOP)
                return value;
            if (oper.isBinaryOp)
                return oper.apply(left.evaluate(x), right.evaluate(x));
            return oper.apply(left.evaluate(x), Double.NaN);
        }

        public String toString() {
            if (isUnknown)
                return "x";
            if (oper == OP.NOP)
                return value + "";
            if (oper.isBinaryOp)
                return oper + "(" + left + "," + right + ")";
            return oper + "(" + left + ")";
        }

    }

    // -------------------------------------------------------------------------
    public static enum OP {
        NOP("NOP", false) {
            public double apply(double left, double right) {
                return Double.NaN;
            }
        },
        ADD("ADD", true) {
            public double apply(double left, double right) {
                return left + right;
            }
        },
        SUB("SUB", true) {
            public double apply(double left, double right) {
                return left - right;
            }
        },
        MUL("MUL", true) {
            public double apply(double left, double right) {
                return left * right;
            }
        },
        DIV("DIV", true) {
            public double apply(double left, double right) {
                return left / right;
            }
        },
        REM("REM", true) {
            public double apply(double left, double right) {
                return left % right;
            }
        },
        NEG("NEG", false) {
            public double apply(double left, double right) {
                return -left;
            }
        },
        SQRT("SQRT", false) {
            public double apply(double left, double right) {
                return Math.sqrt(left);
            }
        },
        ABS("ABS", false) {
            public double apply(double left, double right) {
                return Math.abs(left);
            }
        },
        SIN("SIN", false) {
            public double apply(double left, double right) {
                return Math.sin(left);
            }
        },
        COS("COS", false) {
            public double apply(double left, double right) {
                return Math.cos(left);
            }
        },
        TAN("TAN", false) {
            public double apply(double left, double right) {
                return Math.tan(left);
            }
        },
        ASIN("ASIN", false) {
            public double apply(double left, double right) {
                return Math.asin(left);
            }
        },
        ACOS("ACOS", false) {
            public double apply(double left, double right) {
                return Math.acos(left);
            }
        },
        ATAN("ATAN", false) {
            public double apply(double left, double right) {
                return Math.atan(left);
            }
        },
        SINH("SINH", false) {
            public double apply(double left, double right) {
                return Math.sinh(left);
            }
        },
        COSH("COSH", false) {
            public double apply(double left, double right) {
                return Math.cosh(left);
            }
        },
        TANH("TANH", false) {
            public double apply(double left, double right) {
                return Math.tanh(left);
            }
        },
        EXP("EXP", false) {
            public double apply(double left, double right) {
                return Math.exp(left);
            }
        },
        LOG("LOG", false) {
            public double apply(double left, double right) {
                return Math.log(left);
            }
        },
        LOG10("LOG10", false) {
            public double apply(double left, double right) {
                return Math.log10(left);
            }
        },
        RAD("RAD", false) {
            public double apply(double left, double right) {
                return Math.toRadians(left);
            }
        },
        DEG("DEG", false) {
            public double apply(double left, double right) {
                return Math.toDegrees(left);
            }
        },
        POW("POW", true) {
            public double apply(double left, double right) {
                return Math.pow(left, right);
            }
        };

        private final boolean isBinaryOp;

        private final String asString;

        OP(String asString, boolean isBinaryOp) {
            this.asString = asString;
            this.isBinaryOp = isBinaryOp;
        }

        public boolean isBinaryOp() {
            return isBinaryOp;
        }

        public abstract double apply(double left, double right);

        public String toString() {
            return this.asString;
        }
    }

}
