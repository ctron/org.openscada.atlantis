package org.openscada.da.core.common.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import org.openscada.da.core.browser.Entry;
import org.openscada.da.core.browser.NoSuchFolderException;
import org.openscada.da.core.browser.common.DataItemEntryCommon;
import org.openscada.da.core.browser.common.FolderEntryCommon;
import org.openscada.da.core.common.DataItem;

public class FolderCommon implements Folder
{

    private Map < String, Entry > _entryMap = new HashMap < String, Entry > ();
    
    public Entry[] list ( Stack<String> path ) throws NoSuchFolderException
    {
        if ( path.isEmpty () )
            return getAllEntries ();
        else
            return getFolderEntry ( path.pop () ).list ( path );
    }

    private Entry [] getAllEntries ()
    {
        synchronized ( _entryMap )
        {
            return _entryMap.values ().toArray ( new Entry[_entryMap.size()] );
        }
    }
    
    private Entry getEntry ( String name )
    {
        synchronized ( _entryMap )
        {
            return _entryMap.get ( name );
        }
    }
    
    private Folder getFolderEntry ( String name ) throws NoSuchFolderException
    {
        Entry entry = getEntry ( name );
        if ( entry instanceof FolderEntryCommon )
        {
            return ((FolderEntryCommon)entry).getFolder ();
        }
        else
        {
            throw new NoSuchFolderException ();
        }
            
    }
    
    public boolean add ( String name, Folder folder )
    {
        synchronized ( _entryMap )
        {
            if ( !_entryMap.containsKey ( name ) )
            {
                _entryMap.put ( name, new FolderEntryCommon ( name, folder ) );
                return true;
            }
            else
                return false;
        }
    }
    
    public boolean add ( String name, DataItem item )
    {
        synchronized ( _entryMap )
        {
            if ( !_entryMap.containsKey ( name ) )
            {
                _entryMap.put ( name, new DataItemEntryCommon ( name, item ) );
                return true;
            }
            else
                return false;
        }
    }
    
    public boolean remove ( String name )
    {
        synchronized ( _entryMap )
        {
            if ( _entryMap.containsKey ( name ) )
            {
                _entryMap.remove ( name );
                return true;
            }
            else
                return false;
        }
    }
    
    public boolean remove ( DataItem item )
    {
        synchronized ( _entryMap )
        {
            for ( Iterator < Map.Entry < String, Entry > > i = _entryMap.entrySet ().iterator () ; i.hasNext () ; )
            {
                Map.Entry<String, Entry> entry = i.next ();
                if ( entry instanceof DataItemEntryCommon )
                    if ( ((DataItemEntryCommon)entry).getItem () == item )
                    {
                        i.remove ();
                        return true;
                    }
            }
            return false;
        }
    }
    
}
