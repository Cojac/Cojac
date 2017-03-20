#include <fenv.h>
#include "NativeRoundingMode.h"

/*  Header file generation:
 *	javah -jni -cp <CojacDir>/target/classes/ com.github.cojac.models.ConversionBehaviour
 *  Dll generation (windows), needs minGW or equivalent: 
 *  64 bits
 *  gcc -m64 -s -D_JNI_IMPLEMENTATION_ -Wl,--kill-at -I"<path-to-JDK>\include" -I"<path-to-JDK>\include\win32" -shared NativeRoundingMode.c -o NativeRoundingMode.dll
 *  32 bits
 *  gcc -m32 -s -D_JNI_IMPLEMENTATION_ -Wl,--kill-at -I"<path-to-JDK>\include" -I"<path-to-JDK>\include\win32" -shared NativeRoundingMode.c -o NativeRoundingMode.dll
 *
 *  SO generation (linux)
 *	gcc -fpic  -I"<path-to-JDK>/include" -I"<path-to-JDK>/include/linux" -shared NativeRoundingMode.c -o libNativeRoundingMode.so
 * 
 */

/*
 *
 */

JNIEXPORT jint JNICALL Java_com_github_cojac_models_behaviours_ConversionBehaviour_changeRounding
  (JNIEnv *env , jclass cl, jint mode){
	
    jint originalMode = fegetround();
    fesetround(mode);
   
    return originalMode;

}