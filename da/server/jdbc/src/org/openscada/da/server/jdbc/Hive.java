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

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.openscada.da.jdbc.configuration.QueryType;
import org.openscada.da.jdbc.configuration.RootDocument;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.common.ValidationStrategy;
import org.openscada.da.server.common.impl.HiveCommon;
import org.w3c.dom.Node;

public class Hive extends HiveCommon
{
    private static Logger logger = Logger.getLogger ( Hive.class );

    private FolderCommon rootFolder = null;

    public Hive ()
    {
        super ();

        // create root folder
        this.rootFolder = new FolderCommon ();
        setRootFolder ( this.rootFolder );

        setValidatonStrategy ( ValidationStrategy.FULL_CHECK );
    }

    public Hive ( final Node node ) throws XmlException
    {
        this ();

        configure ( RootDocument.Factory.parse ( node ) );
    }

    private void configure ( final RootDocument doc )
    {
        for ( final QueryType queryType : doc.getRoot ().getQueryList () )
        {
            createQuery ( queryType );
        }
    }

    private void createQuery ( final QueryType queryType )
    {
        String sql = queryType.getSql ();
        if ( sql == null || sql.isEmpty () )
        {
            sql = queryType.getSql2 ();
        }

        addQuery ( new Query ( queryType.getId (), queryType.getPeriod (), queryType.getConnectionClass (), queryType.getUri (), sql ) );
    }

    private void addQuery ( final Query query )
    {
        // TODO Auto-generated method stub

    }
}
