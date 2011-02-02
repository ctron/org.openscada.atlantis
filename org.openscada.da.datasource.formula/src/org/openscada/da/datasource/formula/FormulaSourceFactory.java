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

package org.openscada.da.datasource.formula;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import org.openscada.da.datasource.DataSource;
import org.openscada.utils.osgi.ca.factory.AbstractServiceConfigurationFactory;
import org.openscada.utils.osgi.pool.ObjectPoolHelper;
import org.openscada.utils.osgi.pool.ObjectPoolImpl;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FormulaSourceFactory extends AbstractServiceConfigurationFactory<ForumulaDataSource>
{

    private final static Logger logger = LoggerFactory.getLogger ( FormulaSourceFactory.class );

    private final ScheduledExecutorService executor;

    private final ObjectPoolTracker poolTracker;

    private final ObjectPoolImpl objectPool;

    private final ServiceRegistration poolRegistration;

    public FormulaSourceFactory ( final BundleContext context, final ScheduledExecutorService executor ) throws InvalidSyntaxException
    {
        super ( context );
        this.executor = executor;

        this.objectPool = new ObjectPoolImpl ();
        this.poolRegistration = ObjectPoolHelper.registerObjectPool ( context, this.objectPool, DataSource.class.getName () );

        this.poolTracker = new ObjectPoolTracker ( context, DataSource.class.getName () );
        this.poolTracker.open ();
    }

    @Override
    public synchronized void dispose ()
    {
        this.poolRegistration.unregister ();
        this.objectPool.dispose ();
        this.poolTracker.close ();
        super.dispose ();
    }

    @Override
    protected Entry<ForumulaDataSource> createService ( final String configurationId, final BundleContext context, final Map<String, String> parameters ) throws Exception
    {
        final ForumulaDataSource source = new ForumulaDataSource ( context, this.poolTracker, this.executor );
        source.update ( parameters );

        final Dictionary<String, String> properties = new Hashtable<String, String> ();
        properties.put ( DataSource.DATA_SOURCE_ID, configurationId );

        this.objectPool.addService ( configurationId, source, properties );

        return new Entry<ForumulaDataSource> ( configurationId, source );
    }

    @Override
    protected void disposeService ( final String id, final ForumulaDataSource service )
    {
        logger.info ( "Disposing: {}", id );

        this.objectPool.removeService ( id, service );

        service.dispose ();
    }

    @Override
    protected Entry<ForumulaDataSource> updateService ( final String configurationId, final Entry<ForumulaDataSource> entry, final Map<String, String> parameters ) throws Exception
    {
        entry.getService ().update ( parameters );
        return null;
    }

}
