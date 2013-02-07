/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.core.server.common.stats;

import java.lang.management.ManagementFactory;
import java.net.SocketAddress;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.openscada.core.info.StatisticEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ManagedConnection implements ManagedConnectionMXBean
{

    private final static Logger logger = LoggerFactory.getLogger ( ManagedConnection.class );

    private ObjectName name;

    public ManagedConnection ()
    {

    }

    public void setName ( final ObjectName name )
    {
        this.name = name;
    }

    public void dispose ()
    {
        final MBeanServer mbs = ManagementFactory.getPlatformMBeanServer ();
        try
        {
            mbs.unregisterMBean ( this.name );
        }
        catch ( final Exception e )
        {
            logger.warn ( "Failed to unregister MXBean", e );
        }
    }

    @Override
    public StatisticInformation[] getStatistics ()
    {
        final Collection<StatisticInformation> result = new ArrayList<StatisticInformation> ();
        for ( final StatisticEntry entry : getEntries () )
        {
            if ( entry.getValue () != null )
            {
                result.add ( new StatisticInformation ( entry.getLabel (), makeDouble ( entry.getValue ().getCurrent () ), makeDouble ( entry.getValue ().getMinimum () ), makeDouble ( entry.getValue ().getMaximum () ) ) );
            }
        }
        return result.toArray ( new StatisticInformation[result.size ()] );

    }

    private Double makeDouble ( final Number number )
    {
        if ( number == null )
        {
            return null;
        }
        else
        {
            return number.doubleValue ();
        }
    }

    protected abstract Collection<StatisticEntry> getEntries ();

    public static ManagedConnection register ( final ManagedConnection connection, final SocketAddress socketAddress, final String baseName )
    {
        try
        {
            final MBeanServer mbs = ManagementFactory.getPlatformMBeanServer ();
            logger.debug ( "Creating name for: {}", socketAddress );

            final String remote = URLEncoder.encode ( socketAddress.toString (), "UTF-8" );

            final ObjectName name = new ObjectName ( baseName, "remote", remote );
            connection.setName ( name );
            mbs.registerMBean ( connection, name );
            return connection;
        }
        catch ( final Exception e )
        {
            logger.warn ( "Failed to register MXBean", e );
            return null;
        }

    }
}
