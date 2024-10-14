/*
* *
*    Copyright 2011 Baptiste Wicht & Frédéric Bapst
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
*
*/

package com.github.cojac.unit;

import org.junit.Test;

import com.github.cojac.Arg;
import com.github.cojac.Args;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class CliParsingTest {
    @Test
    public void cli05_help() {
        String[] args = {"-h"};
        Args a = new Args();
        boolean b = a.parse(args);
        assertTrue(b);
    }

	@Test
	public void cli07_jmx() {
		String[] args = { 
                "-jmxenable", 
		        "-jmxhost", "127.0.0.1", 
		        "-jmxport", "1234", 
		        "-jmxname", "COJACTEST"
		        };
		Args a = new Args();
		boolean b = a.parse(args);
		assertTrue(b);
    assertEquals("127.0.0.1", a.getValue(Arg.JMX_HOST));
    assertEquals("1234", a.getValue(Arg.JMX_PORT));
    assertEquals("COJACTEST", a.getValue(Arg.JMX_NAME));
	}
}
