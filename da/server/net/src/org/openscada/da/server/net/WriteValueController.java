package org.openscada.da.server.net;

import org.openscada.core.Variant;
import org.openscada.da.core.server.Hive;
import org.openscada.da.core.server.InvalidItemException;
import org.openscada.da.core.server.InvalidSessionException;
import org.openscada.da.core.server.Session;
import org.openscada.da.core.server.WriteOperationListener;
import org.openscada.net.base.ConnectionHandlerBase;
import org.openscada.net.base.data.LongValue;
import org.openscada.net.base.data.Message;
import org.openscada.net.base.data.StringValue;
import org.openscada.net.da.handler.Messages;
import org.openscada.net.utils.MessageCreator;
import org.openscada.utils.lang.Holder;

public class WriteValueController implements WriteOperationListener
{
    private Hive _hive = null;
    private Session _session = null;
    private ConnectionHandlerBase _connection = null;
    
    private Long _id = null; 
    
    public WriteValueController ( Hive hive, Session session, ConnectionHandlerBase connection )
    {
        _hive = hive;
        _session = session;
        _connection = connection;
    }
    
    public void run ( Message request )
    {
        try
        {
            Holder<String> itemId = new Holder<String> ();
            Holder<Variant> value = new Holder<Variant> ();
            
            org.openscada.net.da.handler.WriteOperation.parse ( request, itemId, value );
            
            _id = _hive.startWrite ( _session, itemId.value, value.value, this );
        }
        catch ( InvalidSessionException e )
        {
            sendFailure ( request, e );
        }
        catch ( InvalidItemException e )
        {
            sendFailure ( request, e );  
        }
        
        // send out ACK with operation id
        Message message = MessageCreator.createACK ( request );
        message.getValues ().put ( "id", new LongValue ( _id ) );
        _connection.getConnection ().sendMessage ( message );
        
        try
        {
            _hive.thawOperation ( _session, _id );
        }
        catch ( InvalidSessionException e )
        {
            // should never happen
        }
    }

    private void sendFailure ( Message request, Throwable e )
    {
        Message message = MessageCreator.createFailedMessage ( request, e );
        _connection.getConnection ().sendMessage ( message );
    }
    
    public void failure ( Throwable throwable )
    {
        Message replyMessage = new Message ( Messages.CC_WRITE_OPERATION_RESULT );
        replyMessage.getValues ().put ( Message.FIELD_ERROR_INFO, new StringValue ( throwable.getMessage () ) );
        replyMessage.getValues ().put ( "id", new LongValue ( _id ) );
        _connection.getConnection().sendMessage ( replyMessage );
    }

    public void success ()
    {
        Message replyMessage = new Message ( Messages.CC_WRITE_OPERATION_RESULT );
        replyMessage.getValues ().put ( "id", new LongValue ( _id ) );
        _connection.getConnection().sendMessage ( replyMessage );
    }
}
