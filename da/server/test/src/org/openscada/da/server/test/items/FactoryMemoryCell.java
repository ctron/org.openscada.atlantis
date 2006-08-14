package org.openscada.da.server.test.items;

import org.openscada.da.core.common.MemoryDataItem;
import org.openscada.da.core.common.SuspendableItem;
import org.openscada.da.server.test.Hive;

public class FactoryMemoryCell extends MemoryDataItem implements SuspendableItem
{
    private Hive _hive = null;
    
    public FactoryMemoryCell ( Hive hive, String id )
    {
        super ( id );
        _hive = hive;
    }

    public void suspend ()
    {
        _hive.removeMemoryFactoryItem (  this );
    }

    public void wakeup ()
    {
        // no op
    }

}
