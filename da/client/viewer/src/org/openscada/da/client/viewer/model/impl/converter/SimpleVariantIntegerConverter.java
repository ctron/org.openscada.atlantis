package org.openscada.da.client.viewer.model.impl.converter;

import org.openscada.core.Variant;
import org.openscada.da.client.viewer.model.impl.BaseDynamicObject;
import org.openscada.da.client.viewer.model.impl.IntegerSetterOutput;
import org.openscada.da.client.viewer.model.impl.PropertyInput;

public class SimpleVariantIntegerConverter extends BaseDynamicObject
{
    private IntegerSetterOutput _output = new IntegerSetterOutput ( "value" );
    private Variant _value = null;
    private long _defaultValue = 0;
    
    public SimpleVariantIntegerConverter ( String id )
    {
        super ( id );
        
        addOutput ( _output );
        addInput ( new PropertyInput ( this, "value" ) );
        addInput ( new PropertyInput ( this, "defaultValue" ) );
    }
    
    public void setValue ( Variant value )
    {
        _value = value;
        update ();
    }
    
    public void setDefaultValue ( Long defaultValue )
    {
        if ( defaultValue != null )
        {
            _defaultValue = defaultValue.longValue ();
            update ();
        }
    }
    
    public void update ()
    {
        try
        {
            _output.setValue ( _value.asLong () );
        }
        catch ( Exception e )
        {
            _output.setValue ( _defaultValue );
        }
    }
}
