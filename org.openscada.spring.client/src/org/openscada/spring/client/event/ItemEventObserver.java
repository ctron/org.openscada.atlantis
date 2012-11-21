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

package org.openscada.spring.client.event;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.openscada.core.AttributesHelper;
import org.openscada.core.Variant;
import org.openscada.core.subscription.SubscriptionState;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.client.DataItemValue.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ItemEventObserver extends AbstractItemEventObserver
{

    private final static Logger logger = LoggerFactory.getLogger ( ItemEventObserver.class );

    private DataItemValue value = DataItemValue.DISCONNECTED;

    private ItemEventListener listener = null;

    /**
     * a flag which indicates if cache events should be suppressed or not
     */
    private boolean suppressCacheEvents = true;

    /**
     * a flag which indicates if attribute events should be suppressed or not
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
    private Level valueLogLevel = Level.FINER;

    public void setValueLogLevel ( final String valueLogLevel )
    {
        this.valueLogLevel = Level.parse ( valueLogLevel );
    }

    public void setSuppressAttributeEvents ( final boolean suppressAttributeEvents )
    {
        this.suppressAttributeEvents = suppressAttributeEvents;
    }

    @Override
    public void notifySubscriptionChange ( final SubscriptionState subscriptionState, final Throwable subscriptionError )
    {
        log ( String.format ( "Subscription change for item '%s' to '%s'", this.itemName, subscriptionState ) );

        final Builder builder = new Builder ( this.value );

        builder.setSubscriptionState ( subscriptionState );
        switch ( subscriptionState )
        {
        case DISCONNECTED:
        case GRANTED:
            builder.setAttributes ( new HashMap<String, Variant> ( 0 ) );
            builder.setValue ( Variant.NULL );
            break;
        }
        this.value = builder.build ();
        fireChange ();
    }

    private void log ( final String string )
    {
        if ( this.valueLogLevel == Level.FINEST || this.valueLogLevel == Level.FINER )
        {
            logger.debug ( string );
        }
        else if ( this.valueLogLevel == Level.FINE )
        {
            logger.info ( string );
        }
        else if ( this.valueLogLevel == Level.INFO )
        {
            logger.info ( string );
        }
        else if ( this.valueLogLevel == Level.WARNING )
        {
            logger.warn ( string );
        }
    }

    @Override
    public void notifyDataChange ( final Variant value, final Map<String, Variant> attributes, final boolean cache )
    {
        log ( String.format ( "Value change for item '%s' to '%s' (cache: %s)", this.itemName, value, cache ) );

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

        if ( !this.suppressCacheEvents && cache || !cache )
        {
            final boolean attributesChanged = attributes != null ? !attributes.isEmpty () : false;

            if ( value != null || attributesChanged && !this.suppressAttributeEvents )
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
