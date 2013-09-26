/*
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
 *
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.hd.server.importer.hsdb;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;

import org.eclipse.scada.hd.data.HistoricalItemInformation;
import org.eclipse.scada.hd.data.QueryParameters;
import org.eclipse.scada.hds.ValueVisitor;
import org.openscada.hd.Query;
import org.openscada.hd.QueryListener;
import org.openscada.hd.server.common.HistoricalItem;
import org.openscada.hd.server.storage.common.QueryImpl;
import org.openscada.hd.server.storage.common.ValueSourceManager;

public class HSDBHistoricalItem implements HistoricalItem, ValueSourceManager
{
    private final HistoricalItemInformation information;

    private final ScheduledExecutorService executor;

    private final HSDBValueSource source;

    private Set<QueryImpl> queries = new HashSet<QueryImpl> ();

    public HSDBHistoricalItem ( final ScheduledExecutorService executor, final HSDBValueSource source, final HistoricalItemInformation information )
    {
        this.executor = executor;
        this.information = information;
        this.source = source;
    }

    @Override
    public Query createQuery ( final QueryParameters parameters, final QueryListener listener, final boolean updateData )
    {
        final QueryImpl query = new QueryImpl ( this, this.executor, this.executor, parameters, listener, updateData, this.source.getStartTimestamp (), this.source.getEndTimestamp () );
        synchronized ( this )
        {
            if ( this.queries == null )
            {
                query.close ();
                return null;
            }
            this.queries.add ( query );
        }
        return query;
    }

    public void dispose ()
    {
        final Set<QueryImpl> queries;
        synchronized ( this )
        {
            queries = this.queries;
            this.queries = null;
        }

        for ( final QueryImpl query : queries )
        {
            query.close ();
        }
    }

    @Override
    public HistoricalItemInformation getInformation ()
    {
        return this.information;
    }

    @Override
    public void queryClosed ( final QueryImpl query )
    {
        synchronized ( this )
        {
            if ( this.queries != null )
            {
                this.queries.remove ( query );
            }
        }
    }

    @Override
    public boolean visit ( final QueryParameters parameters, final ValueVisitor visitor )
    {
        return this.source.visit ( visitor, new Date ( parameters.getStartTimestamp () ), new Date ( parameters.getEndTimestamp () ) );
    }

}
