/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
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

package org.openscada.spring.client.event;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.openscada.core.Variant;
import org.openscada.core.subscription.SubscriptionState;
import org.openscada.core.utils.AttributesHelper;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.client.DataItemValue.Builder;

public class ItemEventObserver extends AbstractItemEventObserver
{
    private static Logger log = Logger.getLogger ( ItemEventObserver.class );

    private DataItemValue value = new DataItemValue ();

    private ItemEventListener listener = null;

    /**
     * a flag which indicates if cache events should be suppressed or not
     */
    private boolean suppressCacheEvents = true;

    /**
     * a flag which indicates if attribute events should be supressed or not
     */
    private boolean suppressAttributeEvents = true;

    /**
     * An alias name of the topic the will be fired instead of the real
     * item name
     */
    private String alias;

    /**
     * The log level of value changes. Defaults to DEBUG
     */
    private Level valueLogLevel = Level.DEBUG;

    public void setValueLogLevel ( final String valueLogLevel )
    {
        this.valueLogLevel = Level.toLevel ( valueLogLevel );
    }

    public void setSuppressAttributeEvents ( final boolean suppressAttributeEvents )
    {
        this.suppressAttributeEvents = suppressAttributeEvents;
    }

    public void notifySubscriptionChange ( final SubscriptionState subscriptionState, final Throwable subscriptionError )
    {
        log.log ( this.valueLogLevel, String.format ( "Subscription change for item '%s' to '%s'", this.itemName, subscriptionState ) );

        final Builder builder = new Builder ( this.value );

        builder.setSubscriptionState ( subscriptionState );
        switch ( subscriptionState )
        {
        case DISCONNECTED:
        case GRANTED:
            builder.setAttributes ( new HashMap<String, Variant> () );
            builder.setValue ( new Variant () );
            break;
        }
        this.value = builder.build ();
        fireChange ();
    }

    public void notifyDataChange ( final Variant value, final Map<String, Variant> attributes, final boolean cache )
    {
        log.log ( this.valueLogLevel, String.format ( "Value change for item '%s' to '%s' (cache: %s)", this.itemName, value, cache ) );

        final Builder builder = new Builder ( this.value );

        if ( value != null )
        {
            builder.setValue ( value );
        }
        if ( attributes != null )
        {
            final Map<String, Variant> newAttributes = new HashMap<String, Variant> ( this.value.getAttributes () );
            AttributesHelper.mergeAttributes ( newAttributes, attributes, cache );
            builder.setAttributes ( newAttributes );
        }

        this.value = builder.build ();

        if ( ( !this.suppressCacheEvents && cache ) || !cache )
        {
            final boolean attributesChanged = attributes != null ? !attributes.isEmpty () : false;

            if ( ( value != null ) || ( attributesChanged && !this.suppressAttributeEvents ) )
            {
                fireChange ();
            }
        }
    }

    private void fireChange ()
    {
        if ( this.listener != null )
        {
            final String topic = this.alias != null ? this.alias : this.itemName;
            this.listener.itemEvent ( topic, this.value );
        }
    }

    public void setListener ( final ItemEventListener listener )
    {
        this.listener = listener;
    }

    public void setSuppressCacheEvents ( final boolean suppressCacheEvents )
    {
        this.suppressCacheEvents = suppressCacheEvents;
    }

    public void setAlias ( final String alias )
    {
        this.alias = alias;
    }
}
