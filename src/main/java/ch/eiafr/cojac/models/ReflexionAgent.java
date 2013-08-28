package ch.eiafr.cojac.models;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;

/** This class makes the cojac-agent-test.jar, used for 
 * benchmarks and junit tests.
 * 
 * @author Vincent Pasquier, frederic.bapst
 *
 */

public class ReflexionAgent {
    public static void premain(@SuppressWarnings("unused") String agentArgs, Instrumentation inst) {
        try {
            Class<?> classz = Class.forName("ch.eiafr.cojac.unit.AgentTest", true, ClassLoader.getSystemClassLoader());
            Field field = classz.getDeclaredField("instrumentation");
            field.setAccessible(true);
            field.set(null, inst);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
