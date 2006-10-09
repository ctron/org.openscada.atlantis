package org.openscada.da.client.viewer.model.impl.converter;

import org.eclipse.swt.graphics.RGB;
import org.openscada.da.client.viewer.model.impl.BaseDynamicObject;
import org.openscada.da.client.viewer.model.impl.ColorSetterOutput;
import org.openscada.da.client.viewer.model.impl.PropertyInput;

public class ColorComposer extends BaseDynamicObject
{
    private RGB _color = new RGB ( 0, 0, 0 );
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
        _color.red = value.intValue ();
        update ();
    }
    
    public void setGreen ( Long value )
    {
        _color.green = value.intValue ();
        update ();
    }
    
    public void setBlue ( Long value )
    {
        _color.blue = value.intValue ();
        update ();
    }
    
    protected void update ()
    {
        _output.setValue ( _color );
    }
}
