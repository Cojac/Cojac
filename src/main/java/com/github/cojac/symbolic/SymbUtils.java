/*
 *    Copyright 2017 Frédéric Bapst et al.
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
 */

package com.github.cojac.symbolic;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.github.cojac.models.wrappers.SymbolicExpression.OP;

import com.github.cojac.models.wrappers.SymbolicExpression;

public class SymbUtils {

    // -------------------------------------------------------------------------

    public static SymbolicExpression derivateNOP(SymbolicExpression se) {
        if (se.containsUnknown)
            // x -> 1
            return symbExpr(1d);
        // C -> 0
        return symbExpr(0d);
    }

    // u + v -> u' + v'
    public static SymbolicExpression derivateADD(SymbolicExpression se) {

        return symbExpr(OP.ADD, se.left.derivate(), se.right.derivate());
    }

    // u - v -> u' - v'
    public static SymbolicExpression derivateSUB(SymbolicExpression se) {

        return symbExpr(OP.SUB, se.left.derivate(), se.right.derivate());
    }

    // u * v -> u' * v + u * v'
    public static SymbolicExpression derivateMUL(SymbolicExpression se) {
        SymbolicExpression dl_mul_r = symbExpr(OP.MUL, se.left.derivate(), se.right);
        SymbolicExpression l_mul_dr = symbExpr(OP.MUL, se.left, se.right.derivate());
        return symbExpr(OP.ADD, dl_mul_r, l_mul_dr);
    }

    // u / v -> (u' * v - u * v') / v^2
    public static SymbolicExpression derivateDIV(SymbolicExpression se) {
        SymbolicExpression dl_mul_r = symbExpr(OP.MUL, se.left.derivate(), se.right);
        SymbolicExpression l_mul_dr = symbExpr(OP.MUL, se.left, se.right.derivate());
        SymbolicExpression n = symbExpr(OP.SUB, dl_mul_r, l_mul_dr);
        SymbolicExpression d = symbExpr(OP.POW, se.right, symbExpr(2));
        return symbExpr(OP.DIV, n, d);
    }

    public static SymbolicExpression derivateNEG(SymbolicExpression se) {
        return symbExpr(OP.NEG, se.left.derivate(), null);
    }

    public static SymbolicExpression derivateREM(SymbolicExpression se) {
        if (se.containsUnknown) {
            Logger.getLogger(se.getClass().getPackage().getName()).log(Level.WARNING, "Can not derivate symbolic expressions containing operator %");
            return symbExpr(Double.NaN);
        }
        return symbExpr(0d);
    }

    // sqrt(u) -> u'/(2*sqrt(u))
    public static SymbolicExpression derivateSQRT(SymbolicExpression se) {
        SymbolicExpression n = se.left.derivate();
        SymbolicExpression d = symbExpr(OP.MUL, symbExpr(2), se);
        return symbExpr(OP.DIV, n, d);
    }

    // |u| -> u*u'/|u|
    public static SymbolicExpression derivateABS(SymbolicExpression se) {
        SymbolicExpression n = symbExpr(OP.MUL, se.left, se.left.derivate());
        return symbExpr(OP.DIV, n, se);
    }

    // sin(u) -> cos(u) * u'
    public static SymbolicExpression derivateSIN(SymbolicExpression se) {
        SymbolicExpression l = symbExpr(OP.COS, se.left, null);
        return symbExpr(OP.MUL, l, se.left.derivate());
    }

    // cos(u) -> -sin(u) * u'
    public static SymbolicExpression derivateCOS(SymbolicExpression se) {
        SymbolicExpression l1 = symbExpr(OP.SIN, se.left, null);
        SymbolicExpression l2 = symbExpr(OP.NEG, l1, null);
        return symbExpr(OP.MUL, l2, se.left.derivate());
    }

    // tan(u) -> u'/cos^2(u)
    public static SymbolicExpression derivateTAN(SymbolicExpression se) {
        SymbolicExpression n = se.left.derivate();
        SymbolicExpression d = symbExpr(OP.COS, se.left, null);
        d = symbExpr(OP.POW, d, symbExpr(2));
        return symbExpr(OP.DIV, n, d);
    }

    // asin(u) -> u'/sqrt(1-u^2)
    public static SymbolicExpression derivateASIN(SymbolicExpression se) {
        SymbolicExpression n = se.left.derivate();
        SymbolicExpression d = symbExpr(OP.POW, se.left, symbExpr(2));
        d = symbExpr(OP.SUB, symbExpr(1), d);
        d = symbExpr(OP.SQRT, d, null);
        return symbExpr(OP.DIV, n, d);
    }

    // acos(u) -> -u'/sqrt(1-u^2)
    public static SymbolicExpression derivateACOS(SymbolicExpression se) {
        SymbolicExpression n = symbExpr(OP.NEG, se.left.derivate(), null);
        SymbolicExpression d = symbExpr(OP.POW, se.left, symbExpr(2));
        d = symbExpr(OP.SUB, symbExpr(1), d);
        d = symbExpr(OP.SQRT, d, null);
        return symbExpr(OP.DIV, n, d);
    }

    // atan(u) -> u'/(1-u^2)
    public static SymbolicExpression derivateATAN(SymbolicExpression se) {
        SymbolicExpression n = se.left.derivate();
        SymbolicExpression d = symbExpr(OP.POW, se.left, symbExpr(2));
        d = symbExpr(OP.ADD, symbExpr(1), d);
        return symbExpr(OP.DIV, n, d);
    }

    // sinh(u) -> cosh(u) * u'
    public static SymbolicExpression derivateSINH(SymbolicExpression se) {
        SymbolicExpression l = symbExpr(OP.COSH, se.left, null);
        return symbExpr(OP.MUL, l, se.left.derivate());
    }

    // cosh(u) -> sinh(u) * u'
    public static SymbolicExpression derivateCOSH(SymbolicExpression se) {
        SymbolicExpression l = symbExpr(OP.SINH, se.left, null);
        return symbExpr(OP.MUL, l, se.left.derivate());
    }

    // tanh(u) -> u'/cosh^2(u)
    public static SymbolicExpression derivateTANH(SymbolicExpression se) {
        SymbolicExpression n = se.left.derivate();
        SymbolicExpression d = symbExpr(OP.COSH, se.left, null);
        d = symbExpr(OP.POW, d, symbExpr(2));
        return symbExpr(OP.DIV, n, d);
    }

    // e^u -> e^u * u'
    public static SymbolicExpression derivateEXP(SymbolicExpression se) {
        return symbExpr(OP.MUL, se, se.left.derivate());
    }

    // log(u) -> u'/u
    public static SymbolicExpression derivateLOG(SymbolicExpression se) {
        return symbExpr(OP.DIV, se.left.derivate(), se.left);
    }

    // log10(u) -> u'/(u*log(10))
    public static SymbolicExpression derivateLOG10(SymbolicExpression se) {
        SymbolicExpression n = se.left.derivate();
        SymbolicExpression d = symbExpr(OP.LOG, symbExpr(10), null);
        d = symbExpr(OP.MUL, d, se.left);
        return symbExpr(OP.DIV, n, d);
    }

    public static SymbolicExpression derivateRAD(SymbolicExpression se) {
        return symbExpr(OP.MUL, se.left, symbExpr(Math.PI / 180)).derivate();
    }

    public static SymbolicExpression derivateDEG(SymbolicExpression se) {
        return symbExpr(OP.MUL, se.left, symbExpr(180 / Math.PI)).derivate();
    }

    public static SymbolicExpression derivateMIN(SymbolicExpression se) {
        if (se.containsUnknown) {
            Logger.getLogger(se.getClass().getPackage().getName()).log(Level.WARNING, "Can not derivate symbolic expressions containing operator min");
            return symbExpr(Double.NaN);
        }
        return symbExpr(0d);
    }

    public static SymbolicExpression derivateMAX(SymbolicExpression se) {
        if (se.containsUnknown) {
            Logger.getLogger(se.getClass().getPackage().getName()).log(Level.WARNING, "Can not derivate symbolic expressions containing operator max");
            return symbExpr(Double.NaN);
        }
        return symbExpr(0d);
    }

    // u^v = e^log(u^v) = e^(v*log(u))
    public static SymbolicExpression derivatePOW(SymbolicExpression se) {
        // distinger ?? a^x et x^a??
        SymbolicExpression p = symbExpr(OP.LOG, se.left, null);
        p = symbExpr(OP.MUL, se.right, p);
        p = symbExpr(OP.EXP, p, null);
        return p.derivate();
    }

    public static SymbolicExpression derivateHYPOT(SymbolicExpression se) {
        // TODO write this
        throw new UnsupportedOperationException();
    }

    // -------------------------------------------------------------------------

    public static SymbolicExpression symbExpr() {
        return new SymbolicExpression();
    }

    public static SymbolicExpression symbExpr(double value) {
        return new SymbolicExpression(value);
    }


    public static SymbolicExpression symbExpr(OP oper, SymbolicExpression left, SymbolicExpression right) {
        return new SymbolicExpression(oper, left, right);
    }

    // -------------------------------------------------------------------------

    @FunctionalInterface
    public interface SymbolicDerivationOperator {
        SymbolicExpression derivate(SymbolicExpression se);
    }
}
