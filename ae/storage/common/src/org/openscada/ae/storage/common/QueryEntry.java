/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
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

package org.openscada.ae.storage.common;

import org.openscada.ae.core.QueryDescription;

class QueryEntry
{
    private Query _query = null;
    private QueryDescription _description = null;
    
    public QueryEntry ( Query query, QueryDescription description )
    {
        super ();
        _query = query;
        _description = description;
    }
    
    public QueryDescription getDescription ()
    {
        return _description;
    }
    public void setDescription ( QueryDescription description )
    {
        _description = description;
    }
    
    public Query getQuery ()
    {
        return _query;
    }
    public void setQuery ( Query query )
    {
        _query = query;
    }
}