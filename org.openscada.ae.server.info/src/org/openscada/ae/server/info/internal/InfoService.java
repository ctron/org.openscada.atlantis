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

package org.openscada.ae.server.info.internal;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

import org.openscada.ae.MonitorStatus;
import org.openscada.ae.MonitorStatusInformation;
import org.openscada.ae.monitor.MonitorListener;
import org.openscada.ae.monitor.MonitorService;
import org.openscada.core.Variant;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.client.DataItemValue.Builder;
import org.openscada.da.datasource.base.DataInputOutputSource;
import org.openscada.da.datasource.base.DataInputSource;
import org.openscada.da.datasource.base.WriteHandler;
import org.openscada.sec.UserInformation;
import org.openscada.utils.osgi.pool.ObjectPool;
import org.openscada.utils.osgi.pool.ObjectPoolImpl;
import org.openscada.utils.osgi.pool.ObjectPoolListener;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.openscada.utils.osgi.pool.ObjectPoolTracker.ObjectPoolServiceListener;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InfoService
{
    private final static String ALERT_ACTIVE_ITEM = "ALERT_ACTIVE";

    private final static String ALERT_DISABLED_ITEM = "ALERT_DISABLED";

    private final static Logger logger = LoggerFactory.getLogger ( InfoService.class );

    private final Executor executor;

    private final ObjectPoolImpl dataSourcePool;

    private final Map<String, DataInputSource> items = new HashMap<String, DataInputSource> ();

    private final DataInputOutputSource alertActiveItem;

    private final DataInputOutputSource alertDisabledItem;

    private String prefix;

    private final Map<String, MonitorStatusInformation> cachedMonitors = new HashMap<String, MonitorStatusInformation> ();

    private final Map<MonitorStatus, AtomicInteger> aggregatedMonitors = new HashMap<MonitorStatus, AtomicInteger> ();

    private volatile boolean alertDisabled = false;

    ///////////////////////////////////////////////////////////////////////
    // constructor
    ///////////////////////////////////////////////////////////////////////

    /**
     * @param context
     * @param executor
     * @param monitorPoolTracker
     * @param dataSourcePool
     */
    public InfoService ( final BundleContext context, final Executor executor, final ObjectPoolTracker monitorPoolTracker, final ObjectPoolImpl dataSourcePool )
    {
        // set final fields
        this.executor = executor;
        this.dataSourcePool = dataSourcePool;
        clearAggregatedMonitors ();

        // create items
        for ( MonitorStatus monitorStatus : MonitorStatus.values () )
        {
            items.put ( monitorStatus.name (), new DataInputSource ( executor ) );
        }
        alertActiveItem = new DataInputOutputSource ( executor );
        alertActiveItem.setWriteHandler ( new WriteHandler () {
            public void handleWrite ( UserInformation userInformation, Variant value ) throws Exception
            {
                setValue ( alertActiveItem, value.asBoolean ( false ) );
            }
        } );
        alertDisabledItem = new DataInputOutputSource ( executor );
        alertDisabledItem.setWriteHandler ( new WriteHandler () {
            public void handleWrite ( UserInformation userInformation, Variant value ) throws Exception
            {
                setAlertDisabled ( new Variant ( value ).asBoolean ( false ) );
            }
        } );

        // listener for single monitor
        final MonitorListener ml = new MonitorListener () {
            public void statusChanged ( final MonitorStatusInformation status )
            {
                executor.execute ( new Runnable () {
                    public void run ()
                    {
                        handleStatusChanged ( status );
                    }
                } );
            }
        };

        // listener for all monitors of a pool
        final ObjectPoolListener objectPoolListener = new ObjectPoolListener () {
            public void serviceAdded ( Object service, Dictionary<?, ?> properties )
            {
                ( (MonitorService)service ).addStatusListener ( ml );
            }

            public void serviceRemoved ( Object service, Dictionary<?, ?> properties )
            {
                ( (MonitorService)service ).removeStatusListener ( ml );
            }

            public void serviceModified ( Object service, Dictionary<?, ?> properties )
            {
                ( (MonitorService)service ).removeStatusListener ( ml );
                ( (MonitorService)service ).addStatusListener ( ml );
            }
        };

        // listener for all monitor pools
        monitorPoolTracker.addListener ( new ObjectPoolServiceListener () {
            public void poolAdded ( ObjectPool objectPool, int priority )
            {
                objectPool.addListener ( objectPoolListener );
            }

            public void poolRemoved ( ObjectPool objectPool )
            {
                objectPool.removeListener ( objectPoolListener );
            }

            public void poolModified ( ObjectPool objectPool, int newPriority )
            {
                objectPool.removeListener ( objectPoolListener );
                objectPool.addListener ( objectPoolListener );
            }
        } );
        logger.debug ( "InfoService created" );

        // set default values
        notifyChanges ();
        setValue ( alertActiveItem, false );
        setValue ( alertDisabledItem, alertDisabled );
    }

    ///////////////////////////////////////////////////////////////////////
    // public api
    ///////////////////////////////////////////////////////////////////////

    /**
     * @param parameters
     */
    public void update ( final Map<String, String> parameters )
    {
        executor.execute ( new Runnable () {
            public void run ()
            {
                handleUpdate ( parameters );
            }

        } );
    }

    /**
     * 
     */
    public void dispose ()
    {
        executor.execute ( new Runnable () {
            public void run ()
            {
                unregisterItems ( prefix );
            }
        } );
    }

    ///////////////////////////////////////////////////////////////////////
    // private api
    ///////////////////////////////////////////////////////////////////////

    /**
     * @param parameters
     */
    private void handleUpdate ( Map<String, String> parameters )
    {
        logger.debug ( "parameter change requested" );
        unregisterItems ( this.prefix );
        this.prefix = defaultParameter ( parameters, "prefix", InfoServiceFactory.FACTORY_ID );
        registerItems ( this.prefix );
        logger.info ( "parameters changed" );
    }

    /**
     * @param prefix
     */
    private void registerItems ( final String prefix )
    {
        for ( Entry<String, DataInputSource> entry : items.entrySet () )
        {
            final String id = prefix + "." + entry.getKey ();
            logger.info ( "register item with id {}", id );
            this.dataSourcePool.addService ( id, entry.getValue (), new Properties () );
        }
        this.dataSourcePool.addService ( prefix + "." + ALERT_ACTIVE_ITEM, alertActiveItem, new Properties () );
        this.dataSourcePool.addService ( prefix + "." + ALERT_DISABLED_ITEM, alertDisabledItem, new Properties () );
    }

    /**
     * @param prefix
     */
    private void unregisterItems ( final String prefix )
    {
        for ( Entry<String, DataInputSource> entry : items.entrySet () )
        {
            final String id = prefix + "." + entry.getKey ();
            this.dataSourcePool.removeService ( id, entry.getValue () );
            logger.info ( "unregister item with id {}", id );
        }
        this.dataSourcePool.removeService ( prefix + "." + ALERT_ACTIVE_ITEM, alertActiveItem );
        this.dataSourcePool.removeService ( prefix + "." + ALERT_DISABLED_ITEM, alertDisabledItem );
    }

    /**
     * @param msi
     */
    private void handleStatusChanged ( MonitorStatusInformation msi )
    {
        if ( msi == null )
        {
            throw new IllegalArgumentException ( "'monitorInformation' must not be null" );
        }
        MonitorStatusInformation oldMsi = cachedMonitors.put ( msi.getId (), msi );
        if ( oldMsi != null )
        {
            aggregatedMonitors.get ( oldMsi.getStatus () ).decrementAndGet ();
            if ( oldMsi.getStatus () == MonitorStatus.NOT_OK_NOT_AKN || oldMsi.getStatus () == MonitorStatus.NOT_AKN )
            {
                silenceAlert ();
            }
        }
        aggregatedMonitors.get ( msi.getStatus () ).incrementAndGet ();
        if ( msi.getStatus () == MonitorStatus.NOT_OK_NOT_AKN || msi.getStatus () == MonitorStatus.NOT_AKN )
        {
            alert ();
        }
        notifyChanges ();
    }

    /**
     * 
     */
    private void alert ()
    {
        logger.info ( "alert enabled" );
        if ( !alertDisabled )
        {
            setValue ( alertActiveItem, true );
        }
    }

    /**
     * 
     */
    private void silenceAlert ()
    {
        logger.info ( "alert disabled" );
        setValue ( alertActiveItem, false );
    }

    /**
     * @param disabled
     */
    private void setAlertDisabled ( boolean disabled )
    {
        alertDisabled = disabled;
        setValue ( alertDisabledItem, alertDisabled );
        if ( disabled )
        {
            silenceAlert ();
        }
    }

    /**
     * reset summation of monitors
     */
    private void clearAggregatedMonitors ()
    {
        aggregatedMonitors.clear ();
        for ( MonitorStatus status : MonitorStatus.values () )
        {
            aggregatedMonitors.put ( status, new AtomicInteger ( 0 ) );
        }
    }

    /**
     * send all changes to client
     */
    private void notifyChanges ()
    {
        for ( MonitorStatus monitorStatus : MonitorStatus.values () )
        {
            setValue ( items.get ( monitorStatus.name () ), aggregatedMonitors.get ( monitorStatus ) );
        }
    }

    ///////////////////////////////////////////////////////////////////////
    // helper methods
    ///////////////////////////////////////////////////////////////////////

    /**
     * @param item
     * @param value
     */
    private void setValue ( DataInputSource item, Object value )
    {
        Builder div = new DataItemValue.Builder ();
        div.setValue ( new Variant ( value ) );
        item.setValue ( div.build () );
    }

    /**
     * @param parameters
     * @param key
     * @param defaultValue
     * @return
     */
    private String defaultParameter ( Map<String, String> parameters, String key, String defaultValue )
    {
        String value = parameters.get ( key );
        if ( value == null || "".equals ( value.trim () ) )
        {
            return defaultValue;
        }
        return value;
    }
}
