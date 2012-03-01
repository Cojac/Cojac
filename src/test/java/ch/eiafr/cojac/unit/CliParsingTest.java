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

package ch.eiafr.cojac.unit;

import ch.eiafr.cojac.Arg;
import ch.eiafr.cojac.Args;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;


public class CliParsingTest {
    @Test
    public void cli05_help() {
        String[] args = {"-h"};
        String[] files = {};
        String[] appArgs = {};
        Args a = new Args();
        boolean b = a.parse(args);
        assertTrue(b);
        String[] effFiles = a.getFiles().toArray(new String[0]);
        String[] effAppArgs = a.getAppArgs();
        assertTrue(Arrays.equals(files, effFiles));
        assertTrue(Arrays.equals(appArgs, effAppArgs));
    }

	@Test
	public void cli07_jmx() {
		String[] args = { 
                "-jmxenable", 
		        "-jmxhost", "127.0.0.1", 
		        "-jmxport", "1234", 
		        "-jmxname", "COJACTEST", "a.jar"
		        };
		String[] files = { "a.jar" };
		String[] appArgs = {};
		Args a = new Args();
		boolean b = a.parse(args);
		assertTrue(b);
		String[] effFiles = a.getFiles().toArray(new String[0]);
		String[] effAppArgs = a.getAppArgs();
		assertTrue(Arrays.equals(files, effFiles));
		assertTrue(Arrays.equals(appArgs, effAppArgs));
		assertTrue(a.getValue(Arg.JMX_HOST).equals("127.0.0.1"));
		assertTrue(a.getValue(Arg.JMX_PORT).equals("1234"));
		assertTrue(a.getValue(Arg.JMX_NAME).equals("COJACTEST"));
	}
}
