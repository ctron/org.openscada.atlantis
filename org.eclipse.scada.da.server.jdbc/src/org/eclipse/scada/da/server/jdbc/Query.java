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

package org.eclipse.scada.da.server.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.scada.core.Variant;
import org.eclipse.scada.da.server.common.AttributeMode;
import org.eclipse.scada.da.server.common.chain.DataItemInputChained;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Query extends AbstractQuery
{
    private final static Logger logger = LoggerFactory.getLogger ( Query.class );

    private final Map<String, DataItemInputChained> items = new HashMap<String, DataItemInputChained> ();

    public Query ( final String id, final int period, final String sql, final Connection connection, final Map<Integer, String> columnAliases )
    {
        super ( id, period, sql, connection, columnAliases );
    }

    @Override
    protected void setGlobalError ( final Throwable e )
    {
        logger.error ( "Failed to query", e );

        for ( final Map.Entry<String, DataItemInputChained> entry : this.items.entrySet () )
        {
            setError ( entry.getKey (), e );
        }
    }

    @Override
    protected void doQuery () throws Exception
    {
        logger.debug ( "Perform query" );
        try (java.sql.Connection connection = this.connection.getConnection ())
        {
            try (final PreparedStatement stmt = connection.prepareStatement ( this.sql ))
            {
                if ( this.connection.getTimeout () != null )
                {
                    stmt.setQueryTimeout ( this.connection.getTimeout () / 1000 );
                }

                try (final ResultSet result = stmt.executeQuery ())
                {
                    if ( result.next () )
                    {
                        final int count = result.getMetaData ().getColumnCount ();

                        for ( int i = 0; i < count; i++ )
                        {
                            updateField ( i + 1, result );
                        }
                    }
                }
            }
        }
    }

    private void updateField ( final int i, final ResultSet result ) throws SQLException
    {
        final String field = mapFieldName ( i, result );

        try
        {
            setValue ( field, Variant.valueOf ( result.getObject ( i ) ) );
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

        item = this.itemFactory.createInput ( key, null );
        this.items.put ( key, item );

        return item;
    }

    private void setValue ( final String key, final Variant value )
    {
        logger.debug ( "Setting value: {} = {}", key, value );

        final Map<String, Variant> attributes = new HashMap<String, Variant> ();
        attributes.put ( "jdbc.error", null );
        attributes.put ( "jdbc.error.message", null );

        getItem ( key ).updateData ( value, attributes, AttributeMode.UPDATE );
    }

    private void setError ( final String key, final Throwable e )
    {
        logger.debug ( "Setting error: {} = {}", key, e.getMessage () );

        final Map<String, Variant> attributes = new HashMap<String, Variant> ();
        attributes.put ( "jdbc.error", Variant.TRUE );
        attributes.put ( "jdbc.error.message", Variant.valueOf ( e.getMessage () ) );

        getItem ( key ).updateData ( null, attributes, AttributeMode.UPDATE );
    }

}
