/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.scada.hd.data.QueryParameters;
import org.eclipse.scada.hd.data.ValueInformation;
import org.openscada.hd.Query;
import org.openscada.hd.QueryListener;
import org.openscada.hd.QueryState;

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

    @Override
    public void updateData ( final int index, final Map<String, List<Double>> values, final List<ValueInformation> valueInformation )
    {
        this.connectionHandler.sendQueryData ( this.id, index, values, valueInformation );
    }

    @Override
    public void updateParameters ( final QueryParameters parameters, final Set<String> valueTypes )
    {
        this.connectionHandler.sendQueryParameters ( this.id, parameters, valueTypes );
    }

    @Override
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
