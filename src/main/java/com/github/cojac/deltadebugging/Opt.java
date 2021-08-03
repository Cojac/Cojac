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

package com.github.cojac.deltadebugging;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Cette classe contient l'ensemble des options du programme.
 * 
 * @author Remi Badoud
 *
 */
public enum Opt {

	/**
	 * Define the path to JVM use to execute the specified program.
	 */
	JAVA("java"),
	/**
	 * Define the path to COJAC. Ex : "/path/to/COJAC.jar".
	 */
	COJAC("cojac"),
	/**
	 * Define the classpath use to execute the specified program. The classpath
	 * corresponding to the java option.
	 */
	CLASSPATH("classpath"),
	/**
	 * Define the name of the main class. Ex : "package.MainClass".
	 */
	MAINCLASS("mainclass"),
	/**
	 * Define the executable jar to execute. Ex: "/path/to/executable.jar".
	 */
	JAR("jar"),
	/**
	 * Define the path the XML file that will contains the result of Delta
	 * Debegging. Ex "/path/to/outputfile.xml".
	 */
	BEHAVIOURSFILE("behavioursfile"),
	/**
	 * Define the mode to use (std or wrap)
	 */
	MODE("mode");

	/** Contains all options that will be parse. */
	private static Options	opts;
	/** Option name. */
	private final String	name;
	/** Option value. */
	private String			value;

	/**
	 * Class constructor.
	 * 
	 * @param name The option name.
	 */
	Opt(String name) {
		this.name = name;
	}

	public boolean hasValue() {
		return value != null;
	}

	/**
	 * 
	 * @return The value of the options.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Initialization of options.
	 */
	static {
		opts = new Options();

		Option opt = new Option(JAVA.name, true,
				"Indicates the path to java virtual machine used to execute the program.");
		opt.setRequired(false);
		opt.setArgName("path");
		opts.addOption(opt);

		opt = new Option(COJAC.name, true, "Indicates the path to COJAC jar");
		opt.setRequired(true);
		opt.setArgName("path");
		opts.addOption(opt);

		opt = new Option(CLASSPATH.name, true, "Indicates the classpath used to execute the program.");
		opt.setRequired(false);
		opt.setArgName("path");
		opts.addOption(opt);

		OptionGroup optionGroup = new OptionGroup(); // Mutual exclusivity between MAINCLASS option and JAR option
		opt = new Option(MAINCLASS.name, true, "Indicates the name of the main class, eg \"package.MainClass\".");
		opt.setRequired(true);
		opt.setArgName("classname");
		optionGroup.addOption(opt);

		opt = new Option(JAR.name, true, "Indicates the path to the executable jar.");
		opt.setRequired(true);
		opt.setArgName("path");
		optionGroup.addOption(opt);
		opts.addOptionGroup(optionGroup); // End of group

		opt = new Option(BEHAVIOURSFILE.name, true,
				"Indicates the path to the XML file that will contain the result.");
		opt.setRequired(true);
		opt.setArgName("file");
		opts.addOption(opt);

		opt = new Option(MODE.name, true, "Indicates the mode to use: std or wrap");
		opt.setRequired(true);
		opt.setArgName("mode");
		opts.addOption(opt);

	}

	/**
	 * Parse arguments of the program and initialize option values.
	 * 
	 * @param args Arguments of the program
	 * @throws ParseException If there are any problems while parsing arguments.
	 */
	public static void paseArgs(String[] args) throws ParseException {
		CommandLineParser parser = new BasicParser();
		CommandLine cmdLine;

		cmdLine = parser.parse(opts, args);

		for (Opt opt : Opt.values())
			if (cmdLine.hasOption(opt.name)) opt.value = cmdLine.getOptionValue(opt.name);
	}

	/**
	 * Print the usage of the program (all options and how to use them).
	 */
	public static void printHelp() {
		new HelpFormatter().printHelp("COJACDeltaDebugging", opts, true);
	}

}
