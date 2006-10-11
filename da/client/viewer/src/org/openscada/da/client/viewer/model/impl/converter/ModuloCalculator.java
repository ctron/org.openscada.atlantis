package org.openscada.da.client.viewer.model.impl.converter;

import org.openscada.da.client.viewer.model.impl.BaseDynamicObject;
import org.openscada.da.client.viewer.model.impl.IntegerSetterOutput;
import org.openscada.da.client.viewer.model.impl.PropertyInput;

public class ModuloCalculator extends BaseDynamicObject
{
    private Long _value = null;
    private Long _modulo = null;
    
    protected IntegerSetterOutput _output = new IntegerSetterOutput ( "value" );
    
    public ModuloCalculator ( String id )
    {
        super ( id );
        
        addInput ( new PropertyInput ( this, "value" ) );
        addInput ( new PropertyInput ( this, "modulo" ) );
        
        addOutput ( _output );
    }
    
    public void setValue ( Long value )
    {
        _value = value;
        update ();
    }
    
    public void setModulo ( Long value )
    {
        _modulo = value;
        update ();
    }
    
    protected void update ()
    {
        if ( ( _value != null ) && ( _modulo != null ) )
        {
            _output.setValue ( _value.longValue () % _modulo  );
        }
        else
            _output.setValue ( (Long)null );
    }
}
