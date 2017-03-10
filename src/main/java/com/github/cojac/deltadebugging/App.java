package com.github.cojac.deltadebugging;

import java.util.BitSet;

import org.apache.commons.cli.ParseException;

import com.github.cojac.deltadebugging.utils.*;

/**
 * Main class of the program that launch the Delta Debugging process. Manage the
 * global process.
 * 
 * @author Remi Badoud
 *
 * Example:
 * java -cp d:\Git-MyRepository\cojac\target\cojac.jar com.github.cojac.deltadebugging.App -h
 * 
 */
public class App {

	/**
	 * Main method.
	 * 
	 * @param args Argument of the program see {@link Opt}.
	 */
	public static void main(String[] args) {

		/* Parse arguments and initialize options, if any problems print the
		 * usage */
		try {
			Opt.paseArgs(args);
		} catch (ParseException e) {
		    System.out.println(e.getMessage());
			Opt.printHelp();
			System.exit(0);
		}

		ExecutionUtils eu=ExecutionUtils.INSTANCE;
		BehaviourEditor be = BehaviourEditor.getInstance();

		// TODO : lancer le programme java sans COJAC et vérifier que l'éxécution soit vailde

		/* Execute the java program with COJAC to initialize the file that
		 * contains all instructions and behaviors */
		eu.executeWithCOJACListing();

		/* Execute the java program with COJAC and all behaviours define as
		 * float. If the execution is valid, no need to launch Delta Debugging */
		be.editBehaviours(new BitSet());
		if (eu.executeWithCOJACBehaviours()) {
			System.out.println("Delta Debugging succeed");
			System.exit(0);
		}

		/* Initialize and launch Delta Debugging process on the specified java
		 * program */
		DeltaDebugger ddbg = new DeltaDebugger(BehaviourEditor.getInstance(), eu);
		BitSet b = ddbg.launchDeltaDebugging();
		BehaviourEditor.getInstance().editBehaviours(b);

		// TODO : mettre au propre, ajouter options
//		new Colorizor().colorizeClass(
//				"/Users/remibadoud/Documents/Git_badoud_sp6/cojac/src/test/java/demo/Simpsons.java",
//				"/Users/remibadoud/Desktop/maClass.html", "demo/Simpsons");

		boolean ok=eu.executeWithCOJACBehaviours();
		if (ok) {
			System.out.println("Delta Debugging succeed");
		} else {
			System.out.println("Delta Debugging failed");
		}
	}

}
