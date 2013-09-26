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

package org.eclipse.scada.da.master.common.scale;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.scada.ca.ConfigurationAdministrator;
import org.eclipse.scada.ca.ConfigurationDataHelper;
import org.eclipse.scada.core.Variant;
import org.eclipse.scada.core.server.OperationParameters;
import org.eclipse.scada.da.client.DataItemValue;
import org.eclipse.scada.da.client.DataItemValue.Builder;
import org.eclipse.scada.da.core.WriteAttributeResults;
import org.eclipse.scada.da.master.MasterItem;
import org.eclipse.scada.da.master.common.AbstractCommonHandlerImpl;
import org.eclipse.scada.da.master.common.internal.Activator;
import org.eclipse.scada.sec.UserInformation;
import org.eclipse.scada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.util.tracker.ServiceTracker;

public class ScaleHandlerImpl extends AbstractCommonHandlerImpl
{
    private boolean active = false;

    private double factor = 1.0;

    private double offset = 0.0;

    private final String attrActive;

    private final String attrFactor;

    private final String attrOffset;

    private final String attrValueOriginal;

    public ScaleHandlerImpl ( final String configurationId, final ObjectPoolTracker<MasterItem> poolTracker, final int priority, final ServiceTracker<ConfigurationAdministrator, ConfigurationAdministrator> caTracker )
    {
        super ( configurationId, poolTracker, priority, caTracker, ScaleHandlerFactoryImpl.FACTORY_ID, ScaleHandlerFactoryImpl.FACTORY_ID );

        this.attrActive = getPrefixed ( "active", Activator.getStringInterner () ); //$NON-NLS-1$
        this.attrFactor = getPrefixed ( "factor", Activator.getStringInterner () ); //$NON-NLS-1$
        this.attrOffset = getPrefixed ( "offset", Activator.getStringInterner () ); //$NON-NLS-1$
        this.attrValueOriginal = getPrefixed ( "value.original", Activator.getStringInterner () ); //$NON-NLS-1$
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
    public synchronized void update ( final UserInformation userInformation, final Map<String, String> parameters ) throws Exception
    {
        super.update ( userInformation, parameters );

        final ConfigurationDataHelper cfg = new ConfigurationDataHelper ( parameters );
        this.factor = cfg.getDouble ( "factor", 1 ); //$NON-NLS-1$
        this.offset = cfg.getDouble ( "offset", 0 ); //$NON-NLS-1$
        this.active = cfg.getBoolean ( "active", false ); //$NON-NLS-1$

        reprocess ();
    }

    protected void injectAttributes ( final Builder builder )
    {
        builder.setAttribute ( this.attrActive, this.active ? Variant.TRUE : Variant.FALSE );
        builder.setAttribute ( this.attrFactor, Variant.valueOf ( this.factor ) );
        builder.setAttribute ( this.attrOffset, Variant.valueOf ( this.offset ) );
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
