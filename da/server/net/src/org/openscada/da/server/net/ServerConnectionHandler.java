/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
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
import org.openscada.core.CancellationNotSupportedException;
import org.openscada.core.InvalidSessionException;
import org.openscada.core.UnableToCreateSessionException;
import org.openscada.core.Variant;
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
import org.openscada.net.base.ConnectionHandlerBase;
import org.openscada.net.base.MessageListener;
import org.openscada.net.base.data.LongValue;
import org.openscada.net.base.data.Message;
import org.openscada.net.base.data.Value;
import org.openscada.net.da.handler.ListBrowser;
import org.openscada.net.da.handler.Messages;
import org.openscada.net.io.net.Connection;
import org.openscada.net.utils.MessageCreator;

public class ServerConnectionHandler extends ConnectionHandlerBase implements ItemChangeListener, FolderListener
{

    public final static String VERSION = "0.1.6";

    private static Logger _log = Logger.getLogger ( ServerConnectionHandler.class );

    private Hive _hive = null;
    private Session _session = null;

    public ServerConnectionHandler ( Hive hive )
    {
        super ();

        _hive = hive;

        getMessageProcessor ().setHandler ( Messages.CC_CREATE_SESSION, new MessageListener () {

            public void messageReceived ( Connection connection, Message message )
            {
                createSession ( message );
            }
        } );

        getMessageProcessor ().setHandler ( Messages.CC_CLOSE_SESSION, new MessageListener () {

            public void messageReceived ( Connection connection, Message message )
            {
                closeSession ();
            }
        } );

        getMessageProcessor ().setHandler ( Messages.CC_SUBSCRIBE_ITEM, new MessageListener () {

            public void messageReceived ( Connection connection, Message message )
            {
                subscribe ( message );
            }
        } );

        getMessageProcessor ().setHandler ( Messages.CC_UNSUBSCRIBE_ITEM, new MessageListener () {

            public void messageReceived ( Connection connection, Message message )
            {
                unsubscribe ( message );
            }
        } );

        getMessageProcessor ().setHandler ( Messages.CC_WRITE_OPERATION, new MessageListener () {

            public void messageReceived ( Connection connection, Message message )
            {
                performWrite ( message );
            }
        } );

        getMessageProcessor ().setHandler ( Messages.CC_WRITE_ATTRIBUTES_OPERATION, new MessageListener () {

            public void messageReceived ( Connection connection, Message message )
            {
                performWriteAttributes ( message );
            }
        } );

        getMessageProcessor ().setHandler ( Messages.CC_BROWSER_LIST_REQ, new MessageListener () {

            public void messageReceived ( Connection connection, Message message )
            {
                performBrowse ( message );
            }
        } );

        getMessageProcessor ().setHandler ( Messages.CC_BROWSER_SUBSCRIBE, new MessageListener () {

            public void messageReceived ( Connection connection, Message message )
            {
                performBrowserSubscribe ( message );
            }
        } );

        getMessageProcessor ().setHandler ( Messages.CC_BROWSER_UNSUBSCRIBE, new MessageListener () {

            public void messageReceived ( Connection connection, Message message )
            {
                performBrowserUnsubscribe ( message );
            }
        } );
    }

    private void createSession ( Message message )
    {
        // if session exists this is an error
        if ( _session != null )
        {
            getConnection ().sendMessage (
                    MessageCreator.createFailedMessage ( message, "Connection already bound to a session" ) );
            return;
        }

        Properties props = new Properties ();
        for ( Map.Entry<String, Value> entry : message.getValues ().getValues ().entrySet () )
        {
            props.put ( entry.getKey (), entry.getValue ().toString () );
        }

        // now check client version
        String clientVersion = props.getProperty ( "client-version", "" );
        if ( clientVersion.equals ( "" ) )
        {
            getConnection ().sendMessage (
                    MessageCreator.createFailedMessage ( message,
                            "client does not pass \"client-version\" property! You may need to upgrade your client!" ) );
            return;
        }
        // client version does not match server version
        if ( !clientVersion.equals ( VERSION ) )
        {
            getConnection ().sendMessage (
                    MessageCreator.createFailedMessage ( message, "protocol version mismatch: client '" + clientVersion
                            + "' server: '" + VERSION + "'" ) );
            return;
        }

        try
        {
            _session = _hive.createSession ( props );
        }
        catch ( UnableToCreateSessionException e )
        {
            getConnection ().sendMessage ( MessageCreator.createFailedMessage ( message, e.getReason () ) );
            return;
        }

        // unknown reason why we did not get a session
        if ( _session == null )
        {
            getConnection ().sendMessage ( MessageCreator.createFailedMessage ( message, "unable to create session" ) );
            return;
        }

        // we have a working session .. so connect listeners
        _session.setListener ( (ItemChangeListener)this );
        _session.setListener ( (FolderListener)this );

        // send success
        getConnection ().sendMessage ( MessageCreator.createACK ( message ) );
    }

    private void disposeSession ()
    {
        // if session does not exists, silently ignore it
        if ( _session != null )
        {
            try
            {
                _hive.closeSession ( _session );
            }
            catch ( InvalidSessionException e )
            {
                e.printStackTrace ();
            }
        }
    }

    private void closeSession ()
    {
        disposeSession ();
        // also shut down communcation connection
        getConnection ().close ();
    }

    private void subscribe ( Message message )
    {
        if ( _session == null )
        {
            getConnection ().sendMessage ( MessageCreator.createFailedMessage ( message, "No session" ) );
            return;
        }

        String itemName = message.getValues ().get ( "item-id" ).toString ();
        boolean initial = message.getValues ().containsKey ( "cache-read" );

        _log.debug ( "Subscribe to " + itemName + " initial " + initial );

        try
        {
            _hive.subscribeItem ( _session, itemName );
        }
        catch ( InvalidSessionException e )
        {
            getConnection ().sendMessage ( MessageCreator.createFailedMessage ( message, "Invalid session" ) );
        }
        catch ( InvalidItemException e )
        {
            getConnection ().sendMessage ( MessageCreator.createFailedMessage ( message, "Invalid item" ) );
        }

    }

    private void unsubscribe ( Message message )
    {
        if ( _session == null )
        {
            getConnection ().sendMessage ( MessageCreator.createFailedMessage ( message, "No session" ) );
            return;
        }

        String itemName = message.getValues ().get ( "item-id" ).toString ();

        try
        {
            _hive.unsubscribeItem ( _session, itemName );
        }
        catch ( InvalidSessionException e )
        {
            getConnection ().sendMessage ( MessageCreator.createFailedMessage ( message, "Invalid session" ) );
        }
        catch ( InvalidItemException e )
        {
            getConnection ().sendMessage ( MessageCreator.createFailedMessage ( message, "Invalid item" ) );
        }
    }

    private void cleanUp ()
    {
        disposeSession ();
    }

    @Override
    public void closed ( Exception error )
    {
        cleanUp ();
        super.closed ( error );
    }

    public void valueChanged ( String name, Variant value, boolean initial )
    {
        getConnection ().sendMessage ( Messages.notifyValue ( name, value, initial ) );
    }

    public void attributesChanged ( String name, Map<String, Variant> attributes, boolean initial )
    {
        getConnection ().sendMessage ( Messages.notifyAttributes ( name, attributes, initial ) );
    }

    public void subscriptionChanged ( String item, SubscriptionState subscriptionState )
    {
        getConnection ().sendMessage ( Messages.notifySubscriptionChange ( item, subscriptionState ) );
    }

    private void performWrite ( final Message message )
    {
        WriteValueController c = new WriteValueController ( _hive, _session, this );
        c.run ( message );
    }

    private void performWriteAttributes ( final Message message )
    {
        WriteAttributesController c = new WriteAttributesController ( _hive, _session, this );
        c.run ( message );
    }

    private void performBrowse ( final Message message )
    {
        BrowseController c = new BrowseController ( _hive, _session, this );
        c.run ( message );
    }

    public void folderChanged ( Location location, Collection<Entry> added, Collection<String> removed, boolean full )
    {
        _log.debug ( "Got folder change event from hive for folder: " + location.toString () );
        getConnection ().sendMessage ( ListBrowser.createEvent ( location.asArray (), added, removed, full ) );
    }

    private void performBrowserSubscribe ( Message message )
    {
        HiveBrowser browser = _hive.getBrowser ();

        if ( browser == null )
        {
            _log.warn ( "Unable to subscribe to folder: no hive browser set" );
            getConnection ().sendMessage ( MessageCreator.createFailedMessage ( message, "Interface not supported" ) );
            return;
        }

        Location location = new Location ( ListBrowser.parseSubscribeMessage ( message ) );

        try
        {
            _log.debug ( "Subscribe to folder: " + location.toString () );
            browser.subscribe ( _session, location );
        }
        catch ( NoSuchFolderException e )
        {
            _log.warn ( "Unable to subscribe to folder: " + location.toString (), e );
            getConnection ().sendMessage ( MessageCreator.createFailedMessage ( message, "Folder not found" ) );
            return;
        }
        catch ( InvalidSessionException e )
        {
            _log.warn ( "Unable to subscribe to folder: " + location.toString (), e );
            getConnection ().sendMessage ( MessageCreator.createFailedMessage ( message, "Invalid session" ) );
            return;
        }
        catch ( Exception e )
        {
            _log.warn ( "Browsing failed", e );
            getConnection ().sendMessage ( MessageCreator.createFailedMessage ( message, e ) );
            return;
        }
    }

    private void performBrowserUnsubscribe ( Message message )
    {
        HiveBrowser browser = _hive.getBrowser ();

        if ( browser == null )
        {
            _log.warn ( "Unable to unsubscribe from folder: no hive browser set" );
            getConnection ().sendMessage ( MessageCreator.createFailedMessage ( message, "Interface not supported" ) );
            return;
        }

        Location location = new Location ( ListBrowser.parseUnsubscribeMessage ( message ) );

        try
        {
            _log.debug ( "Unsubscribe from folder: " + location.toString () );
            browser.unsubscribe ( _session, location );
        }
        catch ( NoSuchFolderException e )
        {
            getConnection ().sendMessage ( MessageCreator.createFailedMessage ( message, "Folder not found" ) );
            return;
        }
        catch ( InvalidSessionException e )
        {
            getConnection ().sendMessage ( MessageCreator.createFailedMessage ( message, "Invalid session" ) );
            return;
        }
    }
}
