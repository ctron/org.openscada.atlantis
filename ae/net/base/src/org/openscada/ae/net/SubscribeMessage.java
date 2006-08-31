package org.openscada.ae.net;

import org.openscada.net.base.data.Message;
import org.openscada.net.base.data.StringValue;

public class SubscribeMessage
{
    private String _queryId = null;

    public String getQueryId ()
    {
        return _queryId;
    }

    public void setQueryId ( String queryId )
    {
        _queryId = queryId;
    }
    
    public Message toMessage ()
    {
        Message message = new Message ( Messages.CC_SUBSCRIBE );
        message.getValues ().put ( "query-id", new StringValue ( _queryId ) );
        return message;
    }
    
    public static SubscribeMessage fromMessage ( Message message )
    {
        SubscribeMessage subscribeMessge = new SubscribeMessage ();
        subscribeMessge.setQueryId ( message.getValues ().get ( "query-id" ).toString () );
        return subscribeMessge;
    }
}
