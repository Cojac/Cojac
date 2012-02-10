package ch.eiafr.cojac.models;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;

public class ReflexionAgent {
    public static void premain(String agentArgs, Instrumentation inst) {
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
