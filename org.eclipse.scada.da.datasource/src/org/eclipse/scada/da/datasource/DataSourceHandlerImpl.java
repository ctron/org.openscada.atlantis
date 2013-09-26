/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.eclipse.scada.da.datasource;

import org.eclipse.scada.core.VariantType;
import org.eclipse.scada.da.client.DataItemValue;
import org.eclipse.scada.da.datasource.SingleDataSourceTracker.ServiceListener;
import org.eclipse.scada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.InvalidSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataSourceHandlerImpl implements DataSourceListener, DataSourceHandler
{

    private final static Logger logger = LoggerFactory.getLogger ( DataSourceHandlerImpl.class );

    private final DataSourceHandlerListener listener;

    private final SingleDataSourceTracker tracker;

    private DataSource service;

    private DataItemValue value;

    private final VariantType type;

    public DataSourceHandlerImpl ( final ObjectPoolTracker<DataSource> poolTracker, final String datasourceId, final DataSourceHandlerListener listener, final VariantType type ) throws InvalidSyntaxException
    {
        this.listener = listener;
        this.type = type;

        this.tracker = new SingleDataSourceTracker ( poolTracker, datasourceId, new ServiceListener () {

            @Override
            public void dataSourceChanged ( final DataSource dataSource )
            {
                DataSourceHandlerImpl.this.setDataSource ( dataSource );
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

    /* (non-Javadoc)
     * @see org.eclipse.scada.da.datasource.DataSourceHandler#dispose()
     */
    @Override
    public void dispose ()
    {
        if ( this.tracker != null )
        {
            this.tracker.close ();
        }
        disconnectService ();
    }

    /* (non-Javadoc)
     * @see org.eclipse.scada.da.datasource.DataSourceHandler#getValue()
     */
    @Override
    public DataItemValue getValue ()
    {
        return this.value;
    }

    /* (non-Javadoc)
     * @see org.eclipse.scada.da.datasource.DataSourceHandler#getType()
     */
    @Override
    public VariantType getType ()
    {
        return this.type;
    }

    @Override
    public void stateChanged ( final DataItemValue value )
    {
        this.value = value;
        fireValueChange ();
    }
}
