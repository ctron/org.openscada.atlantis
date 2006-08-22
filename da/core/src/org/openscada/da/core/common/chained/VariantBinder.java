package org.openscada.da.core.common.chained;

import org.openscada.da.core.data.Variant;

public class VariantBinder implements AttributeBinder
{
    private Variant _defaultValue = null;
    private Variant _value = null;
    
    public VariantBinder ( Variant defaultValue )
    {
        super ();
        _defaultValue = defaultValue;
    }
    
    public VariantBinder ()
    {
        super ();
    }
    
    public void bind ( Variant value ) throws Exception
    {
        _value = value;
    }

    public Variant getValue ()
    {
        if ( _value == null )
            return _defaultValue;
        return _value;
    }

    public Variant getAttributeValue ()
    {
        return new Variant ( getValue () );
    }

}
