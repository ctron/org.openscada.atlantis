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