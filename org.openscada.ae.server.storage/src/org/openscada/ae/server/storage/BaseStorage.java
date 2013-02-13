/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
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

package org.openscada.ae.server.storage;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

import org.openscada.ae.Event;
import org.openscada.ae.Event.EventBuilder;
import org.openscada.core.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseStorage implements Storage
{

    private final static Logger logger = LoggerFactory.getLogger ( BaseStorage.class );

    private static final boolean allowEntryTimestamp = Boolean.getBoolean ( "org.openscada.ae.server.storage.allowExternalEntryTimestamp" ); //$NON-NLS-1$

    private static final String providedNodeId = System.getProperty ( "org.openscada.ae.server.storage.nodeId" ); //$NON-NLS-1$

    private final Variant nodeId;

    public BaseStorage ()
    {
        if ( providedNodeId != null )
        {
            this.nodeId = Variant.valueOf ( providedNodeId );
        }
        else
        {
            this.nodeId = Variant.valueOf ( getHostname () );
        }
    }

    private static String getHostname ()
    {
        try
        {
            return InetAddress.getLocalHost ().getCanonicalHostName ();
        }
        catch ( final UnknownHostException e )
        {
            logger.warn ( "Failed to obtain hostname", e );
            return "<unknown>";
        }
    }

    @Override
    public Event store ( final Event event )
    {
        return store ( event, null );
    }

    protected Event createEvent ( final Event event )
    {
        final EventBuilder builder = Event.create ().event ( event ).id ( UUID.randomUUID () );

        final Date now = new GregorianCalendar ().getTime ();

        if ( !allowEntryTimestamp || event.getEntryTimestamp () == null )
        {
            // if we are not allowed to have prefilled entryTimestamps
            // or a missing the timestamp anyway
            builder.entryTimestamp ( now );
        }
        if ( event.getSourceTimestamp () == null )
        {
            builder.sourceTimestamp ( now );
        }

        builder.attribute ( "nodeId", this.nodeId );

        return builder.build ();
    }

    @Override
    public Event update ( final UUID id, final String comment ) throws Exception
    {
        return update ( id, comment, null );
    }
}
