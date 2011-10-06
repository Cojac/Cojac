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

import ch.eiafr.cojac.Arg;
import ch.eiafr.cojac.Args;
import ch.eiafr.cojac.FilesInstrumenter;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.*;
import java.util.Arrays;

public class BasicFilesTest extends AbstractFullTests {
    private final Tests tests;

    public BasicFilesTest() throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        super();

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

        //TODO : Review path for Maven

        File srcdir = new File(System.getProperty("java.io.tmpdir"), "java-src");
        srcdir.mkdirs();


        File dstdir = new File(System.getProperty("java.io.tmpdir"), "java-dst");
        dstdir.mkdirs();

        Iterable<? extends JavaFileObject> compilationUnits = fileManager
            .getJavaFileObjectsFromStrings(Arrays.asList("src/test/java/ch/eiafr/cojac/unit/SimpleOperations.java"));

        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, Arrays.asList("-d", srcdir.getAbsolutePath()), null, compilationUnits);

        task.call();
        fileManager.close();

        String oldDir = System.getProperty("user.dir");
        System.setProperty("user.dir", srcdir.getAbsolutePath());

        Args args = new Args();

        args.specify(Arg.ALL);
        args.specify(Arg.EXCEPTION);

        specifyArgs(args);

        args.setValue(Arg.PATH, dstdir.getAbsolutePath());

        args.getFiles().add("ch/eiafr/cojac/unit/SimpleOperations.class");

        new FilesInstrumenter(args).instrument();

        ClassLoader classLoader = new CustomClassLoader(dstdir);

        Class<?> cls = classLoader.loadClass("ch.eiafr.cojac.unit.SimpleOperations");

        tests = new Tests(cls.newInstance());

        System.setProperty("user.dir", oldDir);
    }

    protected void specifyArgs(Args args) {
        //Nothing to do by default
    }

    @Override
    public Tests getTests() {
        return tests;
    }

    private static class CustomClassLoader extends ClassLoader {
        private final File file;

        private CustomClassLoader(File file) {
            super();
            this.file = file;
        }

        @Override
        protected Class<?> loadClass(String className, boolean resolve) throws ClassNotFoundException {
            if (className.contains("SimpleOperations")) {
                byte[] classBytes = readBytes(className);

                return resolve(className, resolve, classBytes);
            }

            return super.loadClass(className, resolve);
        }

        private byte[] readBytes(String className) {
            String clsFile = className.replace('.', '/') + ".class";

            try {
                InputStream in = new FileInputStream(new File(file, clsFile));

                byte[] buffer = new byte[2048];
                ByteArrayOutputStream out = new ByteArrayOutputStream();

                int n;
                while ((n = in.read(buffer, 0, 2048)) != -1) {
                    out.write(buffer, 0, n);
                }

                return out.toByteArray();
            } catch (IOException e) {
                System.out.println("ERROR loading class file. Due to " + e);
            }

            return null;
        }

        private Class<?> resolve(String className, boolean resolve, byte[] classBytes) throws ClassNotFoundException {
            Class<?> cls;
            try {
                cls = defineClass(className, classBytes, 0, classBytes.length);

                if (resolve) {
                    resolveClass(cls);
                }
            } catch (SecurityException e) {
                cls = super.loadClass(className, resolve);
            }

            return cls;
        }
    }
}
