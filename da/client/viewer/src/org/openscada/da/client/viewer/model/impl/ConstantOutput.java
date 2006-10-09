package org.openscada.da.client.viewer.model.impl;

import java.util.EnumSet;

import org.openscada.da.client.viewer.model.Type;

public class ConstantOutput extends BaseOutput
{
    public ConstantOutput ( String name )
    {
        super ( name );
    }

    private Type _type = Type.NULL;
    private Object _value = null;
    
    public void setValue ( Type type, Object value )
    {
        _type = type;
        _value = value;
        
        fireEvent ( _type, _value );
    }

    public Type getType ()
    {
        return _type;
    }

    public Object getValue ()
    {
        return _value;
    }

    public EnumSet<Type> getSupportedTypes ()
    {
        return EnumSet.of ( _type );
    }
}
