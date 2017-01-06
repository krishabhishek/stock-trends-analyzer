package ca.uwaterloo.stock_trends_analyzer.exceptions;

public class InternalAppError extends Exception
{
    public InternalAppError()
    {
        super();
    }

    public InternalAppError(String s)
    {
        super(s);
    }

    public InternalAppError(String s, Throwable throwable)
    {
        super(s, throwable);
    }

    public InternalAppError(Throwable throwable)
    {
        super(throwable);
    }

    protected InternalAppError(String s, Throwable throwable, boolean b, boolean b1)
    {
        super(s, throwable, b, b1);
    }
}
