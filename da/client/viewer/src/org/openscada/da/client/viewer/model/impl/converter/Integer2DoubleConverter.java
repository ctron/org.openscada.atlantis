package org.openscada.da.client.viewer.model.impl.converter;

import org.openscada.da.client.viewer.model.impl.BaseDynamicObject;
import org.openscada.da.client.viewer.model.impl.DoubleSetterOutput;
import org.openscada.da.client.viewer.model.impl.PropertyInput;

public class Integer2DoubleConverter extends BaseDynamicObject
{
    private Long _value = null;

    protected DoubleSetterOutput _output = new DoubleSetterOutput ( "value" );
    
    public Integer2DoubleConverter ( String id )
    {
        super ( id );
        
        addInput ( new PropertyInput ( this, "value" ) );
        
        addOutput ( _output );
    }
    
    public void setValue ( Long value )
    {
        _value = value.longValue ();
        update ();
    }

    protected void update ()
    {
        if ( _value == null )
            _output.setValue ( (Double)null );
        else
            _output.setValue ( _value.doubleValue () );
    }
}
