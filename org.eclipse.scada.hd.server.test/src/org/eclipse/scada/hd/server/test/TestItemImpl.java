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

package org.eclipse.scada.hd.server.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.scada.core.Variant;
import org.eclipse.scada.hd.Query;
import org.eclipse.scada.hd.QueryListener;
import org.eclipse.scada.hd.data.HistoricalItemInformation;
import org.eclipse.scada.hd.data.QueryParameters;
import org.eclipse.scada.hd.server.common.HistoricalItem;

public class TestItemImpl implements HistoricalItem
{

    private final Set<TestQueryImpl> queries = new HashSet<TestQueryImpl> ();

    @Override
    public Query createQuery ( final QueryParameters parameters, final QueryListener listener, final boolean updateData )
    {
        final TestQueryImpl query = new TestQueryImpl ( this, parameters, listener );
        this.queries.add ( query );
        return query;
    }

    @Override
    public HistoricalItemInformation getInformation ()
    {
        return new HistoricalItemInformation ( "test1", new HashMap<String, Variant> () );
    }

    public void dispose ()
    {

        final Collection<TestQueryImpl> queries = new ArrayList<TestQueryImpl> ( this.queries );

        for ( final TestQueryImpl query : queries )
        {
            query.close ();
        }
    }

    protected void remove ( final TestQueryImpl query )
    {
        this.queries.remove ( query );
    }

}
