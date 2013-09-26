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

package org.openscada.da.master.common.round;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.scada.core.Variant;
import org.eclipse.scada.sec.UserInformation;
import org.eclipse.scada.utils.osgi.pool.ObjectPoolTracker;
import org.openscada.ca.ConfigurationAdministrator;
import org.openscada.ca.ConfigurationDataHelper;
import org.openscada.core.server.OperationParameters;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.client.DataItemValue.Builder;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.master.MasterItem;
import org.openscada.da.master.common.AbstractCommonHandlerImpl;
import org.openscada.da.master.common.internal.Activator;
import org.osgi.util.tracker.ServiceTracker;

public class RoundHandlerImpl extends AbstractCommonHandlerImpl
{
    private enum RoundType
    {
        NONE,
        CEIL,
        FLOOR,
        ROUND,
    }

    private boolean active = false;

    private RoundType type = RoundType.NONE;

    private String error = null;

    private final String attrValueOriginal;

    private final String attrActive;

    private final String attrType;

    private final String attrError;

    public RoundHandlerImpl ( final String configurationId, final ObjectPoolTracker<MasterItem> poolTracker, final int priority, final ServiceTracker<ConfigurationAdministrator, ConfigurationAdministrator> caTracker )
    {
        super ( configurationId, poolTracker, priority, caTracker, RoundHandlerFactoryImpl.FACTORY_ID, RoundHandlerFactoryImpl.FACTORY_ID );

        this.attrActive = getPrefixed ( "active", Activator.getStringInterner () );
        this.attrType = getPrefixed ( "type", Activator.getStringInterner () );
        this.attrError = getPrefixed ( "error", Activator.getStringInterner () );
        this.attrValueOriginal = getPrefixed ( "value.original", Activator.getStringInterner () );
    }

    @Override
    protected void processDataUpdate ( final Map<String, Object> context, final DataItemValue.Builder builder ) throws Exception
    {
        injectAttributes ( builder );
        builder.setAttribute ( this.attrValueOriginal, builder.getValue () );

        final Variant val = builder.getValue ();
        if ( val == null || val.isNull () )
        {
            return;
        }

        builder.setValue ( handleDataUpdate ( builder.getValue () ) );
    }

    private Variant handleDataUpdate ( final Variant value )
    {
        if ( !this.active || this.type == RoundType.NONE || value == null || value.isNull () )
        {
            return value;
        }
        else
        {
            switch ( this.type )
            {
                case CEIL:
                    return Variant.valueOf ( Math.ceil ( value.asDouble ( null ) ) );
                case FLOOR:
                    return Variant.valueOf ( Math.floor ( value.asDouble ( null ) ) );
                case ROUND:
                    return Variant.valueOf ( (double)Math.round ( value.asDouble ( null ) ) );
                case NONE:
                    break;
            }
            return value;
        }
    }

    @Override
    public synchronized void update ( final UserInformation userInformation, final Map<String, String> parameters ) throws Exception
    {
        super.update ( userInformation, parameters );

        final ConfigurationDataHelper cfg = new ConfigurationDataHelper ( parameters );
        this.active = cfg.getBoolean ( "active", false ); //$NON-NLS-1$
        try
        {
            this.type = RoundType.valueOf ( cfg.getString ( "type", "NONE" ) );
            this.error = null;
        }
        catch ( final IllegalArgumentException e )
        {
            this.type = RoundType.NONE;
            this.error = e.getMessage ();
        }
        catch ( final NullPointerException e )
        {
            this.type = RoundType.NONE;
            this.error = e.getMessage ();
        }

        reprocess ();
    }

    protected void injectAttributes ( final Builder builder )
    {
        builder.setAttribute ( this.attrActive, this.active ? Variant.TRUE : Variant.FALSE );
        if ( this.type != null && this.type != RoundType.NONE )
        {
            builder.setAttribute ( this.attrType, Variant.valueOf ( this.type.toString () ) );
        }
        if ( this.error != null )
        {
            builder.setAttribute ( this.attrError, Variant.valueOf ( this.error ) );
        }
    }

    @Override
    protected WriteAttributeResults handleUpdate ( final Map<String, Variant> attributes, final OperationParameters operationParameters ) throws Exception
    {
        final Map<String, String> data = new HashMap<String, String> ();

        final Variant active = attributes.get ( "active" ); //$NON-NLS-1$
        final Variant type = attributes.get ( "type" ); //$NON-NLS-1$

        if ( active != null && !active.isNull () )
        {
            data.put ( "active", active.asString () ); //$NON-NLS-1$
        }
        if ( type != null && !type.isNull () )
        {
            data.put ( "type", type.asString () ); //$NON-NLS-1$
        }

        return updateConfiguration ( data, attributes, false, operationParameters );
    }
}
