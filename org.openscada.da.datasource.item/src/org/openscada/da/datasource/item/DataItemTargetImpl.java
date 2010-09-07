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
import org.openscada.sec.osgi.AuthorizationHelper;
import org.openscada.utils.concurrent.InstantErrorFuture;
import org.openscada.utils.concurrent.InstantFuture;
import org.openscada.utils.concurrent.NotifyFuture;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.InvalidSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataItemTargetImpl extends DataItemBase implements DataSourceListener
{
    private static final String OBJECT_TYPE = "DataItem";

    private final static Logger logger = LoggerFactory.getLogger ( DataItemTargetImpl.class );

    private DataItemValue currentValue = new DataItemValue ();

    private final SingleDataSourceTracker tracker;

    private DataSource dataSource;

    private final AuthorizationHelper authorization;

    private final String id;

    public DataItemTargetImpl ( final ObjectPoolTracker poolTracker, final DataItemInformation information, final String dataSourceId, final AuthorizationHelper authorization ) throws InvalidSyntaxException
    {
        super ( information );

        this.id = information.getName ();

        this.authorization = authorization;

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
        final NotifyFuture<WriteAttributeResults> future = this.authorization.authorize ( this.id, OBJECT_TYPE, "WRITE_ATTRIBUTES", session.getUserInformation (), makeSetAttributesContext ( attributes ) ).asFuture ();
        if ( future != null )
        {
            return future;
        }

        if ( this.dataSource != null )
        {
            return this.dataSource.startWriteAttributes ( new WriteInformation ( session.getUserInformation () ), attributes );
        }
        else
        {
            return new InstantErrorFuture<WriteAttributeResults> ( new OperationException ( "Disconnected data source" ) );
        }
    }

    private Map<String, Object> makeSetAttributesContext ( final Map<String, Variant> attributes )
    {
        final Map<String, Object> context = new HashMap<String, Object> ( 1 );
        context.put ( "attributes", attributes );
        return context;
    }

    public synchronized NotifyFuture<WriteResult> startWriteValue ( final UserSession session, final Variant value )
    {
        final NotifyFuture<WriteResult> future = this.authorization.authorize ( this.id, OBJECT_TYPE, "WRITE", session.getUserInformation (), makeWriteValueContext ( value ) ).asFuture ();
        if ( future != null )
        {
            return future;
        }

        if ( this.dataSource != null )
        {
            return this.dataSource.startWriteValue ( new WriteInformation ( session.getUserInformation () ), value );
        }
        else
        {
            return new InstantErrorFuture<WriteResult> ( new OperationException ( "Disconnected data source" ) );
        }
    }

    private Map<String, Object> makeWriteValueContext ( final Variant value )
    {
        final Map<String, Object> context = new HashMap<String, Object> ( 1 );
        context.put ( "value", value );
        return context;
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
        logger.debug ( "State changed: {}", value );

        if ( value == null )
        {
            this.currentValue = value;
            notifyData ( Variant.NULL, new HashMap<String, Variant> (), true );
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

            final Variant oldValue = Variant.valueOf ( this.currentValue.getValue () );
            final Variant newValue = Variant.valueOf ( value.getValue () );

            this.currentValue = value;

            if ( !diff.isEmpty () || !oldValue.equals ( newValue ) )
            {
                notifyData ( value.getValue (), diff );
            }

        }
    }
}
