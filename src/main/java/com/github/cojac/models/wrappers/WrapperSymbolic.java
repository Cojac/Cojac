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
    
    //private final double value;
    //private final boolean isUnknown;
    private final SymbolicExpression expr;

    private WrapperSymbolic() {
        this.expr = new SymbolicExpression();
    }
    
    private WrapperSymbolic(double value) {
        this.expr = new SymbolicExpression(value);
    }
    
    private WrapperSymbolic(SymbolicExpression expr) {
        this.expr = expr;
    }
   
    //-------------------------------------------------------------------------
    //----------------- Necessary constructor  -------------------------------
    //-------------------------------------------------------------------------
    public WrapperSymbolic(ACojacWrapper w) {//????
        this(w==null ? null : symb(w).expr);
        System.out.println("WrapperSymbolic");
    }
    
    
    // ATTENTION redefinire toutes les opérations pour garder une trace
    
    //-------------------------------------------------------------------------
    // Most of the operations do not follow those "operator" rules, 
    // and are thus fully redefined
   /* @Override
    public ACojacWrapper applyUnaryOp(DoubleUnaryOperator op) {
        System.out.println("applyUnaryOp");
        return new WrapperSymbolic(op.applyAsDouble(value), false);
    }

    @Override
    public ACojacWrapper applyBinaryOp(DoubleBinaryOperator op, ACojacWrapper b) {
        System.out.println("applyBinaryOp");
        WrapperSymbolic bb=(WrapperSymbolic)b;
        return new WrapperSymbolic(op.applyAsDouble(value, bb.value),
                                     false);
    }*/
    
    //-------------------------------------------------------------------------

    @Override
    public ACojacWrapper dadd(ACojacWrapper b) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ACojacWrapper dsub(ACojacWrapper b) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ACojacWrapper dmul(ACojacWrapper b) {
        System.out.println("("+this.expr.value+"*"+symb(b).expr.value+")");
        //double d=this.value*der(b).deriv + this.deriv*der(b).value;
        return new WrapperSymbolic(new SymbolicExpression(this.expr,OP.MUL,symb(b).expr));
    }

    @Override
    public ACojacWrapper ddiv(ACojacWrapper b) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ACojacWrapper drem(ACojacWrapper b) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ACojacWrapper dneg() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ACojacWrapper math_sqrt() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ACojacWrapper math_abs() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ACojacWrapper math_sin() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ACojacWrapper math_cos() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ACojacWrapper math_tan() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ACojacWrapper math_asin() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ACojacWrapper math_acos() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ACojacWrapper math_atan() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ACojacWrapper math_sinh() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ACojacWrapper math_cosh() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ACojacWrapper math_tanh() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ACojacWrapper math_exp() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ACojacWrapper math_log() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ACojacWrapper math_log10() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ACojacWrapper math_toRadians() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ACojacWrapper math_toDegrees() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ACojacWrapper math_pow(ACojacWrapper b) {
        // TODO Auto-generated method stub
        return null;
    }
   
    //-------------------------------------------------------------------------
    @Override public double toDouble() {
        return expr.value;
    }

    @Override
    public ACojacWrapper fromDouble(double a, boolean wasFromFloat) {
        return new WrapperSymbolic(new SymbolicExpression(a));
    }

    @Override public String asInternalString() {
        return expr.value+" (isUnknown="+expr.isUnknown+")";
    }

    @Override public String wrapperName() {
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
        WrapperSymbolic res=new WrapperSymbolic();
        return new CommonDouble(res);
    }
    
    public static CommonFloat COJAC_MAGIC_asSymbolicUnknown(CommonFloat d) {
        WrapperSymbolic res = new WrapperSymbolic();
        return new CommonFloat(res);
    }
    
    public static CommonDouble COJAC_MAGIC_evaluateSymbolicAt(CommonDouble d, CommonDouble x) {
        double result= symb(d.val).expr.evaluate(symb(x.val).expr.value);
        WrapperSymbolic res = new WrapperSymbolic(result);
        return new CommonDouble(res);
    }
    
    public static CommonFloat COJAC_MAGIC_evaluateSymbolicAt(CommonFloat d, CommonFloat x) {
        double result= symb(d.val).expr.evaluate(symb(x.val).expr.value);
        WrapperSymbolic res = new WrapperSymbolic(result);
        return new CommonFloat(res);
    }
    

    //-------------------------------------------------------------------------
    private static WrapperSymbolic symb(ACojacWrapper w) {
        return (WrapperSymbolic)w;
    }

    //-------------------------------------------------------------------------
    private class SymbolicExpression {
        
        private double value;
        private boolean isUnknown;
        private OP oper;
        private SymbolicExpression left;
        private SymbolicExpression right;
        
        public SymbolicExpression () {
            this.value = Double.NaN;
            this.isUnknown = true;
            this.oper = OP.NOP;
            this.right = null;
            this.left = null;
        }
        
        public SymbolicExpression (double value) {
            this.value = value;
            this.isUnknown = false;
            this.oper = OP.NOP;
            this.left = null;
            this.right = null;
        }
        
        public SymbolicExpression (SymbolicExpression left, OP oper,SymbolicExpression right) {
            this.value = Double.NaN;
            this.isUnknown = false;
            this.oper = oper;
            this.left = left;
            this.right = right;
        }
        
        public double evaluate (double x) {
            if(isUnknown) return x;
            if(oper == OP.NOP) return value;
            return left.evaluate(x) * right.evaluate(x);
        }
        
    }
    
    //-------------------------------------------------------------------------
    public static enum OP {
        NOP, ADD, SUB, MUL, DIV; //...
    } 
   
}
