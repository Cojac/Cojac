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
 * ----------------BAPST-------------------
 * TO BE DOCUMENTED... General idea: 
 * 
 * You have a computation code using "double" and giving accurate results.
 * You want to know if parts of the computation could be performed in "float"
 * without loosing to much accuracy.
 * 
 * 1) Prepare a "judge", a program that tests on some data that the computation 
 *    is accurate enough (exit code=0) or not (exit code != 0). 
 *    The testing code should NOT use doubles (use BigDecimal instead). 
 *    Examples: ConFrac, Simpsons...
 *              See in com.github.cojac.misctests.deltaDebugging
 *              
 * 2) Run your "judge" under Cojac, with the options: 
 * 
 *      java -jar:cojac.jar=" -Bdaf -Bddwrite /path/to/Simpsons.xml " dd.Simpsons
 *      
 *    As everything is computed as "float", it typically fails. An XML file
 *    holding every "double" statements is produced
 *    
 * 3) Run this Delta-Debugging App: 
 * 
 *      java -cp cojac.jar com.github.cojac.deltadebugging.App 
 *               -behavioursfile /path/to/Simpsons.xml 
 *               -cojac /path/to/cojac.jar 
 *               -mode std 
 *               -mainclass dd.Simpsons 
 *               -classpath classPathForSimpsons
 * 
 *    It will launch your judge several times (using dd_min algorithm), to find 
 *    a "minimal" set of "double" statements that satisfies the judge.
 *    The XML file is modified to hold the solution
 *    
 * 4) Run the Colorizor to produce a colorized version of your source code
 * 
 *    java -cp cojac.jar com.github.cojac.deltadebugging.Colorizor
 *                /path/to/Simpsons.xml
 *                /path/to/dd/Simpsons.java
 *                /path/to/Simpsons.java.html
 *                dd/Simpsons                   <-- "internal" classname
 *     
 * 5) Inspect the html result manually to guess where you can act on the source
 *    code, i.e. which variable declarations could be changed from "double" to 
 *    "float"
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
		DeltaDebugger ddbg = new DeltaDebugger(be, eu);
		BitSet b = ddbg.launchDeltaDebugging();
		be.editBehaviours(b);

		// TODO : mettre au propre, ajouter options
//		new Colorizor(null).colorizeClass(
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
