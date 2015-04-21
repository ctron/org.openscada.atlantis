/*******************************************************************************
 * Copyright (c) 2015 IBH SYSTEMS GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBH SYSTEMS GmbH - initial API and implementation
 *******************************************************************************/
package org.openscada.da.server.opc.xmlda;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.eclipse.scada.da.core.DataItemInformation;
import org.eclipse.scada.da.core.server.browser.NoSuchFolderException;
import org.eclipse.scada.da.data.IODirection;
import org.eclipse.scada.da.server.browser.common.Folder;
import org.eclipse.scada.da.server.browser.common.FolderCommon;
import org.eclipse.scada.da.server.browser.common.FolderListener;
import org.eclipse.scada.da.server.common.DataItemInformationBase;
import org.openscada.opc.xmlda.Connection;
import org.openscada.opc.xmlda.browse.Browser;
import org.openscada.opc.xmlda.browse.BrowserListener;
import org.openscada.opc.xmlda.browse.BrowserState;
import org.openscada.opc.xmlda.requests.BrowseEntry;

public class BrowserFolder extends FolderCommon implements BrowserListener
{

    private final Connection connection;

    private final String itemName;

    private Browser browser;

    private final String itemPath;

    private final String prefix;

    public BrowserFolder ( final Connection connection, final String prefix, final String itemName, final String itemPath )
    {
        this.connection = connection;
        this.prefix = prefix;
        this.itemName = itemName;
        this.itemPath = itemPath;
    }

    @Override
    public synchronized void subscribe ( final Stack<String> path, final FolderListener listener, final Object tag ) throws NoSuchFolderException
    {
        super.subscribe ( path, listener, tag );

        if ( hasSubscribers () && this.browser == null )
        {
            start ();
        }
    }

    @Override
    public synchronized void unsubscribe ( final Stack<String> path, final Object tag ) throws NoSuchFolderException
    {
        super.unsubscribe ( path, tag );
        if ( !hasSubscribers () && this.browser != null )
        {
            stop ();
        }
    }

    protected void start ()
    {
        this.browser = this.connection.createBrowser ( this.itemName, this.itemPath, this, 5_000L, 100, false );
    }

    protected void stop ()
    {
        if ( this.browser != null )
        {
            this.browser.dispose ();
            this.browser = null;
        }

        clear ();
    }

    @Override
    public void stateChange ( final BrowserState state, final Throwable error )
    {
        if ( state == BrowserState.ERROR )
        {
            clear ();
        }
    }

    @Override
    public void dataChange ( final List<BrowseEntry> entries )
    {
        final Map<String, Folder> folders = new HashMap<> ();
        final Map<String, DataItemInformation> items = new HashMap<> ();;

        for ( final BrowseEntry entry : entries )
        {
            if ( entry.isParent () )
            {
                // add as folder
                final Folder folder = makeFolder ( entry );
                if ( folder != null )
                {
                    folders.put ( entry.getName (), folder );
                }
            }
            else
            {
                // add as item
                final DataItemInformation item = makeItem ( entry );
                if ( item != null )
                {
                    items.put ( entry.getName (), item );
                }
            }
        }

        add ( folders, items );
    }

    private DataItemInformation makeItem ( final BrowseEntry entry )
    {
        final Set<IODirection> dir = EnumSet.allOf ( IODirection.class );
        return new DataItemInformationBase ( this.prefix + entry.getItemName (), dir );
    }

    private Folder makeFolder ( final BrowseEntry entry )
    {
        return new BrowserFolder ( this.connection, this.prefix, entry.getItemName (), entry.getItemPath () );
    }
}
