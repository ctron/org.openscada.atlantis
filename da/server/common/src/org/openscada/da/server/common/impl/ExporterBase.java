package org.openscada.da.server.common.impl;


import java.io.IOException;

import org.openscada.da.core.server.Hive;

public class ExporterBase
{
    protected Hive _hive = null;
    
    public ExporterBase ( Hive hive ) throws IOException
    {
        _hive = hive;
    }
    
    public ExporterBase ( Class<?> hiveClass ) throws InstantiationException, IllegalAccessException, IOException
    {
        this ( createInstance ( hiveClass ) );
    }
    
    public ExporterBase ( String hiveClassName ) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException
    {
        this ( createInstance ( Class.forName ( hiveClassName ) ) );
    }
    
    private static Hive createInstance ( Class<?> hiveClass ) throws InstantiationException, IllegalAccessException
    {
        return (Hive)hiveClass.newInstance ();
    }
    
    public Class<?> getHiveClass ()
    {
        return _hive.getClass ();
    }
    
}
