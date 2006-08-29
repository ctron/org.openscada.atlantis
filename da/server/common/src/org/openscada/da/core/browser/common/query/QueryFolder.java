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

package org.openscada.da.core.browser.common.query;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.openscada.core.Variant;
import org.openscada.da.core.browser.common.FolderCommon;
import org.openscada.da.core.browser.common.FolderListener;
import org.openscada.da.core.common.DataItem;
import org.openscada.da.core.server.browser.Entry;
import org.openscada.da.core.server.browser.NoSuchFolderException;

public class QueryFolder implements StorageBasedFolder
{
    private Matcher _matcher = null;
    private NameProvider _nameProvider = null;
    private FolderCommon _folder = new FolderCommon ();
    
    private List<StorageBasedFolder> _folders = new ArrayList<StorageBasedFolder> ();
    private Set<ItemDescriptor> _items = new HashSet<ItemDescriptor> ();
    
    public QueryFolder ( Matcher matcher, NameProvider nameProvider )
    {
        _matcher = matcher;
        _nameProvider = nameProvider;
    }
    
    public void addChild ( String name, StorageBasedFolder folder, Map<String, Variant> attributes )
    {
        synchronized ( this )
        {
            _folder.add ( name, folder, attributes );
            _folders.add ( folder );

            // now push all possible descriptors
            for ( ItemDescriptor desc : _items )
            {
                folder.added ( desc );
            }
        }
    }
    
    public void removeChild ( QueryFolder folder )
    {
        synchronized ( this )
        {
            _folder.remove ( folder );
            _folders.remove ( folder );
        }
    }
    
    private boolean match ( ItemDescriptor desc )
    {
        if ( _matcher != null )
            return _matcher.matches ( desc );
        
        return false;
    }
    
    public void added ( ItemDescriptor desc )
    {
        synchronized ( this )
        {
            if ( _items.contains ( desc ) )
                return;

            if ( match ( desc ) )
            {
                _items.add ( desc );
                notifyAdd ( desc );
            }
        }
    }
    
    public void removed ( ItemDescriptor desc )
    {
        synchronized ( this )
        {
            if ( !_items.contains ( desc ) )
                return;

            _items.remove ( desc );
            notifyRemove ( desc );
        }
    }
    
    public void removeAllForItem ( DataItem dataItem )
    {
        synchronized ( this )
        {
            List<ItemDescriptor> removeList = new LinkedList<ItemDescriptor> ();
            for ( ItemDescriptor desc : _items )
            {
                if ( desc.getItem () == dataItem )
                {
                    removeList.add ( desc );
                }
            }
            for ( ItemDescriptor desc : removeList )
            {
                removed ( desc );
            }
        }
    }
    
    private void notifyAdd ( ItemDescriptor desc )
    {
        String name = _nameProvider.getName ( desc );
        if ( name != null )
        {
            _folder.add ( desc.getItem ().getInformation ().getName (), desc.getItem (), desc.getAttributes () );
        }
        
        // notify childs
        for ( StorageBasedFolder folder : _folders )
        {
            folder.added ( desc );
        }
    }
    
    private void notifyRemove ( ItemDescriptor desc )
    {
        String name = _nameProvider.getName ( desc );
        if ( name != null )
        {
            _folder.remove ( desc.getItem () );
        }
        
        // notify childs
        for ( StorageBasedFolder folder : _folders )
        {
            folder.removed ( desc );
        }
    }

    public Entry[] list ( Stack<String> path ) throws NoSuchFolderException
    {
        return _folder.list ( path );
    }

    public void subscribe ( Stack<String> path, FolderListener listener, Object tag ) throws NoSuchFolderException
    {
        _folder.subscribe ( path, listener, tag );
    }

    public void unsubscribe ( Stack<String> path, Object tag ) throws NoSuchFolderException
    {
        _folder.unsubscribe ( path, tag );
    }

    public void added ()
    {
        _folder.added ();
    }

    public void removed ()
    {
        _folder.removed ();
    }
}
