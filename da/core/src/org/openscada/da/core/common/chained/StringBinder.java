package org.openscada.da.core.common.chained;

import org.openscada.da.core.data.Variant;

public class StringBinder implements AttributeBinder
{
    private String _value = null;
    
    public void bind ( Variant value ) throws Exception
    {
        if ( value == null )
            _value = null;
        else
            _value = value.toString ();
    }

    public String getValue ()
    {
        return _value;
    }

    public Variant getAttributeValue ()
    {
        return new Variant ( getValue () );
    }

}
