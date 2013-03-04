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

package org.openscada.da.master.common.marker;

import java.util.HashMap;
import java.util.Map;

import org.openscada.ae.Event.EventBuilder;
import org.openscada.ae.Event.Fields;
import org.openscada.ae.event.EventProcessor;
import org.openscada.ca.ConfigurationAdministrator;
import org.openscada.ca.ConfigurationDataHelper;
import org.openscada.core.Variant;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.client.DataItemValue.Builder;
import org.openscada.da.core.OperationParameters;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.master.MasterItem;
import org.openscada.da.master.common.AbstractCommonHandlerImpl;
import org.openscada.sec.UserInformation;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.util.tracker.ServiceTracker;

public class MarkerHandlerImpl extends AbstractCommonHandlerImpl
{

    private static class Configuration extends AbstractConfiguration
    {
        private boolean active;

        private boolean exportAttribute;

        private boolean alwaysExport;

        private Map<String, Object> markers;

        public Configuration ( final Configuration currentConfiguration, final AbstractCommonHandlerImpl commonHandler, final EventProcessor eventProcessor )
        {
            super ( currentConfiguration, commonHandler, eventProcessor );
            if ( currentConfiguration != null )
            {
                this.active = currentConfiguration.active;
                this.exportAttribute = currentConfiguration.exportAttribute;
                this.alwaysExport = currentConfiguration.alwaysExport;
                this.markers = currentConfiguration.markers;
            }
        }

        public void setActive ( final UserInformation userInformation, final boolean active )
        {
            this.active = update ( userInformation, this.active, active );
        }

        public void setExportAttribute ( final boolean exportAttribute )
        {
            this.exportAttribute = exportAttribute;
        }

        public void setAlwaysExport ( final boolean alwaysExport )
        {
            this.alwaysExport = alwaysExport;
        }

        public void setMarkers ( final Map<String, Object> markers )
        {
            this.markers = markers;
        }

    }

    private Configuration configuration;

    private final EventProcessor eventProcessor;

    public MarkerHandlerImpl ( final String configurationId, final EventProcessor eventProcessor, final ObjectPoolTracker<MasterItem> poolTracker, final int priority, final ServiceTracker<ConfigurationAdministrator, ConfigurationAdministrator> caTracker )
    {
        super ( configurationId, poolTracker, priority, caTracker, MarkerHandlerFactoryImpl.FACTORY_ID + "." + configurationId, MarkerHandlerFactoryImpl.FACTORY_ID );
        this.eventProcessor = eventProcessor;
    }

    @Override
    protected void processDataUpdate ( final Map<String, Object> context, final DataItemValue.Builder builder ) throws Exception
    {
        injectAttributes ( context, builder );
    }

    protected void injectAttributes ( final Map<String, Object> context, final Builder builder )
    {
        if ( this.configuration == null )
        {
            return;
        }

        if ( this.configuration.active )
        {
            context.putAll ( this.configuration.markers );
        }

        if ( this.configuration.exportAttribute )
        {
            if ( this.configuration.active || this.configuration.alwaysExport )
            {
                builder.setAttribute ( getPrefixed ( "active" ), Variant.valueOf ( this.configuration.active ) ); //$NON-NLS-1$
            }
        }
    }

    @Override
    public synchronized void update ( final UserInformation userInformation, final Map<String, String> parameters ) throws Exception
    {
        super.update ( userInformation, parameters );

        final ConfigurationDataHelper cfg = new ConfigurationDataHelper ( parameters );

        final Configuration c = new Configuration ( this.configuration, this, this.eventProcessor );
        c.setActive ( userInformation, cfg.getBoolean ( "active", false ) );
        c.setExportAttribute ( cfg.getBoolean ( "exportAttribute", false ) );
        c.setAlwaysExport ( cfg.getBoolean ( "alwaysExport", false ) );

        // get markers
        final Map<String, Object> markers = new HashMap<String, Object> ();
        for ( final Map.Entry<String, String> entry : cfg.getPrefixed ( "marker." ).entrySet () )
        {
            final String value = entry.getValue ();
            if ( value == null || value.isEmpty () )
            {
                markers.put ( entry.getKey (), true );
            }
            else
            {
                markers.put ( entry.getKey (), value );
            }
        }
        c.setMarkers ( markers );

        this.configuration = c;
        c.sendEvents ();

        reprocess ();
    }

    @Override
    protected void injectEventAttributes ( final EventBuilder builder )
    {
        super.injectEventAttributes ( builder );
        builder.attribute ( Fields.MONITOR_TYPE, "MARKER" );
    }

    @Override
    protected WriteAttributeResults handleUpdate ( final Map<String, Variant> attributes, final OperationParameters operationParameters ) throws Exception
    {
        final Map<String, String> data = new HashMap<String, String> ();

        if ( this.configuration != null )
        {
            final Variant active = attributes.get ( "active" ); //$NON-NLS-1$

            if ( active != null && !active.isNull () )
            {
                data.put ( "active", "" + active.asBoolean () ); //$NON-NLS-1$
            }
        }

        return updateConfiguration ( data, attributes, false, operationParameters );
    }

}
