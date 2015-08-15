package ch.eiafr.cojac.unit.replace;

import ch.eiafr.cojac.Arg;
import ch.eiafr.cojac.Args;

public class WrappingBigDecimalTestt extends WrappingLauncher {
    @Override protected void specifyArgs(Args args) {
        args.setValue(Arg.BIG_DECIMAL_PRECISION, "100");
    }
}
