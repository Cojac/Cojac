/*
 * *
 *    Copyright 2011 Baptiste Wicht & Frédéric Bapst
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

package com.github.cojac.unit;

import com.github.cojac.models.CheckedCasts;
import com.github.cojac.models.ReactionType;

public interface MathOperations {
    int iadd(int a, int b) throws Exception;

    int isub(int a, int b) throws Exception;

    int imul(int a, int b) throws Exception;

    int idiv(int a, int b) throws Exception;

    int iinc(int a, int b) throws Exception;

    int ineg(int a) throws Exception;

    long ladd(long a, long b) throws Exception;

    long lsub(long a, long b) throws Exception;

    long lmul(long a, long b) throws Exception;

    long ldiv(long a, long b) throws Exception;

    long lneg(long a) throws Exception;

    double dadd(double a, double b) throws Exception;

    double dsub(double a, double b) throws Exception;

    double dmul(double a, double b) throws Exception;

    double ddiv(double a, double b) throws Exception;

    double drem(double a, double b) throws Exception;

    int dcmpg(double a, double b) throws Exception;

    int dcmpl(double a, double b) throws Exception;

    float fadd(float a, float b) throws Exception;

    float fsub(float a, float b) throws Exception;

    float fmul(float a, float b) throws Exception;

    float fdiv(float a, float b) throws Exception;

    float frem(float a, float b) throws Exception;

    int fcmpg(float a, float b) throws Exception;

    int fcmpl(float a, float b) throws Exception;

    int l2i(long a) throws Exception;

    short i2s(int a) throws Exception;

    byte i2b(int a) throws Exception;

    char i2c(int a) throws Exception;

    int d2i(double a) throws Exception;

    long d2l(double a) throws Exception;

    int f2i(float a) throws Exception;

    long f2l(float a) throws Exception;

    float d2f(double a) throws Exception;
    
    float i2f(int a) throws Exception;

    double l2d(long a) throws Exception;

    double pow(double a, double b) throws Exception;

    double asin(double a) throws Exception;

    double exp(double a) throws Exception;

    double log(double a) throws Exception;
}