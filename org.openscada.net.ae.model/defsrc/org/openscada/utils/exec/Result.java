package org.openscada.utils.exec;
/**
 * 
 */

public class Result<T>
{
    protected boolean completed = false;

    protected T result;

    protected Throwable error;

    public synchronized T waitForResult ( final long timeout ) throws InterruptedException
    {
        if ( this.completed )
        {
            return this.result;
        }
        wait ( timeout );
        return this.result;
    }

    public T getResult ()
    {
        return this.result;
    }

    protected synchronized void signalResult ( final T result )
    {
        if ( this.completed )
        {
            return;
        }
        this.completed = true;
        this.result = result;
        notifyAll ();
    }

    protected synchronized void signalError ( final Throwable error )
    {
        if ( this.completed )
        {
            return;
        }
        this.completed = true;
        this.error = error;
        notifyAll ();
    }

    protected boolean isCompleted ()
    {
        return this.completed;
    }
}