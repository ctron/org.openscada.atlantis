/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.ae;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.openscada.core.Variant;
import org.openscada.utils.lang.Immutable;

@Immutable
public class Event implements Cloneable, Comparable<Event>, Serializable
{
    private static final long serialVersionUID = 1133904558084283872L;

    public static class EventComparator implements Comparator<Event>
    {
        @Override
        public int compare ( final Event o1, final Event o2 )
        {
            final int s = o1.getSourceTimestamp ().compareTo ( o2.getSourceTimestamp () );
            if ( s == 0 )
            {
                final int e = o1.getEntryTimestamp ().compareTo ( o2.getEntryTimestamp () );
                if ( e == 0 )
                {
                    return o1.getId ().compareTo ( o2.getId () );
                }
                return e;
            }
            return s;
        }
    }

    public static class EventBuilder
    {
        private UUID id;

        private Date sourceTimestamp;

        private Date entryTimestamp;

        private final Map<String, Variant> attributes = new HashMap<String, Variant> ( 1 );

        private EventBuilder ()
        {
        }

        public EventBuilder event ( final Event event )
        {
            this.id = event.getId ();
            this.sourceTimestamp = event.getSourceTimestamp ();
            this.entryTimestamp = event.getEntryTimestamp ();
            this.attributes.putAll ( event.getAttributes () );
            return this;
        }

        public EventBuilder id ( final UUID id )
        {
            this.id = id;
            return this;
        }

        public EventBuilder sourceTimestamp ( final Date sourceTimestamp )
        {
            this.sourceTimestamp = sourceTimestamp;
            return this;
        }

        public EventBuilder entryTimestamp ( final Date entryTimestamp )
        {
            this.entryTimestamp = entryTimestamp;
            return this;
        }

        public EventBuilder attributes ( final Map<String, Variant> attributes )
        {
            this.attributes.putAll ( attributes );
            return this;
        }

        public EventBuilder attribute ( final String key, final Variant value )
        {
            this.attributes.put ( key, value );
            return this;
        }

        public EventBuilder attribute ( final Fields key, final Variant value )
        {
            this.attributes.put ( key.getName (), value );
            return this;
        }

        public EventBuilder attribute ( final String key, final Object value )
        {
            this.attributes.put ( key, new Variant ( value ) );
            return this;
        }

        public EventBuilder attribute ( final Fields key, final Object value )
        {
            this.attributes.put ( key.getName (), new Variant ( value ) );
            return this;
        }

        public Event build ()
        {
            return new Event ( this.id, this.sourceTimestamp, this.entryTimestamp, this.attributes );
        }
    }

    public enum Fields
    {
        MONITOR_TYPE ( "monitorType", String.class ),
        EVENT_TYPE ( "eventType", String.class ),
        VALUE ( "value", Variant.class ),
        MESSAGE ( "message", String.class ),
        MESSAGE_CODE ( "messageSource", String.class ),
        PRIORITY ( "priority", Integer.class ),
        SOURCE ( "source", String.class ),
        ACTOR_NAME ( "actorName", String.class ),
        ACTOR_TYPE ( "actorType", String.class ),
        HIVE ( "hive", String.class ),
        ITEM ( "item", String.class ),
        COMPONENT ( "component", String.class ),
        SYSTEM ( "system", String.class ),
        LOCATION ( "location", String.class ),
        COMMENT ( "comment", String.class );

        private final Class<? extends Object> clazz;

        private final String name;

        Fields ( final String name, final Class<? extends Object> clazz )
        {
            this.name = name;
            this.clazz = clazz;
        }

        public Class<? extends Object> getType ()
        {
            return this.clazz;
        }

        public String getName ()
        {
            return this.name;
        }

        public boolean contains ( final String name )
        {
            return byField ( name ) == null ? false : true;
        }

        public static Fields byField ( final String name )
        {
            for ( final Fields field : values () )
            {
                if ( field.getName ().equals ( name ) )
                {
                    return field;
                }
            }
            return null;
        }
    }

    public static final EventComparator comparator = new EventComparator ();

    private final UUID id;

    private final Date sourceTimestamp;

    private final Date entryTimestamp;

    private final Map<String, Variant> attributes;

    private Event ( final UUID id, final Date sourceTimestamp, final Date entryTimestamp, final Map<String, Variant> attributes )
    {
        this.id = id;
        this.sourceTimestamp = new Date ( sourceTimestamp.getTime () );
        this.entryTimestamp = new Date ( entryTimestamp.getTime () );
        this.attributes = new HashMap<String, Variant> ( attributes );
    }

    private Event ( final Event event )
    {
        this.id = event.getId ();
        this.sourceTimestamp = event.getSourceTimestamp ();
        this.entryTimestamp = event.getEntryTimestamp ();
        this.attributes = new HashMap<String, Variant> ( event.getAttributes () );
    }

    public UUID getId ()
    {
        return this.id;
    }

    public Date getSourceTimestamp ()
    {
        return new Date ( this.sourceTimestamp.getTime () );
    }

    public Date getEntryTimestamp ()
    {
        return new Date ( this.entryTimestamp.getTime () );
    }

    public Map<String, Variant> getAttributes ()
    {
        return Collections.unmodifiableMap ( this.attributes );
    }

    @Override
    public int hashCode ()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( this.id == null ? 0 : this.id.hashCode () );
        return result;
    }

    @Override
    public boolean equals ( final Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( obj == null )
        {
            return false;
        }
        if ( getClass () != obj.getClass () )
        {
            return false;
        }
        final Event other = (Event)obj;
        if ( this.id == null )
        {
            if ( other.id != null )
            {
                return false;
            }
        }
        else if ( !this.id.equals ( other.id ) )
        {
            return false;
        }
        return true;
    }

    public static EventBuilder create ()
    {
        return new EventBuilder ();
    }

    @Override
    public Event clone ()
    {
        return new Event ( this );
    }

    @Override
    public String toString ()
    {
        final StringBuilder sb = new StringBuilder ();
        sb.append ( "Event {" );
        sb.append ( "id: " );
        sb.append ( this.id == null ? null : this.id.toString () );
        sb.append ( ", sourceTimestamp: " );
        sb.append ( this.sourceTimestamp == null ? null : this.sourceTimestamp.toString () );
        sb.append ( ", entryTimestamp: " );
        sb.append ( this.entryTimestamp == null ? null : this.entryTimestamp.toString () );
        for ( final Entry<String, Variant> entry : this.attributes.entrySet () )
        {
            sb.append ( ", " + entry.getKey () );
            sb.append ( ": " );
            sb.append ( entry.getValue () == null ? null : entry.getValue () );
        }
        sb.append ( "}" );
        return sb.toString ();
    }

    @Override
    public int compareTo ( final Event o )
    {
        return comparator.compare ( this, o );
    }

    public Variant getField ( final Fields field )
    {
        if ( field == null )
        {
            return null;
        }
        return this.attributes.get ( field.getName () );
    }

    public void setField ( final Fields field, final Variant value )
    {
        if ( field == null )
        {
            return;
        }
        this.attributes.put ( field.getName (), value );
    }
}
