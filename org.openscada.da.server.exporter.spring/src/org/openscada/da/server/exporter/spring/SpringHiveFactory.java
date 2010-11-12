/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.da.server.exporter.spring;

import java.util.HashMap;
import java.util.Map;

import org.openscada.da.core.server.Hive;
import org.openscada.da.server.exporter.ConfigurationException;
import org.openscada.da.server.exporter.HiveConfigurationType;
import org.openscada.da.server.exporter.HiveFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class SpringHiveFactory implements HiveFactory
{
    protected Map<String, ApplicationContext> ctxMap = new HashMap<String, ApplicationContext> ();

    public Hive createHive ( final String reference, final HiveConfigurationType configuration ) throws ConfigurationException
    {
        final String[] tok = reference.split ( "#", 2 );

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

        final Hive hive = (Hive)getApplicationContext ( file ).getBean ( beanName, Hive.class );
        return hive;
    }

    protected ApplicationContext getApplicationContext ( final String file )
    {
        ApplicationContext ctx = this.ctxMap.get ( file );
        if ( ctx == null )
        {
            ctx = new FileSystemXmlApplicationContext ( file );
            this.ctxMap.put ( file, ctx );
        }
        return ctx;
    }
}
