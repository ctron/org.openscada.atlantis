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

package org.openscada.da.server.common.impl.stats;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

import org.openscada.core.Variant;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.da.server.common.impl.HiveCommon;
import org.openscada.utils.concurrent.DirectExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataItemCounterOutput implements CounterOutput
{

    private final static Logger logger = LoggerFactory.getLogger ( DataItemCounterOutput.class );

    private final DataItemInputChained averageItem;

    private final DataItemInputChained totalItem;

    private final MBeanServer mbs;

    private final CounterOutputMXBeanImpl averageExport;

    private final CounterOutputMXBeanImpl totalExport;

    private ObjectInstance averageInstance;

    private ObjectInstance totalInstance;

    private final String itemId;

    public DataItemCounterOutput ( final String itemId )
    {
        this.itemId = itemId;
        this.averageItem = new DataItemInputChained ( itemId + ".average", DirectExecutor.INSTANCE );
        this.totalItem = new DataItemInputChained ( itemId + ".total", DirectExecutor.INSTANCE );

        this.mbs = ManagementFactory.getPlatformMBeanServer ();

        this.averageExport = new CounterOutputMXBeanImpl ();
        this.totalExport = new CounterOutputMXBeanImpl ();
    }

    @Override
    public void register ( final HiveCommon hive, final FolderCommon rootFolder, final String description )
    {
        final Map<String, Variant> attributes = new HashMap<String, Variant> ();

        hive.registerItem ( this.averageItem );
        attributes.put ( "description", Variant.valueOf ( description + " - Average value" ) );
        rootFolder.add ( this.averageItem.getInformation ().getName (), this.averageItem, attributes );

        attributes.clear ();

        hive.registerItem ( this.totalItem );
        attributes.put ( "description", Variant.valueOf ( description + " - Total counter" ) );
        rootFolder.add ( this.totalItem.getInformation ().getName (), this.totalItem, attributes );

        try
        {
            this.averageInstance = this.mbs.registerMBean ( this.averageExport, makeName ( "org.openscada.da.server.common.stats", this.itemId, "average" ) );
            this.totalInstance = this.mbs.registerMBean ( this.totalExport, makeName ( "org.openscada.da.server.common.stats", this.itemId, "total" ) );
        }
        catch ( final Exception e )
        {
            logger.warn ( "Failed to export statistics", e );
        }
    }

    private ObjectName makeName ( final String domain, final String description, final String type ) throws MalformedObjectNameException, NullPointerException
    {
        final Hashtable<String, String> properties = new Hashtable<String, String> ( 2 );
        properties.put ( "value", description );
        properties.put ( "type", type );
        return new ObjectName ( domain, properties );
    }

    @Override
    public void unregister ( final HiveCommon hive, final FolderCommon rootFolder )
    {
        rootFolder.remove ( this.averageItem );
        hive.unregisterItem ( this.averageItem );

        rootFolder.remove ( this.totalItem );
        hive.unregisterItem ( this.totalItem );

        if ( this.averageInstance != null )
        {
            try
            {
                this.mbs.unregisterMBean ( this.averageInstance.getObjectName () );
            }
            catch ( final Exception e )
            {
                logger.warn ( "Failed to unregister average", e );
            }
            this.averageInstance = null;
        }
        if ( this.totalInstance != null )
        {
            try
            {
                this.mbs.unregisterMBean ( this.totalInstance.getObjectName () );
            }
            catch ( final Exception e )
            {
                logger.warn ( "Failed to unregister total", e );
            }
            this.totalInstance = null;
        }
    }

    @Override
    public void setTickValue ( final double average, final long total )
    {
        this.averageItem.updateData ( Variant.valueOf ( average ), null, null );
        this.totalItem.updateData ( Variant.valueOf ( total ), null, null );
    }

}
