/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.ae.server.akn.proxy;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.openscada.ae.connection.provider.ConnectionService;
import org.openscada.ae.server.common.akn.AknHandler;
import org.openscada.ca.ConfigurationDataHelper;
import org.openscada.ca.ConfigurationFactory;
import org.openscada.core.connection.provider.ConnectionIdTracker;
import org.openscada.sec.UserInformation;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyAknService implements AknHandler, ConfigurationFactory
{

    private final static Logger logger = LoggerFactory.getLogger ( ProxyAknService.class );

    public static final String FACTORY_ID = "org.openscada.ae.server.akn.proxy";

    private static final class EntryPriorityComparator implements Comparator<Entry>
    {
        public static final EntryPriorityComparator INSTANCE = new EntryPriorityComparator ();

        @Override
        public int compare ( final Entry o1, final Entry o2 )
        {
            return Integer.valueOf ( o1.priority ).compareTo ( o2.priority );
        }
    }

    private static class Entry
    {
        private final Pattern pattern;

        private final boolean authorative;

        private final String connectionId;

        private final String id;

        private final int priority;

        public Entry ( final String id, final int priority, final Pattern pattern, final boolean authorative, final String connectionId )
        {
            this.id = id;
            this.priority = priority;
            this.pattern = pattern;
            this.authorative = authorative;
            this.connectionId = connectionId;
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
            final Entry other = (Entry)obj;
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
    }

    private final List<Entry> entries = new LinkedList<Entry> ();

    private final BundleContext context;

    public ProxyAknService ( final BundleContext context )
    {
        this.context = context;
    }

    @Override
    public void update ( final UserInformation userInformation, final String configurationId, final Map<String, String> parameters ) throws Exception
    {
        final ConfigurationDataHelper cfg = new ConfigurationDataHelper ( parameters );

        // first parse
        final Entry entry = new Entry ( configurationId, cfg.getIntegerChecked ( "priority", "'priority' must be set" ), Pattern.compile ( cfg.getStringNonEmpty ( "pattern" ) ), cfg.getBoolean ( "authorative", true ), cfg.getStringNonEmpty ( "connectionId" ) );

        // now modify
        // FIXME: write lock
        delete ( userInformation, configurationId );
        this.entries.add ( entry );
        Collections.sort ( this.entries, EntryPriorityComparator.INSTANCE );
    }

    @Override
    public void delete ( final UserInformation userInformation, final String configurationId ) throws Exception
    {
        // FIXME: write lock
        final Iterator<Entry> i = this.entries.iterator ();
        while ( i.hasNext () )
        {
            if ( i.next ().id.equals ( configurationId ) )
            {
                i.remove ();
            }
        }
    }

    @Override
    public boolean acknowledge ( final String monitorId, final UserInformation userInformation, final Date aknTimestamp )
    {
        logger.info ( "acknowledge - monitorId: {}, userInformation: {}, aknTimestamp: {}", new Object[] { monitorId, userInformation, aknTimestamp } );

        int matches = 0;

        // FIXME: read lock
        for ( final Entry entry : this.entries )
        {
            if ( entry.pattern.matcher ( monitorId ).matches () )
            {
                matches++;
                akn ( entry.connectionId, monitorId, userInformation, aknTimestamp );
                if ( entry.authorative )
                {
                    break;
                }
            }
        }

        return matches > 0;
    }

    private void akn ( final String connectionId, final String monitorId, final UserInformation userInformation, final Date aknTimestamp )
    {
        logger.info ( "passing on acknowledge - connectionid: {}, monitorId: {}, userInformation: {}, aknTimestamp: {}", new Object[] { connectionId, monitorId, userInformation, aknTimestamp } );

        final ConnectionIdTracker tracker = new ConnectionIdTracker ( this.context, connectionId, null, ConnectionService.class );
        tracker.open ();
        try
        {
            final ConnectionService connection = (ConnectionService)tracker.waitForService ( 1000 );
            connection.getConnection ().acknowledge ( monitorId, aknTimestamp );
        }
        catch ( final InterruptedException e )
        {
            logger.warn ( "Failed to wait for connection: " + connectionId, e );
        }
        finally
        {
            tracker.close ();
        }
    }
}
