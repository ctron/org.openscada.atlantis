/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openscada.da.datasource.base;

import org.openscada.da.client.DataItemValue;
import org.openscada.da.datasource.DataSource;
import org.openscada.da.datasource.DataSourceListener;
import org.openscada.da.datasource.SingleDataSourceTracker;
import org.openscada.da.datasource.SingleDataSourceTracker.ServiceListener;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.InvalidSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataSourceHandler implements DataSourceListener
{

    private final static Logger logger = LoggerFactory.getLogger ( DataSourceHandler.class );

    private final DataSourceHandlerListener listener;

    private final SingleDataSourceTracker tracker;

    private DataSource service;

    private DataItemValue value;

    public DataSourceHandler ( final ObjectPoolTracker poolTracker, final String datasourceId, final DataSourceHandlerListener listener ) throws InvalidSyntaxException
    {
        this.listener = listener;

        this.tracker = new SingleDataSourceTracker ( poolTracker, datasourceId, new ServiceListener () {

            public void dataSourceChanged ( final DataSource dataSource )
            {
                DataSourceHandler.this.setDataSource ( dataSource );
            }
        } );
        this.tracker.open ();
    }

    protected void setDataSource ( final DataSource service )
    {
        // disconnect
        disconnectService ();

        // connect
        if ( service != null )
        {
            this.service = service;
            this.service.addListener ( this );
        }
    }

    private void disconnectService ()
    {
        if ( this.service != null )
        {
            this.service.removeListener ( this );
            this.service = null;
            this.value = null;
            fireValueChange ();
        }
    }

    private void fireValueChange ()
    {
        try
        {
            this.listener.handleChange ();
        }
        catch ( final Exception e )
        {
            logger.warn ( "Failed to handle state change", e );
        }
    }

    public void dispose ()
    {
        if ( this.tracker != null )
        {
            this.tracker.close ();
        }
        disconnectService ();
    }

    public DataItemValue getValue ()
    {
        return this.value;
    }

    public void stateChanged ( final DataItemValue value )
    {
        this.value = value;
        fireValueChange ();
    }
}
