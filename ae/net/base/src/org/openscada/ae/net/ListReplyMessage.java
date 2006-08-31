package org.openscada.ae.net;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openscada.ae.core.QueryDescription;
import org.openscada.core.net.MessageHelper;
import org.openscada.net.base.data.LongValue;
import org.openscada.net.base.data.MapValue;
import org.openscada.net.base.data.Message;
import org.openscada.net.base.data.Value;

public class ListReplyMessage
{
    private Set<QueryDescription> _queries = new HashSet<QueryDescription> ();

    public Set<QueryDescription> getQueries ()
    {
        return _queries;
    }

    public void setQueries ( Set<QueryDescription> queries )
    {
        _queries = queries;
    }
    
    /**
     * Create a message in order to send it back
     * @param id The operation id
     * @return
     */
    public Message toMessage ( long id )
    {
        Message message = new Message ( Messages.CC_LIST_REPLY );
        
        MapValue list = new MapValue ();
        
        for ( QueryDescription description : _queries )
        {
            list.put ( description.getId (), MessageHelper.attributesToMap ( description.getAttributes () ) );
        }
        
        message.getValues ().put ( "queries", list );
        message.getValues ().put ( "id", new LongValue ( id ) );
        
        return message;
    }
    
    public static ListReplyMessage fromMessage ( Message message )
    {
        ListReplyMessage listReplyMessage = new ListReplyMessage ();
        
        MapValue list = (MapValue)message.getValues ().get ( "queries" );
        
        for ( Map.Entry<String,Value> entry : list.getValues ().entrySet () )
        {
            listReplyMessage.getQueries ().add ( new QueryDescription (
                    entry.getKey (),
                    MessageHelper.mapToAttributes ( (MapValue)entry.getValue () )
                    ));
        }
        
        return listReplyMessage;
    }
    
}
