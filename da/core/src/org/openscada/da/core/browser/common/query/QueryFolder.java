package org.openscada.da.core.browser.common.query;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.openscada.da.core.browser.Entry;
import org.openscada.da.core.browser.NoSuchFolderException;
import org.openscada.da.core.browser.common.FolderCommon;
import org.openscada.da.core.browser.common.FolderListener;
import org.openscada.da.core.data.Variant;

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
}
