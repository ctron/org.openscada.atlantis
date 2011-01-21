/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.spring.server;

import org.openscada.core.ConnectionInformation;
import org.openscada.da.core.server.Hive;
import org.openscada.da.server.net.Exporter;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

public class NetExporter implements InitializingBean, DisposableBean
{

    private Hive hive;

    private Exporter exporter;

    private String connectionString;

    public void setConnectionString ( final String connectionString )
    {
        this.connectionString = connectionString;
    }

    public void setHive ( final Hive hive )
    {
        this.hive = hive;
    }

    @Override
    public void afterPropertiesSet () throws Exception
    {
        Assert.notNull ( this.hive, "'hive' must be set" );
        Assert.hasText ( this.connectionString, "'connectionString' must be set" );

        this.exporter = new Exporter ( this.hive, ConnectionInformation.fromURI ( this.connectionString ) );
        this.exporter.start ();
    }

    @Override
    public void destroy () throws Exception
    {
        this.exporter.stop ();
        this.exporter = null;
    }

}
