package ch.eiafr.cojac.unit;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;

public abstract class AgentTest extends AbstractFullTests {

	private Tests tests;
	public static Instrumentation instrumentation; // Do not make it final or
													// inline it. Do not
													// rename it !

	protected AgentTest()  {
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
