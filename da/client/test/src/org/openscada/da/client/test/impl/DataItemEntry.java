package org.openscada.da.client.test.impl;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.openscada.da.core.IODirection;
import org.openscada.da.core.data.Variant;

public class DataItemEntry extends BrowserEntry
{
    private String _id = null;
    private EnumSet<IODirection> _ioDirection = EnumSet.noneOf ( IODirection.class );
    
    private enum Properties
    {
        ITEM_ID,
        IO_DIRECTION
    }
    
    public DataItemEntry ( String name, Map<String, Variant> attributes, FolderEntry parent, HiveConnection connection, String id, EnumSet<IODirection> ioDirection )
    {
        super ( name, attributes, connection, parent );
        _id = id;
        _ioDirection = ioDirection;
    }

    public String getId ()
    {
        return _id;
    }
    
    @Override
    protected void fillPropertyDescriptors ( List<IPropertyDescriptor> list )
    {
        {
            PropertyDescriptor pd = new PropertyDescriptor ( Properties.ITEM_ID, "Item ID" );
            pd.setCategory ( "Data Item Info" );
            pd.setAlwaysIncompatible ( true );
            list.add ( pd );
        }
        {
            PropertyDescriptor pd = new PropertyDescriptor ( Properties.IO_DIRECTION, "IO Direction" );
            pd.setCategory ( "Data Item Info" );
            list.add ( pd );
        }
        super.fillPropertyDescriptors ( list );
    }
    
    @Override
    public Object getPropertyValue ( Object id )
    {
        if ( id.equals ( Properties.ITEM_ID ) )
            return _id;
        if ( id.equals ( Properties.IO_DIRECTION ))
            return _ioDirection;
        
        return super.getPropertyValue ( id );
    }

    public EnumSet<IODirection> getIoDirection ()
    {
        return _ioDirection;
    }
}
