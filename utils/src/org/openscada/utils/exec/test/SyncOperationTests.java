package org.openscada.utils.exec.test;

import junit.framework.TestCase;

import org.openscada.utils.exec.Operation;
import org.openscada.utils.exec.OperationResult;
import org.openscada.utils.exec.SyncBasedOperation;

public class SyncOperationTests extends TestCase
{
    Operation<String,String> _opSyncSuccess = null;
    
    @Override
    protected void setUp () throws Exception
    {
        _opSyncSuccess = new SyncBasedOperation<String,String>(){

            public String execute ( String arg0 ) throws Exception
            {
               Thread.sleep(1000);
               System.out.println ( "Say hello: " + arg0 );
               Thread.sleep(1000);
               return "Hello to: " + arg0;
            }
        };
        
        super.setUp ();
    }
    
    public void testSync () throws Exception
    {
        assertEquals ( _opSyncSuccess.execute("Alice"), "Hello to: Alice" );
    }
    
    public void testAsync () throws Exception
    {
        OperationResult<String> or = _opSyncSuccess.startExecute("Bob");
        System.out.println("Started execution");
        
        or.complete();
        
        assertTrue ( or.isComplete() );
        assertTrue ( or.isSuccess() );
    }
    
    public void testAsyncHandler () throws Exception
    {
        TestOperationHandler<String> handler = new TestOperationHandler<String>();
        
        OperationResult<String> or = _opSyncSuccess.startExecute(handler, "Bob");
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
