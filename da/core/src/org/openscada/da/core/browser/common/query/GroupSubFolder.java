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

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.openscada.da.core.browser.Entry;
import org.openscada.da.core.browser.NoSuchFolderException;
import org.openscada.da.core.browser.common.Folder;
import org.openscada.da.core.browser.common.FolderCommon;
import org.openscada.da.core.browser.common.FolderListener;
import org.openscada.da.core.common.DataItem;
import org.openscada.da.core.data.Variant;
import org.openscada.utils.collection.MapBuilder;

public class GroupSubFolder implements Folder
{
    private static Logger _log = Logger.getLogger ( GroupSubFolder.class );
    
    private GroupSubFolder _parent = null;
    private NameProvider _nameProvider = null;
    private FolderCommon _folder = new FolderCommon ();
    private Map<String, GroupSubFolder> _subFolders = new HashMap<String, GroupSubFolder> ();
    
    public GroupSubFolder ( NameProvider nameProvider )
    { 
        this ( null, nameProvider );
    }
    
    private GroupSubFolder ( GroupSubFolder parent, NameProvider nameProvider )
    {
        _parent = parent;
        _nameProvider = nameProvider;
    }
    
    @Override
    protected void finalize () throws Throwable
    {
        _log.debug ( "Finalized: " + this );
        super.finalize ();
    }
    
    public GroupSubFolder add ( Stack<String> path, ItemDescriptor descriptor )
    {
        if ( path.isEmpty () )
        {
            return insertItem ( descriptor ) ? this : null;
        }
        else
        {
            String next = path.pop ();
            GroupSubFolder subFolder = getSubFolder ( next );
            return subFolder.add ( path, descriptor );
        }
    }

    private boolean insertItem ( ItemDescriptor descriptor )
    {
        String name = _nameProvider.getName ( descriptor );
        
        if ( name == null )
            return false;
        
        _folder.add ( name, descriptor.getItem (), descriptor.getAttributes () );
        
        return true;
    }
    
    private GroupSubFolder getSubFolder ( String name )
    {
        if ( !_subFolders.containsKey ( name ) )
        {
            GroupSubFolder folder = new GroupSubFolder ( this, _nameProvider );
            _subFolders.put ( name, folder );
            MapBuilder<String, Variant> builder = new MapBuilder<String, Variant> (); 
            _folder.add ( name, folder, builder.getMap () );
        }
        return _subFolders.get ( name );
    }
    
    public void remove ( ItemDescriptor descriptor )
    {
        DataItem item = descriptor.getItem ();
        
        if ( !_folder.remove ( item ) )
            return;
        
        if ( _folder.size () <= 0 )
        {
            if ( _parent != null )
                _parent.removeSubFolder ( this );
        }
    }
    
    private void removeSubFolder ( GroupSubFolder subFolder )
    {
        String folderName = _folder.findEntry ( subFolder );
        if ( folderName == null )
            return;
        
        subFolder.clearSubscribers ();
        _folder.remove ( folderName );
        _subFolders.remove ( folderName );
        
        if ( _folder.size () <= 0 )
        {
            if ( _parent != null )
            {
                _parent.removeSubFolder ( this );
            }
        }
    }
    
    synchronized public Entry[] list ( Stack<String> path ) throws NoSuchFolderException
    {
       return _folder.list ( path );
    }

    synchronized public void subscribe ( Stack<String> path, FolderListener listener, Object tag ) throws NoSuchFolderException
    {
        _folder.subscribe ( path, listener, tag );
    }

    synchronized public void unsubscribe ( Stack<String> path, Object tag ) throws NoSuchFolderException
    {
        _folder.unsubscribe ( path, tag );
    }

    public void clearSubscribers ()
    {
        _folder.clearListeners ();
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
