package ch.eiafr.cojac.unit.replace;

import ch.eiafr.cojac.Arg;
import ch.eiafr.cojac.Args;

public class WrappingDerivativeTest extends WrappingLauncher {
    @Override protected void specifyArgs(Args args) {
        args.specify(Arg.AUTOMATIC_DERIVATION);
    }
}
