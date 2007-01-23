package org.openscada.da.client.ice;

import org.openscada.utils.exec.LongRunningListener;
import org.openscada.utils.exec.LongRunningOperation;
import org.openscada.utils.exec.LongRunningState;

public class AsyncBaseOperation implements LongRunningOperation
{
    private  boolean _complete = false;
    private Throwable _error = null;
    private LongRunningListener _listener = null;

    public AsyncBaseOperation ( LongRunningListener listener )
    {
        super ();
        _listener = listener;
        if ( _listener != null )
        {
            _listener.stateChanged ( LongRunningState.RUNNING, null );
        }
    }

    public synchronized void cancel ()
    {
        if ( _complete )
        {
            return;
        }
        
        _complete = true;
        
        if ( _listener != null )
        {
            _listener.stateChanged ( LongRunningState.FAILURE, null );
        }
        notifyAll ();
    }

    public Throwable getError ()
    {
        return _error;
    }

    public boolean isComplete ()
    {
        return _complete;
    }

    public synchronized void waitForCompletion () throws InterruptedException
    {
        if ( !_complete )
        {
            wait ();
        }
    }

    public synchronized void waitForCompletion ( int timeout ) throws InterruptedException
    {
        if ( !_complete )
        {
            wait ( timeout );
        }
    }
    
    protected synchronized void success ()
    {
        if ( _complete )
            return;
        
        _complete = true;
        
        if ( _listener != null )
        {
            _listener.stateChanged ( LongRunningState.SUCCESS, null );
        }
        notifyAll ();
    }
    
    protected synchronized void failure ( Throwable ex )
    {
        if ( _complete )
            return;
        
        _complete = true;
        _error = ex;
        
        if ( _listener != null )
        {
            _listener.stateChanged ( LongRunningState.FAILURE, ex );
        }
        notifyAll ();
    }

}