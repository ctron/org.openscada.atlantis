/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
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

package org.openscada.hd.server.storage;

import java.util.Hashtable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.openscada.ca.ConfigurationAdministrator;
import org.openscada.ca.SelfManagedConfigurationFactory;
import org.openscada.utils.concurrent.NamedThreadFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OSGi activator for package org.openscada.hd.server.storage.
 * @author Ludwig Straub
 */
public class Activator implements BundleActivator
{
    /** Default logger. */
    private final static Logger logger = LoggerFactory.getLogger ( Activator.class );

    /** Lock object for start and stop. */
    private final static Object lockObject = new Object ();

    /** Storage service instance. */
    private static StorageService service = null;

    /** Service registration of storage service. */
    private static ServiceRegistration serviceRegistration = null;

    /** Executor service used for start and stop. */
    private static ExecutorService executor = null;

    /**
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start ( final BundleContext context ) throws Exception
    {
        final Object lock = lockObject;
        executor = Executors.newSingleThreadExecutor ( new NamedThreadFactory ( context.getBundle ().getSymbolicName () ) );
        executor.submit ( new Runnable () {
            public void run ()
            {
                synchronized ( lock )
                {
                    final Object bundleName = context.getBundle ().getHeaders ().get ( Constants.BUNDLE_NAME );
                    final Hashtable<String, String> gescheiteHT = new Hashtable<String, String> ();
                    gescheiteHT.put ( Constants.SERVICE_DESCRIPTION, StorageService.SERVICE_DESCRIPTION );
                    gescheiteHT.put ( Constants.SERVICE_VENDOR, "inavare GmbH" );
                    gescheiteHT.put ( ConfigurationAdministrator.FACTORY_ID, StorageService.FACTORY_ID );
                    logger.info ( bundleName + " starting..." );
                    service = new StorageService ( context );
                    service.start ();
                    logger.info ( bundleName + " service started" );
                    serviceRegistration = context.registerService ( new String[] { StorageService.class.getName (), SelfManagedConfigurationFactory.class.getName () }, service, gescheiteHT );
                    logger.info ( bundleName + " service registered" );
                }
            }
        } );
    }

    /**
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop ( final BundleContext context ) throws Exception
    {
        if ( lockObject != null && executor != null )
        {
            final Object lock = lockObject;
            synchronized ( lock )
            {
                final Object bundleName = context.getBundle ().getHeaders ().get ( Constants.BUNDLE_NAME );
                logger.info ( bundleName + " stopping..." );
                if ( serviceRegistration != null )
                {
                    serviceRegistration.unregister ();
                    serviceRegistration = null;
                    logger.info ( bundleName + "service unregistered" );
                }
                if ( service != null )
                {
                    service.stop ();
                    service = null;
                    logger.info ( bundleName + " service stopped" );
                }
                if ( executor != null )
                {
                    executor = null;
                }
            }
        }
    }
}
