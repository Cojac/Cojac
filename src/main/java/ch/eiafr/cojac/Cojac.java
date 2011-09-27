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

import java.io.IOException;
import java.text.ParseException;

import static ch.eiafr.cojac.Arg.*;

public final class Cojac {
    public static void main(String[] strArgs) throws IOException, ParseException, InterruptedException {
        Args args = new Args();

        if (!args.parse(strArgs) || args.isSpecified(HELP)) {
            args.printHelpAndExit();
        }

        if (args.isSpecified(INSTRUMENT)) {
            new FilesInstrumenter(args).instrument();
        } else {
            new ApplicationStarter(args).start();
        }
    }
}