package ch.eiafr.cojac.unit;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;

import org.junit.Test;

import ch.eiafr.cojac.Agent;
import ch.eiafr.cojac.Arg;
import ch.eiafr.cojac.Args;
import ch.eiafr.cojac.CojacReferences;
import ch.eiafr.cojac.CojacReferences.CojacReferencesBuilder;

public class FloatReplaceTest extends AgentTest {

    Class<?> tinyExample;
    
	public FloatReplaceTest() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        super();
        loadOperationsWithAgent(getClassFileTransformer());
    }

    @Override
    protected ClassFileTransformer getClassFileTransformer() {
        Args args = new Args();
        args.specify(Arg.ALL);
        args.specify(Arg.EXCEPTION);
        args.specify(Arg.REPLACE_FLOATS);

        CojacReferencesBuilder builder = new CojacReferencesBuilder(args);
        builder.setSplitter(new CojacReferences.AgentSplitter());

        return new Agent(builder.build());
    }

    @Override
	public void loadOperationsWithAgent(ClassFileTransformer classFileTransformer) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		instrumentation.addTransformer(classFileTransformer, true);
		ClassLoader classLoader = ClassLoader.getSystemClassLoader();
		
		Class<?> operations = classLoader.loadClass("ch.eiafr.cojac.unit.SimpleOperations");
		super.tests = new Tests(operations.newInstance());
		
		tinyExample = classLoader.loadClass("ch.eiafr.cojac.unit.TinyFloatExample");
		
		instrumentation.removeTransformer(classFileTransformer);
	}
    
    @Test public void testReplaceFloat() throws Exception {
        System.out.println(tinyExample);
        if (tinyExample==null) return;
        Method m = tinyExample.getMethod("go");
        if (m==null) return;
        m.invoke(null);
    }
}
