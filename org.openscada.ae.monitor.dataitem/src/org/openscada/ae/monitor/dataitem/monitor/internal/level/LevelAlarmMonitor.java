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

package org.openscada.ae.monitor.dataitem.monitor.internal.level;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.Executor;

import org.openscada.ae.Event.EventBuilder;
import org.openscada.ae.event.EventProcessor;
import org.openscada.ae.monitor.common.EventHelper;
import org.openscada.ae.monitor.dataitem.AbstractNumericMonitor;
import org.openscada.ae.monitor.dataitem.DataItemMonitor;
import org.openscada.ca.ConfigurationDataHelper;
import org.openscada.core.Variant;
import org.openscada.da.client.DataItemValue.Builder;
import org.openscada.da.core.WriteAttributeResult;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.master.MasterItem;
import org.openscada.sec.UserInformation;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Interner;

public class LevelAlarmMonitor extends AbstractNumericMonitor implements DataItemMonitor
{

    private final static Logger logger = LoggerFactory.getLogger ( LevelAlarmMonitor.class );

    private Double limit;

    private final boolean lowerOk;

    private final int priority;

    private final boolean cap;

    private boolean failure;

    private final boolean includedOk;

    public LevelAlarmMonitor ( final BundleContext context, final Executor executor, final Interner<String> stringInterner, final ObjectPoolTracker<MasterItem> poolTracker, final EventProcessor eventProcessor, final String id, final String prefix, final String defaultMonitorType, final boolean lowerOk, final boolean includedOk, final int priority, final boolean cap )
    {
        super ( context, executor, stringInterner, poolTracker, eventProcessor, id, prefix, defaultMonitorType );
        this.lowerOk = lowerOk;
        this.includedOk = includedOk;
        this.priority = priority;
        this.cap = cap;
    }

    @Override
    protected String getFactoryId ()
    {
        return this.prefix;
    }

    @Override
    protected String getConfigurationId ()
    {
        return getId ();
    }

    @Override
    public synchronized void update ( final UserInformation userInformation, final Map<String, String> properties ) throws Exception
    {
        super.update ( userInformation, properties );

        final ConfigurationDataHelper cfg = new ConfigurationDataHelper ( properties );

        final Double newLimit = cfg.getDouble ( "preset" ); //$NON-NLS-1$

        logger.debug ( "New limit: {}", newLimit ); //$NON-NLS-1$

        if ( newLimit == null )
        {
            setActive ( userInformation, false );
        }

        if ( isDifferent ( this.limit, newLimit ) )
        {
            this.limit = newLimit;
            if ( !isInitialUpdate () )
            {
                final EventBuilder builder = EventHelper.newConfigurationEvent ( userInformation, getId (), Messages.getString ( "LevelAlarmMonitor.message.changePreset" ), Variant.valueOf ( newLimit ), new Date () ); //$NON-NLS-1$
                builder.setAllowOverrideAttributes ( false );
                injectEventAttributes ( builder );
                publishEvent ( builder );
            }
        }

        reprocess ();
    }

    @Override
    protected boolean isError ()
    {
        return this.cap;
    }

    @Override
    protected void injectAttributes ( final Builder builder )
    {
        super.injectAttributes ( builder );

        final boolean active = isActive ();

        if ( active )
        {
            builder.setAttribute ( intern ( this.prefix + ".preset" ), Variant.valueOf ( this.limit ) ); //$NON-NLS-1$
        }
        builder.setAttribute ( intern ( this.prefix + ".active" ), active ? Variant.TRUE : Variant.FALSE ); //$NON-NLS-1$

        if ( this.cap && this.failure )
        {
            builder.setAttribute ( intern ( this.prefix + ".value.original" ), Variant.valueOf ( this.value ) ); //$NON-NLS-1$
        }
    }

    @Override
    protected void handleConfigUpdate ( final Map<String, String> configUpdate, final Map<String, Variant> attributes, final WriteAttributeResults result )
    {
        super.handleConfigUpdate ( configUpdate, attributes, result );

        final Variant active = attributes.get ( this.prefix + ".active" ); //$NON-NLS-1$
        if ( active != null )
        {
            configUpdate.put ( "active", "" + active.asBoolean () ); //$NON-NLS-1$ //$NON-NLS-2$
        }

        final Variant preset = attributes.get ( this.prefix + ".preset" ); //$NON-NLS-1$
        if ( preset != null )
        {
            if ( preset.isNull () )
            {
                configUpdate.put ( "active", "" + false ); //$NON-NLS-1$ //$NON-NLS-2$
            }
            else
            {
                configUpdate.put ( "preset", "" + preset.asDouble ( 0.0 ) ); //$NON-NLS-1$ //$NON-NLS-2$
            }
            result.put ( intern ( this.prefix + ".preset" ), WriteAttributeResult.OK ); //$NON-NLS-1$
        }

    }

    @Override
    protected int getDefaultPriority ()
    {
        return this.priority;
    }

    @Override
    protected synchronized void update ( final Builder builder )
    {
        logger.debug ( "Handle data update: {} (value: {}, timestamp: {}, limit: {})", new Object[] { builder, this.value, this.timestamp, this.limit } ); //$NON-NLS-1$

        if ( this.value == null || this.timestamp == null || this.limit == null )
        {
            setUnsafe ();
            return;
        }
        else if ( !LevelHelper.isFailure ( this.value.doubleValue (), this.limit, this.lowerOk, this.includedOk ) )
        {
            this.failure = false;
            setOk ( Variant.valueOf ( this.value ), this.timestamp );
        }
        else
        {
            this.failure = true;
            if ( this.cap && isActive () )
            {
                builder.setValue ( Variant.valueOf ( this.limit ) );
            }
            setFailure ( Variant.valueOf ( this.value ), this.timestamp );
        }
    }

}
