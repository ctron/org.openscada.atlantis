/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2008-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.ca.jdbc.internal;

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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openscada.ca.common.AbstractConfigurationAdministrator;
import org.openscada.ca.common.ConfigurationImpl;
import org.openscada.sec.UserInformation;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurationAdministratorImpl extends AbstractConfigurationAdministrator
{
    private final static Logger logger = LoggerFactory.getLogger ( ConfigurationAdministratorImpl.class );

    private static final int INITIAL_CFG_SIZE = Integer.getInteger ( "org.openscada.ca.jdbc.initialConfigurationHashSize", 4 );

    private final boolean disableIntern = Boolean.getBoolean ( "org.openscada.ca.jdbc.disableIntern" );

    private final JdbcStorageDAO jdbcStorageDAO;

    public ConfigurationAdministratorImpl ( final BundleContext context, final JdbcStorageDAO jdbcStorageDAO )
    {
        super ( context );
        this.jdbcStorageDAO = jdbcStorageDAO;
    }

    @Override
    public synchronized void start () throws Exception
    {
        super.start ();
        initialLoad ();
    }

    protected synchronized void initialLoad () throws SQLException
    {
        logger.debug ( "Starting initial load" );

        final List<String> factoryIds = this.jdbcStorageDAO.listFactories ();
        // map
        final Map<String, Map<String, Map<String, String>>> factories = new HashMap<String, Map<String, Map<String, String>>> ( factoryIds.size () );

        for ( final String factoryId : factoryIds )
        {
            // load
            final List<Entry> result = this.jdbcStorageDAO.loadFactory ( factoryId );

            logger.debug ( "Loaded {} initial entries for factory {}", result.size (), factoryId );

            for ( final Entry entry : result )
            {
                if ( ( entry.getFactoryId () == null ) || ( entry.getConfigurationId () == null ) || ( entry.getKey () == null ) )
                {
                    continue;
                }

                Map<String, Map<String, String>> factory = factories.get ( entry.getFactoryId () );
                if ( factory == null )
                {
                    factory = new HashMap<String, Map<String, String>> ();
                    factories.put ( intern ( entry.getFactoryId () ), factory );
                }
                Map<String, String> cfg = factory.get ( entry.getConfigurationId () );
                if ( cfg == null )
                {
                    // no need to intern configuration ids as they are unique
                    cfg = new HashMap<String, String> ( INITIAL_CFG_SIZE );
                    factory.put ( entry.getConfigurationId (), cfg );
                }

                final String value = intern ( entry.getValue () );
                final String key = intern ( entry.getKey () );

                cfg.put ( key, value );
            }
        }

        // announce
        for ( final Map.Entry<String, Map<String, Map<String, String>>> factory : factories.entrySet () )
        {
            final Collection<ConfigurationImpl> configurations = new ArrayList<ConfigurationImpl> ( factory.getValue ().size () );

            for ( final Map.Entry<String, Map<String, String>> configurationEntry : factory.getValue ().entrySet () )
            {
                final ConfigurationImpl cfg = new ConfigurationImpl ( configurationEntry.getKey (), factory.getKey (), configurationEntry.getValue () );
                configurations.add ( cfg );
            }

            addStoredFactory ( factory.getKey (), configurations.toArray ( new ConfigurationImpl[configurations.size ()] ) );
        }
    }

    private String intern ( final String string )
    {
        if ( this.disableIntern )
        {
            return string;
        }
        if ( string == null )
        {
            return null;
        }
        return string.intern ();
    }

    @Override
    protected synchronized void performDeleteConfiguration ( final UserInformation userInformation, final String factoryId, final String configurationId, final ConfigurationFuture future ) throws Exception
    {
        this.jdbcStorageDAO.deleteConfiguration ( factoryId, configurationId );

        changeConfiguration ( userInformation, factoryId, configurationId, null, future );
    }

    @Override
    protected synchronized void performPurge ( final UserInformation userInformation, final String factoryId, final PurgeFuture future )
    {
        logger.info ( "Purging: {}", factoryId );

        final Set<String> done = new HashSet<String> ();
        for ( final Entry entry : this.jdbcStorageDAO.purgeFactory ( factoryId ) )
        {
            if ( done.add ( entry.getConfigurationId () ) )
            {
                final ConfigurationFuture subFuture = new ConfigurationFuture ();
                changeConfiguration ( userInformation, factoryId, entry.getConfigurationId (), null, subFuture );

                future.addChild ( subFuture );
            }
        }
        logger.info ( "Purging: {} complete", factoryId );
        future.setComplete ();
    }

    @Override
    protected synchronized void performStoreConfiguration ( final UserInformation userInformation, final String factoryId, final String configurationId, final Map<String, String> properties, final boolean fullSet, final ConfigurationFuture future ) throws Exception
    {
        final Map<String, String> resultProperties = this.jdbcStorageDAO.storeConfiguration ( factoryId, configurationId, properties, fullSet );

        changeConfiguration ( userInformation, factoryId, configurationId, resultProperties, future );
    }

}
