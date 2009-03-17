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

package org.openscada.da.server.net;

import java.util.Collection;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.mina.core.session.IoSession;
import org.openscada.core.ConnectionInformation;
import org.openscada.core.InvalidSessionException;
import org.openscada.core.UnableToCreateSessionException;
import org.openscada.core.Variant;
import org.openscada.core.net.MessageHelper;
import org.openscada.core.server.net.AbstractServerConnectionHandler;
import org.openscada.core.subscription.SubscriptionState;
import org.openscada.da.core.Location;
import org.openscada.da.core.browser.Entry;
import org.openscada.da.core.server.Hive;
import org.openscada.da.core.server.InvalidItemException;
import org.openscada.da.core.server.ItemChangeListener;
import org.openscada.da.core.server.Session;
import org.openscada.da.core.server.browser.FolderListener;
import org.openscada.da.core.server.browser.HiveBrowser;
import org.openscada.da.core.server.browser.NoSuchFolderException;
import org.openscada.net.base.MessageListener;
import org.openscada.net.base.data.MapValue;
import org.openscada.net.base.data.Message;
import org.openscada.net.base.data.Value;
import org.openscada.net.da.handler.ListBrowser;
import org.openscada.net.da.handler.Messages;
import org.openscada.net.utils.MessageCreator;

public class ServerConnectionHandler extends AbstractServerConnectionHandler implements ItemChangeListener, FolderListener
{
    public final static String VERSION = "0.1.8";

    private static Logger logger = Logger.getLogger ( ServerConnectionHandler.class );

    private Hive hive = null;

    private Session session = null;

    public ServerConnectionHandler ( final Hive hive, final IoSession ioSession, final ConnectionInformation connectionInformation )
    {
        super ( ioSession, connectionInformation );

        this.hive = hive;

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

        this.messenger.setHandler ( Messages.CC_SUBSCRIBE_ITEM, new MessageListener () {

            public void messageReceived ( final Message message )
            {
                subscribe ( message );
            }
        } );

        this.messenger.setHandler ( Messages.CC_UNSUBSCRIBE_ITEM, new MessageListener () {

            public void messageReceived ( final Message message )
            {
                unsubscribe ( message );
            }
        } );

        this.messenger.setHandler ( Messages.CC_WRITE_OPERATION, new MessageListener () {

            public void messageReceived ( final Message message )
            {
                performWrite ( message );
            }
        } );

        this.messenger.setHandler ( Messages.CC_WRITE_ATTRIBUTES_OPERATION, new MessageListener () {

            public void messageReceived ( final Message message )
            {
                performWriteAttributes ( message );
            }
        } );

        this.messenger.setHandler ( Messages.CC_BROWSER_LIST_REQ, new MessageListener () {

            public void messageReceived ( final Message message )
            {
                performBrowse ( message );
            }
        } );

        this.messenger.setHandler ( Messages.CC_BROWSER_SUBSCRIBE, new MessageListener () {

            public void messageReceived ( final Message message )
            {
                performBrowserSubscribe ( message );
            }
        } );

        this.messenger.setHandler ( Messages.CC_BROWSER_UNSUBSCRIBE, new MessageListener () {

            public void messageReceived ( final Message message )
            {
                performBrowserUnsubscribe ( message );
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
            this.session = this.hive.createSession ( props );
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

        // we have a working session .. so connect listeners
        this.session.setListener ( (ItemChangeListener)this );
        this.session.setListener ( (FolderListener)this );

        // send success
        this.messenger.sendMessage ( MessageCreator.createACK ( message ) );
    }

    private void disposeSession ()
    {
        // if session does not exists, silently ignore it
        if ( this.session != null )
        {
            try
            {
                this.hive.closeSession ( this.session );
            }
            catch ( final InvalidSessionException e )
            {
                e.printStackTrace ();
            }
        }
    }

    private void closeSession ()
    {
        cleanUp ();
    }

    private void subscribe ( final Message message )
    {
        if ( this.session == null )
        {
            this.messenger.sendMessage ( MessageCreator.createFailedMessage ( message, "No session" ) );
            return;
        }

        final String itemName = message.getValues ().get ( "item-id" ).toString ();
        final boolean initial = message.getValues ().containsKey ( "cache-read" );

        logger.debug ( "Subscribe to " + itemName + " initial " + initial );

        try
        {
            this.hive.subscribeItem ( this.session, itemName );
        }
        catch ( final InvalidSessionException e )
        {
            this.messenger.sendMessage ( MessageCreator.createFailedMessage ( message, "Invalid session" ) );
        }
        catch ( final InvalidItemException e )
        {
            this.messenger.sendMessage ( MessageCreator.createFailedMessage ( message, "Invalid item" ) );
        }

    }

    private void unsubscribe ( final Message message )
    {
        if ( this.session == null )
        {
            this.messenger.sendMessage ( MessageCreator.createFailedMessage ( message, "No session" ) );
            return;
        }

        final String itemName = message.getValues ().get ( "item-id" ).toString ();

        try
        {
            this.hive.unsubscribeItem ( this.session, itemName );
        }
        catch ( final InvalidSessionException e )
        {
            this.messenger.sendMessage ( MessageCreator.createFailedMessage ( message, "Invalid session" ) );
        }
        catch ( final InvalidItemException e )
        {
            this.messenger.sendMessage ( MessageCreator.createFailedMessage ( message, "Invalid item" ) );
        }
    }

    @Override
    protected void cleanUp ()
    {
        super.cleanUp ();
        disposeSession ();
    }

    public void dataChanged ( final String itemId, final Variant value, final Map<String, Variant> attributes, final boolean cache )
    {
        this.messenger.sendMessage ( Messages.notifyData ( itemId, value, attributes, cache ) );
    }

    public void subscriptionChanged ( final String item, final SubscriptionState subscriptionState )
    {
        this.messenger.sendMessage ( Messages.notifySubscriptionChange ( item, subscriptionState ) );
    }

    private void performWrite ( final Message message )
    {
        final WriteValueController c = new WriteValueController ( this.hive, this.session, this.messenger );
        c.run ( message );
    }

    private void performWriteAttributes ( final Message message )
    {
        final WriteAttributesController c = new WriteAttributesController ( this.hive, this.session, this.messenger );
        c.run ( message );
    }

    private void performBrowse ( final Message message )
    {
        final BrowseController c = new BrowseController ( this.hive, this.session, this.messenger );
        c.run ( message );
    }

    public void folderChanged ( final Location location, final Collection<Entry> added, final Collection<String> removed, final boolean full )
    {
        logger.debug ( "Got folder change event from hive for folder: " + location.toString () );
        this.messenger.sendMessage ( ListBrowser.createEvent ( location.asArray (), added, removed, full ) );
    }

    private void performBrowserSubscribe ( final Message message )
    {
        final HiveBrowser browser = this.hive.getBrowser ();

        if ( browser == null )
        {
            logger.warn ( "Unable to subscribe to folder: no hive browser set" );
            this.messenger.sendMessage ( MessageCreator.createFailedMessage ( message, "Interface not supported" ) );
            return;
        }

        final Location location = new Location ( ListBrowser.parseSubscribeMessage ( message ) );

        try
        {
            logger.debug ( "Subscribe to folder: " + location.toString () );
            browser.subscribe ( this.session, location );
        }
        catch ( final NoSuchFolderException e )
        {
            logger.warn ( "Unable to subscribe to folder: " + location.toString (), e );
            this.messenger.sendMessage ( MessageCreator.createFailedMessage ( message, "Folder not found" ) );
            return;
        }
        catch ( final InvalidSessionException e )
        {
            logger.warn ( "Unable to subscribe to folder: " + location.toString (), e );
            this.messenger.sendMessage ( MessageCreator.createFailedMessage ( message, "Invalid session" ) );
            return;
        }
        catch ( final Exception e )
        {
            logger.warn ( "Browsing failed", e );
            this.messenger.sendMessage ( MessageCreator.createFailedMessage ( message, e ) );
            return;
        }
    }

    private void performBrowserUnsubscribe ( final Message message )
    {
        final HiveBrowser browser = this.hive.getBrowser ();

        if ( browser == null )
        {
            logger.warn ( "Unable to unsubscribe from folder: no hive browser set" );
            this.messenger.sendMessage ( MessageCreator.createFailedMessage ( message, "Interface not supported" ) );
            return;
        }

        final Location location = new Location ( ListBrowser.parseUnsubscribeMessage ( message ) );

        try
        {
            logger.debug ( "Unsubscribe from folder: " + location.toString () );
            browser.unsubscribe ( this.session, location );
        }
        catch ( final NoSuchFolderException e )
        {
            this.messenger.sendMessage ( MessageCreator.createFailedMessage ( message, "Folder not found" ) );
            return;
        }
        catch ( final InvalidSessionException e )
        {
            this.messenger.sendMessage ( MessageCreator.createFailedMessage ( message, "Invalid session" ) );
            return;
        }
    }

}
