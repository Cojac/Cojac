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

import ch.eiafr.cojac.CojacReferences.CojacReferencesBuilder;

import java.beans.IntrospectionException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

final class ApplicationStarter {
    private static final Pattern CLASSPATH_PATTERN = Pattern.compile(";");
    private final Args args;

    ApplicationStarter(Args args) {
        super();

        this.args = args;
    }

    public void start() {
        if (args.getFiles().isEmpty()) {
            System.err.println("You must specify one file to start");

            return;
        }

        String applicationFile = args.getFiles().get(0);
        try {
            if (applicationFile.endsWith(".jar")) {
                launchJar(applicationFile);
            } else {
                launchClass(applicationFile);
            }
        } catch (MalformedURLException e) {
            System.err.println("Unable to start the application");
        } catch (IntrospectionException e) {
            System.err.println("Unable to start the application : Due to " + e.getMessage());
        }
    }

    private void launchJar(String jarFile) throws IntrospectionException {
        try {
            launchClass(getMainClass(jarFile),
                new File(System.getProperty("user.dir")).toURI().toURL(),
                new File(jarFile).toURI().toURL());
        } catch (IOException e) {
            System.err.println("Unable to run JAR");
        }
    }

    private void launchClass(String className) throws MalformedURLException, IntrospectionException {
        launchClass(className, new File(System.getProperty("user.dir")).toURI().toURL());
    }

    private void launchClass(String className, URL... urls) throws MalformedURLException, IntrospectionException {
        URL[] classpath = parseClassPath(args, urls);

        for (URL url : classpath) {
            addURLToSystemClassLoader(url);
        }

        final ClassLoader loader = new CojacClassLoader(classpath, new CojacReferencesBuilder(args));

        try {
            Thread.currentThread().setContextClassLoader(loader);

            Class<?> launcher = loader.loadClass(className);

            Class<?>[] argTypes = {String[].class};

            Method method = launcher.getMethod("main", argTypes);
            method.setAccessible(true);

            Object[] passedArgv = {args.getAppArgs()};

            method.invoke(null, passedArgv);
        } catch (ClassNotFoundException e) {
            System.err.println("The main class of the JAR has not been found. ");
        } catch (NoSuchMethodException e) {
            System.err.println("The main class has no main methods. ");
        } catch (InvocationTargetException e) {
            System.err.println("Error invoking the main method due to " + e);
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            System.err.println("Unable to access the main method due to " + e);
            e.printStackTrace();
        }
    }

    private static URL[] parseClassPath(Args args, URL[] urls) throws MalformedURLException {
        if (!args.isSpecified(Arg.CLASSPATH)) {
            return urls;
        }

        String[] parsed = CLASSPATH_PATTERN.split(args.getValue(Arg.CLASSPATH));
        URL[] classpath = new URL[urls.length + parsed.length];
        int i = 0;
        for (; i < urls.length; i++) {
            classpath[i] = urls[i];
        }
        for (int j = 0; j < parsed.length; j++) {
            classpath[i + j] = new File(parsed[j]).toURI().toURL();
        }
        return classpath;
    }

    private static String getMainClass(String jarFile) throws IOException {
        JarFile file = new JarFile(new File(jarFile));
        return file.getManifest().getMainAttributes().getValue("Main-Class");
    }

    private static void addURLToSystemClassLoader(URL url) throws IntrospectionException {
        URLClassLoader systemClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();

        try {
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
            method.setAccessible(true);
            method.invoke(systemClassLoader, url);
        } catch (Throwable t) {
            IntrospectionException e = new IntrospectionException("Error when adding url to system ClassLoader ");
            e.initCause(t);
            throw e;
        }
    }
}