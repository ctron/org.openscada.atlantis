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

package org.openscada.ae.monitor.datasource.common.level;

import java.util.Map;
import java.util.concurrent.Executor;

import org.openscada.ae.event.EventProcessor;
import org.openscada.ae.monitor.common.Severity;
import org.openscada.ae.monitor.datasource.AbstractNumericMonitor;
import org.openscada.ae.monitor.datasource.Helper;
import org.openscada.ca.ConfigurationDataHelper;
import org.openscada.core.Variant;
import org.openscada.da.client.DataItemValue.Builder;
import org.openscada.da.core.WriteAttributeResult;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.master.MasterItem;
import org.openscada.sec.UserInformation;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.BundleContext;

import com.google.common.collect.Interner;

public class LevelMonitor extends AbstractNumericMonitor
{

    private final String firstPrefix;

    private Configuration configuration;

    public LevelMonitor ( final BundleContext context, final Executor executor, final Interner<String> stringInterner, final ObjectPoolTracker<MasterItem> poolTracker, final EventProcessor eventProcessor, final String id, final String prefix, final String defaultMonitorType )
    {
        super ( context, executor, stringInterner, poolTracker, eventProcessor, id, LevelMonitorFactory.FACTORY_ID, prefix, defaultMonitorType );
        this.firstPrefix = prefix;
    }

    @Override
    protected void performNumericUpdate ( final Builder builder, final Number value )
    {
        if ( this.configuration == null || this.configuration.preset == null )
        {
            setOk ( builder.getValue (), Helper.getTimestamp ( builder ) );
            return;
        }

        final Variant originalValue = builder.getValue ();

        final boolean failure = LevelHelper.isFailure ( value.doubleValue (), this.configuration.preset, this.configuration.lowerOk, this.configuration.includedOk );
        if ( failure )
        {
            if ( this.configuration.cap )
            {
                builder.setValue ( capValue ( this.configuration.preset, value ) );
            }
            setFailure ( originalValue, Helper.getTimestamp ( builder ), this.configuration.severity, this.configuration.requireAck );
        }
        else
        {
            setOk ( originalValue, Helper.getTimestamp ( builder ) );
        }
    }

    private Variant capValue ( final double preset, final Number value )
    {
        if ( this.configuration.lowerOk )
        {
            if ( value instanceof Long )
            {
                return Variant.valueOf ( Math.min ( (long)preset, value.longValue () ) );
            }
            if ( value instanceof Integer )
            {
                return Variant.valueOf ( Math.min ( (int)preset, value.intValue () ) );
            }
            return Variant.valueOf ( Math.min ( preset, value.doubleValue () ) );
        }
        else
        {
            if ( value instanceof Long )
            {
                return Variant.valueOf ( Math.max ( (long)preset, value.longValue () ) );
            }
            if ( value instanceof Integer )
            {
                return Variant.valueOf ( Math.max ( (int)preset, value.intValue () ) );
            }
            return Variant.valueOf ( Math.max ( preset, value.doubleValue () ) );
        }
    }

    @Override
    public synchronized void update ( final UserInformation userInformation, final Map<String, String> properties ) throws Exception
    {
        super.update ( userInformation, properties );

        final ConfigurationDataHelper cfg = new ConfigurationDataHelper ( properties );

        final Configuration c = new Configuration ( this.configuration, this );

        c.setPreset ( userInformation, cfg.getDouble ( "preset" ) );
        c.setLowerOk ( userInformation, cfg.getBoolean ( "lowerOk" ) );
        c.setIncludedOk ( userInformation, cfg.getBoolean ( "includedOk", true ) );
        c.setSecondPrefix ( userInformation, cfg.getString ( "prefix", null ) );
        c.setCap ( userInformation, cfg.getBoolean ( "cap", false ) );
        c.setSeverity ( userInformation, cfg.getEnum ( "severity", Severity.class, Severity.ALARM ) );

        this.configuration = c;
        c.sendEvents ();

        if ( this.configuration.secondPrefix != null )
        {
            this.prefix = this.firstPrefix + "." + this.configuration.secondPrefix;
        }
        else
        {
            this.prefix = this.firstPrefix;
        }

        reprocess ();
    }

    @Override
    protected void injectAttributes ( final Builder builder )
    {
        super.injectAttributes ( builder );

        builder.setAttribute ( intern ( this.prefix + ".preset" ), Variant.valueOf ( this.configuration.preset ) );
        builder.setAttribute ( intern ( this.prefix + ".lowerOk" ), Variant.valueOf ( this.configuration.lowerOk ) );
        builder.setAttribute ( intern ( this.prefix + ".includedOk" ), Variant.valueOf ( this.configuration.includedOk ) );
    }

    @Override
    protected void handleConfigUpdate ( final Map<String, String> configUpdate, final Map<String, Variant> attributes, final WriteAttributeResults result )
    {
        super.handleConfigUpdate ( configUpdate, attributes, result );

        final Variant preset = attributes.get ( this.prefix + ".preset" ); //$NON-NLS-1$
        if ( preset != null )
        {
            configUpdate.put ( "preset", "" + preset.asDouble ( 0.0 ) ); //$NON-NLS-1$ //$NON-NLS-2$
            result.put ( intern ( this.prefix + ".preset" ), WriteAttributeResult.OK ); //$NON-NLS-1$
        }
    }
}
