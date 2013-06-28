/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2013 Jürgen Rose (cptmauli@googlemail.com)
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

package org.openscada.da.server.exporter.mqtt;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.openscada.ca.ConfigurationAdministrator;
import org.openscada.ca.ConfigurationFactory;
import org.openscada.utils.concurrent.NamedThreadFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Activator implements BundleActivator
{

    private final static Logger logger = LoggerFactory.getLogger ( Activator.class );

    private static BundleContext context;

    static BundleContext getContext ()
    {
        return context;
    }

    private ExecutorService executor;

    private MqttExporterFactory mqttExporterFactory;

    private ServiceRegistration<ConfigurationFactory> mqttExporterFactoryRegistration;

    private MqttItemToTopicFactory mqttItemToTopicFactory;

    private ServiceRegistration<ConfigurationFactory> mqttItemToTopicFactoryRegistration;

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start ( final BundleContext bundleContext ) throws Exception
    {
        logger.info ( "starting MQTT exporter" );
        Activator.context = bundleContext;

        this.executor = Executors.newSingleThreadExecutor ( new NamedThreadFactory ( getClass ().getName () ) );

        {
            this.mqttExporterFactory = new MqttExporterFactory ( context, this.executor );

            final Dictionary<String, String> properties = new Hashtable<String, String> ( 3 );
            properties.put ( Constants.SERVICE_DESCRIPTION, "A factory which creates a MQTT connection" );
            properties.put ( Constants.SERVICE_VENDOR, "IBH SYSTEMS GmbH" );
            properties.put ( ConfigurationAdministrator.FACTORY_ID, context.getBundle ().getSymbolicName () );

            this.mqttExporterFactoryRegistration = context.registerService ( ConfigurationFactory.class, this.mqttExporterFactory, properties );
        }

        {
            this.mqttItemToTopicFactory = new MqttItemToTopicFactory ( context );

            final Dictionary<String, String> properties = new Hashtable<String, String> ( 3 );
            properties.put ( Constants.SERVICE_DESCRIPTION, "A factory which creates a items which are published on MQTT" );
            properties.put ( Constants.SERVICE_VENDOR, "IBH SYSTEMS GmbH" );
            properties.put ( ConfigurationAdministrator.FACTORY_ID, context.getBundle ().getSymbolicName () + ".items" );

            this.mqttItemToTopicFactoryRegistration = context.registerService ( ConfigurationFactory.class, this.mqttItemToTopicFactory, properties );
        }
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop ( final BundleContext bundleContext ) throws Exception
    {
        logger.info ( "stopping MQTT exporter" );

        if ( this.mqttItemToTopicFactoryRegistration != null )
        {
            this.mqttItemToTopicFactoryRegistration.unregister ();
        }
        if ( this.mqttItemToTopicFactory != null )
        {
            this.mqttItemToTopicFactory.dispose ();
        }

        if ( this.mqttExporterFactoryRegistration != null )
        {
            this.mqttExporterFactoryRegistration.unregister ();
        }
        if ( this.mqttExporterFactory != null )
        {
            this.mqttExporterFactory.dispose ();
        }

        this.executor = null;

        Activator.context = null;
    }
}
