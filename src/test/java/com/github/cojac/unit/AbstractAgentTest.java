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
