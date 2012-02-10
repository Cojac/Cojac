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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public final class FilesInstrumenter {
    private final Collection<File> files = new ArrayList<File>(15);

    private final Args args;
    private final InstrumentationStats stats = new InstrumentationStats();

    public FilesInstrumenter(Args args) {
        super();

        this.args = args;
    }

    public void instrument() {
        stats.startTimer();

        checkFiles();
        checkFolder();

        FileInstrumenterFactory factory = new FileInstrumenterFactory(args, stats);

        try {
            for (String f : args.getFiles()) {
                factory.getFileInstrumenter(f).instrument(f, args);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (args.isSpecified(Arg.INSTRUMENTATION_STATS)) {
            stats.printInstrumentationStats(args);
        }
    }

    //---------------------------------------------------------
    // check if jar or class exists
    void checkFiles() {
        for (String arg : args.getFiles()) {
            files.add(new File(System.getProperty("user.dir") + '/' + arg));
        }

        if (files.isEmpty()) {
            System.out.println("Must provide at least one .class or .jar file");
        } else {
            verifyFiles(files);
        }
    }

    private void checkFolder() {
        File instrumentationFolder = new File(args.getValue(Arg.PATH));

        if (!instrumentationFolder.exists() && !instrumentationFolder.mkdirs()) {
            System.out.println("Unable to create the folder of instrumentation");
            System.exit(-1);
        }
    }

    private static void verifyFiles(Iterable<File> files) {
        for (File file : files) {
            if (!file.exists()) {
                System.err.println(file + " not found!\nGive a correct path for " + file + " file");
                System.exit(0);
            }
        }
    }
}
