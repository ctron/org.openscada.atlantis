/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.master.common.internal;

import java.util.Dictionary;
import java.util.Hashtable;

import org.openscada.ae.event.EventProcessor;
import org.openscada.ca.ConfigurationAdministrator;
import org.openscada.ca.ConfigurationFactory;
import org.openscada.da.master.MasterItem;
import org.openscada.da.master.common.block.BlockHandlerFactoryImpl;
import org.openscada.da.master.common.manual.ManualHandlerFactoryImpl;
import org.openscada.da.master.common.negate.NegateHandlerFactoryImpl;
import org.openscada.da.master.common.scale.ScaleHandlerFactoryImpl;
import org.openscada.da.master.common.sum.CommonSumHandlerFactoryImpl;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator
{

    private EventProcessor eventProcessor;

    private CommonSumHandlerFactoryImpl factory1;

    private ObjectPoolTracker<MasterItem> poolTracker;

    private ServiceTracker<ConfigurationAdministrator, ConfigurationAdministrator> caTracker;

    private ScaleHandlerFactoryImpl factory5;

    private NegateHandlerFactoryImpl factory6;

    private ManualHandlerFactoryImpl factory7;

    private BlockHandlerFactoryImpl factory8;

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start ( final BundleContext context ) throws Exception
    {
        this.eventProcessor = new EventProcessor ( context );
        this.eventProcessor.open ();

        this.poolTracker = new ObjectPoolTracker<MasterItem> ( context, MasterItem.class );
        this.poolTracker.open ();

        this.caTracker = new ServiceTracker<ConfigurationAdministrator, ConfigurationAdministrator> ( context, ConfigurationAdministrator.class, null );
        this.caTracker.open ();

        {
            this.factory1 = new CommonSumHandlerFactoryImpl ( context, this.poolTracker );
            final Dictionary<String, String> properties1 = new Hashtable<String, String> ();
            properties1.put ( Constants.SERVICE_DESCRIPTION, "An attribute sum handler" );
            properties1.put ( ConfigurationAdministrator.FACTORY_ID, "da.master.handler.sum" );
            context.registerService ( ConfigurationFactory.class.getName (), this.factory1, properties1 );
        }
        {
            this.factory5 = new ScaleHandlerFactoryImpl ( context, this.poolTracker, this.caTracker, 500 );
            final Dictionary<String, String> properties = new Hashtable<String, String> ();
            properties.put ( Constants.SERVICE_DESCRIPTION, "A local scaling master handler" );
            properties.put ( ConfigurationAdministrator.FACTORY_ID, ScaleHandlerFactoryImpl.FACTORY_ID );
            context.registerService ( ConfigurationFactory.class.getName (), this.factory5, properties );
        }

        {
            this.factory6 = new NegateHandlerFactoryImpl ( context, this.poolTracker, this.caTracker, 501 );
            final Dictionary<String, String> properties = new Hashtable<String, String> ();
            properties.put ( Constants.SERVICE_DESCRIPTION, "A local negate master handler" );
            properties.put ( ConfigurationAdministrator.FACTORY_ID, NegateHandlerFactoryImpl.FACTORY_ID );
            context.registerService ( ConfigurationFactory.class.getName (), this.factory6, properties );
        }

        {
            this.factory7 = new ManualHandlerFactoryImpl ( context, this.eventProcessor, this.poolTracker, this.caTracker, 1000 );
            final Dictionary<String, String> properties = new Hashtable<String, String> ();
            properties.put ( Constants.SERVICE_DESCRIPTION, "A local manual override master handler" );
            properties.put ( ConfigurationAdministrator.FACTORY_ID, ManualHandlerFactoryImpl.FACTORY_ID );
            context.registerService ( ConfigurationFactory.class.getName (), this.factory7, properties );
        }

        {
            this.factory8 = new BlockHandlerFactoryImpl ( context, this.eventProcessor, this.poolTracker, this.caTracker, Integer.MIN_VALUE );
            final Dictionary<String, String> properties = new Hashtable<String, String> ();
            properties.put ( Constants.SERVICE_DESCRIPTION, "A blocking operation handler" );
            properties.put ( ConfigurationAdministrator.FACTORY_ID, BlockHandlerFactoryImpl.FACTORY_ID );
            context.registerService ( ConfigurationFactory.class.getName (), this.factory8, properties );
        }
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop ( final BundleContext context ) throws Exception
    {
        this.factory1.dispose ();
        this.factory5.dispose ();
        this.factory6.dispose ();
        this.factory7.dispose ();
        this.factory8.dispose ();

        this.poolTracker.close ();
        this.poolTracker = null;

        this.caTracker.close ();
        this.caTracker = null;

        this.eventProcessor.close ();
        this.eventProcessor = null;
    }

}
