/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscada.da.client.base.browser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import org.apache.log4j.Logger;
import org.eclipse.ui.IActionFilter;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.openscada.core.ConnectionInformation;
import org.openscada.core.Variant;
import org.openscada.core.client.ConnectionState;
import org.openscada.core.client.ConnectionStateListener;
import org.openscada.da.client.Connection;
import org.openscada.da.client.FolderManager;
import org.openscada.da.client.ItemManager;
import org.openscada.da.client.base.connection.ConnectionManagerEntry;

public class HiveConnection extends Observable implements IActionFilter, IPropertySource
{
    private static Logger _log = Logger.getLogger ( HiveConnection.class );

    private boolean _connectionRequested = false;

    private ConnectionInformation connectionInformation = null;

    private Connection connection = null;

    private final Map<String, HiveItem> itemMap = new HashMap<String, HiveItem> ();

    private FolderEntry rootFolder = null;

    private ItemManager itemManager;

    private FolderManager folderManager;

    private final ConnectionManagerEntry connectionEntry;

    private enum Properties
    {
        URI,
        STATE
    };

    public HiveConnection ( final HiveConnectionInformation connectionInfo )
    {
        super ();

        this.connectionInformation = ConnectionInformation.fromURI ( connectionInfo.getConnectionString () );

        // this.connectionEntry = ConnectionManager.getDefault ().getEntry ( this.connectionInformation, false );

        this.connectionEntry = null;

        this.connection = this.connectionEntry.getConnection ();

        if ( this.connection != null )
        {
            this.connection.addConnectionStateListener ( new ConnectionStateListener () {

                public void stateChange ( final org.openscada.core.client.Connection connection, final ConnectionState state, final Throwable error )
                {
                    performStateChange ( state, error );
                }

            } );
            this.itemManager = this.connectionEntry.getItemManager ();
            this.folderManager = new FolderManager ( this.connection );
        }
    }

    public void connect ()
    {
        if ( this.connection == null )
        {
            return;
        }

        this._connectionRequested = true;
        setChanged ();
        notifyObservers ();

        _log.debug ( "Initiating connection..." ); //$NON-NLS-1$

        try
        {
            this.connection.connect ();
        }
        catch ( final Exception e )
        {
            _log.error ( "Failed to start connection", e ); //$NON-NLS-1$
        }
        _log.debug ( "Connection fired up..." ); //$NON-NLS-1$
    }

    public void disconnect ()
    {
        if ( this.connection == null )
        {
            return;
        }

        this._connectionRequested = false;

        setChanged ();
        notifyObservers ();

        this.connection.disconnect ();
    }

    public ConnectionInformation getConnectionInformation ()
    {
        return this.connectionInformation;
    }

    private synchronized void performStateChange ( final ConnectionState state, final Throwable error )
    {
        _log.debug ( String.format ( "State Change to %s (%s)", state, error ) ); //$NON-NLS-1$

        switch ( state )
        {
        case BOUND:
            this.rootFolder = new FolderEntry ( "", new HashMap<String, Variant> (), null, this, true ); //$NON-NLS-1$
            break;
        case CLOSED:
            if ( this.rootFolder != null )
            {
                this.rootFolder.dispose ();
                this.rootFolder = null;
            }
            break;
        default:
            break;
        }

        setChanged ();
        notifyObservers ();

        if ( error != null )
        {
            _log.info ( "Connection failed with additional error", error ); //$NON-NLS-1$
        }
    }

    public Connection getConnection ()
    {
        return this.connection;
    }

    public boolean isConnectionRequested ()
    {
        return this._connectionRequested;
    }

    synchronized public HiveItem lookupItem ( final String itemName )
    {
        return this.itemMap.get ( itemName );
    }

    public boolean testAttribute ( final Object target, final String name, final String value )
    {
        if ( name.equals ( "state" ) ) //$NON-NLS-1$
        {
            return this.connection.getState ().equals ( ConnectionState.valueOf ( value ) );
        }
        return false;
    }

    public FolderEntry getRootFolder ()
    {
        return this.rootFolder;
    }

    public void notifyFolderChange ( final FolderEntry folder )
    {
        setChanged ();
        notifyObservers ( folder );
    }

    protected void fillPropertyDescriptors ( final List<IPropertyDescriptor> list )
    {
        {
            final PropertyDescriptor pd = new PropertyDescriptor ( Properties.URI, Messages.getString ( "HiveConnection.PropertyDescriptor.uri.name" ) ); //$NON-NLS-1$
            pd.setCategory ( Messages.getString ( "HiveConnection.PropertyDescriptor.connectionInformation.category" ) ); //$NON-NLS-1$
            pd.setAlwaysIncompatible ( true );
            list.add ( pd );
        }
        {
            final PropertyDescriptor pd = new PropertyDescriptor ( Properties.STATE, Messages.getString ( "HiveConnection.PropertyDescriptor.state.name" ) ); //$NON-NLS-1$
            pd.setCategory ( Messages.getString ( "HiveConnection.PropertyDescriptor.connection.category" ) ); //$NON-NLS-1$
            pd.setAlwaysIncompatible ( true );
            list.add ( pd );
        }
    }

    public IPropertyDescriptor[] getPropertyDescriptors ()
    {
        final List<IPropertyDescriptor> list = new ArrayList<IPropertyDescriptor> ();

        fillPropertyDescriptors ( list );

        return list.toArray ( new IPropertyDescriptor[list.size ()] );
    }

    public Object getPropertyValue ( final Object id )
    {
        if ( id.equals ( Properties.URI ) )
        {
            return this.connectionInformation.toString ();
        }
        if ( id.equals ( Properties.STATE ) )
        {
            return this.connection.getState ().name ();
        }

        return null;
    }

    public Object getEditableValue ()
    {
        return this.connectionInformation.toString ();
    }

    public boolean isPropertySet ( final Object id )
    {
        return false;
    }

    public void resetPropertyValue ( final Object id )
    {
        // no op
    }

    public void setPropertyValue ( final Object id, final Object value )
    {
        // no op
    }

    public ItemManager getItemManager ()
    {
        return this.itemManager;
    }

    public FolderManager getFolderManager ()
    {
        return this.folderManager;
    }

    public boolean isValid ()
    {
        return this.connection != null;
    }
}
