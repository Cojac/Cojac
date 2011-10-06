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

import java.io.*;

public final class ClassFileInstrumenter implements FileInstrumenter {
    private final ClassInstrumenter classInstrumenter;

    public ClassFileInstrumenter(Args args, InstrumentationStats stats) {
        super();

        classInstrumenter = new SimpleClassInstrumenter(args, stats, new BasicReaction(args),
            new SimpleOpCodeFactory(args, stats));
    }

    @Override
    public void instrument(String file, Args args) throws IOException {
        byte[] byteCode = readBytes(file);

        byte[] newByteCode = classInstrumenter.instrument(byteCode);

        writeBytes(args, file, newByteCode);
    }

    private static byte[] readBytes(String file) throws IOException {
        File f = new File(System.getProperty("user.dir") + '/' + file);

        byte[] bytes = new byte[(int) f.length()];

        InputStream is = null;
        try {
            is = new FileInputStream(f);

            int offset = 0;

            while (offset < f.length()) {
                int read = is.read(bytes, offset, (int) (f.length() - offset));

                if (read == -1) {
                    break;
                }

                offset += read;
            }

            if (offset != bytes.length) {
                throw new IOException("The entry has not been completely read (" + offset + " of " + bytes.length + ')');
            }
        } finally {
            if (is != null) {
                is.close();
            }
        }

        return bytes;
    }

    private static void writeBytes(Args args, String classFile, byte[] newByteCode) throws IOException {
        File file = new File(args.getValue(Arg.PATH) + '/' + classFile);
        file.getParentFile().mkdirs();

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);

            fos.write(newByteCode);
        } finally {
            if (fos != null) {
                fos.close();
            }
        }
    }
}