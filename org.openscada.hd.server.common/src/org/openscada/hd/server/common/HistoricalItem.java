/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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
