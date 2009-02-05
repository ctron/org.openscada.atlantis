/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openscada.ae.net;

import java.util.Calendar;

import org.openscada.ae.core.Event;
import org.openscada.ae.core.EventAction;
import org.openscada.ae.core.EventInformation;
import org.openscada.core.net.MessageHelper;
import org.openscada.net.base.data.IntegerValue;
import org.openscada.net.base.data.LongValue;
import org.openscada.net.base.data.MapValue;
import org.openscada.net.base.data.StringValue;
import org.openscada.net.base.data.Value;

public class Messages
{
    public static final int CC_CREATE_SESSION =                     0x00020000;
    public static final int CC_CLOSE_SESSION =                      0x00020001;
    public static final int CC_CANCEL_OPERATION =                   0x00020002;
    
    public static final int CC_LIST =                               0x00020010;
    public static final int CC_LIST_REPLY =                         0x00020011;
    
    public static final int CC_READ =                               0x00020100;
    public static final int CC_READ_REPLY =                         0x00020101;
    
    public static final int CC_SUBSCRIBE =                          0x00020200;
    public static final int CC_UNSUBSCRIBE =                        0x00020201;
    public static final int CC_SUBSCRIPTION_EVENT =                 0x00020202;
    public static final int CC_SUBSCRIPTION_UNSUBSCRIBED =          0x00020203;
    
    public static final int CC_SUBMIT_EVENT =                       0x00020300;
    public static final int CC_MODIFY_EVENT =                       0x00020301;
    
    
    public static Value eventInformationToValue ( EventInformation eventInformation )
    {
        MapValue value = new MapValue ();
        
        value.put ( "action", new IntegerValue ( eventInformation.getAction ().getId () ) );
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
        eventInformation.setAction ( EventAction.asAction ( ((IntegerValue)mapValue.get ( "action" )).getValue () ) );
        
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
