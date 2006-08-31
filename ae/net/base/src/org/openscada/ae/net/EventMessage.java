package org.openscada.ae.net;

import java.util.LinkedList;
import java.util.List;

import org.openscada.ae.core.EventInformation;
import org.openscada.net.base.data.ListValue;
import org.openscada.net.base.data.LongValue;
import org.openscada.net.base.data.Message;
import org.openscada.net.base.data.StringValue;
import org.openscada.net.base.data.Value;

public class EventMessage
{
    private String _queryId = null;
    private long _listenerId = 0;
    private List<EventInformation> _events = null;
    
    public List<EventInformation> getEvents ()
    {
        return _events;
    }
    public void setEvents ( List<EventInformation> events )
    {
        _events = events;
    }
    public String getQueryId ()
    {
        return _queryId;
    }
    public void setQueryId ( String queryId )
    {
        _queryId = queryId;
    }
    
    public long getListenerId ()
    {
        return _listenerId;
    }
    public void setListenerId ( long listenerId )
    {
        _listenerId = listenerId;
    }
    
    public Message toMessage ()
    {
        Message message = new Message ( Messages.CC_SUBSCRIPTION_EVENT );
        
        message.getValues ().put ( "query-id", new StringValue ( _queryId ) );
        message.getValues ().put ( "listener-id", new LongValue ( _listenerId ) );
        
        ListValue list = new ListValue ();
        
        for ( EventInformation eventInformation : _events )
        {
            list.add ( Messages.eventInformationToValue ( eventInformation ) );
        }
        
        message.getValues ().put ( "events", list );
        
        return message;
    }
    
    public static EventMessage fromMessage ( Message message )
    {
        EventMessage eventMessage = new EventMessage ();
        
        eventMessage.setQueryId ( ((StringValue)message.getValues ().get ( "query-id" )).getValue () );
        eventMessage.setListenerId ( ((LongValue)message.getValues ().get ( "listener-id" ) ).getValue () );
        
        List<EventInformation> events = new LinkedList<EventInformation> ();
        
        ListValue list = (ListValue)message.getValues ().get ( "events" );
        for ( Value value : list.getValues () )
        {
            events.add ( Messages.valueToEventInformation ( value ) );
        }
        eventMessage.setEvents ( events );
        
        return eventMessage;
    }
    
}
