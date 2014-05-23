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

import ch.eiafr.cojac.models.FloatReplacerClasses;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.rmi.registry.LocateRegistry;
import java.util.Map;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import org.objectweb.asm.ClassWriter;

import ch.eiafr.cojac.instrumenters.ClassLoaderInstrumenterFactory;
import ch.eiafr.cojac.instrumenters.IOpcodeInstrumenterFactory;
import ch.eiafr.cojac.models.DoubleWrapper;
import ch.eiafr.cojac.models.FloatWrapper;
import ch.eiafr.cojac.models.Reactions;
import ch.eiafr.cojac.reactions.ClassLoaderReaction;
import ch.eiafr.cojac.reactions.IReaction;
import ch.eiafr.cojac.utils.ReflectionUtils;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class CojacReferences {
    public static final String BYPASS_SEPARATOR = ";";

    private final Args args;
    private final InstrumentationStats stats;
    private final IReaction reaction;
    private final IOpcodeInstrumenterFactory factory;
    private final MBeanServer mbServer;
    private final String[] bypassList;
    
    private final String[] loadedClasses;

	private final String floatWrapper;
	private final String doubleWrapper;
	
    private CojacReferences(CojacReferencesBuilder builder) {
        this.args = builder.args;
        this.stats = builder.stats;
        this.reaction = builder.reaction;
        this.factory = builder.factory;
        this.mbServer = builder.mbServer;
        this.bypassList = builder.bypassList;
        Reactions.stats = stats;
        
        this.loadedClasses = builder.loadedClasses;
		this.floatWrapper = builder.floatWrapper;
		this.doubleWrapper = builder.doubleWrapper;
    }

	public String getFloatWrapper() {
		return floatWrapper;
	}

	public String getDoubleWrapper() {
		return doubleWrapper;
	}

	public String[] getBypassList(){
		return bypassList;
	}
	
    public String[] getLoadedClasses(){
        return loadedClasses;
    }
    
    public Args getArgs() {
        return args;
    }

    public InstrumentationStats getStats() {
        return stats;
    }

    public IReaction getReaction() {
        return reaction;
    }

    public IOpcodeInstrumenterFactory getOpCodeInstrumenterFactory() {
        return factory;
    }

    public MBeanServer getMBeanServer() {
        return mbServer;
    }

    public boolean hasToBeInstrumented(String className) {
        // TODO: deal with native methods
        if (args.isSpecified(Arg.REPLACE_FLOATS) && loadedClasses != null){
            for (String passClass : loadedClasses) {
                if(className.equals(passClass)){
                    return false;
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
        //return ClassWriter.COMPUTE_FRAMES;//return ClassWriter.COMPUTE_MAXS;
    }

    public static final class CojacReferencesBuilder {
        private final Args args;
        private ClassLoader loader;
        private InstrumentationStats stats;
        private IReaction reaction;
        private IOpcodeInstrumenterFactory factory;
        private MBeanServer mbServer;
        private StringBuilder sbBypassList;
        private String[] bypassList;
        private Splitter splitter;
		private String floatWrapper;
		private String doubleWrapper;
        
        private final String[] loadedClasses;

        private static final String STANDARD_PACKAGES = "com.sun.;java.;javax.;sun.;sunw.;"
                + "org.xml.sax.;org.w3c.dom.;org.omg.;org.ietf.jgss.;"
                + "com.apple.;apple.;" + "ch.eiafr.cojac.models;" + "org.slf4j";

        public CojacReferencesBuilder() {
            this(new Args(), null);
        }
        
        public CojacReferencesBuilder(final Args args) {
            this(args, null);
        }

        public CojacReferencesBuilder(final Args args, final String[] loadedClasses) {
            this.args = args;
            this.loader = ClassLoader.getSystemClassLoader();
            this.splitter = new CojacClassLoaderSplitter();
            this.loadedClasses = loadedClasses;
        }

        public CojacReferencesBuilder setSplitter(final Splitter splitter) {
            this.splitter = splitter;
            return this;
        }

        public CojacReferences build() {
            this.stats = new InstrumentationStats();
            this.reaction = new ClassLoaderReaction(args);
            this.sbBypassList = new StringBuilder(STANDARD_PACKAGES);

            
			
			if(args.isSpecified(Arg.REPLACE_FLOATS)){ // Only for proxy tests
				sbBypassList.append(BYPASS_SEPARATOR);
                sbBypassList.append("ch.eiafr.cojac.unit.replace.FloatProxyNotInstrumented");
			
				Class clazz = null;
				try {
					clazz = loader.loadClass("ch.eiafr.cojac.models.FloatReplacerClasses");
				} catch (ClassNotFoundException ex) {
					Logger.getLogger(CojacReferences.class.getName()).log(Level.SEVERE, null, ex);
				}
				
				if(args.isSpecified(Arg.FLOAT_WRAPPER) && args.getValue(Arg.FLOAT_WRAPPER).length()>0){
					floatWrapper = args.getValue(Arg.FLOAT_WRAPPER);
					if(floatWrapper.startsWith("cojac."))
						floatWrapper = floatWrapper.replace("cojac.", "ch.eiafr.cojac.models.wrappers.");
					//FloatReplacerClasses.setFloatWrapper(floatWrapper);
					try {
						clazz.getMethod("setFloatWrapper", String.class).invoke(clazz, floatWrapper);
					} catch (Exception ex) {
						Logger.getLogger(CojacReferences.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
				else{
					try {
						clazz.getMethod("setFloatWrapper", String.class).invoke(clazz, FloatWrapper.class.getCanonicalName());
					} catch (Exception ex) {
						Logger.getLogger(CojacReferences.class.getName()).log(Level.SEVERE, null, ex);
					}
				}

				if(args.isSpecified(Arg.DOUBLE_WRAPPER) && args.getValue(Arg.DOUBLE_WRAPPER).length()>0){
					doubleWrapper = args.getValue(Arg.DOUBLE_WRAPPER);
					if(doubleWrapper.startsWith("cojac."))
						doubleWrapper = doubleWrapper.replace("cojac.", "ch.eiafr.cojac.models.wrappers.");
					try {
						clazz.getMethod("setDoubleWrapper", String.class).invoke(clazz, doubleWrapper);
					} catch (Exception ex) {
						Logger.getLogger(CojacReferences.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
				else{
					try {
						clazz.getMethod("setDoubleWrapper", String.class).invoke(clazz, DoubleWrapper.class.getCanonicalName());
					} catch (Exception ex) {
						Logger.getLogger(CojacReferences.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
			}
			
			this.factory = new ClassLoaderInstrumenterFactory(args, stats); // Must be instantiate after the set of the Wrappers
			
            if (args.isSpecified(Arg.FILTER)) {
                ReflectionUtils.setStaticFieldValue(loader, "ch.eiafr.cojac.models.Reactions", "filtering", true);
            }

            if (args.isOperationEnabled(Arg.JMX_ENABLE)) {
                mbServer = ManagementFactory.getPlatformMBeanServer();
                registerInstrumentationStats(mbServer, stats);
            }

            if (args.isSpecified(Arg.INSTRUMENTATION_STATS)) {
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    @Override
                    public void run() {
                        stats.printInstrumentationStats(args);
                    }
                });
            }

            if (args.isSpecified(Arg.BYPASS) && args.getValue(Arg.BYPASS).length()>0) {
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

        private void registerInstrumentationStats(MBeanServer mbServer, InstrumentationStats stats) {
            try {
                String name = args.getValue(Arg.JMX_NAME);
                int port = Integer.parseInt(args.getValue(Arg.JMX_PORT));
                String host = args.getValue(Arg.JMX_HOST);
                ObjectName statsName = new ObjectName("COJAC:type=InstrumentationMXBean,name=" + name);
                LocateRegistry.createRegistry(port);
                StringBuilder sb = new StringBuilder("/jndi/rmi://").append(host).append(":").append(port);
                sb.append("/").append(name);
                JMXServiceURL url = new JMXServiceURL("rmi", null, 0, sb.toString());
                JMXConnectorServer jmxConnectorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, null, mbServer);
                jmxConnectorServer.start();
                if (!mbServer.isRegistered(statsName)) {
                    mbServer.registerMBean(stats, statsName);
                }
            } catch (MalformedObjectNameException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (InstanceAlreadyExistsException e) {
                e.printStackTrace();
            } catch (MBeanRegistrationException e) {
                e.printStackTrace();
            } catch (NotCompliantMBeanException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public interface Splitter {
        String[] split(String list);
    }

    public static class CojacClassLoaderSplitter implements Splitter {
        @Override
        public String[] split(String list) {
            return list.split(BYPASS_SEPARATOR);
        }
    }

    public static final class AgentSplitter extends CojacClassLoaderSplitter {
        @Override
        public String[] split(String list) {
            list = list.replaceAll("\\.", "/");
            return super.split(list);
        }
    }
}
