package org.openscada.da.client.viewer.model.impl;

import java.util.EnumSet;

import org.openscada.da.client.viewer.model.OutputDefinition;
import org.openscada.da.client.viewer.model.Type;

public class IntegerSetterOutput extends BaseOutput implements OutputDefinition
{
    public IntegerSetterOutput ( String name )
    {
        super ( name );
    }
    
    public EnumSet<Type> getSupportedTypes ()
    {
        return EnumSet.of ( Type.INTEGER );
    }

    public void setValue ( Long value )
    {
        fireEvent ( Type.INTEGER, value );
    }
    
    public void setValue ( Integer value )
    {
        setValue ( value.longValue () );
    }
}
