package ch.eiafr.cojac.unit.replace;

import ch.eiafr.cojac.Arg;
import ch.eiafr.cojac.Args;

public class WrappingIntervalTest extends WrappingLauncher {
    @Override protected void specifyArgs(Args args) {
        args.setValue(Arg.INTERVAL, "0.001");
    }
}
