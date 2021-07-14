cmake_minimum_required(VERSION 3.12)

# We are only interested in finding jni.h: we do not care about extended JVM
# functionality or the AWT library.
set(JAVA_AWT_LIBRARY NotNeeded)
set(JAVA_JVM_LIBRARY NotNeeded)
set(JAVA_AWT_INCLUDE_PATH NotNeeded)

find_package(Java REQUIRED)
find_package(JNI REQUIRED)
include(UseJava)

include_directories(${JNI_INCLUDE_DIRS} ${JAVA_INCLUDE_PATH2})

project(native)

file(GLOB SOFT_POSIT libraries/SoftPosit/*.c)

add_library(posits_jni SHARED
        src/com_github_cojac_utils_Posit32Utils.c
        ${SOFT_POSIT}
        )

target_include_directories(posits_jni PRIVATE include ${JNI_INCLUDE_DIRS} ${JAVA_INCLUDE_PATH2} libraries/SoftPosit/source/include)

TARGET_LINK_LIBRARIES (posits_jni ${CMAKE_SOURCE_DIR}/libraries/SoftPosit/build/Linux-x86_64-GCC/softposit.a)