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

package org.openscada.ae.monitor.datasource.common.list;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import org.openscada.ae.event.EventProcessor;
import org.openscada.ae.monitor.datasource.AbstractVariantMonitor;
import org.openscada.ae.monitor.datasource.Helper;
import org.openscada.ae.monitor.datasource.common.list.Configuration.ListSeverity;
import org.openscada.ca.ConfigurationDataHelper;
import org.openscada.core.Variant;
import org.openscada.core.VariantEditor;
import org.openscada.da.client.DataItemValue.Builder;
import org.openscada.da.master.MasterItem;
import org.openscada.sec.UserInformation;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.BundleContext;

import com.google.common.collect.Interner;

public class ListMonitor extends AbstractVariantMonitor
{

    private Configuration configuration;

    public ListMonitor ( final BundleContext context, final Executor executor, final Interner<String> stringInterner, final ObjectPoolTracker<MasterItem> poolTracker, final EventProcessor eventProcessor, final String id, final String prefix, final String defaultMonitorType )
    {
        super ( context, executor, stringInterner, poolTracker, eventProcessor, id, ListMonitorFactory.FACTORY_ID, prefix, defaultMonitorType );
    }

    @Override
    public synchronized void update ( final UserInformation userInformation, final Map<String, String> properties ) throws Exception
    {
        super.update ( userInformation, properties );

        final ConfigurationDataHelper cfg = new ConfigurationDataHelper ( properties );

        final Configuration c = new Configuration ( this.configuration, this );

        // parse configuration
        c.setDefaultAck ( cfg.getBoolean ( "defaultAck", false ) );
        c.setDefaultSeverity ( cfg.getEnum ( "defaultSeverity", ListSeverity.class, ListSeverity.OK ) );

        // read in severities
        final Map<Variant, ListSeverity> severityMap = new HashMap<Variant, Configuration.ListSeverity> ();
        for ( final ListSeverity severity : ListSeverity.values () )
        {
            for ( final String str : cfg.getPrefixed ( "values." + severity.name () + "." ).values () )
            {
                severityMap.put ( VariantEditor.toVariant ( str ), severity );
            }
        }
        c.setSeverityMap ( severityMap );

        // read on ack flags
        final Map<Variant, Boolean> ackMap = new HashMap<Variant, Boolean> ();
        for ( final String str : cfg.getPrefixed ( "values.ack." ).values () )
        {
            ackMap.put ( VariantEditor.toVariant ( str ), true );
        }
        for ( final String str : cfg.getPrefixed ( "values.nak." ).values () )
        {
            ackMap.put ( VariantEditor.toVariant ( str ), false );
        }
        c.setAckMap ( ackMap );

        // apply
        c.sendEvents ();
        this.configuration = c;

        reprocess ();
    }

    @Override
    protected void performValueUpdate ( final Builder builder, final Variant value )
    {
        if ( this.configuration == null )
        {
            setOk ( value, Helper.getTimestamp ( builder ) );
            return;
        }

        Boolean requireAck = this.configuration.ackMap.get ( value );
        ListSeverity severity = this.configuration.severityMap.get ( value );

        if ( requireAck == null )
        {
            requireAck = this.configuration.defaultAck;
        }
        if ( severity == null )
        {
            severity = this.configuration.defaultSeverity;
        }

        if ( severity.getSeverity () == null )
        {
            setOk ( value, Helper.getTimestamp ( builder ) );
        }
        else
        {
            setFailure ( value, Helper.getTimestamp ( builder ), severity.getSeverity (), requireAck );
        }
    }
}
