package org.openscada.utils.exec.test;

import org.openscada.utils.exec.AsyncBasedOperation;
import org.openscada.utils.exec.Operation;
import org.openscada.utils.exec.OperationResult;
import org.openscada.utils.exec.SyncBasedOperation;

import junit.framework.TestCase;

public class AsyncOperationTests extends TestCase
{
    
    Operation<String,String> _opAsyncSuccess = null;
    
    @Override
    protected void setUp () throws Exception
    {
        _opAsyncSuccess = new AsyncBasedOperation<String,String>(){

            @Override
            protected void startExecute ( final OperationResult<String> or, final String arg0 )
            {
               new Thread( new Runnable () {

                public void run ()
                {
                    try
                    {
                        Thread.sleep(1000);
                        System.out.println ( "Say hello: " + arg0 );
                        Thread.sleep(1000);
                        
                        or.notifySuccess ( "Hello to: " + arg0 );
                    }
                    catch ( Exception e )
                    {
                        or.notifyFailure(e);
                    }
                }} ).start();
            }

           
        };
        
        super.setUp ();
    }
    
    public void testSync () throws Exception
    {
        assertEquals ( _opAsyncSuccess.execute("Alice"), "Hello to: Alice" );
    }
    
    public void testAsync () throws Exception
    {
        OperationResult<String> or = _opAsyncSuccess.startExecute("Bob");
        System.out.println("Started execution");
        
        or.complete();
        
        assertTrue ( or.isComplete() );
        assertTrue ( or.isSuccess() );
    }
    
    public void testAsyncHandler () throws Exception
    {
        TestOperationHandler<String> handler = new TestOperationHandler<String>();
        
        OperationResult<String> or = _opAsyncSuccess.startExecute(handler, "Bob");
        System.out.println("Started execution");
        
        or.complete();
        
        assertTrue ( or.isComplete() );
        assertTrue ( or.isSuccess() );
        
        assertTrue ( handler.isSuccess() );
        assertFalse ( handler.isFailure() );
        
        assertNull ( handler.getException() );
        assertEquals ( handler.getResult(), "Hello to: Bob" );
    }
}
