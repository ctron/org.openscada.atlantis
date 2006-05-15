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
    
    protected abstract void startExecute ( OperationResult<R> or, T arg0 );
    
    public OperationResult<R> startExecute ( final T arg0 )
    {
        final OperationResult<R> or = new OperationResult<R> ();
        
        startExecute ( or, arg0 );
        
        return or;
    }
    
    public OperationResult<R> startExecute ( OperationResultHandler<R> handler, T arg0 )
    {
        final OperationResult<R> or = new OperationResult<R> ( handler );
        
        startExecute ( or, arg0 );
        
        return or;
    }

}
