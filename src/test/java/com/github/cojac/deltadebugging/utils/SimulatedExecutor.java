package com.github.cojac.deltadebugging.utils;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class SimulatedExecutor implements Executor {
	//
	//	private BitSet plannedResponses;
	//	private int crtResponseIndex;
	//
	//	public SimulatedExecutor(BitSet plannedResponses) {
	//		this.plannedResponses = plannedResponses;
	//		this.crtResponseIndex = 0;
	//	}
	//
	//	@Override
	//	public boolean executeWithCOJACBehaviours() {
	//		return plannedResponses.get(crtResponseIndex++);
	//	}
	private SimulatedEditor	editor;
	private List<BitSet>	expectedSets;

	public SimulatedExecutor(SimulatedEditor editor, BitSet expectedSet) {
		this.editor = editor;
		this.expectedSets = new ArrayList<>();
		this.expectedSets.add(expectedSet);
	}

	public SimulatedExecutor(SimulatedEditor editor, List<BitSet> expectedSets) {
		this.editor = editor;
		this.expectedSets = expectedSets;
	}

	@Override
	public boolean executeWithCOJACBehaviours() {
		return crtSetContainsAnyExpectedSet();
	}

	private boolean crtSetContainsAnyExpectedSet() {
		BitSet crtSet = editor.getCurrentInstructionSet();

		for (BitSet expectedSet : expectedSets) {
			if (crtSet.cardinality() < expectedSet.cardinality()) continue;
			boolean isSetValid = true;
			for (int i = expectedSet.nextSetBit(0); i >= 0; i = expectedSet.nextSetBit(i + 1))
				if (!crtSet.get(i)) {
					isSetValid = false;
					break;
				}
			if (isSetValid) return true;
		}
		return false;
	}

}
