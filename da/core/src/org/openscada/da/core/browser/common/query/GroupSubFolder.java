package org.openscada.da.core.browser.common.query;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

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
    
    synchronized GroupSubFolder add ( Stack<String> path, ItemDescriptor descriptor )
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
    
    synchronized public void remove ( ItemDescriptor descriptor )
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
    
    synchronized private void removeSubFolder ( GroupSubFolder subFolder )
    {
        String folderName = _folder.findEntry ( subFolder );
        if ( folderName == null )
            return;
         
        _folder.remove ( folderName );
        _subFolders.remove ( folderName );
        
        if ( _folder.size () <= 0 )
        {
            if ( _parent != null )
                _parent.removeSubFolder ( this );
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

}
