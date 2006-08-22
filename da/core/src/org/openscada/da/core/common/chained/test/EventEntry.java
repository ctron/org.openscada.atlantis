/**
 * 
 */
package org.openscada.da.core.common.chained.test;

import java.util.Map;

import org.openscada.da.core.common.DataItem;
import org.openscada.da.core.data.Variant;

public class EventEntry
{
    /**
     * 
     */
    private DataItem _item = null;
    private Variant _value = null;
    private Map<String, Variant> _attributes = null;
    
    public EventEntry ( DataItem item, Variant value, Map<String, Variant> attributes )
    {
        _item = item;
        _value = value;
        _attributes = attributes;
    }

    @Override
    public int hashCode ()
    {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ( ( _attributes == null ) ? 0 : _attributes.hashCode () );
        result = PRIME * result + ( ( _item == null ) ? 0 : _item.hashCode () );
        result = PRIME * result + ( ( _value == null ) ? 0 : _value.hashCode () );
        return result;
    }

    @Override
    public boolean equals ( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        final EventEntry other = (EventEntry)obj;
        if ( _attributes == null )
        {
            if ( other._attributes != null )
                return false;
        }
        else
            if ( !_attributes.equals ( other._attributes ) )
                return false;
        if ( _item == null )
        {
            if ( other._item != null )
                return false;
        }
        else
            if ( !_item.equals ( other._item ) )
                return false;
        if ( _value == null )
        {
            if ( other._value != null )
                return false;
        }
        else
            if ( !_value.equals ( other._value ) )
                return false;
        return true;
    }
}