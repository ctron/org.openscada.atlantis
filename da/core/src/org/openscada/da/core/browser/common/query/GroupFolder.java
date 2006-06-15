package org.openscada.da.core.browser.common.query;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.openscada.da.core.browser.Entry;
import org.openscada.da.core.browser.Location;
import org.openscada.da.core.browser.NoSuchFolderException;
import org.openscada.da.core.browser.common.FolderListener;

public class GroupFolder implements StorageBasedFolder
{
    private Map<ItemDescriptor, GroupSubFolder> _itemList = new HashMap<ItemDescriptor, GroupSubFolder> ();
    private GroupProvider _groupProvider = null;
    private NameProvider _nameProvider = null;
    
    private GroupSubFolder _folder = null;
    
    public GroupFolder ( GroupProvider groupProvider, NameProvider nameProvider )
    {
        _groupProvider = groupProvider;
        _nameProvider = nameProvider;
        
        _folder = new GroupSubFolder ( _nameProvider );
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

    public void added ( ItemDescriptor descriptor )
    {
        if ( _itemList.containsKey ( descriptor ) )
            return;
        
        String [] groupingArray = _groupProvider.getGrouping ( descriptor );
        if ( groupingArray == null )
            return;
        
        Location grouping = new Location ( groupingArray );
        
        GroupSubFolder subFolder = _folder.add ( grouping.getPathStack (), descriptor );
        
        if ( subFolder != null )
            _itemList.put ( descriptor, subFolder );
    }

    public void removed ( ItemDescriptor descriptor )
    {
        GroupSubFolder folder = _itemList.get ( descriptor );
        
        if ( folder == null )
            return;
        
        folder.remove ( descriptor );
        _itemList.remove ( descriptor );
    }
}
