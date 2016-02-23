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

package com.github.cojac.models;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;

/** This class makes the cojac-agent-test.jar, used for 
 * benchmarks and junit tests.
 * 
 * @author Vincent Pasquier, frederic.bapst
 *
 */

public class ReflexionAgent {
    public static void premain(@SuppressWarnings("unused") String agentArgs, Instrumentation inst) {
        try {
            Class<?> classz = Class.forName("com.github.cojac.unit.AgentTest", true, ClassLoader.getSystemClassLoader());
            Field field = classz.getDeclaredField("instrumentation");
            field.setAccessible(true);
            field.set(null, inst);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
