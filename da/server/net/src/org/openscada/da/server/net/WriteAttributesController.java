package org.openscada.da.server.net;

import java.util.Map;

import org.openscada.da.core.Hive;
import org.openscada.da.core.InvalidItemException;
import org.openscada.da.core.InvalidSessionException;
import org.openscada.da.core.Session;
import org.openscada.da.core.WriteAttributesOperationListener;
import org.openscada.da.core.data.Variant;
import org.openscada.net.base.ConnectionHandlerBase;
import org.openscada.net.base.data.LongValue;
import org.openscada.net.base.data.Message;
import org.openscada.net.da.handler.WriteAttributesOperation;
import org.openscada.net.utils.MessageCreator;
import org.openscada.utils.lang.Holder;

public class WriteAttributesController implements WriteAttributesOperationListener
{
    private Hive _hive = null;
    private Session _session = null;
    private ConnectionHandlerBase _connection = null;
    
    private Long _id = null; 
    
    public WriteAttributesController ( Hive hive, Session session, ConnectionHandlerBase connection )
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
            Holder<Map<String,Variant>> attributes = new Holder<Map<String,Variant>> ();
            
            WriteAttributesOperation.parseRequest ( request, itemId, attributes );
            
            _id = _hive.startWriteAttributes ( _session, itemId.value, attributes.value, this );
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
    
    public void complete ( Results results )
    {
        if ( _id != null )
        {
            Message message = WriteAttributesOperation.createResponse ( _id, results );
            _connection.getConnection ().sendMessage ( message );
        }
    }
}
