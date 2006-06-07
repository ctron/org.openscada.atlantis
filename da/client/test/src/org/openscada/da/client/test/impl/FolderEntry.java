package org.openscada.da.client.test.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.operations.OperationStatus;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.openscada.da.client.net.Connection;
import org.openscada.da.client.test.Openscada_da_client_testPlugin;
import org.openscada.da.core.browser.Entry;
import org.openscada.da.core.data.Variant;

public class FolderEntry extends BrowserEntry
{
    private static Logger _log = Logger.getLogger ( FolderEntry.class );
    
    private boolean _needRefresh = true;
    private BrowserEntry[] _entries = null;

    public FolderEntry ( String name, Map<String, Variant> attributes, FolderEntry parent, HiveConnection connection )
    {
        super ( name, attributes, connection, parent );
    }

    public void refresh ()
    {
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

    private void performRefresh ( IProgressMonitor monitor ) throws Exception
    {
        monitor.beginTask ( "Refreshing tree", 1 );
        Entry [] entries = getConnection ().getConnection ().browse ( getPath() );

        synchronized ( this )
        {
            _entries = null;
        }
        
        List<BrowserEntry> list = new ArrayList<BrowserEntry> ();
        int i = 0;
        for ( Entry entry : entries )
        {
            if ( entry instanceof org.openscada.da.core.browser.FolderEntry )
            {
                list.add ( new FolderEntry ( entry.getName (), entry.getAttributes (), this, getConnection () ) ); 
            }
            else if ( entry instanceof org.openscada.da.core.browser.DataItemEntry )
            {
                org.openscada.da.core.browser.DataItemEntry itemEntry = (org.openscada.da.core.browser.DataItemEntry)entry;
                list.add ( new DataItemEntry ( entry.getName(), entry.getAttributes (), this, getConnection (), itemEntry.getId (), itemEntry.getIODirections () ) ); 
            }
            else
                _log.warn ( "Unknown entry type in tree: " + entry.getClass ().getName () );
            i++;
        }

        synchronized ( this )
        {
            _entries = list.toArray ( new BrowserEntry [ list.size () ] );
        }
        
        for ( BrowserEntry entry : _entries )
        {
            _log.debug ( "Entry: " + entry.getName () );
        }
        
        monitor.worked ( 1 );
        
        notifyChange ( this );
    }


    private String [] getPath ()
    {
        List<String> path = new LinkedList<String> ();
        
        BrowserEntry current = this;
        while ( current != null )
        {
            // only add name if folder is not root folder
            if ( current.getParent () != null )
                path.add ( current.getName () );
            current = current.getParent ();
        }
        
        return path.toArray ( new String[0] );
    }

    synchronized public boolean hasChildren ()
    {
        if ( !getConnection ().getConnection ().getState ().equals ( Connection.State.BOUND ) )
            return false;
        
        checkRefresh ();
        
        if ( _entries != null )
        {
            return _entries.length > 0;
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
            return _entries;
        else
            return new BrowserEntry[0];
    }
    
    private void notifyChange ( FolderEntry originEntry )
    {
        setChanged ();
        notifyObservers ( originEntry );
        
        if ( getParent () != null )
            getParent ().notifyChange ( originEntry );            
    }
    
    synchronized private void checkRefresh ()
    {
        if ( _needRefresh )
        {
            refresh ();
            _needRefresh = false;
        }
    }
    
    synchronized public void clear ()
    {
        _needRefresh = true;
        _entries = null;
        
        notifyChange ( this );
    }
    
    
}
