package org.openscada.da.client.viewer.model.impl;

import java.util.EnumSet;

import org.openscada.da.client.viewer.model.OutputDefinition;
import org.openscada.da.client.viewer.model.Type;

public class AnySetterOutput extends BaseOutput implements OutputDefinition
{
    public AnySetterOutput ( String name )
    {
        super ( name );
    }
    
    public EnumSet<Type> getSupportedTypes ()
    {
        return EnumSet.allOf ( Type.class );
    }

    public void setValue ( AnyValue value )
    {
        fireEvent ( value.getType (), value.getValue () );
    }
}
