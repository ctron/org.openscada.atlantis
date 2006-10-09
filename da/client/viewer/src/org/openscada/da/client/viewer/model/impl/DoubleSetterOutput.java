package org.openscada.da.client.viewer.model.impl;

import java.util.EnumSet;

import org.openscada.da.client.viewer.model.OutputDefinition;
import org.openscada.da.client.viewer.model.Type;

public class DoubleSetterOutput extends BaseOutput implements OutputDefinition
{
    public DoubleSetterOutput ( String name )
    {
        super ( name );
    }
    
    public EnumSet<Type> getSupportedTypes ()
    {
        return EnumSet.of ( Type.DOUBLE );
    }

    public void setValue ( Double value )
    {
        fireEvent ( Type.DOUBLE, value );
    }
    
    public void setValue ( Float value )
    {
        setValue ( value.doubleValue () );
    }
}
