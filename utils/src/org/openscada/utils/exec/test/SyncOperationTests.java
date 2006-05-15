package org.openscada.utils.exec.test;

import org.openscada.utils.exec.Operation;
import org.openscada.utils.exec.OperationResult;
import org.openscada.utils.exec.OperationResultHandler;
import org.openscada.utils.exec.SyncBasedOperation;

import junit.framework.TestCase;

public class SyncOperationTests extends TestCase
{
    
    private class TestOperationHandler<R> implements OperationResultHandler<R> 
    {
        private R _result = null;
        private Exception _exception = null;
        private boolean _failure = false;
        private boolean _success = false;
        
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
    }
    
    public void testSync2 () throws Exception
    {
        OperationResult<String> or = _opSyncSuccess.startExecute("Bob");
        System.out.println("Started execution");
        
        or.complete();
        
        assertTrue ( or.isComplete() );
        assertTrue ( or.isSuccess() );
    }
    
    public void testSync3 () throws Exception
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
