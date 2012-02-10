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

package ch.eiafr.cojac.unit;

import ch.eiafr.cojac.InstrumentationStats;

public abstract class ClassLoaderTest extends AbstractFullTests {
    private final Tests tests;

    protected ClassLoaderTest() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        super();

        InstrumentationStats stats = new InstrumentationStats();
        ClassLoader classLoader = getClassLoader(stats);

        Class<?> operations = classLoader.loadClass("ch.eiafr.cojac.unit.SimpleOperations");

        tests = new Tests(operations.newInstance());
    }

    public abstract ClassLoader getClassLoader(InstrumentationStats stats);

    @Override
    public Tests getTests() {
        return tests;
    }
}