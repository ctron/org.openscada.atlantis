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

package org.openscada.da.master.common;

import java.util.Date;
import java.util.Map;

import org.openscada.ae.Event;
import org.openscada.ae.Event.EventBuilder;
import org.openscada.ca.ConfigurationAdministrator;
import org.openscada.core.Variant;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.client.DataItemValue.Builder;
import org.openscada.da.master.AbstractConfigurableMasterHandlerImpl;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.util.tracker.ServiceTracker;

public abstract class AbstractCommonHandlerImpl extends AbstractConfigurableMasterHandlerImpl
{

    public AbstractCommonHandlerImpl ( final String configurationId, final ObjectPoolTracker poolTracker, final int priority, final ServiceTracker<ConfigurationAdministrator, ConfigurationAdministrator> caTracker, final String prefix, final String factoryId )
    {
        super ( configurationId, poolTracker, priority, caTracker, prefix, factoryId );
    }

    protected abstract DataItemValue processDataUpdate ( final DataItemValue value ) throws Exception;

    /**
     * Create a pre-filled event builder
     * @return a new event builder
     */
    protected EventBuilder createEventBuilder ()
    {
        final EventBuilder builder = Event.create ();

        builder.sourceTimestamp ( new Date () );
        builder.entryTimestamp ( new Date () );

        builder.attributes ( this.eventAttributes );

        return builder;
    }

    @Override
    public DataItemValue dataUpdate ( final Map<String, Object> context, final DataItemValue value )
    {
        if ( value == null )
        {
            return null;
        }

        try
        {
            return processDataUpdate ( value );
        }
        catch ( final Throwable e )
        {
            final Builder builder = new Builder ( value );
            builder.setAttribute ( getPrefixed ( "error" ), Variant.TRUE );
            builder.setAttribute ( getPrefixed ( "error.message" ), Variant.valueOf ( e.getMessage () ) );
            return builder.build ();
        }
    }

}