package org.openscada.da.client.viewer.model.impl.converter;

import org.openscada.core.Variant;
import org.openscada.da.client.viewer.model.impl.BaseDynamicObject;
import org.openscada.da.client.viewer.model.impl.DoubleSetterOutput;
import org.openscada.da.client.viewer.model.impl.PropertyInput;

public class SimpleVariantDoubleConverter extends BaseDynamicObject
{
    private DoubleSetterOutput _output = new DoubleSetterOutput ( "value" );
    private Variant _value = null;
    private double _defaultValue = 0;
    
    public SimpleVariantDoubleConverter ()
    {
        super ();
        
        addOutput ( _output );
        addInput ( new PropertyInput ( this, "value" ) );
        addInput ( new PropertyInput ( this, "defaultValue" ) );
    }
    
    public void setValue ( Variant value )
    {
        _value = value;
        update ();
    }
    
    public void setDefaultValue ( Double defaultValue )
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
            _output.setValue ( _value.asDouble () );
        }
        catch ( Exception e )
        {
            _output.setValue ( _defaultValue );
        }
    }
}
