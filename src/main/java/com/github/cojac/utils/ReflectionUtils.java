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

package com.github.cojac.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.regex.Pattern;

public final class ReflectionUtils {
    private static final Class<?>[] CALLBACK_PARAM_TYPES = {String.class};
    private static final Pattern METHOD_SEPARATOR = Pattern.compile("/");

    private ReflectionUtils() {
        throw new AssertionError();
    }

    public static void setStaticFieldValue(ClassLoader loader, String className, String fieldName, Object value) {
        try {
            Class<?> classz = Class.forName(className, true, loader);
            Field field = classz.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(null, value);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getStaticFieldValue(ClassLoader loader, String className, String fieldName) {
        try {
            Class<?> reactions = Class.forName(className, true, loader);
            Field field = reactions.getDeclaredField(fieldName);
            field.setAccessible(true);
            return (T) field.get(null);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }


    // There is a very nasty dependence between the Cojac Eclipse Plugin
    // ch.eiafr.ecojac_core.AnnotationMgr.getRelevantElt()
    // and the precise path leading to the invocation of this method... F. Bapst
    public static void invokeCallback(String callbackName, String callbackParam) {
        int a = callbackName.lastIndexOf('/');
        if (a < 0) {
            throw new IllegalStateException("bad callback format (should be ab/cd/className/methName)");
        }

        String className = METHOD_SEPARATOR.matcher(callbackName.substring(0, a)).replaceAll(".");
        String methodName = callbackName.substring(a + 1);

        try {
//            System.out.println("RRR:"+ClassLoader.getSystemClassLoader());
//            System.out.println("RRR:"+ReflectionUtils.class.getClassLoader());
            Class<?> clazz = Class.forName(className, true, ClassLoader.getSystemClassLoader());
            Method method = clazz.getMethod(methodName, CALLBACK_PARAM_TYPES);
            method.invoke(null, callbackParam);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}