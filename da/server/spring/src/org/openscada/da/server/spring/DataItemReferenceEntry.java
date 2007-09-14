package org.openscada.da.server.spring;

import java.util.EnumSet;

import org.openscada.da.core.IODirection;
import org.openscada.da.server.common.DataItem;

public class DataItemReferenceEntry extends Entry
{
    private EnumSet<IODirection> _ioDirections;
    private String _id;
    private Hive _hive; 

    public EnumSet<IODirection> getIODirections ()
    {
        return _ioDirections;
    }

    public void setIoDirections ( EnumSet<IODirection> ioDirections )
    {
        _ioDirections = ioDirections;
    }

    public DataItem getDataItem ()
    {
        return _hive.findDataItem ( _id );
    }

    public void setId ( String id )
    {
        _id = id;
    }

    public void setHive ( Hive hive )
    {
        _hive = hive;
    }
}
