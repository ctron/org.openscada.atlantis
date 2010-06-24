/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.jdbc;

import java.sql.CallableStatement;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.da.server.common.AttributeMode;
import org.openscada.da.server.common.chain.WriteHandler;
import org.openscada.da.server.common.chain.WriteHandlerItem;
import org.openscada.da.server.common.item.factory.FolderItemFactory;
import org.openscada.sec.UserInformation;
import org.openscada.utils.lang.Immutable;

public class Update
{

    private final String id;

    private final String sql;

    private FolderItemFactory itemFactory;

    private final Connection connection;

    private WriteHandlerItem item;

    @Immutable
    public static class Mapping
    {
        private final String attributes;

        private final String namedParameter;

        /**
         * The constructor that maps the main value to a named parameter
         * @param namedParameter the name of the SQL parameter
         */
        public Mapping ( final String namedParameter )
        {
            this.attributes = null;
            this.namedParameter = namedParameter;
        }

        /**
         * A mapping that maps the specified attribute name to the
         * provided named parameter.
         * <p>
         * Note that if the attribute is <code>null</code> then is defines the main
         * value instead.
         * </p>
         * @param attribute the name of the attribute
         * @param namedParameter the name of the SQL parameter
         */
        public Mapping ( final String attribute, final String namedParameter )
        {
            this.attributes = attribute;
            this.namedParameter = namedParameter;
        }

        public String getAttributes ()
        {
            return this.attributes;
        }

        public String getNamedParameter ()
        {
            return this.namedParameter;
        }
    }

    private final Collection<Mapping> mappings = new LinkedList<Mapping> ();

    public Update ( final String id, final String sql, final Connection connection )
    {
        this.id = id;
        this.sql = sql;
        this.connection = connection;
    }

    public void register ( final DataItemFactory parentItemFactory )
    {
        this.itemFactory = parentItemFactory.createSubFolderFactory ( this.id );
        this.item = this.itemFactory.createInputOutput ( "START", new WriteHandler () {

            public void handleWrite ( final UserInformation userInformation, final Variant value ) throws Exception
            {
                performUpdate ( userInformation, value );
            }
        } );
    }

    protected void performUpdate ( final UserInformation userInformation, final Variant value )
    {
        try
        {
            final int result = doUpdate ( value );
            this.item.updateData ( Variant.valueOf ( result ), new HashMap<String, Variant> (), AttributeMode.SET );
        }
        catch ( final Exception e )
        {
            final Map<String, Variant> attributes = new HashMap<String, Variant> ();
            attributes.put ( "sql.error", Variant.TRUE );
            attributes.put ( "sql.error.message", Variant.valueOf ( e.getMessage () ) );
            this.item.updateData ( Variant.NULL, attributes, AttributeMode.SET );
        }
    }

    private int doUpdate ( final Variant value ) throws Exception
    {
        final java.sql.Connection connection = this.connection.createConnection ();

        try
        {
            connection.setAutoCommit ( true );

            final CallableStatement statement = connection.prepareCall ( this.sql );

            // set the main value
            for ( final Mapping mapping : this.mappings )
            {
                if ( mapping.getAttributes () == null )
                {
                    statement.setObject ( mapping.getNamedParameter (), value.getValue () );
                }
            }

            final int result = statement.executeUpdate ();

            return result;
        }
        finally
        {
            connection.close ();
        }
    }

    public void unregister ()
    {
        this.itemFactory.dispose ();
        this.itemFactory = null;
    }

    /**
     * Add a mapping.
     * <p>
     * Note that the mappings are not unique and adding multiple mappings
     * for the same attribute is not allowed and will result in an
     * undefined behavior.
     * </p>
     * @param mapping the mapping to add
     */
    public void addMapping ( final Mapping mapping )
    {
        this.mappings.add ( mapping );
    }

}
