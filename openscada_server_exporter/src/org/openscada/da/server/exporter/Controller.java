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

        for (final AnnouncerType announcer : configuration.getAnnouncerList () ) {
            final String klass = announcer.getClass1 ();
            announcers.add ( klass );
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
                hives.add ( hiveExport );
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
        for ( final HiveExport hive : hives )
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
        for ( final HiveExport hive : hives )
        {
            hive.stop ();
        }
    }
}
