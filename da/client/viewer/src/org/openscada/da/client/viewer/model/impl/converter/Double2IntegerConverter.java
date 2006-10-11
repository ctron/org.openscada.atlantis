package org.openscada.da.client.viewer.model.impl.converter;

import org.openscada.da.client.viewer.model.impl.BaseDynamicObject;
import org.openscada.da.client.viewer.model.impl.IntegerSetterOutput;
import org.openscada.da.client.viewer.model.impl.PropertyInput;

public class Double2IntegerConverter extends BaseDynamicObject
{
    private Double _value = null;

    protected IntegerSetterOutput _output = new IntegerSetterOutput ( "value" );
    
    public Double2IntegerConverter ()
    {
        addInput ( new PropertyInput ( this, "value" ) );
        
        addOutput ( _output );
    }
    
    public void setValue ( Double value )
    {
        if ( value == null )
            _value = null;
        else
            _value = value.doubleValue ();
        update ();
    }

    protected void update ()
    {
        if ( _value == null )
            _output.setValue ( (Long)null );
        else
            _output.setValue ( _value.longValue () );
    }
}
