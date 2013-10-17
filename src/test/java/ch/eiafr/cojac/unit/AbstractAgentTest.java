package ch.eiafr.cojac.unit;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;

import static ch.eiafr.cojac.unit.AgentTest.*;

public abstract class AbstractAgentTest extends AbstractFullTests {

	protected Tests tests;
	protected AgentTest dummyAgentTest=new AgentTest(); // just to ensure AgentTest is loaded
	
	protected AbstractAgentTest()  {
		super();
	}

	protected abstract ClassFileTransformer getClassFileTransformer();

	public Tests getTests() {
		return tests;
	}

	public void loadOperationsWithAgent(ClassFileTransformer classFileTransformer) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		instrumentation.addTransformer(classFileTransformer, true);
		ClassLoader classLoader = ClassLoader.getSystemClassLoader();
		Class<?> operations = classLoader.loadClass("ch.eiafr.cojac.unit.SimpleOperations");
		tests = new Tests(operations.newInstance());
		instrumentation.removeTransformer(classFileTransformer);
	}
}
