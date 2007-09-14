package org.openscada.da.server.spring.tools.csv;

import java.util.EnumSet;

import org.openscada.core.InvalidOperationException;
import org.openscada.core.NotConvertableException;
import org.openscada.core.NullValueException;
import org.openscada.core.Variant;
import org.openscada.da.core.IODirection;
import org.openscada.da.server.common.DataItemInformationBase;
import org.openscada.da.server.common.chain.MemoryItemChained;

public class CSVDataItem extends MemoryItemChained
{

    protected CSVControllerDataItem _controllerItem;

    public CSVDataItem ( String name, EnumSet<IODirection> ioDirection )
    {
        super ( new DataItemInformationBase ( name, ioDirection ) );
    }

    @Override
    public Variant readValue () throws InvalidOperationException
    {
        if ( !isReadable () )
        {
            throw new InvalidOperationException ();
        }
        return super.readValue ();
    }

    @Override
    protected void writeCalculatedValue ( Variant value )
    {
        fireWrite ( value );
        if ( isReadable () )
        {
            super.writeCalculatedValue ( value );
        }
    }
    
    @Override
    public void writeValue ( Variant value ) throws InvalidOperationException, NullValueException, NotConvertableException
    {
        if ( !isWriteable () )
        {
            throw new InvalidOperationException ();
        }   
        super.writeValue ( value );
    }

    private void fireWrite ( Variant value )
    {
        CSVControllerDataItem controllerItem = _controllerItem;
        if ( controllerItem != null )
        {
            controllerItem.handleWrite ( value );
        }
    }

    public void setController ( CSVControllerDataItem controllerItem )
    {
        _controllerItem = controllerItem;
    }

    public boolean isReadable ()
    {
        return this.getInformation ().getIODirection ().contains ( IODirection.INPUT );
    }

    public boolean isWriteable ()
    {
        return this.getInformation ().getIODirection ().contains ( IODirection.OUTPUT );
    }
    
}
