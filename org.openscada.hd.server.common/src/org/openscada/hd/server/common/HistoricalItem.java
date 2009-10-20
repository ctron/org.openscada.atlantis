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

package org.openscada.hd.server.common;

import org.openscada.hd.HistoricalItemInformation;
import org.openscada.hd.Query;
import org.openscada.hd.QueryListener;
import org.openscada.hd.QueryParameters;
import org.openscada.hd.QueryState;

/**
 * An interface of a historical item
 * 
 * @author Jens Reimann
 *
 */
public interface HistoricalItem
{
    /**
     * Create a new query
     * @param parameters the initial query parameters
     * @param listener the query listener
     * @param updateData if <code>true</code> then additional updates will be made by the query when
     * data changes although the query is {@link QueryState#COMPLETE}
     * @return the new query or <code>null</code> if the query could be be created
     */
    public Query createQuery ( QueryParameters parameters, QueryListener listener, boolean updateData );

    /**
     * Get the item information
     * @return the item information
     */
    public HistoricalItemInformation getInformation ();
}
