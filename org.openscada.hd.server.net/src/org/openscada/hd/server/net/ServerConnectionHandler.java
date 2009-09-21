/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
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

package org.openscada.hd.server.net;

import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.mina.core.session.IoSession;
import org.openscada.core.ConnectionInformation;
import org.openscada.core.InvalidSessionException;
import org.openscada.core.UnableToCreateSessionException;
import org.openscada.core.net.MessageHelper;
import org.openscada.core.server.net.AbstractServerConnectionHandler;
import org.openscada.hd.server.Service;
import org.openscada.hd.server.Session;
import org.openscada.net.base.MessageListener;
import org.openscada.net.base.data.MapValue;
import org.openscada.net.base.data.Message;
import org.openscada.net.base.data.Value;
import org.openscada.net.utils.MessageCreator;
import org.openscada.utils.concurrent.NotifyFuture;
import org.openscada.utils.concurrent.ResultHandler;
import org.openscada.utils.concurrent.task.DefaultTaskHandler;
import org.openscada.utils.concurrent.task.ResultFutureHandler;
import org.openscada.utils.concurrent.task.TaskHandler;

public class ServerConnectionHandler extends AbstractServerConnectionHandler
{

    public final static String VERSION = "0.1.0";

    private static Logger logger = Logger.getLogger ( ServerConnectionHandler.class );

    private Service service = null;

    private Session session = null;

    private final TaskHandler taskHandler = new DefaultTaskHandler ();

    private final Set<Long> taskMap = new HashSet<Long> ();

    public ServerConnectionHandler ( final Service service, final IoSession ioSession, final ConnectionInformation connectionInformation )
    {
        super ( ioSession, connectionInformation );

        this.service = service;

        this.messenger.setHandler ( MessageHelper.CC_CREATE_SESSION, new MessageListener () {

            public void messageReceived ( final Message message )
            {
                createSession ( message );
            }
        } );

        this.messenger.setHandler ( MessageHelper.CC_CLOSE_SESSION, new MessageListener () {

            public void messageReceived ( final Message message )
            {
                closeSession ();
            }
        } );

    }

    private void createSession ( final Message message )
    {
        // if session exists this is an error
        if ( this.session != null )
        {
            this.messenger.sendMessage ( MessageCreator.createFailedMessage ( message, "Connection already bound to a session" ) );
            return;
        }

        // get the session properties
        final Properties props = new Properties ();
        final Value propertiesValue = message.getValues ().get ( "properties" );
        if ( propertiesValue instanceof MapValue )
        {
            final MapValue properties = (MapValue)propertiesValue;
            for ( final Map.Entry<String, Value> entry : properties.getValues ().entrySet () )
            {
                props.put ( entry.getKey (), entry.getValue ().toString () );
            }
        }

        // now check client version
        final String clientVersion = props.getProperty ( "client-version", "" );
        if ( clientVersion.equals ( "" ) )
        {
            this.messenger.sendMessage ( MessageCreator.createFailedMessage ( message, "client does not pass \"client-version\" property! You may need to upgrade your client!" ) );
            return;
        }
        // client version does not match server version
        if ( !clientVersion.equals ( VERSION ) )
        {
            this.messenger.sendMessage ( MessageCreator.createFailedMessage ( message, "protocol version mismatch: client '" + clientVersion + "' server: '" + VERSION + "'" ) );
            return;
        }

        try
        {
            this.session = (Session)this.service.createSession ( props );
        }
        catch ( final UnableToCreateSessionException e )
        {
            this.messenger.sendMessage ( MessageCreator.createFailedMessage ( message, e.getReason () ) );
            return;
        }

        // unknown reason why we did not get a session
        if ( this.session == null )
        {
            this.messenger.sendMessage ( MessageCreator.createFailedMessage ( message, "unable to create session" ) );
            return;
        }

        // send success
        this.messenger.sendMessage ( MessageCreator.createACK ( message ) );
    }

    @Override
    protected void cleanUp ()
    {
        super.cleanUp ();
        disposeSession ();
    }

    private void disposeSession ()
    {
        // if session does not exists, silently ignore it
        if ( this.session != null )
        {
            final Session session = this.session;
            this.session = null;
            try
            {
                this.service.closeSession ( session );
            }
            catch ( final InvalidSessionException e )
            {
                logger.warn ( "Failed to close session", e );
            }
        }
    }

    private void closeSession ()
    {
        cleanUp ();
    }

    private <T> void scheduleTask ( final NotifyFuture<T> task, final long id, final ResultHandler<T> resultHandler )
    {
        task.addListener ( new ResultFutureHandler<T> ( resultHandler ) );
    }

    private void removeTask ( final long id )
    {
        synchronized ( this.taskMap )
        {
            this.taskMap.remove ( id );
        }
    }

}
