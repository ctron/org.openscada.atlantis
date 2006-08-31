package org.openscada.ae.storage.test;

import org.openscada.ae.core.QueryDescription;
import org.openscada.ae.storage.common.StorageCommon;
import org.openscada.core.Variant;
import org.openscada.utils.collection.MapBuilder;

public class Storage extends StorageCommon
{
    private MemoryQuery _allQuery = new MemoryQuery ();
    
    public Storage ()
    {
        super ();
        addQuery ( new QueryDescription ( "all", new MapBuilder<String, Variant> ()
                .put ( "description", new Variant ( "A query containing all items" ) )
                .getMap () ), _allQuery );
    }
}
