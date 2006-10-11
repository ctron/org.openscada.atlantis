package org.openscada.da.client.viewer.model.impl.converter;

import org.eclipse.swt.graphics.RGB;
import org.openscada.da.client.viewer.model.impl.BaseDynamicObject;
import org.openscada.da.client.viewer.model.impl.ColorSetterOutput;
import org.openscada.da.client.viewer.model.impl.PropertyInput;
import org.openscada.da.client.viewer.model.types.Color;

public class ColorComposer extends BaseDynamicObject
{
    private Color _color = new Color ( 0, 0, 0 );
    private ColorSetterOutput _output = new ColorSetterOutput ( "color" );
    
    public ColorComposer ()
    {
        super ();
        
        addInput ( new PropertyInput ( this, "red" ) );
        addInput ( new PropertyInput ( this, "green" ) );
        addInput ( new PropertyInput ( this, "blue" ) );
        
        addOutput ( _output );
    }
    
    public void setRed ( Long value )
    {
        if ( value == null )
            _color.setRed ( 0 );
        else
            _color.setRed (  value.intValue () );
        update ();
    }
    
    public void setGreen ( Long value )
    {
        if ( value == null )
            _color.setGreen ( 0 );
        else
            _color.setGreen ( value.intValue () );
        update ();
    }
    
    public void setBlue ( Long value )
    {
        if ( value == null )
            _color.setBlue ( 0 );
        else
            _color.setBlue ( value.intValue () );

        update ();
    }
    
    protected void update ()
    {
        _output.setValue ( _color );
    }
}
