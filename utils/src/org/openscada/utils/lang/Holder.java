package org.openscada.utils.lang;

public class Holder < T >
{
    public T value = null;
    
    public Holder ()
    {
        super();
    }
    
    public Holder ( T value )
    {
        this.value = value;
    }
}
