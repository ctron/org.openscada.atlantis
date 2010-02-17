/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2007 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openscada.da.server.exporter.spring;

import java.util.HashMap;
import java.util.Map;

import org.openscada.da.core.server.Hive;
import org.openscada.da.server.exporter.ConfigurationException;
import org.openscada.da.server.exporter.HiveFactory;
import org.openscada.da.server.exporter.HiveConfigurationType;
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
