/*
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
 *
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.da.server.exporter.osgi;

import java.net.MalformedURLException;
import java.net.URL;

import org.openscada.da.server.exporter.ConfigurationException;
import org.openscada.da.server.exporter.Controller;
import org.openscada.da.server.exporter.HiveFactory;
import org.openscada.da.server.exporter.osgi.internal.Activator;
import org.osgi.framework.BundleContext;

public class Exporter
{
    private final Controller controller;

    public Exporter ( final String configurationFileUrl ) throws MalformedURLException, ConfigurationException
    {
        this.controller = new Controller ( createHiveFactory ( Activator.getContext () ), new URL ( configurationFileUrl ) );
    }

    private HiveFactory createHiveFactory ( final BundleContext context )
    {
        return new BundleContextHiveFactory ( context );
    }

    public void start () throws Exception
    {
        this.controller.start ();
    }

    public void stop () throws Exception
    {
        this.controller.stop ();
    }
}
