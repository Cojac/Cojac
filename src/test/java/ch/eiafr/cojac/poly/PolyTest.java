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

package ch.eiafr.cojac.poly;

import java.net.URL;

import ch.eiafr.cojac.Args;
import ch.eiafr.cojac.CojacClassLoader;
import ch.eiafr.cojac.InstrumentationStats;

public class PolyTest {
    public static void main(String[] a) throws Exception {
        CojacClassLoader classLoader = new CojacClassLoader(new URL[]{}, new Args(), new InstrumentationStats());

        Object instance = classLoader.loadClass("ch.eiafr.cojac.poly.Simple").newInstance();

        Simple test = (Simple) instance;
        test.test();
    }
}