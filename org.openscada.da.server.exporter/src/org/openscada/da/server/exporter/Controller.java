/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
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
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.openscada.da.core.server.Hive;
import org.openscada.da.server.exporter.util.ExporterResourceFactoryImpl;
import org.openscada.utils.init.ServiceLoaderProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Controller
{

    private final static Logger logger = LoggerFactory.getLogger ( Controller.class );

    private final List<HiveExport> hives = new LinkedList<HiveExport> ();

    private final List<String> announcers = new LinkedList<String> ();

    private final HiveFactory defaultHiveFactory;

    public Controller ( final DocumentRoot documentRoot ) throws ConfigurationException
    {
        this ( new ServiceLoaderHiveFactory (), documentRoot.getConfiguration () );
    }

    public Controller ( final String file ) throws IOException, ConfigurationException
    {
        this ( parse ( URI.createFileURI ( file ) ) );
    }

    public Controller ( final File file ) throws IOException, ConfigurationException
    {
        this ( parse ( URI.createFileURI ( file.toString () ) ) );
    }

    /**
     * @since 1.1
     */
    public Controller ( final HiveFactory defaultHiveFactory, final ConfigurationType configuration ) throws ConfigurationException
    {
        this.defaultHiveFactory = defaultHiveFactory;
        configure ( configuration );
    }

    /**
     * @since 1.1
     */
    public Controller ( final HiveFactory defaultHiveFactory, final URL url ) throws ConfigurationException
    {
        this ( defaultHiveFactory, parse ( URI.createURI ( url.toString () ) ).getConfiguration () );
    }

    private static DocumentRoot parse ( final URI uri ) throws ConfigurationException
    {
        ExporterPackage.eINSTANCE.eClass ();

        /*
         * we do need to provide the current context classloader, otherwise there
         * seem to be problems finding services when run with jsvc. 
         */
        ServiceLoaderProcessor.initialize ( "emf", Thread.currentThread ().getContextClassLoader () );

        try
        {
            final ResourceSet rs = new ResourceSetImpl ();
            rs.getResourceFactoryRegistry ().getExtensionToFactoryMap ().put ( "*", new ExporterResourceFactoryImpl () );
            final Resource resource = rs.createResource ( uri );
            resource.load ( null );

            final DocumentRoot result = (DocumentRoot)EcoreUtil.getObjectByType ( resource.getContents (), ExporterPackage.Literals.DOCUMENT_ROOT );
            if ( result == null )
            {
                throw new IllegalStateException ( "Document does not contain a configuration" );
            }
            return result;
        }
        catch ( final Exception e )
        {
            throw new ConfigurationException ( "Failed to parse document", e );
        }
    }

    private void configure ( final ConfigurationType configuration ) throws ConfigurationException
    {
        for ( final AnnouncerType announcer : configuration.getAnnouncer () )
        {
            final String klass = announcer.getClass_ ();
            this.announcers.add ( klass );
        }

        for ( final HiveType hive : configuration.getHive () )
        {
            final String ref = hive.getRef ();

            final Hive hiveInstance;
            try
            {
                // create the factory and the hive
                hiveInstance = this.defaultHiveFactory.createHive ( ref, hive.getConfiguration () );
            }
            catch ( final Exception e )
            {
                throw new ConfigurationException ( "Failed to create hive: " + hive.getRef (), e );
            }

            if ( hiveInstance == null )
            {
                throw new ConfigurationException ( "Failed to load hive: " + hive.getRef () );
            }

            // create the hive export object
            final HiveExport hiveExport = new HiveExport ( hiveInstance );

            // export the hive
            for ( final ExportType export : hive.getExport () )
            {
                try
                {
                    logger.debug ( "Adding export: {}", export.getUri () );

                    hiveExport.addExport ( export.getUri () );
                }
                catch ( final ConfigurationException e )
                {
                    logger.error ( String.format ( "Unable to configure export (%s) for hive (%s)", export.getUri (), hive.getRef () ), e );
                }
            }
            this.hives.add ( hiveExport );
        }
    }

    /**
     * Export all hives
     * 
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
     * 
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
