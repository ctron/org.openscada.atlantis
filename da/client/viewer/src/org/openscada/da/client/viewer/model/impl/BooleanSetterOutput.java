package org.openscada.da.client.viewer.model.impl;

import java.util.EnumSet;

import org.openscada.da.client.viewer.model.OutputDefinition;
import org.openscada.da.client.viewer.model.Type;

public class BooleanSetterOutput extends BaseOutput implements OutputDefinition
{
    public BooleanSetterOutput ( String name )
    {
        super ( name );
    }
    
    public EnumSet<Type> getSupportedTypes ()
    {
        return EnumSet.of ( Type.BOOLEAN );
    }

    public void setValue ( Boolean value )
    {
        fireEvent ( Type.BOOLEAN, value );
    }
}
