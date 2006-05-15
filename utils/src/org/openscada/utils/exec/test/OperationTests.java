package org.openscada.utils.exec.test;

import org.openscada.utils.exec.Operation;
import org.openscada.utils.exec.OperationResult;
import org.openscada.utils.exec.SyncBasedOperation;

import junit.framework.TestCase;

public class OperationTests extends TestCase
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
    
    public void testSync1 () throws Exception
    {
        assertEquals ( _opSyncSuccess.execute("Alice"), "Hello to: Alice" );
        
        OperationResult<String> or = _opSyncSuccess.startExecute("Bob");
        System.out.println("Started execution");
        
        or.complete();
        
        assertTrue ( or.isComplete() );
        assertTrue ( or.isSuccess() );
        
    }
}
