package ch.eiafr.cojac.models.wrappers;

public class CojacStabilityException extends Exception
{
    public CojacStabilityException()
    {
        super();
    }

    public CojacStabilityException(String message)
    {
        super(message);
    }

    public CojacStabilityException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public CojacStabilityException(Throwable cause)
    {
        super(cause);
    }

    public CojacStabilityException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
