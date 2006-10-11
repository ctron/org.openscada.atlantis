package org.openscada.da.client.viewer.model.impl.converter;

import org.openscada.da.client.viewer.model.impl.BaseDynamicObject;
import org.openscada.da.client.viewer.model.impl.DoubleSetterOutput;
import org.openscada.da.client.viewer.model.impl.PropertyInput;

public class FactorCalculator extends BaseDynamicObject
{
    private Double _value = null;
    private Double _factor = null;
    
    protected DoubleSetterOutput _output = new DoubleSetterOutput ( "value" );
    
    public FactorCalculator ( String id )
    {
        super ( id );
        
        addInput ( new PropertyInput ( this, "value" ) );
        addInput ( new PropertyInput ( this, "factor" ) );
        
        addOutput ( _output );
    }
    
    public void setValue ( Double value )
    {
        _value = value;
        update ();
    }
    
    public void setFactor ( Double value )
    {
        _factor = value;
        update ();
    }
    
    protected void update ()
    {
        if ( ( _value != null ) && ( _factor != null ) )
        {
            _output.setValue ( _value.doubleValue () * _factor  );
        }
        else
            _output.setValue ( (Double)null );
    }
}
