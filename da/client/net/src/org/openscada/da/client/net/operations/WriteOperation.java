package org.openscada.da.client.net.operations;

import org.apache.log4j.Logger;
import org.openscada.da.client.net.Connection;
import org.openscada.da.client.net.OperationFailedException;
import org.openscada.da.client.net.OperationTimedOutException;
import org.openscada.da.client.net.ProtocolErrorException;
import org.openscada.net.base.MessageStateListener;
import org.openscada.net.base.data.Message;
import org.openscada.utils.exec.AsyncBasedOperation;
import org.openscada.utils.exec.OperationResult;

public class WriteOperation extends AsyncBasedOperation<Object,WriteOperationArguments>
{
    
    private static Logger _log = Logger.getLogger ( WriteOperation.class );
    
    private Connection _connection;
    
    public WriteOperation ( Connection connection )
    {
        _connection = connection;
    }
    
    @Override
    protected void startExecute ( final OperationResult<Object> or, WriteOperationArguments arg0 )
    {
        Message msg = org.openscada.net.da.handler.WriteOperation.create(arg0.itemName, arg0.value);
        
        _connection.getClient().getConnection().sendMessage(msg, new MessageStateListener(){

            public void messageReply ( Message message )
            {
                switch ( message.getCommandCode() )
                {
                
                case Message.CC_ACK:
                    or.notifySuccess(new Object());
                    break;
                    
                case Message.CC_FAILED:
                    String failure = "unknown failure";
                    
                    if ( message.getValues().containsKey ( Message.FIELD_ERROR_INFO ) )
                        failure = message.getValues().get ( Message.FIELD_ERROR_INFO ).toString();
                    
                    or.notifyFailure ( new OperationFailedException ( failure ) );
                    break;
                    
                default:
                    _log.warn ( "Invalid reply to write operation cc=" + message.getCommandCode() );
                    or.notifyFailure(new ProtocolErrorException());
                    break;
                }
            }

            public void messageTimedOut ()
            {
                or.notifyFailure(new OperationTimedOutException());
            }});
    }

}
