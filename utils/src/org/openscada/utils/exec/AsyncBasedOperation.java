package org.openscada.utils.exec;

public abstract class AsyncBasedOperation<R, T> implements Operation<R, T>
{

    public R execute ( T arg0 ) throws Exception
    {
        OperationResult<R> result = startExecute(arg0);
        
        result.complete();
        
        if ( result.isSuccess() )
            return result.get();
        else
            throw result.getException();
    }

}
