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

package ch.eiafr.cojac.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class ReflectionUtils {
    private ReflectionUtils() {
        throw new AssertionError();
    }

    public static void setStaticFieldValue(ClassLoader loader, String className, String fieldName, Object value){
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

    public static <T> T getStaticFieldValue(ClassLoader loader, String className, String fieldName){
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
    // ch.eiafr.ecojac_core.AnnontationMgr.getRelevantElt()
    // and the precise path leading to the invocation of this method... F. Bapst
    public static void invokeCallback(String callbackName, String callbackParam) {
      String className, methodName;
      int a=callbackName.lastIndexOf("/");
      if (a<0) {
        throw new IllegalStateException("bad callback format (should be ab/cd/className/methName)");
      }
      className= callbackName.substring(0, a).replaceAll("/", ".");
      methodName= callbackName.substring(a+1);
      //System.out.println("calling "+className+":"+methodName+"("+callbackParam+")");
      
      try {
        Class<?> clazz = Class.forName(className);
        Class<?> [] prmTypes= {String.class};
        Method meth= clazz.getMethod(methodName, prmTypes);
        meth.invoke(null, callbackParam);
      } catch (Exception e) {
        throw new IllegalStateException(e);
      }    
    }
}