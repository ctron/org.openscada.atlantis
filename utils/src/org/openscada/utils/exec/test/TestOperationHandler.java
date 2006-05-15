/**
 * 
 */
package org.openscada.utils.exec.test;

import org.openscada.utils.exec.OperationResultHandler;

class TestOperationHandler<R> implements OperationResultHandler<R> 
{
    R _result = null;
    Exception _exception = null;
    boolean _failure = false;
    boolean _success = false;
    
    public void failure ( Exception e )
    {
        _result = null;
        _exception = e;
        _success = false;
        _failure = true;
    }

    public void success ( R result )
    {
        _result = result;
        _exception = null;
        _success = true;
        _failure = false;
    }

    public Exception getException ()
    {
        return _exception;
    }

    public boolean isFailure ()
    {
        return _failure;
    }

    public R getResult ()
    {
        return _result;
    }

    public boolean isSuccess ()
    {
        return _success;
    }
    
}