package org.openscada.da.server.test.items;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.openscada.core.InvalidOperationException;
import org.openscada.core.NotConvertableException;
import org.openscada.core.NullValueException;
import org.openscada.core.Variant;
import org.openscada.da.core.IODirection;
import org.openscada.da.core.WriteResult;
import org.openscada.da.server.common.AttributeMode;
import org.openscada.da.server.common.DataItemInformationBase;
import org.openscada.da.server.common.chain.MemoryItemChained;
import org.openscada.da.server.common.chain.item.SumErrorChainItem;
import org.openscada.da.server.common.impl.HiveCommon;
import org.openscada.utils.concurrent.InstantErrorFuture;
import org.openscada.utils.concurrent.InstantFuture;
import org.openscada.utils.concurrent.NotifyFuture;

public class TestItem2 extends MemoryItemChained
{

    public TestItem2 ( final HiveCommon hive, final String id )
    {
        super ( new DataItemInformationBase ( id, EnumSet.of ( IODirection.INPUT, IODirection.OUTPUT ) ) );
        addChainElement ( IODirection.INPUT, new SumErrorChainItem ( hive ) );
    }

    @Override
    protected NotifyFuture<WriteResult> startWriteCalculatedValue ( final Variant value )
    {
        try
        {
            performWriteValue ( value );
            return new InstantFuture<WriteResult> ( new WriteResult () );
        }
        catch ( final Throwable e )
        {
            return new InstantErrorFuture<WriteResult> ( e );
        }
    }

    protected synchronized void performWriteValue ( final Variant value ) throws InvalidOperationException, NullValueException, NotConvertableException
    {
        final Map<String, Variant> attr = new HashMap<String, Variant> ();

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
