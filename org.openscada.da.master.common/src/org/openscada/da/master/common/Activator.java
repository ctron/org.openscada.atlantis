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

package org.openscada.da.master.common;

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
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator
{

    private EventProcessor eventProcessor;

    private CommonSumHandlerFactoryImpl factory1;

    private CommonSumHandlerFactoryImpl factory2;

    private CommonSumHandlerFactoryImpl factory3;

    private CommonSumHandlerFactoryImpl factory4;

    private ObjectPoolTracker poolTracker;

    private ServiceTracker caTracker;

    private ScaleHandlerFactoryImpl factory5;

    private NegateHandlerFactoryImpl factory6;

    private ManualHandlerFactoryImpl factory7;

    private BlockHandlerFactoryImpl factory8;

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start ( final BundleContext context ) throws Exception
    {
        this.eventProcessor = new EventProcessor ( context );
        this.eventProcessor.open ();

        this.poolTracker = new ObjectPoolTracker ( context, MasterItem.class.getName () );
        this.poolTracker.open ();

        this.caTracker = new ServiceTracker ( context, ConfigurationAdministrator.class.getName (), null );
        this.caTracker.open ();

        this.factory2 = makeFactory ( context, this.poolTracker, "alarm", 5020 );
        this.factory3 = makeFactory ( context, this.poolTracker, "manual", 5010 );
        this.factory1 = makeFactory ( context, this.poolTracker, "error", 5000 );
        this.factory4 = makeFactory ( context, this.poolTracker, "ackRequired", 5030 );

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

    private static CommonSumHandlerFactoryImpl makeFactory ( final BundleContext context, final ObjectPoolTracker poolTracker, final String tag, final int priority ) throws InvalidSyntaxException
    {
        final CommonSumHandlerFactoryImpl factory = new CommonSumHandlerFactoryImpl ( context, poolTracker, tag, priority );
        final Dictionary<String, String> properties = new Hashtable<String, String> ();
        properties.put ( Constants.SERVICE_DESCRIPTION, String.format ( "A sum %s handler", tag ) );
        properties.put ( ConfigurationAdministrator.FACTORY_ID, "da.master.handler.sum." + tag );
        context.registerService ( ConfigurationFactory.class.getName (), factory, properties );
        return factory;
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop ( final BundleContext context ) throws Exception
    {

        this.factory1.dispose ();
        this.factory2.dispose ();
        this.factory3.dispose ();
        this.factory4.dispose ();
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
