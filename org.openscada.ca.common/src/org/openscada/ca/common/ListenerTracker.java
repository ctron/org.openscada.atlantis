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

package org.openscada.ca.common;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.openscada.ca.ConfigurationAdministratorListener;
import org.openscada.ca.ConfigurationEvent;
import org.openscada.ca.FactoryEvent;
import org.openscada.utils.concurrent.NamedThreadFactory;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListenerTracker
{

    private final static Logger logger = LoggerFactory.getLogger ( ListenerTracker.class );

    private final ServiceTracker<ConfigurationAdministratorListener, ConfigurationAdministratorListener> listenerTracker;

    private final ExecutorService executor;

    public ListenerTracker ( final BundleContext context )
    {
        this.executor = Executors.newSingleThreadExecutor ( new NamedThreadFactory ( "org.openscada.ca.common.ListenerTracker" ) );
        this.listenerTracker = new ServiceTracker<ConfigurationAdministratorListener, ConfigurationAdministratorListener> ( context, ConfigurationAdministratorListener.class, null );
    }

    public void open ()
    {
        this.listenerTracker.open ();
    }

    public void close ()
    {
        this.listenerTracker.close ();
    }

    public void dispose ()
    {
        this.executor.shutdown ();
    }

    public void fireEvent ( final ConfigurationEvent configurationEvent )
    {
        logger.debug ( "Fire configuration event: {}", configurationEvent );

        final Object[] services = this.listenerTracker.getServices ();

        if ( services != null && services.length > 0 )
        {
            this.executor.execute ( new Runnable () {

                @Override
                public void run ()
                {
                    if ( services != null )
                    {
                        for ( final Object o : services )
                        {
                            if ( o instanceof ConfigurationAdministratorListener )
                            {
                                final ConfigurationAdministratorListener listener = (ConfigurationAdministratorListener)o;
                                try
                                {
                                    listener.configurationEvent ( configurationEvent );
                                }
                                catch ( final Throwable e )
                                {
                                    logger.warn ( "Failed to handle listener", e );
                                }
                            }
                        }
                    }
                }
            } );
        }
    }

    public void fireEvent ( final FactoryEvent factoryEvent )
    {
        logger.debug ( "Fire factory event: {}", factoryEvent );

        final Object[] services = this.listenerTracker.getServices ();
        if ( services != null && services.length > 0 )
        {
            this.executor.execute ( new Runnable () {

                @Override
                public void run ()
                {
                    if ( services != null )
                    {
                        for ( final Object o : services )
                        {
                            if ( o instanceof ConfigurationAdministratorListener )
                            {
                                final ConfigurationAdministratorListener listener = (ConfigurationAdministratorListener)o;
                                try
                                {
                                    listener.factoryEvent ( factoryEvent );
                                }
                                catch ( final Throwable e )
                                {
                                    logger.warn ( "Failed to handle listener", e );
                                }
                            }
                        }
                    }
                }
            } );
        }
    }
}
