package org.openscada.da.client.viewer.model.impl;

import java.util.EnumSet;

import org.openscada.da.client.viewer.model.OutputDefinition;
import org.openscada.da.client.viewer.model.Type;
import org.openscada.da.client.viewer.model.types.Color;

public class ColorSetterOutput extends BaseOutput implements OutputDefinition
{
    public ColorSetterOutput ( String name )
    {
        super ( name );
    }
    
    public EnumSet<Type> getSupportedTypes ()
    {
        return EnumSet.of ( Type.COLOR );
    }

    public void setValue ( Color value )
    {
        fireEvent ( Type.COLOR, value );
    }
}
