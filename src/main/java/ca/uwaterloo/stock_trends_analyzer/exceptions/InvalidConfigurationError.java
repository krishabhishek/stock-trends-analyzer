package ca.uwaterloo.stock_trends_analyzer.exceptions;

public class InvalidConfigurationError extends Exception
{
    public InvalidConfigurationError()
    {
        super();
    }

    public InvalidConfigurationError(String s)
    {
        super(s);
    }

    public InvalidConfigurationError(String s, Throwable throwable)
    {
        super(s, throwable);
    }

    public InvalidConfigurationError(Throwable throwable)
    {
        super(throwable);
    }

    protected InvalidConfigurationError(String s, Throwable throwable, boolean b, boolean b1)
    {
        super(s, throwable, b, b1);
    }
}
