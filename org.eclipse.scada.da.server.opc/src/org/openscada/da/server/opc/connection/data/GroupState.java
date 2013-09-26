/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.server.opc.connection.data;

import org.eclipse.scada.utils.beans.AbstractPropertyChange;
import org.openscada.opc.dcom.da.OPCGroupState;

public class GroupState extends AbstractPropertyChange
{
    private final static String PROP_CONNECTED = "connected";

    private final static String PROP_UPDATE_RATE = "updateRate";

    private final static String PROP_PERCENT_DEADBAND = "percentDeadband";

    private final static String PROP_NAME = "name";

    private final static String PROP_LOCALE_ID = "localeId";

    private boolean connected;

    private Integer updateRate;

    private Float percentDeadband;

    private String name;

    private Integer localeId;

    public boolean isConnected ()
    {
        return this.connected;
    }

    protected void setConnected ( final boolean connected )
    {
        final boolean oldConnected = this.connected;
        this.connected = connected;
        firePropertyChange ( PROP_CONNECTED, oldConnected, connected );
    }

    public Integer getUpdateRate ()
    {
        return this.updateRate;
    }

    protected void setUpdateRate ( final Integer updateRate )
    {
        final Integer oldUpdateRate = this.updateRate;
        this.updateRate = updateRate;
        firePropertyChange ( PROP_UPDATE_RATE, oldUpdateRate, updateRate );
    }

    public Float getPercentDeadband ()
    {
        return this.percentDeadband;
    }

    protected void setPercentDeadband ( final Float percentDeadband )
    {
        final Float oldPercentDeadband = this.percentDeadband;
        this.percentDeadband = percentDeadband;
        firePropertyChange ( PROP_PERCENT_DEADBAND, oldPercentDeadband, percentDeadband );
    }

    public String getName ()
    {
        return this.name;
    }

    protected void setName ( final String name )
    {
        final String oldName = this.name;
        this.name = name;
        firePropertyChange ( PROP_NAME, oldName, name );
    }

    public Integer getLocaleId ()
    {
        return this.localeId;
    }

    protected void setLocaleId ( final Integer localeId )
    {
        final Integer oldLocaleId = this.localeId;
        this.localeId = localeId;
        firePropertyChange ( PROP_LOCALE_ID, oldLocaleId, localeId );
    }

    public void update ( final OPCGroupState state )
    {
        if ( state == null )
        {
            setConnected ( false );
            setUpdateRate ( null );
            setPercentDeadband ( null );
            setName ( null );
            setLocaleId ( null );
            return;
        }

        setConnected ( true );
        setUpdateRate ( state.getUpdateRate () );
        setPercentDeadband ( state.getPercentDeadband () );
        setName ( state.getName () );
        setLocaleId ( state.getLocaleID () );
    }
}
