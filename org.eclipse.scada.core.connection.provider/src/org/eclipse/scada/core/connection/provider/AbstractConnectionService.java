/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.eclipse.scada.core.connection.provider;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.scada.core.client.AutoReconnectController;
import org.eclipse.scada.core.client.Connection;
import org.eclipse.scada.core.connection.provider.info.ConnectionInformationProvider;
import org.eclipse.scada.core.info.StatisticEntry;
import org.eclipse.scada.core.info.StatisticsImpl;
import org.eclipse.scada.core.info.StatisticsProvider;
import org.eclipse.scada.sec.callback.CallbackHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractConnectionService implements org.eclipse.scada.core.connection.provider.ConnectionService, ConnectionInformationProvider
{
    private static final Logger logger = LoggerFactory.getLogger ( AbstractConnectionService.class );

    private Connection connection;

    private AutoReconnectController controller;

    protected final StatisticsImpl statistics = new StatisticsImpl ();

    private final boolean lazyActivation;

    private boolean connectionRequested = false;

    private final Integer autoReconnectDelay;

    private CallbackHandler connectCallbackHandler;

    public AbstractConnectionService ( final Integer autoReconnectDelay, final boolean lazyActivation )
    {
        this.lazyActivation = lazyActivation;
        this.autoReconnectDelay = autoReconnectDelay;
    }

    @Override
    public void setConnectCallbackHandler ( final CallbackHandler callbackHandler )
    {
        this.connectCallbackHandler = callbackHandler;
    }

    protected void setConnection ( final Connection connection )
    {
        this.connection = connection;
        if ( this.autoReconnectDelay != null )
        {
            this.controller = new AutoReconnectController ( connection, this.autoReconnectDelay );
        }
        else
        {
            this.controller = null;
        }
    }

    @Override
    public void dispose ()
    {
        logger.info ( "Disposing: {}", this.connection );
        disconnect ();

        if ( this.controller != null )
        {
            this.controller.dispose ();
        }
        if ( this.connection != null )
        {
            this.connection.dispose ();
        }
    }

    @Override
    public AutoReconnectController getAutoReconnectController ()
    {
        return this.controller;
    }

    protected boolean shouldConnect ()
    {
        return true;
    }

    protected synchronized void checkConnect ()
    {
        if ( this.connectionRequested && ( !this.lazyActivation || shouldConnect () ) )
        {
            performConnect ();
        }
        else
        {
            performDisconnect ();
        }
    }

    @Override
    public synchronized void connect ()
    {
        this.connectionRequested = true;
        checkConnect ();
    }

    protected void performConnect ()
    {
        if ( this.controller != null )
        {
            this.controller.connect ( this.connectCallbackHandler );
        }
        else
        {
            this.connection.connect ( this.connectCallbackHandler );
        }
    }

    @Override
    public synchronized void disconnect ()
    {
        this.connectionRequested = false;
        checkConnect ();
    }

    protected void performDisconnect ()
    {
        if ( this.controller != null )
        {
            this.controller.disconnect ();
        }
        else
        {
            this.connection.disconnect ();
        }
    }

    @Override
    public String getLabel ()
    {
        return this.connection.getConnectionInformation ().toMaskedString ();
    }

    @Override
    public Collection<StatisticEntry> getStatistics ()
    {
        final Collection<StatisticEntry> result = new ArrayList<StatisticEntry> ();
        result.addAll ( this.statistics.getEntries () );
        if ( this.connection instanceof StatisticsProvider )
        {
            result.addAll ( ( (StatisticsProvider)this.connection ).getStatistics () );
        }
        if ( this.controller instanceof StatisticsProvider )
        {
            result.addAll ( ( (StatisticsProvider)this.controller ).getStatistics () );
        }
        return result;
    }

}