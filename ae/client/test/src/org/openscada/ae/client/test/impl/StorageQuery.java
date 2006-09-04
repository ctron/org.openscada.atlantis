package org.openscada.ae.client.test.impl;

import org.openscada.ae.core.QueryDescription;

public class StorageQuery
{
    private StorageConnection _connection = null;
    private QueryDescription _queryDescription  = null;
    
    public StorageQuery ()
    {
        super ();
    }
    
    public StorageQuery ( StorageConnection connection, QueryDescription queryDescription )
    {
        super ();
        _connection = connection;
        _queryDescription = queryDescription;
    }
    
    public StorageConnection getConnection ()
    {
        return _connection;
    }
    public void setConnection ( StorageConnection connection )
    {
        _connection = connection;
    }
    public QueryDescription getQueryDescription ()
    {
        return _queryDescription;
    }
    public void setQueryDescription ( QueryDescription queryDescription )
    {
        _queryDescription = queryDescription;
    }
}
