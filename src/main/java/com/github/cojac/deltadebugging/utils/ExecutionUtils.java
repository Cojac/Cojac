package com.github.cojac.deltadebugging.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.github.cojac.deltadebugging.Opt;

// singleton-via-enum idiom
public enum ExecutionUtils implements Executor {
    INSTANCE;

	private static final String		JAVA_DEFAULT				= "java";
	private static final String		AGENT_OPTION				= "-javaagent:";
	private static final String		COJAC_START_OPTION			= "=\" ";
	private static final String		COJAC_END_OPTION			= " \"";
	private static final String		COJAC_OPTION_BEHAVIOUR		= "-BD2F -Lbm ";
	private static final String		COJAC_OPTION_LISTING		= "-BD2F -Li ";
	private static final String		COJAC_OPTION_WRAP_PBLOAD	= "-Rpbload ";
	private static final String		COJAC_OPTION_WRAP_PBL		= "-Rpbl ";
	private static final String		JAR_OPTION					= "-jar";
	private static final String		CLASSPATH_OPTION			= "-classpath";

	private String[]				cmdExecuteWithBehaviours;
	private String[]				cmdExecuteListing;

	/**
	 * Class constructor.
	 */
	private ExecutionUtils() {
		initCmdExecuteWithBehaviours();
		initCmdExecuteListing();
	}

	/**
	 * 
	 * @return The singleton instance of the class.
	 */
	public static ExecutionUtils getinstance() {
		return INSTANCE;
	}

	/**
	 * Initialize the command that execute the program with COJAC and the
	 * behaviour.
	 */
	private void initCmdExecuteWithBehaviours() {
		String[] tmp;

		if (Opt.MODE.getValue().equals("wrap"))
			tmp = new String[] { Opt.JAVA.hasValue() ? Opt.JAVA.getValue() : JAVA_DEFAULT,
					AGENT_OPTION + Opt.COJAC.getValue() + COJAC_START_OPTION + COJAC_OPTION_WRAP_PBLOAD
							+ Opt.BEHAVIOURSFILE.getValue() + COJAC_END_OPTION,
					Opt.CLASSPATH.hasValue() ? CLASSPATH_OPTION : null, Opt.CLASSPATH.getValue(),
					Opt.JAR.hasValue() ? JAR_OPTION : null, Opt.JAR.getValue(), Opt.MAINCLASS.getValue() };
		else tmp = new String[] { Opt.JAVA.hasValue() ? Opt.JAVA.getValue() : JAVA_DEFAULT,
				AGENT_OPTION + Opt.COJAC.getValue() + COJAC_START_OPTION + COJAC_OPTION_BEHAVIOUR
						+ Opt.BEHAVIOURSFILE.getValue() + COJAC_END_OPTION,
				Opt.CLASSPATH.hasValue() ? CLASSPATH_OPTION : null, Opt.CLASSPATH.getValue(),
				Opt.JAR.hasValue() ? JAR_OPTION : null, Opt.JAR.getValue(), Opt.MAINCLASS.getValue() };

		cmdExecuteWithBehaviours = cleanCmd(tmp);
	}

	/**
	 * Initialize the command that execute the program with COJAC listing.
	 */
	private void initCmdExecuteListing() {
	  ArrayList<String> l=new ArrayList<>();
	  l.add(Opt.JAVA.hasValue() ? Opt.JAVA.getValue() : JAVA_DEFAULT);
	  String optionForReferencing = COJAC_OPTION_LISTING;
	  if (Opt.MODE.getValue().equals("wrap")) 
	    optionForReferencing = COJAC_OPTION_WRAP_PBL;
	  String agentOpt=AGENT_OPTION + Opt.COJAC.getValue() + COJAC_START_OPTION;
	  agentOpt +=  optionForReferencing + Opt.BEHAVIOURSFILE.getValue();
	  agentOpt +=  COJAC_END_OPTION;
	  l.add(agentOpt);
	  if(Opt.CLASSPATH.hasValue()) {
        l.add(CLASSPATH_OPTION); 
	    l.add(Opt.CLASSPATH.getValue());
	  }
      if(Opt.JAR.hasValue()) {
        l.add(JAR_OPTION); 
        l.add(Opt.JAR.getValue());
      }
	  l.add(Opt.MAINCLASS.getValue());
	  cmdExecuteListing = l.toArray(new String[l.size()]);
	}

	/**
	 * Clean the command. Suppress all NULL occurrences.
	 * 
	 * @param cmd Command to clean.
	 * @return Cleaned command.
	 */
	private String[] cleanCmd(String[] cmd) {
		int cmdLength = 0;
		for (String param : cmd)
			if (param != null) cmdLength++;

		String[] cleanCmd = new String[cmdLength];

		int index = 0;
		for (String param : cmd)
			if (param != null) cleanCmd[index++] = param;
		return cleanCmd;
	}

	/**
	 * Execute the program with COJAC and behaviour.
	 * 
	 * @return True if the program execution considered as valid (if the program
	 *         exit value is zero)
	 */
	public boolean executeWithCOJACBehaviours() {
		boolean success = false;
		try {
			Process p = Runtime.getRuntime().exec(cmdExecuteWithBehaviours);
			p.waitFor();
			printStream(p);
			if (p.exitValue() == 0) success = true;
			p.destroy();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return success;
	}

	/**
	 * Execute the program with COJAC and listing option. Used for initialize
	 * instruction file.
	 * 
	 * @return
	 */
	public void executeWithCOJACListing() {
		try {
			Process p = Runtime.getRuntime().exec(cmdExecuteListing);
			p.waitFor();
			printStream(p);
			p.destroy();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Print the standard OutpoutStrem of the program.
	 * 
	 * @param p Process corresponding to java program.
	 * @throws IOException
	 */
	private void printStream(Process p) throws IOException {
		InputStream in = p.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String line;
		while ((line = br.readLine()) != null) {
			System.out.println(line);
		}
		br.close();
		in.close();

	}

}
