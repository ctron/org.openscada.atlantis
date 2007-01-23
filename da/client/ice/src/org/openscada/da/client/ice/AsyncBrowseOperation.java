package org.openscada.da.client.ice;

import org.openscada.utils.exec.LongRunningListener;
import org.openscada.utils.exec.LongRunningOperation;

import Ice.LocalException;
import Ice.UserException;
import OpenSCADA.DA.AMI_Hive_browse;
import OpenSCADA.DA.Browser.Entry;

public class AsyncBrowseOperation extends AMI_Hive_browse implements LongRunningOperation
{
    private AsyncBaseOperation _op;
    private Entry[] _result = null;
    
    public AsyncBrowseOperation ( LongRunningListener listener )
    {
        super ();
        _op = new AsyncBaseOperation ( listener );
    }
    
    @Override
    public void ice_exception ( LocalException ex )
    {
        _op.failure ( ex );
    }

    @Override
    public void ice_exception ( UserException ex )
    {
        _op.failure ( ex );        
    }

    @Override
    public void ice_response ( Entry[] __ret )
    {
        _result = __ret;
    }
    
    public Entry[] getResult ()
    {
        return _result;
    }
    
    // Forward to AsyncBaseOperation
    
    public void cancel ()
    {
        _op.cancel ();
    }

    public Throwable getError ()
    {
        return _op.getError ();
    }

    public boolean isComplete ()
    {
        return _op.isComplete ();
    }

    public void waitForCompletion () throws InterruptedException
    {
        _op.waitForCompletion ();
    }

    public void waitForCompletion ( int timeout ) throws InterruptedException
    {
       _op.waitForCompletion ( timeout );
    }
}
