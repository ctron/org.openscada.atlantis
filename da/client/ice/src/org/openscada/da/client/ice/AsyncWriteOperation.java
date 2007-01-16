package org.openscada.da.client.ice;

import org.openscada.utils.exec.LongRunningListener;
import org.openscada.utils.exec.LongRunningOperation;
import org.openscada.utils.exec.LongRunningState;

import Ice.LocalException;
import Ice.UserException;
import OpenSCADA.DA.AMI_Hive_write;

public class AsyncWriteOperation extends AMI_Hive_write implements LongRunningOperation
{
    private boolean _complete = false;
    private Throwable _error = null;
    private LongRunningListener _listener = null;
    
    public AsyncWriteOperation ( LongRunningListener listener )
    {
        super ();
        _listener = listener;
        _listener.stateChanged ( LongRunningState.RUNNING, null );
    }
    
    @Override
    public synchronized void ice_exception ( LocalException ex )
    {
        _complete = true;
        _error = ex;
        _listener.stateChanged ( LongRunningState.FAILURE, ex );
    }

    @Override
    public synchronized void ice_exception ( UserException ex )
    {
        _complete = true;
        _error = ex;
        notifyAll ();
        _listener.stateChanged ( LongRunningState.FAILURE, ex );
    }

    @Override
    public synchronized void ice_response ()
    {
        _complete = true;
        notifyAll ();
        _listener.stateChanged ( LongRunningState.SUCCESS, null );
    }

    public synchronized void cancel ()
    {
        _complete = true;
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
            wait ();
    }

    public synchronized void waitForCompletion ( int timeout ) throws InterruptedException
    {
        if ( !_complete )
            wait ( timeout );
    }
}
