package org.openscada.utils.exec;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Implements an operation that is based on a synchronous operation
 * 
 * @author jens
 *
 * @param <R> The result type
 * @param <T> The argument type
 */
public abstract class SyncBasedOperation < R, T > implements Operation < R, T >
{

    private static Executor _defaultExecutor = Executors.newCachedThreadPool();
    private Executor _executor = null;
    
    private void performJob ( OperationResult<R> or, T arg0 )
    {
        try
        {
            R result = execute ( arg0 );
            or.notifySuccess ( result );
        }
        catch ( Exception e )
        {
            or.notifyFailure ( e );
        }
    }
    
    public SyncBasedOperation ( )
    {
        this ( _defaultExecutor );
    }
    
    public SyncBasedOperation ( Executor executor )
    {
        _executor = executor;
    }
    
    private void startExecute ( final OperationResult<R> or, final T arg0 )
    {
        _executor.execute(new Runnable(){

            public void run ()
            {
                performJob( or, arg0 );
            }});
    }
    
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
