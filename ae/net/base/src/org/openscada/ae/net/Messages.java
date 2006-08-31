package org.openscada.ae.net;

import java.util.Calendar;

import org.openscada.ae.core.Event;
import org.openscada.ae.core.EventInformation;
import org.openscada.core.net.MessageHelper;
import org.openscada.core.utils.AttributesHelper;
import org.openscada.net.base.data.IntegerValue;
import org.openscada.net.base.data.LongValue;
import org.openscada.net.base.data.MapValue;
import org.openscada.net.base.data.StringValue;
import org.openscada.net.base.data.Value;

import sun.util.calendar.CalendarUtils;

public class Messages
{
    public final static int CC_CREATE_SESSION =                     0x00020000;
    public final static int CC_CLOSE_SESSION =                      0x00020001;
    public final static int CC_CANCEL_OPERATION =                   0x00020002;
    
    public final static int CC_LIST =                               0x00020010;
    public final static int CC_LIST_REPLY =                         0x00020011;
    
    public final static int CC_READ =                               0x00020100;
    public final static int CC_READ_REPLY =                         0x00020101;
    
    public final static int CC_SUBSCRIBE =                          0x00020200;
    public final static int CC_UNSUBSCRIBE =                        0x00020201;
    public final static int CC_SUBSCRIPTION_EVENT =                 0x00020202;
    
    public final static int CC_MODIFY_EVENT =                       0x00020300;
    
    public static Value eventInformationToValue ( EventInformation eventInformation )
    {
        MapValue value = new MapValue ();
        
        value.put ( "action", new IntegerValue ( eventInformation.getAction () ) );
        value.put ( "timestamp", new LongValue ( eventInformation.getTimestamp ().getTimeInMillis () ) );
        
        value.put ( "event", eventToValue ( eventInformation.getEvent () ) );
        
        return value;
    }
    
    public static Value eventToValue ( Event event )
    {
        MapValue value = new MapValue ();
        
        value.put ( "id", new StringValue ( event.getId () ) );
        value.put ( "timestamp", new LongValue ( event.getTimestamp ().getTimeInMillis () ) );
        value.put ( "attributes", MessageHelper.attributesToMap ( event.getAttributes () ) );
        
        return value;
    }
    
    public static EventInformation valueToEventInformation ( Value value )
    {
        // FIXME: create encoding for calendar/timestamp
        EventInformation eventInformation = new EventInformation ();
        
        MapValue mapValue = (MapValue)value;
        eventInformation.setAction ( ((IntegerValue)mapValue.get ( "action" )).getValue () );
        
        Calendar timestamp = Calendar.getInstance ();
        timestamp.setTimeInMillis ( ((LongValue)mapValue.get ( "timestamp" )).getValue () );
        eventInformation.setTimestamp ( timestamp );
        
        eventInformation.setEvent ( valueToEvent ( mapValue.get ( "event" ) ) );
        
        return eventInformation;
    }
    
    public static Event valueToEvent ( Value value )
    {
        // FIXME: create encoding for calendar/timestamp        
        MapValue mapValue = (MapValue)value;
        
        Event event = new Event ( ((StringValue)mapValue.get ( "id" )).getValue () );
        
        event.setAttributes ( MessageHelper.mapToAttributes ( (MapValue)mapValue.get ( "attributes" ) ) );
        
        Calendar timestamp = Calendar.getInstance ();
        timestamp.setTimeInMillis ( ((LongValue)mapValue.get ( "timestamp" )).getValue () );
        event.setTimestamp ( timestamp );
        
        return event;
    }
}
