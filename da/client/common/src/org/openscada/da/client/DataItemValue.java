/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
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

package org.openscada.da.client;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.core.subscription.SubscriptionState;

/**
 * A current value snapshot of the {@link DataItem}
 * @author Jens Reimann
 *
 */
public class DataItemValue
{
    private Variant value = new Variant ();

    private Map<String, Variant> attributes = new HashMap<String, Variant> ();

    private SubscriptionState subscriptionState = SubscriptionState.DISCONNECTED;

    private Throwable subscriptionError = null;

    public DataItemValue ()
    {
        super ();
    }

    public DataItemValue ( final Variant value, final Map<String, Variant> attributes, final SubscriptionState subscriptionState )
    {
        super ();
        this.attributes = attributes;
        this.subscriptionState = subscriptionState;
        this.value = value;
    }

    public DataItemValue ( final Variant value, final Map<String, Variant> attributes, final SubscriptionState subscriptionState, final Throwable subscriptionError )
    {
        super ();
        this.attributes = attributes;
        this.subscriptionState = subscriptionState;
        this.value = value;
        this.subscriptionError = subscriptionError;
    }

    public DataItemValue ( final DataItemValue arg0 )
    {
        this.attributes = new HashMap<String, Variant> ( arg0.attributes );
        this.value = new Variant ( arg0.value );
        this.subscriptionError = arg0.subscriptionError;
        this.subscriptionState = arg0.subscriptionState;
    }

    public Variant getValue ()
    {
        return this.value;
    }

    public void setValue ( final Variant value )
    {
        this.value = value;
    }

    public Map<String, Variant> getAttributes ()
    {
        return this.attributes;
    }

    public void setAttributes ( final Map<String, Variant> attributes )
    {
        this.attributes = attributes;
    }

    public SubscriptionState getSubscriptionState ()
    {
        return this.subscriptionState;
    }

    public void setSubscriptionState ( final SubscriptionState subscriptionState )
    {
        this.subscriptionState = subscriptionState;
    }

    public Throwable getSubscriptionError ()
    {
        return this.subscriptionError;
    }

    public void setSubscriptionError ( final Throwable subscriptionError )
    {
        this.subscriptionError = subscriptionError;
    }

    /**
     * Get the value of the attribute
     * @param attributeName the name of the attribute
     * @return the value of the attribute or <code>null</code> if the attribute is not set
     */
    public Boolean isAttribute ( final String attributeName )
    {
        try
        {
            final Variant value = this.attributes.get ( attributeName );
            if ( value == null )
            {
                return null;
            }
            return value.asBoolean ();
        }
        catch ( final Throwable e )
        {
            return null;
        }
    }

    /**
     * Get the boolean value of the named attribute
     * @param attributeName the attribute name to check
     * @param defaultValue the default value, if the attribute is not set
     * @return the attribute value or the default value it the attribute is not available
     */
    public boolean isAttribute ( final String attributeName, final boolean defaultValue )
    {
        final Boolean value = isAttribute ( attributeName );
        if ( value != null )
        {
            return value;
        }
        return defaultValue;
    }

    /**
     * Check if the value has the manual override attribute set
     * @return <code>true</code> if the value is manually overridden, <code>false</code> otherwise
     */
    public boolean isManual ()
    {
        return isAttribute ( "org.openscada.da.manual.active", false );
    }

    public boolean isAlarm ()
    {
        return isAttribute ( "alarm", false );
    }

    public boolean isConnected ()
    {
        return this.subscriptionState == SubscriptionState.CONNECTED;
    }

    public boolean isError ()
    {
        if ( isConnected () )
        {
            return isAttribute ( "error", false );
        }
        return false;
    }

    /**
     * get the timestamp of the value
     * @return the timestamp or <code>null</code> if the timestamp property is not set
     */
    public Calendar getTimestamp ()
    {
        final Variant value = this.attributes.get ( "timestamp" );
        if ( value == null )
        {
            return null;
        }

        if ( !value.isLong () )
        {
            return null;
        }

        final Calendar c = Calendar.getInstance ();
        try
        {
            c.setTimeInMillis ( value.asLong () );
        }
        catch ( final Throwable e )
        {
            return null;
        }
        return c;
    }

    @Override
    public String toString ()
    {
        final StringBuilder sb = new StringBuilder ();

        if ( this.value != null )
        {
            sb.append ( this.value.toString () );
        }
        sb.append ( "[" );
        if ( isConnected () )
        {
            sb.append ( "C" );
        }
        if ( isAlarm () )
        {
            sb.append ( "A" );
        }
        if ( isError () )
        {
            sb.append ( "E" );
        }
        if ( isManual () )
        {
            sb.append ( "M" );
        }
        sb.append ( "]" );

        final Calendar c = getTimestamp ();
        if ( c != null )
        {
            sb.append ( String.format ( "[%1$tF %1$tT,%1$tL]", c ) );
        }
        else
        {
            sb.append ( "[none]" );
        }

        return sb.toString ();
    }
}
