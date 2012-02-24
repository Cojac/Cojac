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

import ch.eiafr.cojac.instrumenters.ClassLoaderOpSizeInstrumenterFactory;
import ch.eiafr.cojac.instrumenters.OpCodeInstrumenterFactory;
import ch.eiafr.cojac.instrumenters.SimpleOpCodeFactory;
import ch.eiafr.cojac.models.Reactions;
import ch.eiafr.cojac.reactions.ClassLoaderReaction;
import ch.eiafr.cojac.reactions.Reaction;
import ch.eiafr.cojac.utils.ReflectionUtils;

public final class CojacReferences {
    public static final String BYPASS_SEPARATOR = ";";

    private final Args args;
    private final InstrumentationStats stats;
    private final Reaction reaction;
    private final OpCodeInstrumenterFactory factory;
    private final MBeanServer mbServer;
    private final String[] bypassList;

    private CojacReferences(CojacReferencesBuilder builder) {
        this.args = builder.args;
        this.stats = builder.stats;
        this.reaction = builder.reaction;
        this.factory = builder.factory;
        this.mbServer = builder.mbServer;
        this.bypassList = builder.bypassList;
        Reactions.stats = stats;
    }

    public Args getArgs() {
        return args;
    }

    public InstrumentationStats getStats() {
        return stats;
    }

    public Reaction getReaction() {
        return reaction;
    }

    public OpCodeInstrumenterFactory getOpCodeInstrumenterFactory() {
        return factory;
    }

    public MBeanServer getMBeanServer() {
        return mbServer;
    }

    public boolean hasToBeInstrumented(String className) {
        for (String standardPackage : bypassList) {
            if (className.startsWith(standardPackage)) {
                return false;
            }
        }
        return true;
    }

    public static int getFlags(Args args) {
        return ClassWriter.COMPUTE_MAXS;
        //return ClassWriter.COMPUTE_FRAMES;//return ClassWriter.COMPUTE_MAXS;
    }

    public static final class CojacReferencesBuilder {
        private final Args args;
        private ClassLoader loader;
        private InstrumentationStats stats;
        private Reaction reaction;
        private OpCodeInstrumenterFactory factory;
        private MBeanServer mbServer;
        private StringBuilder sbBypassList;
        private String[] bypassList;
        private Splitter splitter;

        private static final String STANDARD_PACKAGES = "com.sun.;java.;javax.;sun.;sunw.;"
                + "org.xml.sax.;org.w3c.dom.;org.omg.;org.ietf.jgss.;"
                + "com.apple.;apple.;" + "ch.eiafr.cojac.models;" + "org.slf4j";

        public CojacReferencesBuilder() {
            this(new Args());
        }

        public CojacReferencesBuilder(final Args args) {
            this.args = args;
            this.loader = ClassLoader.getSystemClassLoader();
            this.splitter = new CojacClassLoaderSplitter();
        }

        public CojacReferencesBuilder setLoader(final ClassLoader loader) {
            this.loader = loader;
            return this;
        }

        public CojacReferencesBuilder setSplitter(final Splitter splitter) {
            this.splitter = splitter;
            return this;
        }

        public CojacReferences build() {
            this.stats = new InstrumentationStats();
            this.reaction = new ClassLoaderReaction(args);
            this.sbBypassList = new StringBuilder(STANDARD_PACKAGES);

            if (!args.isSpecified(Arg.WASTE_SIZE)) {
                this.factory = new ClassLoaderOpSizeInstrumenterFactory(args, stats);
            } else {
                this.factory = new SimpleOpCodeFactory(args, stats);
            }

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
