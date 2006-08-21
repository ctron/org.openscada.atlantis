package org.openscada.da.server.test.items;

import java.util.EnumSet;

import org.openscada.da.core.IODirection;
import org.openscada.da.core.InvalidOperationException;
import org.openscada.da.core.common.DataItemInformationBase;
import org.openscada.da.core.common.chained.DataItemInputChained;
import org.openscada.da.core.data.NotConvertableException;
import org.openscada.da.core.data.NullValueException;
import org.openscada.da.core.data.Variant;

public class MemoryChainedItem extends DataItemInputChained
{

    public MemoryChainedItem ( String id )
    {
        super ( new DataItemInformationBase ( id, EnumSet.of ( IODirection.INPUT, IODirection.OUTPUT ) ) );
    }
    
    @Override
    public void setValue ( Variant value ) throws InvalidOperationException, NullValueException, NotConvertableException
    {
        updateValue ( value );
    }

}
