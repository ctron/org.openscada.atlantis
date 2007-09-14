package org.openscada.da.server.spring.tools.csv;

import org.openscada.core.InvalidOperationException;
import org.openscada.core.NotConvertableException;
import org.openscada.core.Variant;
import org.openscada.da.server.common.chain.DataItemInputOutputChained;

public class CSVControllerDataItem extends DataItemInputOutputChained
{
    private CSVDataItem _item;
    
    public CSVControllerDataItem ( CSVDataItem item )
    {
        super ( item.getInformation ().getName () + "#controller" );
        _item = item;
        _item.setController ( this );
    }

    public void handleWrite ( Variant value )
    {
        updateValue ( value );
    }

    @Override
    protected void writeCalculatedValue ( Variant value ) throws NotConvertableException, InvalidOperationException
    {
        _item.updateValue ( value );
    }
}
