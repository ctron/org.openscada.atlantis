/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://inavare.com)
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

import java.util.HashMap;
import java.util.Map;

import org.openscada.ca.ConfigurationDataHelper;
import org.openscada.core.Variant;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.client.DataItemValue.Builder;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.datasource.WriteInformation;
import org.openscada.da.master.common.AbstractCommonHandlerImpl;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.util.tracker.ServiceTracker;

public class ScaleHandlerImpl extends AbstractCommonHandlerImpl
{
    private boolean active = false;

    private double factor = 1.0;

    private double offset = 0.0;

    public ScaleHandlerImpl ( final String configurationId, final ObjectPoolTracker poolTracker, final int priority, final ServiceTracker caTracker )
    {
        super ( configurationId, poolTracker, priority, caTracker, ScaleHandlerFactoryImpl.FACTORY_ID, ScaleHandlerFactoryImpl.FACTORY_ID );
    }

    protected DataItemValue processDataUpdate ( final DataItemValue value ) throws Exception
    {
        final Builder builder = new Builder ( value );

        injectAttributes ( builder );
        builder.setAttribute ( getPrefixed ( "raw" ), value.getValue () );

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
            return new Variant ( value.asDouble ( null ) * this.factor + this.offset );
        }
    }

    @Override
    public synchronized void update ( final Map<String, String> parameters ) throws Exception
    {
        super.update ( parameters );

        final ConfigurationDataHelper cfg = new ConfigurationDataHelper ( parameters );
        this.factor = cfg.getDouble ( "factor", 1 );
        this.offset = cfg.getDouble ( "offset", 0 );
        this.active = cfg.getBoolean ( "active", false );

        reprocess ();
    }

    protected void injectAttributes ( final Builder builder )
    {
        builder.setAttribute ( getPrefixed ( "active" ), this.active ? Variant.TRUE : Variant.FALSE );
        builder.setAttribute ( getPrefixed ( "factor" ), new Variant ( this.factor ) );
        builder.setAttribute ( getPrefixed ( "offset" ), new Variant ( this.offset ) );
    }

    @Override
    protected WriteAttributeResults handleUpdate ( final WriteInformation writeInformation, final Map<String, Variant> attributes ) throws Exception
    {
        final Map<String, String> data = new HashMap<String, String> ();

        final Variant active = attributes.get ( "active" );
        final Variant factor = attributes.get ( "factor" );
        final Variant offset = attributes.get ( "offset" );

        if ( active != null && !active.isNull () )
        {
            data.put ( "active", active.asString () );
        }
        if ( factor != null && !factor.isNull () )
        {
            data.put ( "factor", factor.asString () );
        }
        if ( offset != null && !offset.isNull () )
        {
            data.put ( "offset", offset.asString () );
        }

        return updateConfiguration ( data, attributes, false );
    }

}
