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

package org.eclipse.scada.da.master.common;

import java.util.Date;
import java.util.Map;

import org.eclipse.scada.ae.Event;
import org.eclipse.scada.ae.Event.EventBuilder;
import org.eclipse.scada.ae.Event.Fields;
import org.eclipse.scada.ae.event.EventProcessor;
import org.eclipse.scada.ae.utils.AbstractBaseConfiguration;
import org.eclipse.scada.ca.ConfigurationAdministrator;
import org.eclipse.scada.core.Variant;
import org.eclipse.scada.da.client.DataItemValue;
import org.eclipse.scada.da.master.AbstractConfigurableMasterHandlerImpl;
import org.eclipse.scada.da.master.MasterItem;
import org.eclipse.scada.da.master.common.internal.Activator;
import org.eclipse.scada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.util.tracker.ServiceTracker;

public abstract class AbstractCommonHandlerImpl extends AbstractConfigurableMasterHandlerImpl
{

    public static class AbstractConfiguration extends AbstractBaseConfiguration
    {

        private final AbstractCommonHandlerImpl commonHandler;

        private final EventProcessor eventProcessor;

        public AbstractConfiguration ( final AbstractConfiguration currentConfiguration, final AbstractCommonHandlerImpl commonHandler, final EventProcessor eventProcessor )
        {
            super ( currentConfiguration );
            this.commonHandler = commonHandler;
            this.eventProcessor = eventProcessor;
        }

        @Override
        protected void injectEventAttributes ( final EventBuilder builder )
        {
            this.commonHandler.injectEventAttributes ( builder );
        }

        @Override
        protected void sendEvent ( final Event event )
        {
            this.eventProcessor.publishEvent ( event );
        }

    }

    private final String sourceName;

    public AbstractCommonHandlerImpl ( final String configurationId, final ObjectPoolTracker<MasterItem> poolTracker, final int priority, final ServiceTracker<ConfigurationAdministrator, ConfigurationAdministrator> caTracker, final String prefix, final String factoryId )
    {
        super ( configurationId, poolTracker, priority, caTracker, prefix, factoryId );
        this.sourceName = configurationId;
    }

    protected abstract void processDataUpdate ( Map<String, Object> context, final DataItemValue.Builder builder ) throws Exception;

    /**
     * Create a pre-filled event builder
     * 
     * @return a new event builder
     */
    protected EventBuilder createEventBuilder ()
    {
        final EventBuilder builder = Event.create ();

        builder.sourceTimestamp ( new Date () );
        builder.entryTimestamp ( new Date () );

        injectEventAttributes ( builder );

        return builder;
    }

    protected void injectEventAttributes ( final EventBuilder builder )
    {
        builder.attribute ( Fields.SOURCE, this.sourceName );
        builder.attributes ( this.eventAttributes );
    }

    @Override
    public void dataUpdate ( final Map<String, Object> context, final DataItemValue.Builder builder )
    {
        if ( builder == null )
        {
            return;
        }

        try
        {
            processDataUpdate ( context, builder );
        }
        catch ( final Throwable e )
        {
            builder.setAttribute ( getPrefixed ( "error", Activator.getStringInterner () ), Variant.TRUE );
            builder.setAttribute ( getPrefixed ( "error.message", Activator.getStringInterner () ), Variant.valueOf ( e.getMessage () ) );
        }
    }

}