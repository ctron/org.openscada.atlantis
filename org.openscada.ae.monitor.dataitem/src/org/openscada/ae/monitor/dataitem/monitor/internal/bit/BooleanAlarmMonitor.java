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

package org.openscada.ae.monitor.dataitem.monitor.internal.bit;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.Executor;

import org.openscada.ae.Event.EventBuilder;
import org.openscada.ae.event.EventProcessor;
import org.openscada.ae.monitor.common.EventHelper;
import org.openscada.ae.monitor.dataitem.AbstractBooleanMonitor;
import org.openscada.ae.monitor.dataitem.DataItemMonitor;
import org.openscada.core.Variant;
import org.openscada.da.client.DataItemValue.Builder;
import org.openscada.da.core.WriteAttributeResult;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.sec.UserInformation;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.BundleContext;

public class BooleanAlarmMonitor extends AbstractBooleanMonitor implements DataItemMonitor
{

    public static final String FACTORY_ID = "ae.monitor.da.booleanAlarm";

    private boolean reference;

    private final int defaultPriority;

    public BooleanAlarmMonitor ( final BundleContext context, final Executor executor, final ObjectPoolTracker poolTracker, final EventProcessor eventProcessor, final String id, final int defaultPriority )
    {
        super ( context, executor, poolTracker, eventProcessor, id, "ae.monitor.booleanAlarm", "VALUE" );
        this.defaultPriority = defaultPriority;
    }

    @Override
    protected int getDefaultPriority ()
    {
        return this.defaultPriority;
    }

    @Override
    protected String getFactoryId ()
    {
        return FACTORY_ID;
    }

    @Override
    protected String getConfigurationId ()
    {
        return getId ();
    }

    @Override
    public void update ( final UserInformation userInformation, final Map<String, String> properties ) throws Exception
    {
        super.update ( userInformation, properties );

        final boolean newReference = Boolean.parseBoolean ( properties.get ( "reference" ) );
        if ( isDifferent ( this.reference, newReference ) )
        {
            final EventBuilder builder = EventHelper.newConfigurationEvent ( userInformation, getId (), "Change reference value", Variant.valueOf ( newReference ), new Date () );
            injectEventAttributes ( builder );
            publishEvent ( builder );
            this.reference = newReference;
        }

        update ();
    }

    @Override
    protected void injectAttributes ( final Builder builder )
    {
        super.injectAttributes ( builder );
        builder.setAttribute ( this.prefix + ".reference", Variant.valueOf ( this.reference ) );
    }

    @Override
    protected void handleConfigUpdate ( final Map<String, String> configUpdate, final Map<String, Variant> attributes, final WriteAttributeResults result )
    {
        super.handleConfigUpdate ( configUpdate, attributes, result );
        final Variant reference = attributes.get ( this.prefix + ".reference" );
        if ( reference != null )
        {
            configUpdate.put ( "reference", reference.asBoolean () ? "true" : "false" );
            result.put ( this.prefix + ".reference", WriteAttributeResult.OK );
        }
    }

    @Override
    protected void update ()
    {
        if ( this.value == null || this.timestamp == null )
        {
            setUnsafe ();
        }
        else if ( this.value == this.reference )
        {
            setOk ( Variant.valueOf ( this.value ), this.timestamp );
        }
        else
        {
            setFailure ( Variant.valueOf ( this.value ), this.timestamp );
        }
    }
}
