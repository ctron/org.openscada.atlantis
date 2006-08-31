package org.openscada.core.net;

import org.openscada.net.base.ConnectionHandlerBase;
import org.openscada.net.base.data.LongValue;
import org.openscada.net.base.data.Message;
import org.openscada.net.utils.MessageCreator;

public class OperationController
{
    private ConnectionHandlerBase _connection = null;

    public OperationController ( ConnectionHandlerBase connection )
    {
        super ();
        _connection = connection;
    }
    
    protected void sendACK ( Message request, long id )
    {
        Message message = MessageCreator.createACK ( request );
        message.getValues ().put ( "id", new LongValue ( id ) );
        _connection.getConnection ().sendMessage ( message );
    }

    protected void sendFailure ( Message request, Throwable e )
    {
        Message message = MessageCreator.createFailedMessage ( request, e );
        _connection.getConnection ().sendMessage ( message );
    }
}
