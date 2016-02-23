package com.github.cojac.unit;

import static com.github.cojac.unit.AgentTest.*;

import java.lang.instrument.ClassFileTransformer;

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
		Class<?> operations = classLoader.loadClass("com.github.cojac.unit.SimpleOperations");
		tests = new Tests(operations.newInstance());
		instrumentation.removeTransformer(classFileTransformer);
	}
}
