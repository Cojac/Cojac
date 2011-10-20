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

import ch.eiafr.cojac.instrumenters.ClassLoaderOpSizeInstrumenterFactory;
import ch.eiafr.cojac.instrumenters.OpCodeInstrumenterFactory;
import ch.eiafr.cojac.instrumenters.SimpleOpCodeFactory;
import ch.eiafr.cojac.reactions.ClassLoaderReaction;
import ch.eiafr.cojac.utils.ReflectionUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.regex.Pattern;

public final class CojacClassLoader extends URLClassLoader {
    private static final Pattern COMMA_PATTERN = Pattern.compile(";");
    private static final int BUFFER_SIZE = 8192;

    private static final String[] STANDARD_PACKAGES = {
        "com.sun.", "java.", "javax.", "sun.", "sunw.",
        "org.xml.sax.", "org.w3c.dom.", "org.omg.",
        "org.ietf.jgss.",                            // std trusted packages
        "ch.eiafr.cojac.models",                     // cojac method to protect
        "org.slf4j"  //, ...                         // problematic libraries
    };

    private String[] bypassList = {};

    private final ClassInstrumenter classInstrumenter;

    public CojacClassLoader(URL[] urls, Args args, InstrumentationStats stats) {
        super(urls, ClassLoader.getSystemClassLoader());

        if (args.isSpecified(Arg.FILTER)) {
            ReflectionUtils.setStaticFieldValue(this, "ch.eiafr.cojac.models.Reactions", "filtering", true);
        }

        if (args.isSpecified(Arg.BYPASS)) {
            bypassList = parseBypassList(args.getValue(Arg.BYPASS));
        }

        OpCodeInstrumenterFactory factory;
        if (!args.isSpecified(Arg.WASTE_SIZE)) {
            factory = new ClassLoaderOpSizeInstrumenterFactory(args, stats);
        } else {
            factory = new SimpleOpCodeFactory(args, stats);
        }

        classInstrumenter = new ClassLoaderInstrumenter(args, stats, new ClassLoaderReaction(args), factory);
    }

    @Override
    protected Class<?> loadClass(String className, boolean resolve) throws ClassNotFoundException {
        Class<?> cls = findLoadedClass(className);
        if (cls != null) {
            return cls;
        }

        if (!hasToBeInstrumented(className)) {  //BAPST (against AccessibleContext problem under XP ?)
            return getParent().loadClass(className);
        }

        byte[] classBytes = readBytes(className);

        if (classBytes == null) {
            throw new ClassNotFoundException("Cannot load class: " + className);
        }

        if (hasToBeInstrumented(className)) {
            //log("Instrument class " + className);

            //long startTime = System.currentTimeMillis();

            /*System.out.println("Before");

            ClassReader cr = new ClassReader(classBytes);
            ClassWriter cw = new ClassWriter(cr, 0);

            cr.accept(new TraceClassVisitor(new PrintWriter(System.out)), 0);

            cw.toByteArray();*/

            classBytes = classInstrumenter.instrument(classBytes);

            /*System.out.println("After");

            cr = new ClassReader(classBytes);
            cw = new ClassWriter(cr, 0);

            cr.accept(new TraceClassVisitor(new PrintWriter(System.out)), 0);

            cw.toByteArray();

            /*StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            CheckClassAdapter.verify(new ClassReader(classBytes), false, pw);
            System.out.println(sw.toString());*/

            //log("Instrumentation took " + (System.currentTimeMillis() - startTime) + "ms");
        }

        return resolve(className, resolve, classBytes);
    }

    private byte[] readBytes(String className) {
        String clsFile = className.replace('.', '/') + ".class";

        try {
            InputStream in = getResourceAsStream(clsFile);

            if (in == null) {
                return null;
            }

            byte[] buffer = new byte[BUFFER_SIZE];
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            int n;
            while ((n = in.read(buffer, 0, BUFFER_SIZE)) != -1) {
                out.write(buffer, 0, n);
            }

            return out.toByteArray();
        } catch (IOException e) {
            log("ERROR loading class file. Due to " + e);
        }

        return null;
    }

    private static String[] parseBypassList(String bypassList) {
        return COMMA_PATTERN.split(bypassList);
    }

    private boolean hasToBeInstrumented(String className) {
        for (String standardPackage : STANDARD_PACKAGES) {
            if (className.startsWith(standardPackage)) {
                return false;
            }
        }
        for (String prefix : bypassList) {
            if (className.startsWith(prefix))
                return false;
        }

        return true;
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

    private static void log(String s) {
        System.out.println(s);
    }
}