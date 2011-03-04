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

import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.core.subscription.SubscriptionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValueEventObserver extends AbstractItemEventObserver
{

    private final static Logger logger = LoggerFactory.getLogger ( ValueEventObserver.class );

    protected ValueEventListener listener;

    protected String alias;

    /**
     * If set to <code>true</code> then the cache events will be ignored.
     */
    protected boolean ignoreCache = true;

    @Override
    public void notifySubscriptionChange ( final SubscriptionState state, final Throwable e )
    {
        // NO OP
    }

    @Override
    public void notifyDataChange ( final Variant value, final Map<String, Variant> attributes, final boolean cache )
    {
        final ValueEventListener listener = this.listener;

        logger.info ( "Cache: {}, Ignore: {}", cache, this.ignoreCache );
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
