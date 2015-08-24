/*
 * *
 *    Copyright 2011-2014 Baptiste Wicht, Frédéric Bapst & Romain Monnard
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

import ch.eiafr.cojac.instrumenters.ClassLoaderInstrumenterFactory;
import ch.eiafr.cojac.instrumenters.IOpcodeInstrumenterFactory;
import ch.eiafr.cojac.models.Reactions;
import ch.eiafr.cojac.models.wrappers.BigDecimalDouble;
import ch.eiafr.cojac.models.wrappers.BigDecimalFloat;
import ch.eiafr.cojac.utils.ReflectionUtils;

import org.objectweb.asm.ClassWriter;

import javax.management.*;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationTargetException;
import java.rmi.registry.LocateRegistry;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class CojacReferences {
    public static final String BYPASS_SEPARATOR = ";";

    private final Args args;
    private final InstrumentationStats stats;
    private final IOpcodeInstrumenterFactory factory;
    private final MBeanServer mbServer;
    private final String[] bypassList;

    private final String[] loadedClasses;

    private final String floatWrapper;
    private final String doubleWrapper;
    private final int bigDecimalPrecision;
    private final double stabilityThreshold;
    private final boolean checkUnstableComparisons;

    private CojacReferences(CojacReferencesBuilder builder) {
        this.args = builder.args;
        this.stats = builder.stats;
        this.factory = builder.factory;
        this.mbServer = builder.mbServer;
        this.bypassList = builder.bypassList;
        Reactions.stats = stats;

        this.loadedClasses = builder.loadedClasses;
        this.floatWrapper = builder.floatWrapper;
        this.doubleWrapper = builder.doubleWrapper;
        this.bigDecimalPrecision = builder.bigDecimalPrecision;
        this.stabilityThreshold = builder.stabilityThreshold;
        this.checkUnstableComparisons = builder.checkUnstableComparisons;
    }

    public String getFloatWrapper() {
        return floatWrapper;
    }

    public String getDoubleWrapper() {
        return doubleWrapper;
    }

    public int getBigDecimalPrecision() {
        return bigDecimalPrecision;
    }
    
    public double getStabilityThreshold() {
        return stabilityThreshold;
    }

    public boolean getCheckUnstableComparisons() {
        return checkUnstableComparisons;
    }
    
    public String[] getBypassList() {
        return bypassList;
    }

    public String[] getLoadedClasses() {
        return loadedClasses;
    }

    public Args getArgs() {
        return args;
    }

    public InstrumentationStats getStats() {
        return stats;
    }

    public IOpcodeInstrumenterFactory getOpCodeInstrumenterFactory() {
        return factory;
    }

    public MBeanServer getMBeanServer() {
        return mbServer;
    }

    public boolean hasToBeInstrumented(String className) {
        if (className == null)
            return false; 
        // TODO: null name was for lambdas; is our Agent.extractedClassname() fix sound?
        if (args.isSpecified(Arg.REPLACE_FLOATS)) {
            /* BAP, 24.08.15: this comment is probably no more relevant...
             * to do: Allow instrumented items to be stored in Lists and
             * Collections. We don't want to lose the enriched information when
             * the application uses lists or collections. The proxy has to
             * identify them and decide if a conversion is needed. Currently the
             * classes of lists and collections are identified as belonging to
             * the standard packages. When a method of these classes is called,
             * all instrumented objects are converted (loss of information).
             * 
             * Problem to solve: When java/util/ArrayList or/and
             * java/util/Collection are instrumented the JVM will access to some
             * classes in the agent. If the classpath used is the same as the
             * agent, there is no problem but if this is an other classpath, the
             * JVM will not find the classes inside the agent... Example with
             * Java2Demo:
             * "java.lang.NoClassDefFoundError: ch/eiafr/cojac/models/DoubleNumbers"
             * 
             * One solution could be to make a little jar with classes needed
             * evrywhere next to the agent jar and to add it to the classpath of
             * the bootstrap classloader with the option
             * "-Xbootclasspath/p:path_of_jar" but it is better to have all
             * cojac in only one jar...
             * 
             * Consider asking a question to the ASM mailing list.
             */
            /*
             * if(className.startsWith("java/util/ArrayList")){ // Do not lose
             * the enriched informations when store float or double in an
             * arraylist return true; }
             * if(className.startsWith("java/util/Collection")){ // Do not lose
             * the enriched informations when store float or double in an
             * collection return true; }
             */

            if (loadedClasses != null) {
                for (String passClass : loadedClasses) {
                    if (className.equals(passClass)) {
                        return false;
                    }
                }
            }

        }
        for (String standardPackage : bypassList) {
            if (className.startsWith(standardPackage)) {
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings("unused")
    public static int getFlags(Args args) {
        return ClassWriter.COMPUTE_FRAMES;
    }

    // ========================================================================
    public static final class CojacReferencesBuilder {
        private final Args args;
        private ClassLoader loader;
        private InstrumentationStats stats;
        private IOpcodeInstrumenterFactory factory;
        private MBeanServer mbServer;
        private StringBuilder sbBypassList;
        private String[] bypassList;
        private Splitter splitter;
        private String floatWrapper;
        private String doubleWrapper;
        private int bigDecimalPrecision;
        private double stabilityThreshold;
        private boolean checkUnstableComparisons=true;
        
        private final String[] loadedClasses;

        private static final String STANDARD_PACKAGES = "com.sun.;java.;javax.;sun.;sunw.;"
                + "org.xml.sax.;org.w3c.dom.;org.omg.;org.ietf.jgss.;"
                + "com.apple.;apple.;"
                //+ "java2d.demos.Fonts.AttributedStr$ScalableImageGraphicAttribute;" // BAPST: trick to avoid a "callback" issue
                + "ch.eiafr.cojac.models;"
                + "ch.eiafr.cojac.interval;" + "jdk.internal;" + "org.slf4j";

        public CojacReferencesBuilder(final Args args) {
            this(args, null);
        }

        public CojacReferencesBuilder(final Args args, final String[] loadedClasses) {
            this.args = args;
            this.loader = ClassLoader.getSystemClassLoader();
            this.splitter = new AgentSplitter();
            this.loadedClasses = loadedClasses;
        }

        public CojacReferences build() {
            this.stats = new InstrumentationStats();
            this.sbBypassList = new StringBuilder(STANDARD_PACKAGES);

            if (args.isSpecified(Arg.BIG_DECIMAL_PRECISION)) {
                args.specify(Arg.REPLACE_FLOATS);
                args.setValue(Arg.FLOAT_WRAPPER, "ch.eiafr.cojac.models.wrappers.BigDecimalFloat");
                args.setValue(Arg.DOUBLE_WRAPPER, "ch.eiafr.cojac.models.wrappers.BigDecimalDouble");
            }

            if (args.isSpecified(Arg.INTERVAL)) {
                args.specify(Arg.REPLACE_FLOATS);
                args.setValue(Arg.FLOAT_WRAPPER, "ch.eiafr.cojac.models.wrappers.IntervalFloat");
                args.setValue(Arg.DOUBLE_WRAPPER, "ch.eiafr.cojac.models.wrappers.IntervalDouble");
            }

            if (args.isSpecified(Arg.STOCHASTIC)) {
                args.specify(Arg.REPLACE_FLOATS);
                args.setValue(Arg.FLOAT_WRAPPER, "ch.eiafr.cojac.models.wrappers.StochasticFloat");
                args.setValue(Arg.DOUBLE_WRAPPER, "ch.eiafr.cojac.models.wrappers.StochasticDouble");
            }

            if (args.isSpecified(Arg.AUTOMATIC_DERIVATION)) {
                args.specify(Arg.REPLACE_FLOATS);
                args.setValue(Arg.FLOAT_WRAPPER, "ch.eiafr.cojac.models.wrappers.DerivationFloat");
                args.setValue(Arg.DOUBLE_WRAPPER, "ch.eiafr.cojac.models.wrappers.DerivationDouble");
            }

            if (args.isSpecified(Arg.REPLACE_FLOATS)) { 
                sbBypassList.append(BYPASS_SEPARATOR);  // Only for proxy tests
                sbBypassList.append("ch.eiafr.cojac.unit.replace.FloatProxyNotInstrumented");
                try {
                    prepareReplaceFloats();
                } catch (Exception ex) {
                    Logger.getLogger(CojacReferences.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            // Must be instantiated after the set of the Wrappers
            this.factory = new ClassLoaderInstrumenterFactory(args, stats);

            if (args.isSpecified(Arg.FILTER)) {
                ReflectionUtils.setStaticFieldValue(loader, "ch.eiafr.cojac.models.Reactions", "filtering", true);
            }
            ReflectionUtils.setStaticFieldValue(loader, "ch.eiafr.cojac.models.Reactions", "theReactionType", args.getReactionType());
            ReflectionUtils.setStaticFieldValue(loader, "ch.eiafr.cojac.models.Reactions", "theLogFilename", args.getValue(Arg.LOG_FILE));

            if (args.isOperationEnabled(Arg.JMX_ENABLE)) {
                mbServer = ManagementFactory.getPlatformMBeanServer();
                registerInstrumentationStats(mbServer, stats);
            }

            if (args.isSpecified(Arg.INSTRUMENTATION_STATS)) {
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    @Override public void run() {
                        stats.printInstrumentationStats(args);
                    }
                });
            }

            if (args.isSpecified(Arg.BYPASS) &&
                    args.getValue(Arg.BYPASS).length() > 0) {
                sbBypassList.append(BYPASS_SEPARATOR);
                sbBypassList.append(args.getValue(Arg.BYPASS));
            }

            bypassList = splitter.split(sbBypassList.toString());

            if (args.isSpecified(Arg.RUNTIME_STATS)) {
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    @Override
                    public void run() {
                        InstrumentationStats.printRuntimeStats(args, ReflectionUtils.<Map<String, Long>> getStaticFieldValue(loader, "ch.eiafr.cojac.models.Reactions", "EVENTS"));
                    }
                });
            }

            return new CojacReferences(this);
        }

        private void prepareReplaceFloats() 
                throws ClassNotFoundException, IllegalAccessException, 
                IllegalArgumentException, InvocationTargetException, 
                NoSuchMethodException, SecurityException {
            if (args.isSpecified(Arg.FLOAT_WRAPPER) &&
                    args.getValue(Arg.FLOAT_WRAPPER).length() > 0) {
                floatWrapper = args.getValue(Arg.FLOAT_WRAPPER);
                floatWrapper = afterStandardPrefixExpansion(floatWrapper);                
            } else { // default float wrapper
                floatWrapper = BigDecimalFloat.class.getCanonicalName();
            }

            if (args.isSpecified(Arg.DOUBLE_WRAPPER) &&
                    args.getValue(Arg.DOUBLE_WRAPPER).length() > 0) {
                doubleWrapper = args.getValue(Arg.DOUBLE_WRAPPER);
                doubleWrapper = afterStandardPrefixExpansion(doubleWrapper);                
            } else { // default double wrapper
                doubleWrapper = BigDecimalDouble.class.getCanonicalName();
            }

            /* Get the class used to store global variables. WARNING: This
             * is not the only place to set the values, see method
             * "setGlobalFields" in class "Agent" ! */
            Class<?> clazz =  loader.loadClass("ch.eiafr.cojac.models.FloatReplacerClasses");
            clazz.getMethod("setFloatWrapper", String.class).invoke(clazz, floatWrapper);
            clazz.getMethod("setDoubleWrapper", String.class).invoke(clazz, doubleWrapper);

            if (args.isSpecified(Arg.BIG_DECIMAL_PRECISION)) {
                bigDecimalPrecision = Integer.valueOf(args.getValue(Arg.BIG_DECIMAL_PRECISION));
                clazz.getMethod("setBigDecimalPrecision", int.class).invoke(clazz, bigDecimalPrecision);
            }
            
            if (args.isSpecified(Arg.STABILITY_THRESHOLD)) {
                stabilityThreshold = Double.valueOf(args.getValue(Arg.STABILITY_THRESHOLD));
                clazz.getMethod("setStabilityThreshold", double.class).invoke(clazz, stabilityThreshold);
            }

            if (args.isSpecified(Arg.DISABLE_UNSTABLE_COMPARISONS_CHECK)) {
                checkUnstableComparisons=false;
                clazz.getMethod("setCheckUnstableComparisons", boolean.class).invoke(clazz, checkUnstableComparisons);
            }

        }

        private static String afterStandardPrefixExpansion(String className) {
            if (className.startsWith("cojac."))
                className = className.replace("cojac.", "ch.eiafr.cojac.models.wrappers.");
            try {
                Class.forName(className);
            } catch(ClassNotFoundException e) {
                System.err.println("Warning: class "+className+" not found...");
            }
            return className;
        }
        
        private void registerInstrumentationStats(MBeanServer mbServer, InstrumentationStats stats) {
            try {
                String name = args.getValue(Arg.JMX_NAME);
                int port = Integer.parseInt(args.getValue(Arg.JMX_PORT));
                String host = args.getValue(Arg.JMX_HOST);
                ObjectName statsName = new ObjectName("COJAC:type=InstrumentationMXBean,name=" +
                        name);
                LocateRegistry.createRegistry(port);
                StringBuilder sb = new StringBuilder("/jndi/rmi://").append(host).append(":").append(port);
                sb.append("/").append(name);
                JMXServiceURL url = new JMXServiceURL("rmi", null, 0, sb.toString());
                JMXConnectorServer jmxConnectorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, null, mbServer);
                jmxConnectorServer.start();
                if (!mbServer.isRegistered(statsName)) {
                    mbServer.registerMBean(stats, statsName);
                }
            } catch (MalformedObjectNameException | NullPointerException
                    | InstanceAlreadyExistsException
                    | MBeanRegistrationException | NotCompliantMBeanException
                    | IOException e) {
                e.printStackTrace();
            }
        }
    }

    // ========================================================================
    public interface Splitter {
        String[] split(String list);
    }

    // ========================================================================
    public static class CojacClassLoaderSplitter implements Splitter {
        @Override
        public String[] split(String list) {
            return list.split(BYPASS_SEPARATOR);
        }
    }

    // ========================================================================
    public static final class AgentSplitter extends CojacClassLoaderSplitter {
        @Override
        public String[] split(String list) {
            list = list.replaceAll("\\.", "/");
            return super.split(list);
        }
    }
}
