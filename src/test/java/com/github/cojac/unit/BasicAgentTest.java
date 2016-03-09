package com.github.cojac.unit;

import java.lang.instrument.ClassFileTransformer;

import com.github.cojac.Agent;
import com.github.cojac.Arg;
import com.github.cojac.Args;
import com.github.cojac.CojacReferences.CojacReferencesBuilder;

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
