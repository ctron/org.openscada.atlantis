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

package org.openscada.ae.client.net;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import org.openscada.ae.client.ListQueryListener;
import org.openscada.ae.client.QueryListener;
import org.openscada.ae.core.QueryDescriptor;
import org.openscada.ae.net.ListQueryResult;
import org.openscada.ae.net.Query;
import org.openscada.ae.net.ConnectionClient.ListQueriesListener;
import org.openscada.core.ConnectionInformation;
import org.openscada.core.Variant;
import org.openscada.core.client.net.SessionConnectionBase;

public class Connection extends SessionConnectionBase implements org.openscada.ae.client.Connection
{
    public static final String VERSION = "0.2.0";

    public static final String PROP_RECONNECT_DELAY = "reconnect-delay";

    public static final String PROP_AUTO_RECONNECT = "auto-reconnect";

    private final NetConnectionClientImpl clientImpl;

    public Connection ( final ConnectionInformation connectionInformation )
    {
        super ( connectionInformation );

        this.clientImpl = new NetConnectionClientImpl ( this.messenger, this );
    }

    @Override
    public String getRequiredVersion ()
    {
        return VERSION;
    }

    public Collection<QueryDescriptor> listQueries ()
    {
        final ListQueryResult result = this.clientImpl.listQueries ();
        return convertResult ( result );
    }

    private Collection<QueryDescriptor> convertResult ( final ListQueryResult result )
    {
        if ( result == null )
        {
            return null;
        }

        final Collection<QueryDescriptor> resultList = new LinkedList<QueryDescriptor> ();
        for ( final Query query : result.getQueries () )
        {
            final QueryDescriptor desc = new QueryDescriptor ();
            desc.setId ( query.getName () );
            desc.setAttributes ( new HashMap<String, Variant> ( desc.getAttributes () ) );
            resultList.add ( desc );
        }
        return resultList;
    }

    public void listQueries ( final ListQueryListener listener )
    {
        this.clientImpl.startListQueries ( new ListQueriesListener () {

            public void handleError ( final Throwable error )
            {
                listener.handleError ( error );
            }

            public void handleSuccess ( final ListQueryResult result )
            {
                listener.handleSuccess ( convertResult ( result ) );
            }
        }, 0 );
    }

    public void setQueryListener ( final String queryId, final QueryListener listener )
    {

    }
}
