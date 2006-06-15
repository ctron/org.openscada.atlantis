package org.openscada.da.client.test.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.operations.OperationStatus;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.openscada.da.client.net.Connection;
import org.openscada.da.client.net.FolderWatcher;
import org.openscada.da.client.test.Openscada_da_client_testPlugin;
import org.openscada.da.core.browser.Entry;
import org.openscada.da.core.data.Variant;

public class FolderEntry extends BrowserEntry implements Observer
{
    private static Logger _log = Logger.getLogger ( FolderEntry.class );
    
    private boolean _needRefresh = true;
    private boolean _subscribed = false;
    private boolean _shouldSubscribe = false;
    private Map<String, BrowserEntry> _entries = null;
    
    private FolderWatcher _watcher = null;

    public FolderEntry ( String name, Map<String, Variant> attributes, FolderEntry parent, HiveConnection connection, boolean shouldSubscribe )
    {
        super ( name, attributes, connection, parent );
        
        _watcher = new FolderWatcher ( getPath() );
        _watcher.addObserver ( this );

        _shouldSubscribe = shouldSubscribe;
    }
    
    @Override
    protected void finalize () throws Throwable
    {
        dispose ();
        super.finalize ();
    }
    
    public void dispose ()
    {
        unsubscribe ();
    }

    synchronized public void refresh ()
    {
        if ( _subscribed )
            return;
        
        new Job ( "Refresh..." ) {

            @Override
            protected IStatus run ( IProgressMonitor monitor )
            {
                try
                {
                    performRefresh ( monitor );
                    return new OperationStatus ( OperationStatus.OK, Openscada_da_client_testPlugin.PLUGIN_ID, 0, "", null );
                }
                catch ( Exception e )
                {
                    return new OperationStatus ( OperationStatus.ERROR, Openscada_da_client_testPlugin.PLUGIN_ID, 0, "Failed to refresh", e );
                }
                finally
                {
                    monitor.done ();
                }
            }}.schedule ();


    }
    
    private Map<String, BrowserEntry> convert ( Collection<Entry> entries )
    {
        Map<String, BrowserEntry> list = new HashMap<String, BrowserEntry> ();
        int i = 0;
        for ( Entry entry : entries )
        {
            if ( entry instanceof org.openscada.da.core.browser.FolderEntry )
            {
                FolderEntry folder = new FolderEntry ( entry.getName (), entry.getAttributes (), this, getConnection (), true );
                list.put ( entry.getName (), folder ); 
            }
            else if ( entry instanceof org.openscada.da.core.browser.DataItemEntry )
            {
                org.openscada.da.core.browser.DataItemEntry itemEntry = (org.openscada.da.core.browser.DataItemEntry)entry;
                list.put ( entry.getName (), new DataItemEntry ( entry.getName(), entry.getAttributes (), this, getConnection (), itemEntry.getId (), itemEntry.getIODirections () ) ); 
            }
            else
                _log.warn ( "Unknown entry type in tree: " + entry.getClass ().getName () );
            i++;
        }
        return list;
    }

    private void performRefresh ( IProgressMonitor monitor ) throws Exception
    {
        monitor.beginTask ( "Refreshing tree", 1 );

        Entry [] entries = getConnection ().getConnection ().browse ( getPath() );

        Map<String, BrowserEntry> list = convert ( Arrays.asList ( entries ) );

        synchronized ( this )
        {
            // if we have been subscribed .. drop it
            if ( _subscribed )
                return;
            
            update ( list );
        }
        
        for ( Map.Entry<String, BrowserEntry> entry : _entries.entrySet () )
        {
            _log.debug ( "Entry: " + entry.getKey () );
        }
        
        monitor.worked ( 1 );
    }


    private String [] getPath ()
    {
        List<String> path = new LinkedList<String> ();
        
        BrowserEntry current = this;
        while ( current != null )
        {
            // only add name if folder is not root folder
            if ( current.getParent () != null )
                path.add ( 0, current.getName () );
            current = current.getParent ();
        }
        
        return path.toArray ( new String[path.size()] );
    }

    synchronized public boolean hasChildren ()
    {
        if ( !getConnection ().getConnection ().getState ().equals ( Connection.State.BOUND ) )
            return false;
        
        checkRefresh ();
        
        if ( _entries != null )
        {
            return _entries.size () > 0;
        }
        else
        {
            return false;
        }

    }
    
    synchronized public BrowserEntry [] getEntries ()
    {
        if ( !getConnection ().getConnection ().getState ().equals ( Connection.State.BOUND ) )
            return new BrowserEntry[0];
        
        checkRefresh ();
        
        if ( _entries != null )
            return _entries.values ().toArray ( new BrowserEntry[_entries.size()] );
        else
            return new BrowserEntry[0];
    }
    
    private void notifyChange ( FolderEntry originEntry )
    {
        if ( getParent () != null )
        {
            getParent ().notifyChange ( originEntry );
        }
        else
        {
            setChanged ();
            notifyObservers ( originEntry );
        }
    }
    
    synchronized private void checkRefresh ()
    {
        if ( _shouldSubscribe )
        {
            subscribe ();
        }
        else
        {
            if ( _needRefresh )
            {
                refresh ();
                _needRefresh = false;
            }
        }
    }
    
    synchronized public void clear ()
    {
        if ( _entries != null )
        {
            for ( Map.Entry<String, BrowserEntry> entry : _entries.entrySet () )
            {
                if ( entry instanceof FolderEntry )
                {
                    FolderEntry folderEntry = (FolderEntry)entry;
                    folderEntry.unsubscribe ();
                }
            }
            notifyChange ( this );
        }
        _needRefresh = true;
        _entries = null;
    }

    // update from subcsription
    public void update ( Observable o, Object arg )
    {
        if ( _watcher.equals ( o ) )
        {
            synchronized ( this )
            {
                if ( _subscribed )
                {
                    updateFromWatcher ();
                }
            }
        }
    }
    
    private void update ( Map<String, BrowserEntry> newEntries )
    {
        synchronized ( this )
        {
            // if we don't have content it is quite easy
            if ( _entries == null )
            {
                _entries = newEntries;
                notifyChange ( this );
                return;
            }
            
            // not do the merge
            for ( Map.Entry<String, BrowserEntry> entry : _entries.entrySet () )
            {
                if ( !newEntries.containsKey ( entry.getKey () ) )
                {
                    // entry was available in old list but is now gone
                    if ( entry.getValue () instanceof FolderEntry )
                    {
                        ((FolderEntry)entry.getValue ()).unsubscribe (); 
                    }
                }
            }
            
            // new entries are already subscribed
            _entries = newEntries;
        }
        notifyChange ( this );
    }
    
    private void updateFromWatcher ()
    {
        Map<String, BrowserEntry> entries = convert ( _watcher.getList () );
        update ( entries );
    }
    
    synchronized public void subscribe ()
    {
        if ( !_subscribed )
        {
            _needRefresh = false;
            clear ();
            getConnection ().getConnection ().addFolderWatcher ( _watcher );
            _subscribed = true;
        }
    }
    
    synchronized public void unsubscribe ()
    {
        if ( _subscribed )
        {
            _log.debug ( "Unsubscribing from folder: " + _watcher.getLocation ().toString () );
            getConnection ().getConnection ().removeFolderWatcher ( _watcher );
            clear ();
            _subscribed = false;
        }
    }

    public boolean isSubscribed ()
    {
        return _subscribed;
    }

    public void setSubscribed ( boolean subscribed )
    {
        if ( subscribed )
            subscribe ();
        else
            unsubscribe ();    
    }
    
}
