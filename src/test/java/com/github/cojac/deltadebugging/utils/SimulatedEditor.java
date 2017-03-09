package com.github.cojac.deltadebugging.utils;

import java.util.BitSet;

public class SimulatedEditor implements Editor {

	private BitSet	crtInstructionSet;
	private int		nbrOfInstructions;

	public SimulatedEditor(int nbrOfInstructions) {
		this.nbrOfInstructions = nbrOfInstructions;
	}

	@Override
	public void editBehaviours(BitSet instructionSet) {
		this.crtInstructionSet = instructionSet;
	}

	@Override
	public int getNbrOfInstructions() {
		return nbrOfInstructions;
	}

	public BitSet getCurrentInstructionSet() {

		return crtInstructionSet;
	}
}
