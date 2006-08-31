package org.openscada.ae.net;

import org.openscada.net.base.data.IntegerValue;
import org.openscada.net.base.data.Message;
import org.openscada.net.base.data.StringValue;

public class SubscribeMessage
{
    private String _queryId = null;
    private int _maxBatchSize = 0;
    private int _archiveSet = 0;

    public String getQueryId ()
    {
        return _queryId;
    }

    public void setQueryId ( String queryId )
    {
        _queryId = queryId;
    }
    
    public int getArchiveSet ()
    {
        return _archiveSet;
    }

    public void setArchiveSet ( int archiveSet )
    {
        _archiveSet = archiveSet;
    }

    public int getMaxBatchSize ()
    {
        return _maxBatchSize;
    }

    public void setMaxBatchSize ( int maxBatchSize )
    {
        _maxBatchSize = maxBatchSize;
    }

    public Message toMessage ()
    {
        Message message = new Message ( Messages.CC_SUBSCRIBE );
        message.getValues ().put ( "query-id", new StringValue ( _queryId ) );
        message.getValues ().put ( "max-batch-size", new IntegerValue ( _maxBatchSize ) );
        message.getValues ().put ( "archive-set", new IntegerValue ( _archiveSet ) );
        return message;
    }
    
    public static SubscribeMessage fromMessage ( Message message )
    {
        SubscribeMessage subscribeMessage = new SubscribeMessage ();
        subscribeMessage.setQueryId ( message.getValues ().get ( "query-id" ).toString () );
        if ( message.getValues ().containsKey ( "max-batch-size" ) )
            subscribeMessage.setMaxBatchSize ( ((IntegerValue)message.getValues ().get ( "max-batch-size" )).getValue () );
        if ( message.getValues ().containsKey ( "archive-set" ) )
            subscribeMessage.setArchiveSet ( ((IntegerValue)message.getValues ().get ( "archive-set" )).getValue () );
        return subscribeMessage;
    }
}
