/**
 * 
 */
package org.openscada.net.da.handler;

import java.util.EnumSet;
import java.util.Map;

import org.openscada.da.core.IODirection;
import org.openscada.da.core.browser.DataItemEntry;
import org.openscada.da.core.data.Variant;

public class DataItemEntryCommon extends EntryCommon implements DataItemEntry
{
    private String _id = "";
    private EnumSet<IODirection> _directions = EnumSet.noneOf ( IODirection.class );
    
    public DataItemEntryCommon ( String name, EnumSet<IODirection> directions, Map<String, Variant> attributes, String id )
    {
        super ( name, attributes );
        _directions = directions;
        _id = id;
    }
    
    public String getId ()
    {
        return _id;
    }

    public EnumSet<IODirection> getIODirections ()
    {
        return _directions;
    }
}