//
// Created by tache_zmo2jpf on 7/6/2021.
//

#define __int64 long long

#define F2P(VALUE) (((float_posit32_t) {.jfloat = (VALUE)}).posit)
#define P2F(VALUE) (((float_posit32_t) {.posit = (VALUE)}).jfloat)

#include "com_github_cojac_utils_Posit32Utils.h"

#include "softposit.h"

typedef union float_posit32 {
    jfloat jfloat;
    posit32_t posit;
} float_posit32_t;

JNIEXPORT jfloat JNICALL Java_com_github_cojac_utils_Posit32Utils_add
        (JNIEnv *env, jclass positClass, jfloat a, jfloat b) {
    return P2F(p32_add(F2P(a), F2P(b)));
}

JNIEXPORT jfloat JNICALL Java_com_github_cojac_utils_Posit32Utils_substract
        (JNIEnv *env, jclass positClass, jfloat a, jfloat b) {
    return P2F(p32_sub(F2P(a), F2P(b)));
}

JNIEXPORT jfloat JNICALL Java_com_github_cojac_utils_Posit32Utils_multiply
        (JNIEnv *env, jclass positClass, jfloat a, jfloat b) {
    return P2F(p32_mul(F2P(a), F2P(b)));
}

JNIEXPORT jfloat JNICALL Java_com_github_cojac_utils_Posit32Utils_divide
        (JNIEnv *env, jclass positClass, jfloat a, jfloat b) {
    return P2F(p32_div(F2P(a), F2P(b)));
}

JNIEXPORT jfloat JNICALL Java_com_github_cojac_utils_Posit32Utils_toFloat
        (JNIEnv *env, jclass positClass, jfloat a) {
    return (float) convertP32ToDouble(F2P(a));
}

JNIEXPORT jfloat JNICALL Java_com_github_cojac_utils_Posit32Utils_toPosit
        (JNIEnv *env, jclass positClass, jfloat a) {
    return P2F(convertFloatToP32(a));
}

JNIEXPORT jfloat JNICALL Java_com_github_cojac_utils_Posit32Utils_sqrt
        (JNIEnv *env, jclass positClass, jfloat a) {
    return P2F(p32_sqrt(F2P(a)));
}

JNIEXPORT jboolean JNICALL Java_com_github_cojac_utils_Posit32Utils_equals
        (JNIEnv *env, jclass positClass, jfloat a, jfloat b) {
    return p32_eq(F2P(a), F2P(b));
}

JNIEXPORT jboolean JNICALL Java_com_github_cojac_utils_Posit32Utils_isLess
        (JNIEnv *env, jclass positClass, jfloat a, jfloat b) {
    return p32_lt(F2P(a), F2P(b));
}

JNIEXPORT jboolean JNICALL Java_com_github_cojac_utils_Posit32Utils_isLessOrEquals
        (JNIEnv *env, jclass positClass, jfloat a, jfloat b) {
    return p32_le(F2P(a), F2P(b));
}

JNIEXPORT jfloat JNICALL Java_com_github_cojac_utils_Posit32Utils_fma
        (JNIEnv *env, jclass positClass, jfloat a, jfloat b, jfloat c) {
    return P2F(p32_mulAdd(F2P(a), F2P(b), F2P(c)));
}