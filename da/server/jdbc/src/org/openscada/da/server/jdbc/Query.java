/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscada.da.server.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.openscada.core.Variant;
import org.openscada.da.server.common.AttributeMode;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.da.server.common.item.factory.FolderItemFactory;

public class Query
{
    private static Logger logger = Logger.getLogger ( Query.class );

    private final String id;

    private final int period;

    private final String sql;

    private final Connection connection;

    private Timer timer;

    private final TimerTask task;

    private final Map<String, DataItemInputChained> items = new HashMap<String, DataItemInputChained> ();

    private FolderItemFactory itemFactory;

    public Query ( final String id, final int period, final String sql, final Connection connection )
    {
        super ();
        this.id = id;
        this.period = period;
        this.sql = sql;
        this.connection = connection;

        logger.info ( "Created new query: " + this.id );

        this.task = new TimerTask () {

            @Override
            public void run ()
            {
                Query.this.tick ();
            }
        };
    }

    public void register ( final Timer timer, final DataItemFactory parentItemFactory )
    {
        this.timer = timer;
        this.itemFactory = parentItemFactory.createSubFolderFactory ( this.id );

        this.timer.scheduleAtFixedRate ( this.task, 0, this.period );
    }

    public void unregister ()
    {
        this.task.cancel ();

        this.itemFactory.dispose ();
        this.itemFactory = null;
    }

    public void tick ()
    {
        try
        {
            doQuery ();
        }
        catch ( final Throwable e )
        {
            setGlobalError ( e );
        }
    }

    private void setGlobalError ( final Throwable e )
    {
        logger.error ( "Failed to query", e );
        // TODO Auto-generated method stub

        for ( final Map.Entry<String, DataItemInputChained> entry : this.items.entrySet () )
        {
            setError ( entry.getKey (), e );
        }

    }

    private void doQuery () throws Exception
    {
        logger.debug ( "Perform query" );
        final java.sql.Connection connection = this.connection.getConnection ();
        try
        {
            final PreparedStatement stmt = connection.prepareStatement ( this.sql );
            if ( this.connection.getTimeout () != null )
            {
                stmt.setQueryTimeout ( this.connection.getTimeout () / 1000 );
            }

            try
            {
                final ResultSet result = stmt.executeQuery ();
                if ( result.next () )
                {
                    final int count = result.getMetaData ().getColumnCount ();

                    for ( int i = 0; i < count; i++ )
                    {
                        updateField ( i, result );
                    }
                }
                result.close ();
            }
            finally
            {
                stmt.close ();
            }
        }
        finally
        {
            if ( connection != null )
            {
                connection.close ();
            }
        }
    }

    private void updateField ( final int i, final ResultSet result ) throws SQLException
    {
        final String field = result.getMetaData ().getColumnName ( i + 1 );
        try
        {
            setValue ( field, new Variant ( result.getObject ( i + 1 ) ) );
        }
        catch ( final Throwable e )
        {
            setError ( field, e );
        }
    }

    private DataItemInputChained getItem ( final String key )
    {
        DataItemInputChained item = this.items.get ( key );
        if ( item != null )
        {
            return item;
        }

        item = this.itemFactory.createInput ( key );
        this.items.put ( key, item );

        return null;
    }

    private void setValue ( final String key, final Variant value )
    {
        // TODO Auto-generated method stub
        logger.debug ( "Setting value: " + key + "=" + value );

        final Map<String, Variant> attributes = new HashMap<String, Variant> ();
        attributes.put ( "jdbc.error", null );
        attributes.put ( "jdbc.error.message", null );

        getItem ( key ).updateData ( value, attributes, AttributeMode.UPDATE );
    }

    private void setError ( final String key, final Throwable e )
    {
        logger.debug ( "Setting error: " + key + " = " + e.getMessage () );

        final Map<String, Variant> attributes = new HashMap<String, Variant> ();
        attributes.put ( "jdbc.error", new Variant ( true ) );
        attributes.put ( "jdbc.error.message", new Variant ( e.getMessage () ) );

        getItem ( key ).updateData ( null, attributes, AttributeMode.UPDATE );
    }

}
