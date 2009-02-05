package org.openscada.da.server.test.items;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.openscada.core.InvalidOperationException;
import org.openscada.core.NotConvertableException;
import org.openscada.core.NullValueException;
import org.openscada.core.Variant;
import org.openscada.da.core.IODirection;
import org.openscada.da.server.common.AttributeMode;
import org.openscada.da.server.common.DataItemInformationBase;
import org.openscada.da.server.common.chain.MemoryItemChained;
import org.openscada.da.server.common.chain.item.SumErrorChainItem;
import org.openscada.da.server.common.impl.HiveCommon;

public class TestItem2 extends MemoryItemChained
{

    public TestItem2 ( HiveCommon hive, String id )
    {
        super ( new DataItemInformationBase ( id, EnumSet.of ( IODirection.INPUT, IODirection.OUTPUT )) );
        addChainElement ( IODirection.INPUT, new SumErrorChainItem ( hive ) );
    }
    
    @Override
    public synchronized void writeValue ( Variant value ) throws InvalidOperationException, NullValueException, NotConvertableException
    {
        Map<String, Variant> attr = new HashMap<String, Variant> ();
        
        if ( value.isNull () )
        {
            attr.put ( "test.error", null );
        }
        else
        {
            attr.put ( "test.error", new Variant ( value ) );
        }
        updateData ( null, attr, AttributeMode.UPDATE );
    }

}
