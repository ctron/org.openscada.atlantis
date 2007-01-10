package org.openscada.utils.exec;


public interface LongRunningOperation
{

    public abstract boolean isComplete ();

    public abstract void cancel ();

    public abstract Throwable getError ();

    public abstract void waitForCompletion () throws InterruptedException;

    public abstract void waitForCompletion ( int timeout ) throws InterruptedException;

}