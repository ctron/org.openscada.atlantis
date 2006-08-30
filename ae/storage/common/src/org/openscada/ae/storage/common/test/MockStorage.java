package org.openscada.ae.storage.common.test;

import org.openscada.ae.core.QueryDescription;
import org.openscada.ae.storage.common.Query;
import org.openscada.ae.storage.common.StorageCommon;

public class MockStorage extends StorageCommon
{
    public MockStorage ()
    {
        super ();
        
        QueryDescription description;
        Query query;
        
        description = new QueryDescription ( "test1" );
        query = new MockQuery ();
        
        addQuery ( description, query );
    }
}
