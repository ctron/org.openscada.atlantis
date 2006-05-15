package org.openscada.utils.exec;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

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
    
    private SyncBasedOperation ( )
    {
        this ( _defaultExecutor );
    }
    
    private SyncBasedOperation ( Executor executor )
    {
        _executor = executor;
    }
    
    public OperationResult<R> startExecute ( final T arg0 )
    {
        final OperationResult<R> or = new OperationResult<R>();
        
        _executor.execute(new Runnable(){

            public void run ()
            {
                performJob( or, arg0 );
            }});
        
        return or;
    }
    
}
