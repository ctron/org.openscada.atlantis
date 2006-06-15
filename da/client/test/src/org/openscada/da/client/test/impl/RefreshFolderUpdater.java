package org.openscada.da.client.test.impl;

import java.util.Arrays;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.operations.OperationStatus;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.openscada.da.client.test.Openscada_da_client_testPlugin;
import org.openscada.da.core.browser.Entry;

public class RefreshFolderUpdater extends FolderUpdater
{
    private static Logger _log = Logger.getLogger ( RefreshFolderUpdater.class );
    private Job _refreshJob = null;

    public RefreshFolderUpdater ( HiveConnection connection, FolderEntry folder, boolean autoInitialize )
    {
        super ( connection, folder, autoInitialize );
    }
    
    synchronized public void refresh ()
    {
        if ( _refreshJob != null )
            return;
        
        _refreshJob = new Job ( "Refresh..." ) {

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
                    _refreshJob = null;
                }
            }};
            
            _refreshJob.schedule ();
    }

    private void performRefresh ( IProgressMonitor monitor ) throws Exception
    {
        monitor.beginTask ( "Refreshing tree", 1 );

        Entry [] entries = getConnection ().getConnection ().browse ( getFolder ().getLocation ().asArray () );

        Map<String, BrowserEntry> list = convert ( Arrays.asList ( entries ) );

        update ( list );
        
        for ( Map.Entry<String, BrowserEntry> entry : _entries.entrySet () )
        {
            _log.debug ( "Entry: " + entry.getKey () );
        }
        
        monitor.worked ( 1 );
    }

    @Override
    public void dispose ()
    {
        clear ();
    }

    @Override
    public void init ()
    {
        refresh ();
    }
}
