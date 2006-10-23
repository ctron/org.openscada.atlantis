package org.openscada.da.client.viewer.model.impl;

import org.openscada.da.client.viewer.model.Type;

public class AnyValue
{
    private Type _type = Type.NULL;
    private Object _value = null;
    
    public AnyValue ()
    {
    }
    
    public AnyValue ( Type type, Object value )
    {
        _type = type;
        _value = value;
    }
    
    public Type getType ()
    {
        return _type;
    }
    public void setType ( Type type )
    {
        _type = type;
    }
    public Object getValue ()
    {
        return _value;
    }
    public void setValue ( Object value )
    {
        _value = value;
    }
}
