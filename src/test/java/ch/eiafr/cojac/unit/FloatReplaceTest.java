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
import static ch.eiafr.cojac.unit.AgentTest.*;

public class FloatReplaceTest {
    protected AgentTest dummyAgentTest=new AgentTest(); // just to ensure AgentTest is loaded

    Class<?> tinyExample;
    
	public FloatReplaceTest() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        super();
        loadOperationsWithAgent(getClassFileTransformer());
    }

    protected ClassFileTransformer getClassFileTransformer() {
        Args args = new Args();
        args.specify(Arg.ALL);
        args.specify(Arg.EXCEPTION);
        args.specify(Arg.REPLACE_FLOATS);
        args.specify(Arg.VERBOSE);
        args.specify(Arg.INSTRUMENTATION_STATS);

        CojacReferencesBuilder builder = new CojacReferencesBuilder(args);
        builder.setSplitter(new CojacReferences.AgentSplitter());

        return new Agent(builder.build());
    }

	public void loadOperationsWithAgent(ClassFileTransformer classFileTransformer) 
	         throws ClassNotFoundException, InstantiationException, IllegalAccessException {
	    instrumentation.addTransformer(classFileTransformer, true);
	    try {
	        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
	        tinyExample = classLoader.loadClass("ch.eiafr.cojac.unit.TinyFloatExample");
	    } finally {
	        instrumentation.removeTransformer(classFileTransformer);
	    }
	}
    
    @Test 
    public void testReplaceFloat() throws Exception {
        System.out.println(tinyExample);
        if (tinyExample==null) return;
        Method m = tinyExample.getMethod("go");
        if (m==null) return;
        m.invoke(null);
    }
}
