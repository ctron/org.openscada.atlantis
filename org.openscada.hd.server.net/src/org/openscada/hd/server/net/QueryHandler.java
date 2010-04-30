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

package org.openscada.hd.server.net;

import java.util.Map;
import java.util.Set;

import org.openscada.hd.Query;
import org.openscada.hd.QueryListener;
import org.openscada.hd.QueryParameters;
import org.openscada.hd.QueryState;
import org.openscada.hd.Value;
import org.openscada.hd.ValueInformation;

public class QueryHandler implements QueryListener
{

    private Query query;

    private final ServerConnectionHandler connectionHandler;

    private final long id;

    public QueryHandler ( final long id, final ServerConnectionHandler connectionHandler )
    {
        this.id = id;
        this.connectionHandler = connectionHandler;
    }

    public void updateData ( final int index, final Map<String, Value[]> values, final ValueInformation[] valueInformation )
    {
        this.connectionHandler.sendQueryData ( this.id, index, values, valueInformation );
    }

    public void updateParameters ( final QueryParameters parameters, final Set<String> valueTypes )
    {
        this.connectionHandler.sendQueryParameters ( this.id, parameters, valueTypes );
    }

    public void updateState ( final QueryState state )
    {
        this.connectionHandler.sendQueryState ( this.id, state );
    }

    public void setQuery ( final Query query )
    {
        this.query = query;
    }

    public void close ()
    {
        if ( this.query != null )
        {
            this.query.close ();
        }
    }

    public void changeParameters ( final QueryParameters parameters )
    {
        this.query.changeParameters ( parameters );
    }

}
