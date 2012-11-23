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

package org.openscada.hd.server.common.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.openscada.core.server.common.session.AbstractSessionImpl;
import org.openscada.hd.ItemListListener;
import org.openscada.hd.data.HistoricalItemInformation;
import org.openscada.hd.server.Session;
import org.openscada.sec.UserInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionImpl extends AbstractSessionImpl implements Session, ItemListListener
{
    private final static Logger logger = LoggerFactory.getLogger ( SessionImpl.class );

    private final HashMap<String, HistoricalItemInformation> itemCache = new HashMap<String, HistoricalItemInformation> ();

    private final Collection<QueryImpl> queries = new LinkedList<QueryImpl> ();

    private ItemListListener itemListListener;

    public SessionImpl ( final UserInformation user, final Map<String, String> properties )
    {
        super ( user, properties );
        logger.info ( "Created new session" );
    }

    public void dispose ()
    {
        logger.info ( "Disposing session" );
        synchronized ( this )
        {
            // close all queries
            final Collection<QueryImpl> queries = new ArrayList<QueryImpl> ( this.queries );
            for ( final QueryImpl query : queries )
            {
                query.dispose ();
            }
            this.queries.clear ();
        }
    }

    @Override
    public void setItemListListener ( final ItemListListener itemListListener )
    {
        synchronized ( this )
        {
            this.itemListListener = itemListListener;
            if ( itemListListener != null )
            {
                fireListChanged ( new HashSet<HistoricalItemInformation> ( this.itemCache.values () ), null, true );
            }
        }
    }

    @Override
    public void listChanged ( final Set<HistoricalItemInformation> addedOrModified, final Set<String> removed, final boolean full )
    {
        synchronized ( this )
        {
            if ( full )
            {
                this.itemCache.clear ();
            }
            if ( removed != null && !full )
            {
                for ( final String item : removed )
                {
                    this.itemCache.remove ( item );
                }
            }
            if ( addedOrModified != null )
            {
                for ( final HistoricalItemInformation item : addedOrModified )
                {
                    this.itemCache.put ( item.getItemId (), item );
                }
            }
            fireListChanged ( addedOrModified, removed, full );
        }
    }

    protected void fireListChanged ( final Set<HistoricalItemInformation> addedOrModified, final Set<String> removed, final boolean full )
    {
        synchronized ( this )
        {
            if ( this.itemListListener != null )
            {
                this.itemListListener.listChanged ( addedOrModified, removed, full );
            }
        }
    }

    public void addQuery ( final QueryImpl query )
    {
        synchronized ( this )
        {
            this.queries.add ( query );
        }
    }

    public void removeQuery ( final QueryImpl query )
    {
        synchronized ( this )
        {
            this.queries.remove ( query );
        }
    }
}
