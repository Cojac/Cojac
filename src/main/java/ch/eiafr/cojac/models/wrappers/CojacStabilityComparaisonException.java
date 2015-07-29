package ch.eiafr.cojac.models.wrappers;

/**
 * Created by Snipy Julmy on 22.04.15.
 */
// TODO: reconsider this reporting mechanism (merge with the original Cojac
// mechanisms (console, logfile, exception, callback)
public class CojacStabilityComparaisonException extends Exception {
    public CojacStabilityComparaisonException() {
        super();
    }

    public CojacStabilityComparaisonException(String message) {
        super(message);
    }

    public CojacStabilityComparaisonException(String message, Throwable cause) {
        super(message, cause);
    }

    public CojacStabilityComparaisonException(Throwable cause) {
        super(cause);
    }

    public CojacStabilityComparaisonException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
