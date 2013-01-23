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

package org.openscada.core.client.net;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.mina.core.session.IoSession;
import org.openscada.core.ConnectionInformation;
import org.openscada.core.client.PrivilegeListener;
import org.openscada.core.net.ConnectionHelper;
import org.openscada.core.net.MessageHelper;
import org.openscada.net.Constants;
import org.openscada.net.base.MessageListener;
import org.openscada.net.base.MessageStateListener;
import org.openscada.net.base.data.Message;
import org.openscada.utils.concurrent.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SessionConnectionBase extends ConnectionBase
{
    private final static Logger logger = LoggerFactory.getLogger ( SessionConnectionBase.class );

    public static final String SESSION_CLIENT_VERSION = "client-version"; //$NON-NLS-1$

    private final ConnectionInformation connectionInformation;

    protected ScheduledExecutorService executor;

    private final Set<PrivilegeListener> privilegeListeners = new LinkedHashSet<PrivilegeListener> ();

    private Set<String> currentPrivileges;

    public SessionConnectionBase ( final ConnectionInformation connectionInformation )
    {
        super ( connectionInformation );

        this.connectionInformation = connectionInformation;

        this.executor = Executors.newSingleThreadScheduledExecutor ( new NamedThreadFactory ( "ConnectionExecutor/" + getConnectionInformation ().toMaskedString () ) );

        this.messenger.setHandler ( MessageHelper.CC_PRIV_CHANGE, new MessageListener () {

            @Override
            public void messageReceived ( final Message message ) throws Exception
            {
                handlePrivChange ( MessageHelper.getPrivileges ( message ) );
            }
        } );
    }

    public abstract String getRequiredVersion ();

    @Override
    public void dispose ()
    {
        super.dispose ();

        this.executor.shutdown ();
    }

    @Override
    protected void onConnectionEstablished ()
    {
        requestSession ();
    }

    @Override
    protected void onConnectionClosed ()
    {
        super.onConnectionClosed ();
        handlePrivChange ( Collections.<String> emptySet () );
    }

    protected void requestSession ()
    {
        final Properties props = new Properties ();
        props.putAll ( this.connectionInformation.getProperties () );

        props.setProperty ( SESSION_CLIENT_VERSION, getRequiredVersion () );

        final String username = getConnectionInformation ().getProperties ().get ( ConnectionInformation.PROP_USER );
        final String password = getConnectionInformation ().getProperties ().get ( ConnectionInformation.PROP_PASSWORD );

        if ( username != null && password != null )
        {
            props.put ( ConnectionInformation.PROP_USER, username );
            props.put ( ConnectionInformation.PROP_PASSWORD, password );
        }
        else if ( username != null )
        {
            props.put ( ConnectionInformation.PROP_USER, username );
        }

        this.messenger.sendMessage ( MessageHelper.createSession ( props ), new MessageStateListener () {

            @Override
            public void messageReply ( final Message message )
            {
                processSessionReply ( message );
            }

            @Override
            public void messageTimedOut ()
            {
                disconnect ( new OperationTimedOutException ().fillInStackTrace () );
            }
        }, getMessageTimeout () );

    }

    protected void processSessionReply ( final Message message )
    {
        logger.debug ( "Got session reply!" ); //$NON-NLS-1$

        if ( message.getValues ().containsKey ( Message.FIELD_ERROR_INFO ) )
        {
            final String errorInfo = message.getValues ().get ( Message.FIELD_ERROR_INFO ).toString ();
            disconnect ( new DisconnectReason ( String.format ( Messages.getString ( "SessionConnectionBase.Error" ), errorInfo ) ).fillInStackTrace () ); //$NON-NLS-1$
        }
        else if ( message.getCommandCode () != Message.CC_ACK )
        {
            disconnect ( new DisconnectReason ( Messages.getString ( "SessionConnectionBase.InvalidReply" ) ).fillInStackTrace () ); //$NON-NLS-1$
        }
        else
        {
            final Properties properties = new Properties ();
            MessageHelper.getProperties ( properties, message.getValues ().get ( MessageHelper.FIELD_SESSION_PROPERTIES ) );
            logger.debug ( "Session properties: {}", properties );

            final Properties transportProperties = new Properties ();
            MessageHelper.getProperties ( transportProperties, message.getValues ().get ( MessageHelper.FIELD_TRANSPORT_PROPERTIES ) );
            logger.debug ( "Transport properties: {}", transportProperties );

            modifyFilterChain ( this.session, transportProperties );

            setBound ( properties );
        }
    }

    protected void modifyFilterChain ( final IoSession session, final Properties properties )
    {
        ConnectionHelper.injectCompression ( session, properties.getProperty ( Constants.PROP_TR_COMPRESSION ) );
    }

    protected synchronized void handlePrivChange ( final Set<String> privileges )
    {
        final Set<String> newPrivs = Collections.unmodifiableSet ( privileges );
        this.currentPrivileges = newPrivs;

        logger.info ( "Privilege change: {}", privileges );

        for ( final PrivilegeListener listener : this.privilegeListeners )
        {
            this.executor.execute ( new Runnable () {

                @Override
                public void run ()
                {
                    listener.privilegesChanged ( newPrivs );
                }
            } );
        }
    }

    @Override
    public synchronized void addPrivilegeListener ( final PrivilegeListener listener )
    {
        final Set<String> newPrivs = this.currentPrivileges;

        if ( this.privilegeListeners.add ( listener ) )
        {
            // send initial state
            this.executor.execute ( new Runnable () {

                @Override
                public void run ()
                {
                    listener.privilegesChanged ( newPrivs );
                }
            } );
        }
    }

    @Override
    public synchronized void removePrivilegeListener ( final PrivilegeListener listener )
    {
        this.privilegeListeners.remove ( listener );
    }

}
