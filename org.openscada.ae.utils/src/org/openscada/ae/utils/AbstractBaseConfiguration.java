/*
 * This file is part of the openSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.ae.utils;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.scada.sec.UserInformation;
import org.openscada.ae.Event;
import org.openscada.ae.Event.EventBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractBaseConfiguration
{

    private final static Logger logger = LoggerFactory.getLogger ( AbstractBaseConfiguration.class );

    private final List<Event> events = new LinkedList<Event> ();

    protected abstract void injectEventAttributes ( final EventBuilder builder );

    protected abstract void sendEvent ( final Event event );

    protected final boolean initialUpdate;

    public AbstractBaseConfiguration ( final AbstractBaseConfiguration currentConfiguration )
    {
        super ();
        this.initialUpdate = currentConfiguration == null;
    }

    public void sendEvents ()
    {
        if ( !this.initialUpdate )
        {
            for ( final Event event : this.events )
            {
                logger.debug ( "Sending event: {}", event );
                sendEvent ( event );
            }
        }
        this.events.clear ();
    }

    protected void addEvent ( final EventBuilder builder )
    {
        this.events.add ( builder.build () );
    }

    protected EventBuilder create ( final Object value, final UserInformation userInformation )
    {
        final EventBuilder builder = Event.create ();

        injectEventAttributes ( builder );

        builder.attribute ( Event.Fields.EVENT_TYPE, "CFG" );

        if ( userInformation != null )
        {
            builder.attribute ( Event.Fields.ACTOR_TYPE, "USER" );
            builder.attribute ( Event.Fields.ACTOR_NAME, userInformation.getName () );
        }

        builder.attribute ( Event.Fields.VALUE, value );

        return builder;
    }

    protected <T> T update ( final UserInformation userInformation, final T oldValue, final T newValue )
    {
        if ( oldValue == newValue )
        {
            // both are equal ... no event
            return newValue;
        }

        if ( oldValue == null )
        {
            // the old value is null but the new is not ... send event
            addEvent ( create ( newValue, userInformation ) );
            return newValue;
        }

        if ( oldValue.equals ( newValue ) )
        {
            // old value and new value are equal ... no event
            return newValue;
        }

        // old value and new value or not equal ... send event
        addEvent ( create ( newValue, userInformation ) );
        return newValue;
    }

}