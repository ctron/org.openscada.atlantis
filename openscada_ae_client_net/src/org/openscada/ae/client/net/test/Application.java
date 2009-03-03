/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
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

package org.openscada.ae.client.net.test;

import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openscada.ae.client.net.Connection;
import org.openscada.ae.core.QueryDescription;
import org.openscada.core.ConnectionInformation;
import org.openscada.core.OperationException;
import org.openscada.core.Variant;
import org.openscada.core.client.ConnectWaitController;

public class Application
{
    private static Logger logger = Logger.getLogger ( Application.class );

    public static void list ( final Connection connection ) throws InterruptedException, OperationException
    {
        final Set<QueryDescription> queries = connection.list ();
        System.out.println ( "Listing queries:" );
        for ( final QueryDescription description : queries )
        {
            System.out.println ( description.getId () );
            for ( final Map.Entry<String, Variant> entry : description.getAttributes ().entrySet () )
            {
                System.out.println ( "\t'" + entry.getKey () + "'=>'" + entry.getValue ().asString ( "<null>" ) + "'" );
            }
        }
    }

    public static void main ( final String[] args ) throws Throwable
    {
        final ConnectionInformation ci = ConnectionInformation.fromURI ( "da:net://localhost:1302" );

        final Connection connection = new Connection ( ci );
        logger.debug ( "Initiating connection..." );
        new ConnectWaitController ( connection ).connect ();
        logger.debug ( "Connection established" );

        list ( connection );

        connection.subscribe ( "all", new DumpListener (), 10, 10 );

        while ( true )
        {
            Thread.sleep ( 1000 );
        }
    }
}
