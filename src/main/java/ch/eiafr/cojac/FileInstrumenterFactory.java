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

package ch.eiafr.cojac;

final class FileInstrumenterFactory {
    private final FileInstrumenter classFileInstrumenter;
    private final FileInstrumenter jarFileInstrumenter;

    FileInstrumenterFactory(Args args, InstrumentationStats stats) {
        super();

        classFileInstrumenter = new ClassFileInstrumenter(args, stats);
        jarFileInstrumenter = new JARFileInstrumenter(args, stats);
    }

    public FileInstrumenter getFileInstrumenter(String file) {
        if (file.endsWith(".class")) {
            return classFileInstrumenter;
        } else if (file.endsWith(".jar")) {
            return jarFileInstrumenter;
        }

        return null;
    }
}
