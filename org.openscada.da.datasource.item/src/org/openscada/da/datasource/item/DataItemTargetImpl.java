/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
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

package org.openscada.da.datasource.item;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.openscada.core.InvalidOperationException;
import org.openscada.core.OperationException;
import org.openscada.core.Variant;
import org.openscada.core.server.common.session.UserSession;
import org.openscada.core.utils.AttributesHelper;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.core.DataItemInformation;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.core.WriteResult;
import org.openscada.da.datasource.DataSource;
import org.openscada.da.datasource.DataSourceListener;
import org.openscada.da.datasource.SingleDataSourceTracker;
import org.openscada.da.datasource.WriteInformation;
import org.openscada.da.datasource.SingleDataSourceTracker.ServiceListener;
import org.openscada.da.server.common.DataItemBase;
import org.openscada.utils.concurrent.InstantErrorFuture;
import org.openscada.utils.concurrent.InstantFuture;
import org.openscada.utils.concurrent.NotifyFuture;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.InvalidSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataItemTargetImpl extends DataItemBase implements DataSourceListener
{
    private final static Logger logger = LoggerFactory.getLogger ( DataItemTargetImpl.class );

    private DataItemValue currentValue = new DataItemValue ();

    private final SingleDataSourceTracker tracker;

    private DataSource dataSource;

    public DataItemTargetImpl ( final ObjectPoolTracker poolTracker, final DataItemInformation information, final String dataSourceId ) throws InvalidSyntaxException
    {
        super ( information );

        this.tracker = new SingleDataSourceTracker ( poolTracker, dataSourceId, new ServiceListener () {

            public void dataSourceChanged ( final DataSource dataSource )
            {
                DataItemTargetImpl.this.setDataSource ( dataSource );
            }
        } );
        this.tracker.open ();
    }

    @Override
    protected synchronized Map<String, Variant> getCacheAttributes ()
    {
        final DataItemValue value = this.currentValue;

        if ( value != null )
        {
            return value.getAttributes ();
        }
        else
        {
            return null;
        }
    }

    @Override
    protected synchronized Variant getCacheValue ()
    {
        final DataItemValue value = this.currentValue;

        if ( value != null )
        {
            return value.getValue ();
        }
        else
        {
            return null;
        }
    }

    protected synchronized void setDataSource ( final DataSource dataSource )
    {
        logger.info ( "Setting datasource: {}", dataSource );
        disconnectDatasource ();
        connectDataSource ( dataSource );
    }

    private synchronized void connectDataSource ( final DataSource dataSource )
    {
        this.dataSource = dataSource;
        if ( this.dataSource != null )
        {
            this.dataSource.addListener ( this );
        }
    }

    private synchronized void disconnectDatasource ()
    {
        if ( this.dataSource != null )
        {
            this.dataSource.removeListener ( this );
            this.dataSource = null;

            stateChanged ( null );
        }
    }

    public synchronized Map<String, Variant> getAttributes ()
    {
        return Collections.unmodifiableMap ( this.currentValue.getAttributes () );
    }

    public synchronized NotifyFuture<Variant> readValue () throws InvalidOperationException
    {
        return new InstantFuture<Variant> ( this.currentValue.getValue () );
    }

    public synchronized NotifyFuture<WriteAttributeResults> startSetAttributes ( final UserSession session, final Map<String, Variant> attributes )
    {
        if ( this.dataSource != null )
        {
            return this.dataSource.startWriteAttributes ( new WriteInformation ( session.getUserInformation () ), attributes );
        }
        else
        {
            return new InstantErrorFuture<WriteAttributeResults> ( new OperationException ( "Disconnected data source" ) );
        }
    }

    public synchronized NotifyFuture<WriteResult> startWriteValue ( final UserSession session, final Variant value )
    {
        if ( this.dataSource != null )
        {
            return this.dataSource.startWriteValue ( new WriteInformation ( session.getUserInformation () ), value );
        }
        else
        {
            return new InstantErrorFuture<WriteResult> ( new OperationException ( "Disconnected data source" ) );
        }
    }

    public synchronized void dispose ()
    {
        if ( this.tracker != null )
        {
            this.tracker.close ();
        }
    }

    public synchronized void stateChanged ( final DataItemValue value )
    {
        if ( value == null )
        {
            notifyData ( Variant.NULL, new HashMap<String, Variant> (), true );
            this.currentValue = value;
        }
        else
        {
            if ( this.currentValue == null )
            {
                this.currentValue = new DataItemValue ();
            }

            final Map<String, Variant> target = new HashMap<String, Variant> ( this.currentValue.getAttributes () );
            final Map<String, Variant> diff = new HashMap<String, Variant> ();

            AttributesHelper.set ( target, value.getAttributes (), diff );

            if ( !diff.isEmpty () )
            {
                notifyData ( value.getValue (), diff );
            }
            this.currentValue = value;
        }
    }
}
