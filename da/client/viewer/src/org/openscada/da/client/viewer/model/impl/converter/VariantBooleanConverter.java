package org.openscada.da.client.viewer.model.impl.converter;

import org.openscada.core.Variant;
import org.openscada.da.client.viewer.model.impl.BaseDynamicObject;
import org.openscada.da.client.viewer.model.impl.BooleanSetterOutput;
import org.openscada.da.client.viewer.model.impl.PropertyInput;

public class VariantBooleanConverter extends BaseDynamicObject
{
    private BooleanSetterOutput _output = new BooleanSetterOutput ( "value" );
    private BooleanSetterOutput _errorOutput = new BooleanSetterOutput ( "error" );
    private Variant _value = null;
    
    public VariantBooleanConverter ( String id )
    {
        super ( id );
        
        addOutput ( _output );
        addOutput ( _errorOutput );
        addInput ( new PropertyInput ( this, "value" ) );
    }
    
    public void setValue ( Variant value )
    {
        _value = value;
        update ();
    }
    
    public void update ()
    {
        try
        {
            if ( _value.isNull () )
                _output.setValue ( null );
            else
                _output.setValue ( _value.asBoolean () );
        }
        catch ( Exception e )
        {
            _output.setValue ( (Boolean)null );
            _errorOutput.setValue ( true );
        }
    }
}
