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

package org.openscada.ae.monitor.datasource.common.bit;

import java.util.Map;
import java.util.concurrent.Executor;

import org.openscada.ae.Severity;
import org.openscada.ae.event.EventProcessor;
import org.openscada.ae.monitor.datasource.AbstractBooleanMonitor;
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

public class BitMonitor extends AbstractBooleanMonitor
{

    private Configuration configuration;

    public BitMonitor ( final BundleContext context, final Executor executor, final Interner<String> stringInterner, final ObjectPoolTracker<MasterItem> poolTracker, final EventProcessor eventProcessor, final String id, final String prefix, final String defaultMonitorType )
    {
        super ( context, executor, stringInterner, poolTracker, eventProcessor, id, BitMonitorFactory.FACTORY_ID, prefix, defaultMonitorType );
    }

    @Override
    protected int getDefaultHandlerPriority ()
    {
        return 600;
    }

    @Override
    protected void performBooleanUpdate ( final Builder builder, final boolean value )
    {
        if ( this.configuration == null || this.configuration.reference == null )
        {
            setOk ( builder.getValue (), Helper.getTimestamp ( builder ) );
            return;
        }

        final boolean failure = value != this.configuration.reference;
        if ( failure )
        {
            setFailure ( builder.getValue (), Helper.getTimestamp ( builder ), this.configuration.severity, this.configuration.requireAck );
        }
        else
        {
            setOk ( builder.getValue (), Helper.getTimestamp ( builder ) );
        }
    }

    @Override
    public synchronized void update ( final UserInformation userInformation, final Map<String, String> properties ) throws Exception
    {
        super.update ( userInformation, properties );

        final ConfigurationDataHelper cfg = new ConfigurationDataHelper ( properties );

        final Configuration c = new Configuration ( this.configuration, this );

        c.setReference ( userInformation, cfg.getBoolean ( "reference" ) );
        c.setSeverity ( userInformation, cfg.getEnum ( "severity", Severity.class, Severity.ALARM ) );
        c.setRequireAck ( userInformation, cfg.getBoolean ( "requireAck", false ) );

        c.sendEvents ();
        this.configuration = c;

        reprocess ();
    }

    @Override
    protected void injectAttributes ( final Builder builder )
    {
        super.injectAttributes ( builder );

        if ( this.configuration != null )
        {
            builder.setAttribute ( intern ( getPrefix () + ".reference" ), Variant.valueOf ( this.configuration.reference ) );
        }
    }

    @Override
    protected void handleConfigUpdate ( final Map<String, String> configUpdate, final Map<String, Variant> attributes, final WriteAttributeResults result )
    {
        super.handleConfigUpdate ( configUpdate, attributes, result );

        final Variant active = attributes.get ( getPrefix () + ".reference" ); //$NON-NLS-1$
        if ( active != null )
        {
            configUpdate.put ( "reference", "" + active.asBoolean () ); //$NON-NLS-1$ //$NON-NLS-2$
            result.put ( intern ( getPrefix () + ".reference" ), WriteAttributeResult.OK ); //$NON-NLS-1$
        }
    }
}
