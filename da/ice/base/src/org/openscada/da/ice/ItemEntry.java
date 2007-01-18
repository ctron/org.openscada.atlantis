package org.openscada.da.ice;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.core.ice.AttributesHelper;
import org.openscada.da.core.IODirection;
import org.openscada.da.core.browser.DataItemEntry;

public class ItemEntry implements DataItemEntry
{
    private EnumSet<IODirection> _ioDirections = EnumSet.noneOf ( IODirection.class );

    private String _id = "";

    private Map<String, Variant> _attributes = new HashMap<String, Variant> ();

    private String _name = "";

    public ItemEntry ( OpenSCADA.DA.Browser.ItemEntry entry )
    {
        super ();
        _id = entry.itemId;
        _name = entry.name;
        _attributes = AttributesHelper.fromIce ( entry.attributes );
        _ioDirections = EnumSet.noneOf ( IODirection.class );

        for ( int i = 0; i < entry.ioDirectionsM.length; i++ )
        {
            switch ( entry.ioDirectionsM[i].value () )
            {
            case OpenSCADA.DA.IODirection._INPUT:
                _ioDirections.add ( IODirection.INPUT );
                break;
            case OpenSCADA.DA.IODirection._OUTPUT:
                _ioDirections.add ( IODirection.OUTPUT );
                break;
            }
        }
    }

    public Map<String, Variant> getAttributes ()
    {
        return _attributes;
    }

    public String getId ()
    {
        return _id;
    }

    public EnumSet<IODirection> getIODirections ()
    {
        return _ioDirections;
    }

    public String getName ()
    {
        return _name;
    }
}
