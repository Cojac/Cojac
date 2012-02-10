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

import ch.eiafr.cojac.instrumenters.SimpleOpCodeFactory;
import ch.eiafr.cojac.reactions.BasicReaction;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

public final class JARFileInstrumenter implements FileInstrumenter {
    private static final Pattern PATH_SEPARATOR_PATTERN = Pattern.compile("/");

    private final ClassInstrumenter classInstrumenter;

    public JARFileInstrumenter(Args args, InstrumentationStats stats) {
        super();

        classInstrumenter = new SimpleClassInstrumenter(args, stats, new BasicReaction(args),
            new SimpleOpCodeFactory(args, stats));
    }

    @Override
    public void instrument(String file, Args args) throws IOException {
        JarFile jarFile = new JarFile(file);

        JarOutputStream jos = new JarOutputStream(
            new BufferedOutputStream(new FileOutputStream(args.getValue(Arg.PATH) + file)));

        try {
            Enumeration<JarEntry> jarInputEntries = jarFile.entries();

            while (jarInputEntries.hasMoreElements()) {
                JarEntry currentEntry = jarInputEntries.nextElement();

                createFolders(args, currentEntry);

                String crtFileName = args.getValue(Arg.PATH) + currentEntry.getName();

                byte[] byteCode = readBytes(jarFile, currentEntry);

                if (crtFileName.endsWith(".class")) {
                    byteCode = classInstrumenter.instrument(byteCode);
                }

                writeBytes(jos, currentEntry, byteCode);
            }
        } finally {
            jos.close();
        }
    }

    private static void createFolders(Args args, ZipEntry entry) {
        if (entry.getName().contains("/")) {
            String[] s = PATH_SEPARATOR_PATTERN.split(entry.getName());

            StringBuilder dirs = new StringBuilder(50);
            for (int j = 0; j < s.length - 1; j++) {
                dirs.append('/');

                new File(args.getValue(Arg.PATH) + dirs).mkdirs();
            }
        }
    }

    private static byte[] readBytes(JarFile jarFile, ZipEntry entry) throws IOException {
        int size = (int) entry.getSize();

        byte[] bytes = new byte[size];

        if (size == 0) {
            return bytes;
        }

        InputStream stream = jarFile.getInputStream(entry);

        int offset = 0;

        while (offset < size) {
            int read = stream.read(bytes, offset, size - offset);

            if (read == -1) {
                break;
            }

            offset += read;
        }

        if (offset != bytes.length) {
            throw new IOException("The entry has not been completely read (" + offset + " of " + bytes.length + ')');
        }

        return bytes;
    }

    private static void writeBytes(JarOutputStream jos, ZipEntry currentEntry, byte[] bytes) throws IOException {
        jos.putNextEntry(new JarEntry(currentEntry.getName()));
        jos.write(bytes, 0, bytes.length);
        jos.flush();
        jos.closeEntry();
    }
}
