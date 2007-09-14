package org.openscada.da.server.exporter;

import java.util.HashMap;
import java.util.Map;

import org.openscada.da.core.server.Hive;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class SpringHiveFactory implements HiveFactory
{
    protected Map<String, ApplicationContext> ctxMap = new HashMap<String, ApplicationContext> ();
    
    public Hive createHive ( String reference, HiveConfigurationType configuration ) throws ConfigurationException
    {
        String [] tok = reference.split ( "#", 2 );
        
        String beanName = "hive";
        String file = "file:applicationContext.xml";
        
        if ( tok.length < 2 )
        {
            file = tok[0];
        }
        else
        {
            file = tok[0];
            beanName = tok[1];
        }
        
        Hive hive = (Hive)getApplicationContext ( file ).getBean ( beanName, Hive.class );
        return hive;
    }
    
    protected ApplicationContext getApplicationContext ( String file )
    {
        ApplicationContext ctx = ctxMap.get ( file );
        if ( ctx == null )
        {
            ctx = new FileSystemXmlApplicationContext ( file );
            ctxMap.put ( file, ctx );
        }
        return ctx;
    }
}
