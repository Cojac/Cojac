package com.github.cojac.deltadebugging;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import com.github.cojac.deltadebugging.utils.Editor;
import com.github.cojac.deltadebugging.utils.Executor;
import java.lang.Math;

public class DeltaDebugger {

	private Editor		be;
	private Executor	eu;

	public DeltaDebugger(Editor be, Executor eu) throws IllegalArgumentException {
		this.be = be;
		this.eu = eu;
	}

	public BitSet launchDeltaDebugging() {
		BitSet instrumentationSet = new BitSet(be.getNbrOfInstructions());
		instrumentationSet.set(0, be.getNbrOfInstructions());
		return ddMin(instrumentationSet, 2);
	}

	/**
	 * Finds one local minimum set that still valid.
	 * 
	 * @param set
	 * @param n
	 * @return
	 */
	private BitSet ddMin(BitSet set, int n) {
		if (n <= 0) throw new IllegalArgumentException("Parameter (n) must be greater than zero");
		// if the set is empty
		if (set.isEmpty()) return set;
		// if the set contains only one bit set to true
		if (set.cardinality() == 1) {
			// try with an empty set
			BitSet emptySet = new BitSet();
			if (isSetValid(emptySet)) return emptySet;
			else return set;
		}
		// split the set to n nearly equals subset
		List<BitSet> subsetList = splitSet(set, n);
		// if one of those subset is sufficient continue with this one
		for (BitSet subset : subsetList)
			if (isSetValid(subset)) return ddMin(subset, 2);
		// if one of those subset is useless continue without this one
		for (BitSet subset : subsetList) {
			subset.xor(set);
			if (isSetValid(subset)) return ddMin(subset, Math.max(n - 1, 2));
		}
		// if granularity is not max possible increase it and continue
		if (n < set.cardinality()) return ddMin(set, 2 * n);

		//else the set is one of the local minimum
		be.editBehaviours(set);
		return set;
	}

	/**
	 * Split a set into n subset nearly equals.
	 * 
	 * @param set The set to split.
	 * @param n The number of subset.
	 * @return The list that contains all subset.
	 */
	private List<BitSet> splitSet(BitSet set, int n) {
		List<BitSet> subsetList = new ArrayList<>();

		if (set.cardinality() < n) n = set.cardinality();
		int q = set.cardinality() / n;
		int r = set.cardinality() % n;

		int lastIndex = 0;
		for (int i = 0; i < n; i++) {
			BitSet subset = new BitSet();
			int subsetSize = q + (i < r ? 1 : 0);
			int crtIndex = set.nextSetBit(lastIndex);
			for (int j = 0; j < subsetSize; j++) {
				subset.set(crtIndex);
				crtIndex = set.nextSetBit(crtIndex + 1);
			}
			lastIndex = crtIndex;
			subsetList.add(subset);
		}

		return subsetList;
	}

	/**
	 * Check if the configuration is valid
	 * 
	 * @param set
	 * @return
	 */
	public boolean isSetValid(BitSet set) {
		be.editBehaviours(set);
		return eu.executeWithCOJACBehaviours();
	}

}
