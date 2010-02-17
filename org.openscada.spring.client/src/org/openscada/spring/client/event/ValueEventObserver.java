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

import java.util.Map;

import org.apache.log4j.Logger;
import org.openscada.core.Variant;
import org.openscada.core.subscription.SubscriptionState;

public class ValueEventObserver extends AbstractItemEventObserver
{
    private static Logger log = Logger.getLogger ( ValueEventObserver.class );

    protected ValueEventListener listener;

    protected String alias;

    /**
     * If set to <code>true</code> then the cache events will be ignored.
     */
    protected boolean ignoreCache = true;

    public void notifySubscriptionChange ( final SubscriptionState state, final Throwable e )
    {
        // NO OP
    }

    public void notifyDataChange ( final Variant value, final Map<String, Variant> attributes, final boolean cache )
    {
        final ValueEventListener listener = this.listener;

        log.info ( String.format ( "Cache: %s, Ignore: %s", cache, this.ignoreCache ) );
        // if we have a listener
        if ( listener != null && value != null )
        {
            if ( !this.ignoreCache && cache || !cache )
            {
                final String topic = this.alias != null ? this.alias : this.itemName;
                listener.valueEvent ( topic, value );
            }
        }
    }

    public void setListener ( final ValueEventListener listener )
    {
        this.listener = listener;
    }

    public void setAlias ( final String alias )
    {
        this.alias = alias;
    }

    public void setIgnoreCache ( final boolean ignoreCache )
    {
        this.ignoreCache = ignoreCache;
    }

}
