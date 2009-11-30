package org.openscada.ae.server.storage.jdbc;

public class DebugService
{
    private Object prop;
    
    public Object getProp ()
    {
        return prop;
    }
    public void setProp ( Object prop )
    {
        this.prop = prop;
    }
    
    public void start ()
    {
     System.out.println ("started!");
     System.out.println ("** " + prop);
    }

    public void stop ()
    {
        System.out.println ("stopped!");
    }
}
