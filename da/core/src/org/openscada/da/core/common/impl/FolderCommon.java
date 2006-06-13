package org.openscada.da.core.common.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.openscada.da.core.browser.Entry;
import org.openscada.da.core.browser.NoSuchFolderException;
import org.openscada.da.core.browser.common.DataItemEntryCommon;
import org.openscada.da.core.browser.common.Folder;
import org.openscada.da.core.browser.common.FolderEntryCommon;
import org.openscada.da.core.browser.common.FolderListener;
import org.openscada.da.core.common.DataItem;
import org.openscada.da.core.data.Variant;

public class FolderCommon implements Folder
{

    private Map<String, Entry> _entryMap = new HashMap<String, Entry> ();
    private Map<Object, FolderListener> _listeners = new HashMap<Object, FolderListener> ();
    
    public Entry[] list ( Stack<String> path ) throws NoSuchFolderException
    {
        if ( path.isEmpty () )
            return getAllEntries ();
        else
            return getFolderEntry ( path.pop () ).list ( path );
    }

    private Entry [] getAllEntries ()
    {
        synchronized ( this )
        {
            return _entryMap.values ().toArray ( new Entry[_entryMap.size()] );
        }
    }
    
    private Entry getEntry ( String name )
    {
        synchronized ( this )
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
    
    public boolean add ( String name, Folder folder, Map < String, Variant > attributes )
    {
        synchronized ( this )
        {
            if ( !_entryMap.containsKey ( name ) )
            {
                _entryMap.put ( name, new FolderEntryCommon ( name, folder, attributes ) );
                return true;
            }
            else
                return false;
        }
    }
    
    public boolean add ( String name, DataItem item, Map < String, Variant > attributes )
    {
        if ( item.getInformation ().getName () == null )
            throw new NullPointerException ( "Item must have an id" );
        
        synchronized ( this )
        {
            if ( !_entryMap.containsKey ( name ) )
            {
                Entry entry = new DataItemEntryCommon ( name, item, attributes );
                _entryMap.put ( name, entry );
                notifyAdd ( entry );
                return true;
            }
            else
                return false;
        }
    }
    
    public boolean remove ( String name )
    {
        synchronized ( this )
        {
            if ( _entryMap.containsKey ( name ) )
            {
                _entryMap.remove ( name );
                notifyRemove ( name );
                return true;
            }
            else
                return false;
        }
    }
    
    public boolean remove ( DataItem item )
    {
        synchronized ( this )
        {
            for ( Iterator<Map.Entry<String, Entry>> i = _entryMap.entrySet ().iterator () ; i.hasNext () ; )
            {
                Map.Entry<String, Entry> entry = i.next ();
                if ( entry instanceof DataItemEntryCommon )
                    if ( ((DataItemEntryCommon)entry).getItem () == item )
                    {
                        i.remove ();
                        notifyRemove ( entry.getKey () );
                        return true;
                    }
            }
            return false;
        }
    }

    public void subscribe ( Stack<String> path, FolderListener listener, Object tag ) throws NoSuchFolderException
    {
        if ( path.isEmpty () )
            addListener ( listener, tag );
        else
            getFolderEntry ( path.pop () ).subscribe ( path, listener, tag );
    }

    public void unsubscribe ( Stack<String> path, Object tag ) throws NoSuchFolderException
    {
        if ( path.isEmpty () )
            removeListener ( tag );
        else
            getFolderEntry ( path.pop () ).unsubscribe ( path, tag );
    }
    
    private void addListener ( FolderListener listener, Object tag )
    {
        synchronized ( this )
        {
            _listeners.put ( tag, listener );
            sendCurrentList ( listener, tag );
        }
    }
    
    private void removeListener ( Object tag )
    {
        synchronized ( this )
        {
            _listeners.remove ( tag );
        } 
    }
    
    private void sendCurrentList ( FolderListener listener, Object tag )
    {
        synchronized ( this )
        {
            listener.changed ( tag, new ArrayList<Entry> ( _entryMap.values () ), new LinkedList<String> (), true );            
        }
    }
    
    private void notifyAdd ( Entry added )
    {
        synchronized ( this )
        {
            List<Entry> list = new LinkedList<Entry> ();
            list.add ( added );
            for ( Map.Entry<Object, FolderListener> entry: _listeners.entrySet () )
            {
                entry.getValue ().changed ( entry.getKey(), list, new LinkedList<String> (), false );
            }
        }
    }
    
    private void notifyRemove ( String removed )
    {
        synchronized ( this )
        {
            List<String> list = new LinkedList<String> ();
            list.add ( removed );
            for ( Map.Entry<Object, FolderListener> entry: _listeners.entrySet () )
            {
                entry.getValue ().changed ( entry.getKey(), new LinkedList<Entry> (), list , false );
            }
        }
    }
    
}
