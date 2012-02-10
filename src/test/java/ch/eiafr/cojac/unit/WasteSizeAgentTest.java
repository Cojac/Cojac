package ch.eiafr.cojac.unit;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.UnmodifiableClassException;

import ch.eiafr.cojac.Agent;
import ch.eiafr.cojac.Arg;
import ch.eiafr.cojac.Args;
import ch.eiafr.cojac.CojacReferences;
import ch.eiafr.cojac.CojacReferences.CojacReferencesBuilder;

public class WasteSizeAgentTest extends AgentTest {

	public WasteSizeAgentTest() throws ClassNotFoundException, IllegalAccessException, InstantiationException, UnmodifiableClassException {
		super();
		super.loadOperationsWithAgent(getClassFileTransformer());
	}

	@Override
	protected ClassFileTransformer getClassFileTransformer() {
		Args args = new Args();
		args.specify(Arg.ALL);
        args.specify(Arg.WASTE_SIZE);
		args.specify(Arg.EXCEPTION);
		CojacReferencesBuilder builder = new CojacReferencesBuilder(args);
		builder.setSplitter(new CojacReferences.AgentSplitter());
		return new Agent(builder.build());
	}

}
