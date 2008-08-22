/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.browser.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.openscada.core.Variant;
import org.openscada.da.core.browser.Entry;
import org.openscada.da.core.server.browser.NoSuchFolderException;
import org.openscada.da.server.common.DataItem;
import org.openscada.da.server.common.configuration.ConfigurableFolder;

public class FolderCommon implements Folder, ConfigurableFolder
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

    private Entry[] getAllEntries ()
    {
        synchronized ( this )
        {
            return _entryMap.values ().toArray ( new Entry[_entryMap.size ()] );
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
            return ( (FolderEntryCommon)entry ).getFolder ();
        }
        else
        {
            throw new NoSuchFolderException ();
        }

    }

    /* (non-Javadoc)
     * @see org.openscada.da.server.browser.common.ConfigurableFolder#add(java.lang.String, org.openscada.da.server.browser.common.Folder, java.util.Map)
     */
    public boolean add ( String name, Folder folder, Map<String, Variant> attributes )
    {
        synchronized ( this )
        {
            if ( !_entryMap.containsKey ( name ) )
            {
                Entry entry = new FolderEntryCommon ( name, folder, attributes );
                _entryMap.put ( name, entry );
                notifyAdd ( entry );
                folder.added ();
                return true;
            }
            else
                return false;
        }
    }

    /* (non-Javadoc)
     * @see org.openscada.da.server.browser.common.ConfigurableFolder#add(java.lang.String, org.openscada.da.server.common.DataItem, java.util.Map)
     */
    public boolean add ( String name, DataItem item, Map<String, Variant> attributes )
    {
        if ( item.getInformation ().getName () == null )
        {
            throw new NullPointerException ( "Item must have an id" );
        }

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
            {
                return false;
            }
        }
    }

    public synchronized boolean remove ( String name )
    {
        if ( _entryMap.containsKey ( name ) )
        {
            Entry entry = _entryMap.remove ( name );
            if ( entry instanceof FolderEntryCommon )
                ( (FolderEntryCommon)entry ).getFolder ().removed ();

            notifyRemove ( name );
            return true;
        }
        else
            return false;
    }

    public synchronized String findEntry ( DataItem item )
    {
        for ( Iterator<Map.Entry<String, Entry>> i = _entryMap.entrySet ().iterator (); i.hasNext (); )
        {
            Map.Entry<String, Entry> entry = i.next ();
            if ( entry.getValue () instanceof DataItemEntryCommon )
                if ( ( (DataItemEntryCommon)entry.getValue () ).getItem () == item )
                {
                    return entry.getKey ();
                }
        }
        return null;
    }

    public synchronized String findEntry ( Folder folder )
    {
        for ( Iterator<Map.Entry<String, Entry>> i = _entryMap.entrySet ().iterator (); i.hasNext (); )
        {
            Map.Entry<String, Entry> entry = i.next ();
            if ( entry.getValue () instanceof FolderEntryCommon )
                if ( ( (FolderEntryCommon)entry.getValue () ).getFolder () == folder )
                {
                    return entry.getKey ();
                }
        }
        return null;
    }

    public synchronized boolean remove ( Folder folder )
    {
        for ( Iterator<Map.Entry<String, Entry>> i = _entryMap.entrySet ().iterator (); i.hasNext (); )
        {
            Map.Entry<String, Entry> entry = i.next ();
            if ( entry.getValue () instanceof FolderEntryCommon )
                if ( ( (FolderEntryCommon)entry.getValue () ).getFolder () == folder )
                {
                    i.remove ();
                    folder.removed ();
                    notifyRemove ( entry.getKey () );
                    return true;
                }
        }
        return false;
    }

    public synchronized boolean remove ( DataItem item )
    {
        for ( Iterator<Map.Entry<String, Entry>> i = _entryMap.entrySet ().iterator (); i.hasNext (); )
        {
            Map.Entry<String, Entry> entry = i.next ();
            if ( entry.getValue () instanceof DataItemEntryCommon )
                if ( ( (DataItemEntryCommon)entry.getValue () ).getItem () == item )
                {
                    i.remove ();
                    notifyRemove ( entry.getKey () );
                    return true;
                }
        }
        return false;
    }

    public void subscribe ( Stack<String> path, FolderListener listener, Object tag ) throws NoSuchFolderException
    {
        if ( path.isEmpty () )
        {
            addListener ( listener, tag );
        }
        else
        {
            getFolderEntry ( path.pop () ).subscribe ( path, listener, tag );
        }
    }

    public void unsubscribe ( Stack<String> path, Object tag ) throws NoSuchFolderException
    {
        if ( path.isEmpty () )
        {
            removeListener ( tag );
        }
        else
        {
            getFolderEntry ( path.pop () ).unsubscribe ( path, tag );
        }
    }

    private synchronized void addListener ( FolderListener listener, Object tag )
    {
        _listeners.put ( tag, listener );
        sendCurrentList ( listener, tag );
    }

    private synchronized void removeListener ( Object tag )
    {
        _listeners.remove ( tag );
    }

    public synchronized void clearListeners ()
    {
        _listeners.clear ();
    }

    private synchronized void sendCurrentList ( FolderListener listener, Object tag )
    {
        listener.changed ( tag, new ArrayList<Entry> ( _entryMap.values () ), new LinkedList<String> (), true );
    }

    private synchronized void notifyAdd ( Entry added )
    {
        List<Entry> list = new LinkedList<Entry> ();
        list.add ( added );
        for ( Map.Entry<Object, FolderListener> entry : _listeners.entrySet () )
        {
            entry.getValue ().changed ( entry.getKey (), list, new LinkedList<String> (), false );
        }
    }

    private synchronized void notifyRemove ( String removed )
    {
        List<String> list = new LinkedList<String> ();
        list.add ( removed );
        for ( Map.Entry<Object, FolderListener> entry : _listeners.entrySet () )
        {
            entry.getValue ().changed ( entry.getKey (), new LinkedList<Entry> (), list, false );
        }
    }

    /**
     * Get the number of entries in this folder
     * @return the number of entries in this folder
     */
    public int size ()
    {
        return _entryMap.size ();
    }

    public void added ()
    {

    }

    public void removed ()
    {
        clearListeners ();
    }

    public synchronized void clear ()
    {
        for ( Map.Entry<Object, FolderListener> entry : _listeners.entrySet () )
        {
            entry.getValue ().changed ( entry.getKey (), new LinkedList<Entry> (), new LinkedList<String> (), true );
        }
        _entryMap.clear ();
    }
}
