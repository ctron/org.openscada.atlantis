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

package org.openscada.da.server.exporter;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.openscada.da.core.server.Hive;
import org.openscada.da.server.common.configuration.ConfigurationError;

public class Controller
{
    private static Logger log = Logger.getLogger ( Controller.class );

    private final List<HiveExport> hives = new LinkedList<HiveExport> ();

    private final List<String> announcers = new LinkedList<String> ();

    public Controller ( final ConfigurationDocument configurationDocument ) throws ConfigurationException
    {
        super ();
        configure ( configurationDocument );
    }

    public Controller ( final String file ) throws XmlException, IOException, ConfigurationException
    {
        this ( new File ( file ) );
    }

    public Controller ( final File file ) throws XmlException, IOException, ConfigurationException
    {
        this ( ConfigurationDocument.Factory.parse ( file ) );
    }

    /**
     * Create the hive factory
     * @param factoryClass the class to instantiate
     * @return the factory
     * @throws ConfigurationException an error occurred
     */
    protected HiveFactory createHiveFactory ( final String factoryClass ) throws ConfigurationException
    {
        HiveFactory factory;
        if ( factoryClass == null )
        {
            factory = new NewInstanceHiveFactory ();
        }
        else
        {
            try
            {
                factory = (HiveFactory)Class.forName ( factoryClass ).newInstance ();
            }
            catch ( final Throwable e )
            {
                throw new ConfigurationException ( "Failed to create factory", e );
            }
        }
        return factory;
    }

    public void configure ( final ConfigurationDocument configurationDocument )
    {
        final ConfigurationType configuration = configurationDocument.getConfiguration ();

        for ( final AnnouncerType announcer : configuration.getAnnouncerList () )
        {
            final String klass = announcer.getClass1 ();
            this.announcers.add ( klass );
        }

        for ( final HiveType hive : configuration.getHiveList () )
        {
            final String ref = hive.getRef ();
            try
            {
                final HiveFactory factory = createHiveFactory ( hive.getFactory () );

                //create the factory and the hive
                final Hive hiveInstance = factory.createHive ( ref, hive.getConfiguration () );

                // create the hive export object
                final HiveExport hiveExport = new HiveExport ( hiveInstance );

                // export the hive
                for ( final ExportType export : hive.getExportList () )
                {
                    try
                    {
                        log.debug ( String.format ( "Adding export: %s", export.getUri () ) );

                        hiveExport.addExport ( export.getUri () );
                    }
                    catch ( final ConfigurationError e )
                    {
                        log.error ( String.format ( "Unable to configure export (%s) for hive (%s)", hive.getRef (), export.getUri () ) );
                    }
                }
                this.hives.add ( hiveExport );
            }
            catch ( final Throwable e )
            {
                log.error ( String.format ( "Failed to create hive instance '%s' using factory '%s'", ref, hive.getFactory () ), e );
            }
        }
    }

    /**
     * Export all hives
     * @throws Exception
     */
    public synchronized void start () throws Exception
    {
        for ( final HiveExport hive : this.hives )
        {
            hive.start ();
            // announce hive
        }
    }

    /**
     * Stop exporting all hives
     * @throws Exception
     */
    public synchronized void stop () throws Exception
    {
        for ( final HiveExport hive : this.hives )
        {
            hive.stop ();
        }
    }
}
