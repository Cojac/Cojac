package ch.eiafr.cojac.unit.replace;

import ch.eiafr.cojac.Arg;
import ch.eiafr.cojac.Args;

public class WrappingStochasticTest extends WrappingLauncher {
    @Override protected void specifyArgs(Args args) {
        args.setValue(Arg.STOCHASTIC, "0.001");
    }
}
