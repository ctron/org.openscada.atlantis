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

package org.openscada.da.datasource.base;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;

import org.openscada.core.Variant;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.datasource.DataSource;
import org.openscada.da.datasource.DataSourceListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basic implementation of a data source.
 * @author Jens Reimann
 * @since 0.15.0
 *
 */
public abstract class AbstractDataSource implements DataSource
{
    public final static Logger logger = LoggerFactory.getLogger ( AbstractDataSource.class );

    private DataItemValue value = new DataItemValue ();

    protected abstract Executor getExecutor ();

    private final Set<DataSourceListener> listeners = new HashSet<DataSourceListener> ( 1 );

    private Variant lastValue = Variant.NULL;

    private Calendar lastTimestamp = null;

    public synchronized void addListener ( final DataSourceListener listener )
    {
        if ( this.listeners.add ( listener ) )
        {
            final DataItemValue value = this.value;
            getExecutor ().execute ( new Runnable () {

                public void run ()
                {
                    listener.stateChanged ( value );
                }
            } );
        }
    }

    public synchronized void removeListener ( final DataSourceListener listener )
    {
        this.listeners.remove ( listener );
    }

    protected synchronized void updateData ( DataItemValue value )
    {
        logger.debug ( "Update data: {} -> {}", new Object[] { value, value == null ? "" : value.getAttributes () } );

        if ( this.value != null )
        {
            if ( this.value.equals ( value ) )
            {
                logger.debug ( "No data change. Discarding" );
                return;
            }
        }

        value = applyAutoTimestamp ( value );

        this.lastValue = value == null ? null : value.getValue ();
        this.value = value;

        final DataItemValue finalValue = value;

        // fire listeners
        for ( final DataSourceListener listener : this.listeners )
        {
            getExecutor ().execute ( new Runnable () {

                public void run ()
                {
                    listener.stateChanged ( finalValue );
                }

            } );
        }
    }

    private DataItemValue applyAutoTimestamp ( DataItemValue value )
    {
        if ( value != null && value.getTimestamp () == null )
        {
            try
            {
                if ( !this.lastValue.equals ( value.getValue () ) )
                {
                    this.lastTimestamp = Calendar.getInstance ();
                }
            }
            catch ( final Exception e )
            {
                // nothing
                logger.warn ( "Failed to update timestamp", e );
            }

            final DataItemValue.Builder builder = new DataItemValue.Builder ( value );
            builder.setTimestamp ( this.lastTimestamp );
            value = builder.build ();
        }
        return value;
    }

}