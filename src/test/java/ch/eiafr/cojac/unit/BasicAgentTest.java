package ch.eiafr.cojac.unit;

import java.lang.instrument.ClassFileTransformer;

import ch.eiafr.cojac.Agent;
import ch.eiafr.cojac.Arg;
import ch.eiafr.cojac.Args;
import ch.eiafr.cojac.CojacReferences;
import ch.eiafr.cojac.CojacReferences.CojacReferencesBuilder;

public class BasicAgentTest extends AbstractAgentTest {
	public BasicAgentTest() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
		super();

		super.loadOperationsWithAgent(getClassFileTransformer());
	}

	@Override
	protected ClassFileTransformer getClassFileTransformer() {
		Args args = new Args();
		args.specify(Arg.ALL);
		args.specify(Arg.EXCEPTION);
        //args.specify(Arg.INSTRUMENTATION_STATS);

		CojacReferencesBuilder builder = new CojacReferencesBuilder(args);

		return new Agent(builder.build());
	}

}
