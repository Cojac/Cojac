package com.github.cojac.deltadebugging.utils;

import java.util.BitSet;

public interface Editor {

	public void editBehaviours(BitSet set);

	public int getNbrOfInstructions();
}
