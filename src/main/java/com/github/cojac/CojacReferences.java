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

package com.github.cojac;

import org.objectweb.asm.ClassWriter;

import com.github.cojac.instrumenters.ClassLoaderInstrumenterFactory;
import com.github.cojac.instrumenters.IOpcodeInstrumenterFactory;
import com.github.cojac.models.Reactions;
import com.github.cojac.models.behaviours.ConversionBehaviour;
import com.github.cojac.models.behaviours.DoubleIntervalBehaviour;
import com.github.cojac.models.behaviours.PseudoRoundingBehaviour;
import com.github.cojac.models.behaviours.ConversionBehaviour.Conversion;
import com.github.cojac.models.behaviours.PseudoRoundingBehaviour.Rounding;
import com.github.cojac.models.wrappers.BigDecimalDouble;
import com.github.cojac.models.wrappers.BigDecimalFloat;
import com.github.cojac.models.wrappers.WrapperBigDecimalWithNaN;
import com.github.cojac.utils.BehaviourLoader;
import com.github.cojac.utils.InstructionWriter;
import com.github.cojac.utils.ReflectionUtils;


import javax.management.*;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationTargetException;
import java.rmi.registry.LocateRegistry;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class CojacReferences {
    private static final boolean DISPLAY_OPERATION_INFO = true; //change to get info for selective instrumentation
    public static final String BYPASS_SEPARATOR = ";";
    public static final String OPT_IN_CLASS_SEPARATOR = "&";
    public static final String OPT_IN_METHOD_SEPARATOR = ",";
    public static final String OPT_IN_INTERVALS_SEPARATOR = "-";
    public static final String OPT_IN_DESCRIPTOR_START= "{";
    public static final String OPT_IN_DESCRIPTOR_END = "}";
    public static final String OPT_IN_INSTRUCTIONS_SEPARATOR= "_";
    
    private final Args args;
    private final InstrumentationStats stats;
    private final IOpcodeInstrumenterFactory factory;
    private final MBeanServer mbServer;
    private final String[] bypassList;

    private final String[] loadedClasses;

    private final String floatWrapper;
    private final String doubleWrapper;
    private final String ngWrapper;
    private final int bigDecimalPrecision;
    private final double stabilityThreshold;
    private final boolean checkUnstableComparisons;
    private final int arbitraryPrecisionBits;
    private HashMap<String, PartiallyInstrumentable> classesToInstrument = null;
    public String behaviourMapFilePath; // path to XML file that is used to load behaviours
   

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
        this.ngWrapper = builder.ngWrapper;
        this.bigDecimalPrecision = builder.bigDecimalPrecision;
        this.stabilityThreshold = builder.stabilityThreshold;
        this.checkUnstableComparisons = builder.checkUnstableComparisons;
        this.arbitraryPrecisionBits = builder.arbitraryPrecisionBits;
        this.classesToInstrument = builder.classesToInstrument;
        this.behaviourMapFilePath = builder.behaviourMapFilePath;
    }

    public String getNgWrapper() {
        return ngWrapper;
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
    public int getArbitraryPrecisionBits() {
        return arbitraryPrecisionBits;
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
            if(className.startsWith("[")) { // Arrays... think of t.clone()
                return false;
            }

        }
        for (String standardPackage : bypassList) {
            if (className.startsWith(standardPackage)) {
                return false;
            }
        }
        if(classesToInstrument !=null){
            return classesToInstrument.containsKey(className.replace("/","."));
        }
        return true;
    }
    public boolean hasToBeInstrumented(String className, String methodName) {
        if(classesToInstrument != null){
            if (!hasToBeInstrumented(className))
                return false; 
           PartiallyInstrumentable pi =  classesToInstrument.get(className.replace("/", "."));
           if(pi==null)
               return false;
           return pi.instrumentMethod(methodName);
        }
        return true;
    }
    public boolean hasToBeInstrumented(String className, int lineNb, int instructionNb, int opcode) {
        if(DISPLAY_OPERATION_INFO){
            System.out.println("Class "+className+", line: "+lineNb+", instruction offset: "+instructionNb + " opcode: "+opcode);
        }
        if(classesToInstrument != null){
            if (!hasToBeInstrumented(className))
                return false; 
           PartiallyInstrumentable pi =  classesToInstrument.get(className.replace("/", "."));
           if(pi==null)
               return false;
           return  pi.instrumentInstruction(lineNb, instructionNb);
        }
        return true;
    }
    
    @SuppressWarnings("unused")
    public static int getFlags(Args args) {
        return ClassWriter.COMPUTE_FRAMES;
    }

    // ========================================================================
    public static final class CojacReferencesBuilder {
        private String behaviourMapFilePath; // path to XML file that is used to load behaviours
        public HashMap<String, PartiallyInstrumentable> classesToInstrument;
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
        private String ngWrapper;
       private int bigDecimalPrecision;
        private double stabilityThreshold;
        private boolean checkUnstableComparisons=true;
        private int arbitraryPrecisionBits;
        private final String[] loadedClasses;

        private static final String STANDARD_PACKAGES = "com.sun.;java.;javax.;sun.;sunw.;"
                + "org.xml.sax.;org.w3c.dom.;org.omg.;org.ietf.jgss.;"
                + "com.apple.;apple.;"
                + "javafx.;"
                   // BAPST: trick to avoid a "callback" issue. Note that it is
                   // not need to fix that here, we can use the bypass option:
                   //  -b java2d.demos.Fonts.AttributedStr$ScalableImageGraphicAttribute
                + "java2d.demos.Fonts.AttributedStr$ScalableImageGraphicAttribute;" 
                + "com.github.cojac.models;"
                + "com.github.cojac.interval;"
                + "jdk.internal;"
                + "org.slf4j;"
                + "org.apache.commons.math3";

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
            args.setValue(Arg.FLOAT_WRAPPER, "com.github.cojac.models.wrappers.CommonFloat");
            args.setValue(Arg.DOUBLE_WRAPPER, "com.github.cojac.models.wrappers.CommonDouble");

            if (args.isSpecified(Arg.NG_WRAPPER)) {
                args.specify(Arg.REPLACE_FLOATS);
            }
            
            if (args.isSpecified(Arg.BIG_DECIMAL_PRECISION)) {
                args.specify(Arg.REPLACE_FLOATS);
//                args.setValue(Arg.FLOAT_WRAPPER, "com.github.cojac.models.wrappers.BigDecimalFloat");
//                args.setValue(Arg.DOUBLE_WRAPPER, "com.github.cojac.models.wrappers.BigDecimalDouble");
                args.setValue(Arg.NG_WRAPPER, "com.github.cojac.models.wrappers.WrapperBigDecimalWithNaN");

            }

            if (args.isSpecified(Arg.INTERVAL)) {
                args.specify(Arg.REPLACE_FLOATS);
//                args.setValue(Arg.FLOAT_WRAPPER, "com.github.cojac.models.wrappers.IntervalFloat");
//                args.setValue(Arg.DOUBLE_WRAPPER, "com.github.cojac.models.wrappers.IntervalDouble");
                args.setValue(Arg.NG_WRAPPER, "com.github.cojac.models.wrappers.WrapperInterval");
            }

            if (args.isSpecified(Arg.STOCHASTIC)) {
                args.specify(Arg.REPLACE_FLOATS);
//                args.setValue(Arg.FLOAT_WRAPPER, "com.github.cojac.models.wrappers.StochasticFloat");
//                args.setValue(Arg.DOUBLE_WRAPPER, "com.github.cojac.models.wrappers.StochasticDouble");
                args.setValue(Arg.NG_WRAPPER, "com.github.cojac.models.wrappers.WrapperStochastic");
   }

            if (args.isSpecified(Arg.AUTOMATIC_DERIVATION)) {
                args.specify(Arg.REPLACE_FLOATS);
//                args.setValue(Arg.FLOAT_WRAPPER, "com.github.cojac.models.wrappers.DerivationFloat");
//                args.setValue(Arg.DOUBLE_WRAPPER, "com.github.cojac.models.wrappers.DerivationDouble");
                args.setValue(Arg.NG_WRAPPER, "com.github.cojac.models.wrappers.WrapperDerivation");
   }
            
            if (args.isSpecified(Arg.SYMBOLIC)) {
                args.specify(Arg.REPLACE_FLOATS);
//                args.setValue(Arg.FLOAT_WRAPPER, "com.github.cojac.models.wrappers.SymbolicFloat");
//                args.setValue(Arg.DOUBLE_WRAPPER, "com.github.cojac.models.wrappers.SymbolicDouble");
                args.setValue(Arg.NG_WRAPPER, "com.github.cojac.models.wrappers.WrapperSymbolic");
   }
            if (args.isSpecified(Arg.CHEBFUN)) {
                args.specify(Arg.REPLACE_FLOATS);
//                args.setValue(Arg.FLOAT_WRAPPER, "com.github.cojac.models.wrappers.SymbolicFloat");
//                args.setValue(Arg.DOUBLE_WRAPPER, "com.github.cojac.models.wrappers.SymbolicDouble");
                args.setValue(Arg.NG_WRAPPER, "com.github.cojac.models.wrappers.WrapperChebfun");
   }

            if (args.isSpecified(Arg.REPLACE_FLOATS)) { 
                sbBypassList.append(BYPASS_SEPARATOR);  // Only for proxy tests
                sbBypassList.append("com.github.cojac.unit.replace.FloatProxyNotInstrumented");
                try {
                    prepareReplaceFloats();
                } catch (Exception ex) {
                    Logger.getLogger(CojacReferences.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            // Must be instantiated after the set of the Wrappers
            this.factory = new ClassLoaderInstrumenterFactory(args, stats);

            if (args.isSpecified(Arg.FILTER)) {
                ReflectionUtils.setStaticFieldValue(loader, "com.github.cojac.models.Reactions", "filtering", true);
            }
            ReflectionUtils.setStaticFieldValue(loader, "com.github.cojac.models.Reactions", "theReactionType", args.getReactionType());
            ReflectionUtils.setStaticFieldValue(loader, "com.github.cojac.models.Reactions", "theLogFilename", args.getValue(Arg.LOG_FILE));
            if (args.isSpecified(Arg.CALL_BACK)) {
                ReflectionUtils.setStaticFieldValue(loader, "com.github.cojac.models.Reactions", "theCallback", args.getValue(Arg.CALL_BACK));
            }
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
                        InstrumentationStats.printRuntimeStats(args, ReflectionUtils.<Map<String, Long>> getStaticFieldValue(loader, "com.github.cojac.models.Reactions", "EVENTS"));
                    }
                });
            }
            if(args.isSpecified(Arg.INSTRUMENT_SELECTIVELY)){
                System.out.println("CojacReferences.CojacReferencesBuilder.build() : " + args.getValue(Arg.INSTRUMENT_SELECTIVELY));
                this.classesToInstrument = parseClassesIndices(args.getValue(Arg.INSTRUMENT_SELECTIVELY));
            }
            
            if(args.isSpecified(Arg.LISTING_INSTRUCTIONS)){
                System.out.println(args.getValue(Arg.LISTING_INSTRUCTIONS));         
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    @Override public void run() {
                        InstructionWriter.getinstance().writeInstructionDocumentToFile(args);   
                    }
                });
            }
            
            if(args.isSpecified(Arg.LOAD_BEHAVIOUR_MAP)){
                System.out.println(args.getValue(Arg.LOAD_BEHAVIOUR_MAP));
                this.behaviourMapFilePath = args.getValue(Arg.LOAD_BEHAVIOUR_MAP);
                BehaviourLoader.getinstance().initDocument(behaviourMapFilePath);
            }
            
            if(args.isSpecified(Arg.DOUBLE2FLOAT)){
                ConversionBehaviour.setConversion(Conversion.Double2Float);
            }
            if(args.isSpecified(Arg.ROUND_BIASED_UP)){
                PseudoRoundingBehaviour.r = Rounding.UP;
            }
            if(args.isSpecified(Arg.ROUND_BIASED_DOWN)){
                PseudoRoundingBehaviour.r = Rounding.DOWN;
            }
            if(args.isSpecified(Arg.ROUND_BIASED_RANDOM)){
                PseudoRoundingBehaviour.r = Rounding.RANDOM;
            }
            if(args.isSpecified(Arg.ARBITRARY_PRECISION)){
                ConversionBehaviour.setConversion(Conversion.Arbitrary);
                ConversionBehaviour.setSignificativeBits(Integer.valueOf(args.getValue(Arg.ARBITRARY_PRECISION)));
            }
            if(args.isSpecified(Arg.ROUND_NATIVELY_UP)){
                ConversionBehaviour.setConversion(Conversion.NativeRounding);
                ConversionBehaviour.setRoundingMode(ConversionBehaviour.FE_UPWARD);
            }
            if(args.isSpecified(Arg.ROUND_NATIVELY_DOWN)){
                ConversionBehaviour.setConversion(Conversion.NativeRounding);
                ConversionBehaviour.setRoundingMode(ConversionBehaviour.FE_DOWNWARD);
            }
            if(args.isSpecified(Arg.ROUND_NATIVELY_TO_ZERO)){
                ConversionBehaviour.setConversion(Conversion.NativeRounding);
                ConversionBehaviour.setRoundingMode(ConversionBehaviour.FE_TOWARDZERO);
            }
            if(args.isSpecified(Arg.DOUBLE_INTERVAL) ){
                DoubleIntervalBehaviour.setThreshold(Double.parseDouble(args.getValue(Arg.STABILITY_THRESHOLD)));
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
            
            if (args.isSpecified(Arg.NG_WRAPPER) &&
                    args.getValue(Arg.NG_WRAPPER).length() > 0) {
                ngWrapper = args.getValue(Arg.NG_WRAPPER);
                ngWrapper = afterStandardPrefixExpansion(ngWrapper);
            } else { // default wrapper
                ngWrapper = WrapperBigDecimalWithNaN.class.getCanonicalName();
            }
            /* Get the class used to store global variables. WARNING: This
             * is not the only place to set the values, see method
             * "setGlobalFields" in class "Agent" ! */
            Class<?> clazz =  loader.loadClass("com.github.cojac.models.FloatReplacerClasses");
            clazz.getMethod("setFloatWrapper", String.class).invoke(clazz, floatWrapper);
            clazz.getMethod("setDoubleWrapper", String.class).invoke(clazz, doubleWrapper);
            clazz.getMethod("setNgWrapper", String.class).invoke(clazz, ngWrapper);

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
            String prefix="cojac.";
            if (className.startsWith(prefix))
                className = className.replace(prefix, "com.github.cojac.models.wrappers.");
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
        private static HashMap<String, PartiallyInstrumentable> parseClassesIndices(String arg){
            HashMap<String, PartiallyInstrumentable> classesToInstrument = new HashMap<String, PartiallyInstrumentable>();
            CojacClassToInstrumentSplitter sp = new CojacClassToInstrumentSplitter();
            if(arg != null){
                String[] classes = sp.split(arg.replaceAll("\\s+",""));
                
                for(String classe: classes){
                     if(classe.contains(OPT_IN_DESCRIPTOR_START)){
                        int openingCurlyIndex = classe.indexOf(OPT_IN_DESCRIPTOR_START);
                        int closingCurlyIndex = classe.indexOf(OPT_IN_DESCRIPTOR_END);
                        if(openingCurlyIndex+1 < closingCurlyIndex){
                            String name = classe.substring(0, openingCurlyIndex).replace("/", ".");
                            String tmp = classe.substring(openingCurlyIndex+1, closingCurlyIndex);
                            String[] methodsLinesInstructions = tmp.split(OPT_IN_INSTRUCTIONS_SEPARATOR);
                            if(methodsLinesInstructions.length ==0){
                                continue; // I'm kind enough to test "class{_}"...
                            }
                            PartiallyInstrumentable pi;
                            if(tmp.startsWith(OPT_IN_INSTRUCTIONS_SEPARATOR)){
                                
                                pi = new ClassPartiallyInstrumented(name,"" ,methodsLinesInstructions[0]);
                            }else if(tmp.endsWith(OPT_IN_INSTRUCTIONS_SEPARATOR) || !classe.contains(OPT_IN_INSTRUCTIONS_SEPARATOR)){
                                pi = new ClassPartiallyInstrumented(name,methodsLinesInstructions[0],"");
                            }else{
                                pi = new ClassPartiallyInstrumented(name, methodsLinesInstructions[0],methodsLinesInstructions[1]);
                            }
                            classesToInstrument.put(name,pi);
                        }//else: nothing to instrument
                    }else{
                        classesToInstrument.put(classe.replace("/", "."), new ClassFullyInstrumented(classe.replace("/", ".")));
                    }
                    
                }
            }
            return classesToInstrument;
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
    public static class CojacClassToInstrumentSplitter implements Splitter {
        @Override
        public String[] split(String list) {
            return list.split(OPT_IN_CLASS_SEPARATOR);
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
    
    public interface PartiallyInstrumentable{
        public boolean instrumentMethod(String methodName); 
        public boolean instrumentLine(int lineNb);
        public boolean instrumentInstruction(int lineNb, int instructionNumber);
    }
    public static class ClassFullyInstrumented implements PartiallyInstrumentable{
        public String name;
        public ClassFullyInstrumented(String name) {
            this.name = name;
        }
        @Override
        public boolean instrumentMethod(@SuppressWarnings("unused") String methodName) {
            return true;
        }
        @Override
        public boolean instrumentLine(@SuppressWarnings("unused") int lineNb) {
            return true;
        }
        public String toString(){
            return name;
        }
        @Override
        public boolean instrumentInstruction(@SuppressWarnings("unused") int lineNb, @SuppressWarnings("unused") int instructionNumber) {
            return true;
        }
    }
    
    public static class ClassPartiallyInstrumented implements PartiallyInstrumentable{
        private String name;
        private HashSet<String> methods = new HashSet<String>();
        private BitSet lines = new BitSet(256);
        private BitSet instructions = new BitSet(256);
        public ClassPartiallyInstrumented(String className,String methodsOrLines,String instructions) {
            this.name = className;
            String[] methodOrLine = methodsOrLines.split(OPT_IN_METHOD_SEPARATOR);
            if(!methodsOrLines.equals(""))
            for(String s: methodOrLine){
                if(Character.isDigit(s.charAt(0))){/*Check for lineNb or line range*/
                    String[] lineNb = s.split(OPT_IN_INTERVALS_SEPARATOR);
                    if(lineNb.length ==1){//line number
                        lines.set(Integer.parseInt(lineNb[0]));
                    }else if(lineNb.length ==2){//line range
                        int from = Integer.parseInt(lineNb[0]);
                        int to = Integer.parseInt(lineNb[1]);
                        lines.set(from, to+1);
                    }else{//what?
                        throw new RuntimeException("Error parsing methods to instrument for class "+className);
                    }
                }else{
                    methods.add(s);
                }
            }
            String[] instr = instructions.split(OPT_IN_METHOD_SEPARATOR);
            if(!instructions.equals(""))
            for(String s: instr){
                String[] lineNb = s.split(OPT_IN_INTERVALS_SEPARATOR);
                if(lineNb.length ==1){//instruction number
                    this.instructions.set(Integer.parseInt(lineNb[0]));
                }else if(lineNb.length ==2){//line range
                    int from = Integer.parseInt(lineNb[0]);
                    int to = Integer.parseInt(lineNb[1]);
                    this.instructions.set(from, to+1);
                }else{//what?
                    throw new RuntimeException("Error parsing instructions to instrument for class "+className);
                }
            }
        }
        public String toString(){
            String s = ""+name;
            Iterator<String> itr = methods.iterator();
            int i = 0;
            while(itr.hasNext()){
                s = s + "Method "+ (++i)+"= \""+  itr.next()+"\"\n"; 
            }
            s = s + "Lines: "+ lines.toString();
            s = s + "\nInstructions: "+ instructions.toString();
            return s;
        }
        @Override
        public boolean instrumentMethod(String methodName) {
            return methods.contains(methodName);
        }
        @Override
        public boolean instrumentLine(int lineNb) {
            return lines.get(lineNb);
        }
        @Override
        public boolean instrumentInstruction(int lineNb, @SuppressWarnings("unused") int instructionNumber) {
            return instrumentLine(lineNb)|| instructions.get(instructionNumber);
        }
    }
    
}
