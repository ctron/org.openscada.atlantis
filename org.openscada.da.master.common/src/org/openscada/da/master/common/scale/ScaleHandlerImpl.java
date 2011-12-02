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

package org.openscada.da.master.common.scale;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.openscada.ca.ConfigurationAdministrator;
import org.openscada.ca.ConfigurationDataHelper;
import org.openscada.core.Variant;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.client.DataItemValue.Builder;
import org.openscada.da.core.OperationParameters;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.master.common.AbstractCommonHandlerImpl;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.util.tracker.ServiceTracker;

public class ScaleHandlerImpl extends AbstractCommonHandlerImpl
{
    private boolean active = false;

    private double factor = 1.0;

    private double offset = 0.0;

    public ScaleHandlerImpl ( final String configurationId, final ObjectPoolTracker poolTracker, final int priority, final ServiceTracker<ConfigurationAdministrator, ConfigurationAdministrator> caTracker )
    {
        super ( configurationId, poolTracker, priority, caTracker, ScaleHandlerFactoryImpl.FACTORY_ID, ScaleHandlerFactoryImpl.FACTORY_ID );
    }

    @Override
    protected DataItemValue processDataUpdate ( final DataItemValue value ) throws Exception
    {
        final Builder builder = new Builder ( value );

        injectAttributes ( builder );
        builder.setAttribute ( getPrefixed ( "raw" ), value.getValue () ); //$NON-NLS-1$

        final Variant val = value.getValue ();
        if ( val == null || val.isNull () )
        {
            return builder.build ();
        }

        builder.setValue ( handleDataUpdate ( builder.getValue () ) );
        return builder.build ();
    }

    private Variant handleDataUpdate ( final Variant value )
    {
        if ( !this.active )
        {
            return value;
        }
        else
        {
            return Variant.valueOf ( value.asDouble ( null ) * this.factor + this.offset );
        }
    }

    @Override
    public synchronized void update ( final Principal principal, final Map<String, String> parameters ) throws Exception
    {
        super.update ( principal, parameters );

        final ConfigurationDataHelper cfg = new ConfigurationDataHelper ( parameters );
        this.factor = cfg.getDouble ( "factor", 1 ); //$NON-NLS-1$
        this.offset = cfg.getDouble ( "offset", 0 ); //$NON-NLS-1$
        this.active = cfg.getBoolean ( "active", false ); //$NON-NLS-1$

        reprocess ();
    }

    protected void injectAttributes ( final Builder builder )
    {
        builder.setAttribute ( getPrefixed ( "active" ), this.active ? Variant.TRUE : Variant.FALSE ); //$NON-NLS-1$
        builder.setAttribute ( getPrefixed ( "factor" ), Variant.valueOf ( this.factor ) ); //$NON-NLS-1$
        builder.setAttribute ( getPrefixed ( "offset" ), Variant.valueOf ( this.offset ) ); //$NON-NLS-1$
    }

    @Override
    protected WriteAttributeResults handleUpdate ( final Map<String, Variant> attributes, final OperationParameters operationParameters ) throws Exception
    {
        final Map<String, String> data = new HashMap<String, String> ();

        final Variant active = attributes.get ( "active" ); //$NON-NLS-1$
        final Variant factor = attributes.get ( "factor" ); //$NON-NLS-1$
        final Variant offset = attributes.get ( "offset" ); //$NON-NLS-1$

        if ( active != null && !active.isNull () )
        {
            data.put ( "active", active.asString () ); //$NON-NLS-1$
        }
        if ( factor != null && !factor.isNull () )
        {
            data.put ( "factor", factor.asString () ); //$NON-NLS-1$
        }
        if ( offset != null && !offset.isNull () )
        {
            data.put ( "offset", offset.asString () ); //$NON-NLS-1$
        }

        return updateConfiguration ( data, attributes, false, operationParameters );
    }

}
