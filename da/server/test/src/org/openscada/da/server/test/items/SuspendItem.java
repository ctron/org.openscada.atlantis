package org.openscada.da.server.test.items;

import org.apache.log4j.Logger;
import org.openscada.da.core.common.DataItemInputCommon;
import org.openscada.da.core.common.SuspendableItem;

public class SuspendItem extends DataItemInputCommon implements SuspendableItem
{
    private static Logger _log = Logger.getLogger ( SuspendItem.class );
    
    public SuspendItem ( String name )
    {
        super ( name );
    }

    public void suspend ()
    {
       _log.warn ( String.format ( "Item %$1s suspended", getInformation ().getName () ) );
    }

    public void wakeup ()
    {
        _log.warn ( String.format ( "Item %$1s woken up", getInformation ().getName () ) );
    }

}
