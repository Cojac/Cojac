package com.github.cojac.deltadebugging;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import com.github.cojac.deltadebugging.utils.Executor;
import com.github.cojac.deltadebugging.utils.SimulatedEditor;
import com.github.cojac.deltadebugging.utils.SimulatedExecutor;

public class DDTest {

	private static final int	MAX_SET_SIZE		= 200;
	private static final int	MAX_NBR_MINIMAL_SET	= 10;
	private static final int	RND_SEED			= 1234567890;
	private static final int	NBR_OF_RND_TEST		= 500;

	private Random				random;

	@Before
	public void initTest() {
		this.random = new Random(RND_SEED);
	}

	// ---------------------------------------------------------------------
	// RANDOM TESTS
	// ---------------------------------------------------------------------

	@Test
	public void launchRandomTestWithOneMinimalSet() {
		for (int i = 0; i < NBR_OF_RND_TEST; i++)
			executeRandomTest(1); // test with one minimal set
	}

	@Test
	public void launchRandomTestWithMultipleMinimalSet() {
		for (int i = 0; i < NBR_OF_RND_TEST; i++)
			executeRandomTest(random.nextInt(MAX_NBR_MINIMAL_SET) + 1);
	}

	// prepare and execute a test with random input set and random possible expected set
	private void executeRandomTest(int nbrOfMinimalSet) {
		// random set size
		int setSize = random.nextInt(MAX_SET_SIZE) + 1;

		// initialize random expected set
		List<BitSet> expectedSets = new ArrayList<>();
		for (int i = 0; i < nbrOfMinimalSet; i++)
			expectedSets.add(createRandomSet(setSize));

		// execute the current random test
		executeTest(setSize, expectedSets);
	}

	private BitSet createRandomSet(int setSize) {
		// initialize random set
		BitSet randomSet = new BitSet(setSize);
		randomSet.set(0, random.nextInt(setSize));// set a random number of bit to 1
		// shuffle the expected set
		// see : Fisher-Yates shuffle : https://fr.wikipedia.org/wiki/M%C3%A9lange_de_Fisher-Yates
		for (int i = 0; i < setSize; i++) {
			// get random bit index between 0 (inclusive) and i (inclusive)
			int j = random.nextInt(i + 1);
			// swap bit i and j 
			boolean iBit = randomSet.get(i);
			boolean jBit = randomSet.get(j);
			randomSet.set(i, jBit);
			randomSet.set(j, iBit);
		}
		return randomSet;
	}

	//	private BitSet createRandomSubSetFromSet(BitSet set) {
	//		// initialize random sub set
	//		BitSet randomSubSet = new BitSet();
	//		randomSubSet.xor(set);
	//
	//		int nbrBitToClean = random.nextInt(randomSubSet.cardinality() + 1);
	//
	//		for (int i = 0; i < nbrBitToClean; i++) {
	//			int bitToClean = random.nextInt(nbrBitToClean);
	//			for (int j = randomSubSet.nextSetBit(0); j >= 0; j = randomSubSet.nextSetBit(j + 1))
	//				if (bitToClean == 0) {
	//					randomSubSet.clear();
	//					break;
	//				} else bitToClean--;
	//		}
	//		return randomSubSet;
	//	}

	// ---------------------------------------------------------------------
	// EMPTY SET TESTS
	// ---------------------------------------------------------------------
	@Test
	public void launchTestWithEmptyExpectedSet() {

		testEmptySet();

		for (int i = 0; i < NBR_OF_RND_TEST; i++)
			executeRandomTestWithEmptySet(random.nextInt(MAX_NBR_MINIMAL_SET));
	}

	private void testEmptySet() {
		List<BitSet> expectedSets = new ArrayList<>();
		expectedSets.add(new BitSet());
		executeTest(0, expectedSets);
	}

	// prepare and execute a test with random input and random expected set
	// and an empty set that must be the expected minimal set
	private void executeRandomTestWithEmptySet(int nbrOfMinimalSet) {
		// random set size
		int setSize = random.nextInt(MAX_SET_SIZE) + 1;

		// initialize random expected set
		List<BitSet> expectedSets = new ArrayList<>();
		for (int i = 0; i < nbrOfMinimalSet; i++)
			expectedSets.add(createRandomSet(setSize));

		// add an empty set
		BitSet emptySet = new BitSet();
		expectedSets.add(emptySet);

		// execute the current random test with specific expected set
		executeTestWithATrueExpected(setSize, expectedSets, emptySet);
	}

	// ---------------------------------------------------------------------
	// FULL SET TESTS
	// ---------------------------------------------------------------------
	@Test
	public void launchTestWithFullExpectedSet() {

		testFullSet();
		for (int i = 0; i < NBR_OF_RND_TEST; i++)
			executeRandomTestWithFullSet(random.nextInt(MAX_NBR_MINIMAL_SET) + 1);
	}

	private void testFullSet() {
		List<BitSet> expectedSets = new ArrayList<>();
		expectedSets.add(new BitSet());
		executeTest(0, expectedSets);
	}

	// prepare and execute a test with random input and random expected set
	// and an empty set that must be the expected minimal set
	private void executeRandomTestWithFullSet(int nbrOfMinimalSet) {
		// random set size
		int setSize = random.nextInt(MAX_SET_SIZE) + 1;

		// initialize random expected set
		List<BitSet> expectedSets = new ArrayList<>();
		for (int i = 0; i < nbrOfMinimalSet; i++)
			expectedSets.add(createRandomSet(setSize));

		// add an full set
		BitSet fullSet = new BitSet();
		fullSet.set(0, setSize);

		// execute the current random test with specific no expected set
		executeTestWithSpecialNoExpected(setSize, expectedSets, fullSet);
	}

	// ---------------------------------------------------------------------
	// EXECUTION
	// ---------------------------------------------------------------------
	private void executeTest(int setSize, List<BitSet> expectedSets) {
		// execute delta debugging
		SimulatedEditor editor = new SimulatedEditor(setSize);
		Executor executor = new SimulatedExecutor(editor, expectedSets);
		DeltaDebugger deltaDebugger = new DeltaDebugger(editor, executor);
		BitSet resultSet = deltaDebugger.launchDeltaDebugging();
		// check the results
		boolean result = false;
		for (BitSet expectedSet : expectedSets) {
			expectedSet.xor(resultSet);
			if (expectedSet.cardinality() == 0) {
				result = true;
				break;
			}
		}
		assertTrue(result);
	}

	private void executeTestWithATrueExpected(int setSize, List<BitSet> expectedSets, BitSet trueExpectedSet) {
		// execute delta debugging
		SimulatedEditor editor = new SimulatedEditor(setSize);
		Executor executor = new SimulatedExecutor(editor, expectedSets);
		DeltaDebugger deltaDebugger = new DeltaDebugger(editor, executor);
		BitSet resultSet = deltaDebugger.launchDeltaDebugging();
		// check the results
		trueExpectedSet.xor(resultSet);
		assertTrue(trueExpectedSet.cardinality() == 0);
	}

	private void executeTestWithSpecialNoExpected(int setSize, List<BitSet> expectedSets, BitSet noExpectedSet) {
		// execute delta debugging
		SimulatedEditor editor = new SimulatedEditor(setSize);
		Executor executor = new SimulatedExecutor(editor, expectedSets);
		DeltaDebugger deltaDebugger = new DeltaDebugger(editor, executor);
		BitSet resultSet = deltaDebugger.launchDeltaDebugging();
		// check the results
		boolean result = false;
		for (BitSet expectedSet : expectedSets) {
			expectedSet.xor(resultSet);
			if (expectedSet.cardinality() == 0) {
				result = true;
				break;
			}
		}
		assertTrue(result);
		noExpectedSet.xor(resultSet);
		assertFalse(noExpectedSet.cardinality() == 0);
	}

	//	@Test(expected = IllegalArgumentException.class)
	//	public void testIllegalArgument1() {
	//		// initialize all sets
	//		BitSet plannedResponses = new BitSet();
	//		BitSet instructionSet = new BitSet();
	//
	//		// execute delta debugging
	//		Executor executor = new SimulatedExecutor(plannedResponses);
	//		Editor editor = new SimulatedEditor();
	//		DeltaDebugger deltaDebugger = new DeltaDebugger(editor, executor);
	//		deltaDebugger.deltaDebugging(instructionSet, 0);
	//	}
	//
	//	@Test(expected = IllegalArgumentException.class)
	//	public void testIllegalArgument2() {
	//		// initialize all sets
	//		BitSet plannedResponses = new BitSet();
	//		BitSet instructionSet = new BitSet();
	//		// execute delta debugging
	//		Executor executor = new SimulatedExecutor(plannedResponses);
	//		Editor editor = new SimulatedEditor();
	//		DeltaDebugger deltaDebugger = new DeltaDebugger(editor, executor);
	//		deltaDebugger.deltaDebugging(instructionSet, -1);
	//	}

	//	
	//	private void executeTestWithPlannedResponses(BitSet base, BitSet expected, BitSet plannedResponses) {
	//		// execute delta debugging
	//		Executor executor = new SimulatedExecutor(plannedResponses);
	//		Editor editor = new SimulatedEditor();
	//		DeltaDebugger deltaDebugger = new DeltaDebugger(editor, executor);
	//		BitSet result = deltaDebugger.deltaDebugging(base, 1);
	//		// check the results
	//		result.xor(expected);
	//		assertTrue(result.cardinality() == 0);
	//	}
	//	@Test
	//	public void testEmptySet1() {
	//		// initialize all sets
	//		BitSet plannedResponses = new BitSet();
	//		BitSet instructionSet = new BitSet();
	//		BitSet expectedSet = new BitSet();
	//		// execute delta debugging
	//		Executor executor = new SimulatedExecutor(plannedResponses);
	//		Editor editor = new SimulatedEditor();
	//		DeltaDebugger deltaDebugger = new DeltaDebugger(editor, executor);
	//		BitSet reslutSet = deltaDebugger.deltaDebugging(instructionSet, 1);
	//		// check the results
	//		reslutSet.xor(expectedSet);
	//		assertTrue(reslutSet.cardinality() == 0);
	//	}
	//
	//	@Test
	//	public void testEmptySet2() {
	//		// initialize all sets
	//		BitSet plannedResponses = new BitSet();
	//		BitSet instructionSet = new BitSet();
	//		BitSet expectedSet = new BitSet();
	//		// execute delta debugging
	//		Executor executor = new SimulatedExecutor(plannedResponses);
	//		Editor editor = new SimulatedEditor();
	//		DeltaDebugger deltaDebugger = new DeltaDebugger(editor, executor);
	//		BitSet reslutSet = deltaDebugger.deltaDebugging(instructionSet, 2);
	//		// check the results
	//		reslutSet.xor(expectedSet);
	//		assertTrue(reslutSet.cardinality() == 0);
	//	}
	//
	//	@Test
	//	public void testUnarySet1() {
	//		// initialize all sets
	//		BitSet plannedResponses = new BitSet();
	//		plannedResponses.set(0);
	//		BitSet instructionSet = new BitSet();
	//		instructionSet.set(0);
	//		BitSet expectedSet = new BitSet();
	//		// execute delta debugging
	//		Executor executor = new SimulatedExecutor(plannedResponses);
	//		Editor editor = new SimulatedEditor();
	//		DeltaDebugger deltaDebugger = new DeltaDebugger(editor, executor);
	//		BitSet reslutSet = deltaDebugger.deltaDebugging(instructionSet, 1);
	//		// check the results
	//		reslutSet.xor(expectedSet);
	//		assertTrue(reslutSet.cardinality() == 0);
	//	}
	//
	//	@Test
	//	public void testUnarySet2() {
	//		// initialize all sets
	//		BitSet plannedResponses = new BitSet();
	//		BitSet instructionSet = new BitSet();
	//		instructionSet.set(0);
	//		BitSet expectedSet = new BitSet();
	//		expectedSet.set(0);
	//		// execute delta debugging
	//		Executor executor = new SimulatedExecutor(plannedResponses);
	//		Editor editor = new SimulatedEditor();
	//		DeltaDebugger deltaDebugger = new DeltaDebugger(editor, executor);
	//		BitSet reslutSet = deltaDebugger.deltaDebugging(instructionSet, 1);
	//		// check the results
	//		reslutSet.xor(expectedSet);
	//		assertTrue(reslutSet.cardinality() == 0);
	//	}
	//
	//	@Test
	//	public void testBinarySet1() {
	//		// initialize all sets
	//		BitSet plannedResponses = new BitSet();
	//		plannedResponses.set(0);
	//		BitSet instructionSet = new BitSet();
	//		instructionSet.set(0);
	//		instructionSet.set(1);
	//		BitSet expectedSet = new BitSet();
	//		expectedSet.set(0);
	//		// execute delta debugging
	//		Executor executor = new SimulatedExecutor(plannedResponses);
	//		Editor editor = new SimulatedEditor();
	//		DeltaDebugger deltaDebugger = new DeltaDebugger(editor, executor);
	//		BitSet reslutSet = deltaDebugger.deltaDebugging(instructionSet, 2);
	//		System.out.println(reslutSet.toString());
	//		// check the results
	//		reslutSet.xor(expectedSet);
	//		assertTrue(reslutSet.cardinality() == 0);
	//	}
	//
	//	@Test
	//	public void testBinarySet2() {
	//		// initialize all sets
	//		BitSet plannedResponses = new BitSet();
	//		plannedResponses.set(1);
	//		BitSet instructionSet = new BitSet();
	//		instructionSet.set(0);
	//		instructionSet.set(1);
	//		BitSet expectedSet = new BitSet();
	//		expectedSet.set(1);
	//		// execute delta debugging
	//		Executor executor = new SimulatedExecutor(plannedResponses);
	//		Editor editor = new SimulatedEditor();
	//		DeltaDebugger deltaDebugger = new DeltaDebugger(editor, executor);
	//		BitSet reslutSet = deltaDebugger.deltaDebugging(instructionSet, 2);
	//		System.out.println(reslutSet.toString());
	//		// check the results
	//		reslutSet.xor(expectedSet);
	//		assertTrue(reslutSet.cardinality() == 0);
	//	}
	//
	//	@Test
	//	public void testBinarySet3() {
	//		// initialize all sets
	//		BitSet plannedResponses = new BitSet();
	//		BitSet instructionSet = new BitSet();
	//		instructionSet.set(0);
	//		instructionSet.set(1);
	//		BitSet expectedSet = new BitSet();
	//		expectedSet.set(0);
	//		expectedSet.set(1);
	//		// execute delta debugging
	//		Executor executor = new SimulatedExecutor(plannedResponses);
	//		Editor editor = new SimulatedEditor();
	//		DeltaDebugger deltaDebugger = new DeltaDebugger(editor, executor);
	//		BitSet reslutSet = deltaDebugger.deltaDebugging(instructionSet, 1);
	//		System.out.println(reslutSet.toString());
	//		// check the results
	//		reslutSet.xor(expectedSet);
	//		assertTrue(reslutSet.cardinality() == 0);
	//	}
	//
	//	@Test
	//	public void testBinarySet4() {
	//		// initialize all sets
	//		BitSet plannedResponses = new BitSet();
	//		plannedResponses.set(1);
	//		BitSet instructionSet = new BitSet();
	//		instructionSet.set(0);
	//		instructionSet.set(1);
	//		BitSet expectedSet = new BitSet();
	//		// execute delta debugging
	//		Executor executor = new SimulatedExecutor(plannedResponses);
	//		Editor editor = new SimulatedEditor();
	//		DeltaDebugger deltaDebugger = new DeltaDebugger(editor, executor);
	//		BitSet reslutSet = deltaDebugger.deltaDebugging(instructionSet, 1);
	//		System.out.println(reslutSet.toString());
	//		// check the results
	//		reslutSet.xor(expectedSet);
	//		assertTrue(reslutSet.cardinality() == 0);
	//	}
	//
	//	@Test
	//	public void testBinarySet5() {
	//		// initialize all sets
	//		BitSet plannedResponses = new BitSet();
	//		plannedResponses.set(2);
	//		BitSet instructionSet = new BitSet();
	//		instructionSet.set(0);
	//		instructionSet.set(1);
	//		BitSet expectedSet = new BitSet();
	//		expectedSet.set(0);
	//		// execute delta debugging
	//		Executor executor = new SimulatedExecutor(plannedResponses);
	//		Editor editor = new SimulatedEditor();
	//		DeltaDebugger deltaDebugger = new DeltaDebugger(editor, executor);
	//		BitSet reslutSet = deltaDebugger.deltaDebugging(instructionSet, 1);
	//		System.out.println(reslutSet.toString());
	//		// check the results
	//		reslutSet.xor(expectedSet);
	//		assertTrue(reslutSet.cardinality() == 0);
	//	}
	//
	//	@Test
	//	public void testBinarySet6() {
	//		// initialize all sets
	//		BitSet plannedResponses = new BitSet();
	//		plannedResponses.set(3);
	//		BitSet instructionSet = new BitSet();
	//		instructionSet.set(0);
	//		instructionSet.set(1);
	//		BitSet expectedSet = new BitSet();
	//		expectedSet.set(1);
	//		// execute delta debugging
	//		Executor executor = new SimulatedExecutor(plannedResponses);
	//		Editor editor = new SimulatedEditor();
	//		DeltaDebugger deltaDebugger = new DeltaDebugger(editor, executor);
	//		BitSet reslutSet = deltaDebugger.deltaDebugging(instructionSet, 1);
	//		System.out.println(reslutSet.toString());
	//		// check the results
	//		reslutSet.xor(expectedSet);
	//		assertTrue(reslutSet.cardinality() == 0);
	//	}
	//
	//	@Test
	//	public void testTernarySet1() {
	//		// initialize all sets
	//		BitSet plannedResponses = new BitSet();
	//		BitSet instructionSet = new BitSet();
	//		instructionSet.set(0);
	//		instructionSet.set(1);
	//		instructionSet.set(2);
	//		BitSet expectedSet = new BitSet();
	//		expectedSet.set(0);
	//		expectedSet.set(1);
	//		expectedSet.set(2);
	//		// execute delta debugging
	//		Executor executor = new SimulatedExecutor(plannedResponses);
	//		Editor editor = new SimulatedEditor();
	//		DeltaDebugger deltaDebugger = new DeltaDebugger(editor, executor);
	//		BitSet reslutSet = deltaDebugger.deltaDebugging(instructionSet, 1);
	//		System.out.println(reslutSet.toString());
	//		// check the results
	//		reslutSet.xor(expectedSet);
	//		assertTrue(reslutSet.cardinality() == 0);
	//	}
	//
	//	@Test
	//	public void testTernarySet2() {
	//		// initialize all sets
	//		BitSet plannedResponses = new BitSet();
	//		plannedResponses.set(0);
	//		plannedResponses.set(1);
	//		BitSet instructionSet = new BitSet();
	//		instructionSet.set(0);
	//		instructionSet.set(1);
	//		instructionSet.set(2);
	//		BitSet expectedSet = new BitSet();
	//		expectedSet.set(0);
	//		expectedSet.set(1);
	//		// execute delta debugging
	//		Executor executor = new SimulatedExecutor(plannedResponses);
	//		Editor editor = new SimulatedEditor();
	//		DeltaDebugger deltaDebugger = new DeltaDebugger(editor, executor);
	//		BitSet reslutSet = deltaDebugger.deltaDebugging(instructionSet, 1);
	//		System.out.println(reslutSet.toString());
	//		// check the results
	//		reslutSet.xor(expectedSet);
	//		assertTrue(reslutSet.cardinality() == 0);
	//	}
	//
	//	@Test
	//	public void testTernarySet3() {
	//		// initialize all sets
	//		BitSet plannedResponses = new BitSet();
	//		plannedResponses.set(0);
	//		plannedResponses.set(8);
	//		BitSet instructionSet = new BitSet();
	//		instructionSet.set(0);
	//		instructionSet.set(1);
	//		instructionSet.set(2);
	//		BitSet expectedSet = new BitSet();
	//		expectedSet.set(1);
	//		expectedSet.set(2);
	//		// execute delta debugging
	//		Executor executor = new SimulatedExecutor(plannedResponses);
	//		Editor editor = new SimulatedEditor();
	//		DeltaDebugger deltaDebugger = new DeltaDebugger(editor, executor);
	//		BitSet reslutSet = deltaDebugger.deltaDebugging(instructionSet, 1);
	//		System.out.println(reslutSet.toString());
	//		// check the results
	//		reslutSet.xor(expectedSet);
	//		assertTrue(reslutSet.cardinality() == 0);
	//	}
	//
	//	@Test
	//	public void testTernarySet4() {
	//		// initialize all sets
	//		BitSet plannedResponses = new BitSet();
	//		plannedResponses.set(0);
	//		plannedResponses.set(9);
	//		BitSet instructionSet = new BitSet();
	//		instructionSet.set(0);
	//		instructionSet.set(1);
	//		instructionSet.set(2);
	//		BitSet expectedSet = new BitSet();
	//		expectedSet.set(0);
	//		expectedSet.set(2);
	//		// execute delta debugging
	//		Executor executor = new SimulatedExecutor(plannedResponses);
	//		Editor editor = new SimulatedEditor();
	//		DeltaDebugger deltaDebugger = new DeltaDebugger(editor, executor);
	//		BitSet reslutSet = deltaDebugger.deltaDebugging(instructionSet, 1);
	//		System.out.println(reslutSet.toString());
	//		// check the results
	//		reslutSet.xor(expectedSet);
	//		assertTrue(reslutSet.cardinality() == 0);
	//	}
	//
	//	@Test
	//	public void testTernarySet5() {
	//		// initialize all sets
	//		BitSet plannedResponses = new BitSet();
	//		plannedResponses.set(0);
	//		plannedResponses.set(1);
	//		plannedResponses.set(2);
	//		BitSet instructionSet = new BitSet();
	//		instructionSet.set(0);
	//		instructionSet.set(1);
	//		instructionSet.set(2);
	//		BitSet expectedSet = new BitSet();
	//		expectedSet.set(0);
	//		// execute delta debugging
	//		Executor executor = new SimulatedExecutor(plannedResponses);
	//		Editor editor = new SimulatedEditor();
	//		DeltaDebugger deltaDebugger = new DeltaDebugger(editor, executor);
	//		BitSet reslutSet = deltaDebugger.deltaDebugging(instructionSet, 1);
	//		System.out.println(reslutSet.toString());
	//		// check the results
	//		reslutSet.xor(expectedSet);
	//		assertTrue(reslutSet.cardinality() == 0);
	//	}
	//
	//	@Test
	//	public void testTernarySet6() {
	//		// initialize all sets
	//		BitSet plannedResponses = new BitSet();
	//		plannedResponses.set(0);
	//		plannedResponses.set(1);
	//		plannedResponses.set(3);
	//		BitSet instructionSet = new BitSet();
	//		instructionSet.set(0);
	//		instructionSet.set(1);
	//		instructionSet.set(2);
	//		BitSet expectedSet = new BitSet();
	//		expectedSet.set(1);
	//		// execute delta debugging
	//		Executor executor = new SimulatedExecutor(plannedResponses);
	//		Editor editor = new SimulatedEditor();
	//		DeltaDebugger deltaDebugger = new DeltaDebugger(editor, executor);
	//		BitSet reslutSet = deltaDebugger.deltaDebugging(instructionSet, 1);
	//		System.out.println(reslutSet.toString());
	//		// check the results
	//		reslutSet.xor(expectedSet);
	//		assertTrue(reslutSet.cardinality() == 0);
	//	}
	//
	//	@Test
	//	public void testTernarySet7() {
	//		// initialize all sets
	//		BitSet plannedResponses = new BitSet();
	//		plannedResponses.set(0);
	//		plannedResponses.set(2);
	//		BitSet instructionSet = new BitSet();
	//		instructionSet.set(0);
	//		instructionSet.set(1);
	//		instructionSet.set(2);
	//		BitSet expectedSet = new BitSet();
	//		expectedSet.set(2);
	//		// execute delta debugging
	//		Executor executor = new SimulatedExecutor(plannedResponses);
	//		Editor editor = new SimulatedEditor();
	//		DeltaDebugger deltaDebugger = new DeltaDebugger(editor, executor);
	//		BitSet reslutSet = deltaDebugger.deltaDebugging(instructionSet, 1);
	//		System.out.println(reslutSet.toString());
	//		// check the results
	//		reslutSet.xor(expectedSet);
	//		assertTrue(reslutSet.cardinality() == 0);
	//	}
	//
	//	@Test
	//	public void testTernarySet8() {
	//		// initialize all sets
	//		BitSet plannedResponses = new BitSet();
	//		plannedResponses.set(0);
	//		plannedResponses.set(1);
	//		plannedResponses.set(2);
	//		plannedResponses.set(3);
	//		BitSet instructionSet = new BitSet();
	//		instructionSet.set(0);
	//		instructionSet.set(1);
	//		instructionSet.set(2);
	//		BitSet expectedSet = new BitSet();
	//
	//		// execute delta debugging
	//		Executor executor = new SimulatedExecutor(plannedResponses);
	//		Editor editor = new SimulatedEditor();
	//		DeltaDebugger deltaDebugger = new DeltaDebugger(editor, executor);
	//		BitSet reslutSet = deltaDebugger.deltaDebugging(instructionSet, 1);
	//		System.out.println(reslutSet.toString());
	//		// check the results
	//		reslutSet.xor(expectedSet);
	//		assertTrue(reslutSet.cardinality() == 0);
	//	}
	//
	//	@Test
	//	public void testQuaternarySet1() {
	//		// initialize all sets
	//		BitSet plannedResponses = new BitSet();
	//		plannedResponses.set(0);
	//		BitSet instructionSet = new BitSet();
	//		instructionSet.set(0);
	//		instructionSet.set(1);
	//		instructionSet.set(2);
	//		instructionSet.set(3);
	//		BitSet expectedSet = new BitSet();
	//		expectedSet.set(0);
	//		expectedSet.set(1);
	//		expectedSet.set(2);
	//		expectedSet.set(3);
	//		// execute delta debugging
	//		Executor executor = new SimulatedExecutor(plannedResponses);
	//		Editor editor = new SimulatedEditor();
	//		DeltaDebugger deltaDebugger = new DeltaDebugger(editor, executor);
	//		BitSet reslutSet = deltaDebugger.deltaDebugging(instructionSet, 1);
	//		System.out.println(reslutSet.toString());
	//		// check the results
	//		reslutSet.xor(expectedSet);
	//		assertTrue(reslutSet.cardinality() == 0);
	//	}
	//
	//	@Test
	//	public void testQuaternarySet2() {
	//		// initialize all sets
	//		BitSet plannedResponses = new BitSet();
	//		plannedResponses.set(0);
	//		plannedResponses.set(1);
	//		plannedResponses.set(2);
	//		plannedResponses.set(3);
	//		BitSet instructionSet = new BitSet();
	//		instructionSet.set(0);
	//		instructionSet.set(1);
	//		instructionSet.set(2);
	//		instructionSet.set(3);
	//		BitSet expectedSet = new BitSet();
	//		// execute delta debugging
	//		Executor executor = new SimulatedExecutor(plannedResponses);
	//		Editor editor = new SimulatedEditor();
	//		DeltaDebugger deltaDebugger = new DeltaDebugger(editor, executor);
	//		BitSet reslutSet = deltaDebugger.deltaDebugging(instructionSet, 1);
	//		System.out.println(reslutSet.toString());
	//		// check the results
	//		reslutSet.xor(expectedSet);
	//		assertTrue(reslutSet.cardinality() == 0);
	//	}
	//
	//	@Test
	//	public void testQuaternarySet3() {
	//		// initialize all sets
	//		BitSet plannedResponses = new BitSet();
	//		plannedResponses.set(0);
	//		plannedResponses.set(1);
	//		BitSet instructionSet = new BitSet();
	//		instructionSet.set(0);
	//		instructionSet.set(1);
	//		instructionSet.set(2);
	//		instructionSet.set(3);
	//		BitSet expectedSet = new BitSet();
	//		expectedSet.set(0);
	//		expectedSet.set(1);
	//		// execute delta debugging
	//		Executor executor = new SimulatedExecutor(plannedResponses);
	//		Editor editor = new SimulatedEditor();
	//		DeltaDebugger deltaDebugger = new DeltaDebugger(editor, executor);
	//		BitSet reslutSet = deltaDebugger.deltaDebugging(instructionSet, 1);
	//		System.out.println(reslutSet.toString());
	//		// check the results
	//		reslutSet.xor(expectedSet);
	//		assertTrue(reslutSet.cardinality() == 0);
	//	}
	//
	//	@Test
	//	public void testQuaternarySet4() {
	//		// initialize all sets
	//		BitSet plannedResponses = new BitSet();
	//		plannedResponses.set(0);
	//		plannedResponses.set(3);
	//		BitSet instructionSet = new BitSet();
	//		instructionSet.set(0);
	//		instructionSet.set(1);
	//		instructionSet.set(2);
	//		instructionSet.set(3);
	//		BitSet expectedSet = new BitSet();
	//		expectedSet.set(2);
	//		expectedSet.set(3);
	//		// execute delta debugging
	//		Executor executor = new SimulatedExecutor(plannedResponses);
	//		Editor editor = new SimulatedEditor();
	//		DeltaDebugger deltaDebugger = new DeltaDebugger(editor, executor);
	//		BitSet reslutSet = deltaDebugger.deltaDebugging(instructionSet, 1);
	//		System.out.println(reslutSet.toString());
	//		// check the results
	//		reslutSet.xor(expectedSet);
	//		assertTrue(reslutSet.cardinality() == 0);
	//	}
	//
	//	@Test
	//	public void testQuaternarySet5() {
	//		// initialize all sets
	//		BitSet plannedResponses = new BitSet();
	//		plannedResponses.set(0);
	//		plannedResponses.set(10);
	//		plannedResponses.set(16);
	//		BitSet instructionSet = new BitSet();
	//		instructionSet.set(0);
	//		instructionSet.set(1);
	//		instructionSet.set(2);
	//		instructionSet.set(3);
	//		BitSet expectedSet = new BitSet();
	//		expectedSet.set(0);
	//		expectedSet.set(2);
	//		// execute delta debugging
	//		Executor executor = new SimulatedExecutor(plannedResponses);
	//		Editor editor = new SimulatedEditor();
	//		DeltaDebugger deltaDebugger = new DeltaDebugger(editor, executor);
	//		BitSet reslutSet = deltaDebugger.deltaDebugging(instructionSet, 1);
	//		System.out.println(reslutSet.toString());
	//		// check the results
	//		reslutSet.xor(expectedSet);
	//		assertTrue(reslutSet.cardinality() == 0);
	//	}
	//
	//	public void testQuaternarySet6() {
	//		// initialize all sets
	//		BitSet plannedResponses = new BitSet();
	//		plannedResponses.set(0);
	//		plannedResponses.set(10);
	//		plannedResponses.set(15);
	//		BitSet instructionSet = new BitSet();
	//		instructionSet.set(0);
	//		instructionSet.set(1);
	//		instructionSet.set(2);
	//		instructionSet.set(3);
	//		BitSet expectedSet = new BitSet();
	//		expectedSet.set(0);
	//		expectedSet.set(3);
	//		// execute delta debugging
	//		Executor executor = new SimulatedExecutor(plannedResponses);
	//		Editor editor = new SimulatedEditor();
	//		DeltaDebugger deltaDebugger = new DeltaDebugger(editor, executor);
	//		BitSet reslutSet = deltaDebugger.deltaDebugging(instructionSet, 1);
	//		System.out.println(reslutSet.toString());
	//		// check the results
	//		reslutSet.xor(expectedSet);
	//		assertTrue(reslutSet.cardinality() == 0);
	//	}

}
