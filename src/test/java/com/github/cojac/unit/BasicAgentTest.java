/*
 *    Copyright 2017 Frédéric Bapst et al.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.github.cojac.unit;

import java.lang.instrument.ClassFileTransformer;
import java.lang.reflect.InvocationTargetException;

import com.github.cojac.Agent;
import com.github.cojac.Arg;
import com.github.cojac.Args;
import com.github.cojac.CojacReferences.CojacReferencesBuilder;

public class BasicAgentTest extends AbstractAgentTest {
	public BasicAgentTest() throws ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
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
