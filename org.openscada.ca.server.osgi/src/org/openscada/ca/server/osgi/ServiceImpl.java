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

package org.openscada.ca.server.osgi;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.openscada.ca.Configuration;
import org.openscada.ca.ConfigurationAdministrator;
import org.openscada.ca.Factory;
import org.openscada.ca.data.DiffEntry;
import org.openscada.ca.server.FactoryWithData;
import org.openscada.ca.server.Service;
import org.openscada.ca.server.Session;
import org.openscada.core.InvalidSessionException;
import org.openscada.sec.UserInformation;
import org.openscada.utils.concurrent.ExportedExecutorService;
import org.openscada.utils.concurrent.FutureTask;
import org.openscada.utils.concurrent.NotifyFuture;
import org.osgi.framework.BundleContext;

public class ServiceImpl extends AbstractServiceImpl<Session> implements Service
{
    private final ConfigurationAdministrator service;

    private final ExportedExecutorService executor;

    public ServiceImpl ( final ConfigurationAdministrator service, final BundleContext context ) throws Exception
    {
        super ( context );
        this.service = service;
        this.executor = new ExportedExecutorService ( "org.openscada.ca.server.osgi.ServiceImpl", 1, 1, 1, TimeUnit.MINUTES );
    }

    @Override
    public void stop () throws Exception
    {
        super.stop ();
        this.executor.shutdown ();
    }

    @Override
    protected SessionImpl createSessionInstance ( final UserInformation userInformation, final Map<String, String> properties )
    {
        return new SessionImpl ( userInformation, properties );
    }

    @Override
    public synchronized NotifyFuture<Void> applyDiff ( final Session session, final Collection<DiffEntry> changeSet ) throws InvalidSessionException
    {
        final SessionImpl sessionImpl = getSessionImpl ( session, SessionImpl.class );

        return this.service.applyDiff ( sessionImpl.getUserInformation (), changeSet );
    }

    @Override
    public synchronized NotifyFuture<FactoryWithData> getFactory ( final Session session, final String factoryId ) throws InvalidSessionException
    {
        getSessionImpl ( session, SessionImpl.class );

        final FutureTask<FactoryWithData> task = new FutureTask<FactoryWithData> ( new Callable<FactoryWithData> () {

            @Override
            public FactoryWithData call () throws Exception
            {
                final Factory factory = ServiceImpl.this.service.getFactory ( factoryId );
                final Configuration[] configurations = ServiceImpl.this.service.getConfigurations ( factoryId );

                return new FactoryWithData ( factory, configurations );
            }
        } );
        this.executor.submit ( task );
        return task;
    }

    @Override
    public synchronized NotifyFuture<Factory[]> getKnownFactories ( final Session session ) throws InvalidSessionException
    {
        getSessionImpl ( session, SessionImpl.class );

        final FutureTask<Factory[]> task = new FutureTask<Factory[]> ( new Callable<Factory[]> () {

            @Override
            public Factory[] call () throws Exception
            {
                return ServiceImpl.this.service.getKnownFactories ();
            }
        } );
        this.executor.submit ( task );
        return task;
    }

    @Override
    public NotifyFuture<Configuration[]> getConfigurations ( final Session session, final String factoryId ) throws InvalidSessionException
    {
        getSessionImpl ( session, SessionImpl.class );

        final FutureTask<Configuration[]> task = new FutureTask<Configuration[]> ( new Callable<Configuration[]> () {

            @Override
            public Configuration[] call () throws Exception
            {
                return ServiceImpl.this.service.getConfigurations ( factoryId );
            }
        } );
        this.executor.submit ( task );
        return task;
    }

    @Override
    public synchronized NotifyFuture<Configuration> getConfiguration ( final Session session, final String factoryId, final String configurationId ) throws InvalidSessionException
    {
        getSessionImpl ( session, SessionImpl.class );

        final FutureTask<Configuration> task = new FutureTask<Configuration> ( new Callable<Configuration> () {

            @Override
            public Configuration call () throws Exception
            {
                return ServiceImpl.this.service.getConfiguration ( factoryId, configurationId );
            }
        } );
        this.executor.submit ( task );
        return task;

    }

}
