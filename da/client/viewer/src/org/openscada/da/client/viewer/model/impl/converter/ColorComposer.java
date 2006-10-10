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
        if ( value == null )
            _color.red = 0;
        else
            _color.red = value.intValue ();
        update ();
    }
    
    public void setGreen ( Long value )
    {
        if ( value == null )
            _color.green = 0;
        else
            _color.green = value.intValue ();
        update ();
    }
    
    public void setBlue ( Long value )
    {
        if ( value != null )
            _color.blue = 0;
        else
            _color.blue = value.intValue ();

        update ();
    }
    
    protected void update ()
    {
        _output.setValue ( _color );
    }
}
