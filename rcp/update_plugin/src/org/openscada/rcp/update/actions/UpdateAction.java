package org.openscada.rcp.update.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import openscada_rcp_update.Activator;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.operations.OperationStatus;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.PluginVersionIdentifier;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.update.configuration.IConfiguredSite;
import org.eclipse.update.configuration.ILocalSite;
import org.eclipse.update.core.IFeatureReference;
import org.eclipse.update.core.SiteManager;
import org.eclipse.update.core.VersionedIdentifier;
import org.eclipse.update.standalone.UpdateCommand;

public class UpdateAction implements IWorkbenchWindowActionDelegate
{

    private static Logger _log = Logger.getLogger ( UpdateAction.class );
    
    private final static String FEATURE_PREFIX = "org.openscada.";
    
    private Shell _shell;
    private Display _display;
    private IWorkbenchWindow _window;
    private boolean _restart;
    
    public void dispose ()
    {
        if ( _window == null )
        {
            _shell.close ();
            _display.dispose ();
        }
    }

    public void init ( IWorkbenchWindow window )
    {
        _window = window;
        _shell = window.getShell ();
        _display = _shell.getDisplay (); 
    }
    
    private boolean shouldUpdate ( Map<String, PluginVersionIdentifier> featureVersions, String id, PluginVersionIdentifier identifier )
    {
        if ( !id.startsWith ( FEATURE_PREFIX ) )
            return false;
        
        if ( featureVersions.containsKey ( id ) )
            if ( featureVersions.get ( id ).isGreaterOrEqualTo ( identifier ) )
                return false;
        
        return true;
    }
    
    private boolean shouldRestart ( Map<String, PluginVersionIdentifier> featureVersions, String id, PluginVersionIdentifier identifier )
    {
        if ( !id.startsWith ( FEATURE_PREFIX ) )
            return false;
        
        // although we should not get additional features
        if ( !featureVersions.containsKey ( id ) )
            return true;
        
        if ( identifier.isGreaterThan ( featureVersions.get ( id  ) ) )
            return true;
        
        return false;
    }

    public void run ( IAction action )
    {
        if ( _shell == null )
        {
            _display = Display.getCurrent ();
            _shell = new Shell ( _display, SWT.NONE );
        }
        
        final MultiStatus status = new MultiStatus ( Activator.PLUGIN_ID, 90, "Errors during system update", null );
        
        try
        {
            IRunnableWithProgress op = new IRunnableWithProgress () {

                public void run ( IProgressMonitor monitor ) throws InvocationTargetException, InterruptedException
                {
                   try
                   {
                       int work = 0;
                       
                       ILocalSite localSite = SiteManager.getLocalSite ();
                       IConfiguredSite[] sites = localSite.getCurrentConfiguration ().getConfiguredSites ();
                       
                       // get number of features
                       for ( IConfiguredSite site : sites )
                       {
                           work += site.getFeatureReferences ().length;
                       }
                       
                       // set monitor workload
                       monitor.beginTask ( "Please wait while the system is beeing updated", work );
                       
                       Map<String, PluginVersionIdentifier> featureVersions = new HashMap<String, PluginVersionIdentifier>();
                       
                       // iterate over all features
                       for ( IConfiguredSite site : sites )
                       {
                           for ( IFeatureReference featureRef : site.getFeatureReferences () )
                           {
                               VersionedIdentifier identifier = featureRef.getVersionedIdentifier ();
                               String id = identifier.getIdentifier ();
                               
                               _log.debug ( "FeatureRef: " + id + " version: " + identifier.getVersion () );
                               
                               // only update our own features here
                               if ( shouldUpdate ( featureVersions, id, identifier.getVersion() ) )
                               {
                                   featureVersions.put ( id, identifier.getVersion () );
                                   performUpdate ( monitor, id, status );
                               }
                               monitor.worked ( 1 );
                           }
                       }
                       
                       // check if all features are updated
                       loop: for ( IConfiguredSite site : localSite.getCurrentConfiguration ().getConfiguredSites () )
                       {
                           for ( IFeatureReference featureRef : site.getFeatureReferences () )
                           {
                               VersionedIdentifier identifier = featureRef.getVersionedIdentifier ();
                               String id = identifier.getIdentifier ();
                               _log.debug ( "Comapare versions for " + id + " before: " + featureVersions.get ( id ) + " after: " + identifier.getVersion () );
                               if ( shouldRestart ( featureVersions, id, identifier.getVersion () ) )
                               {
                                   _log.info ( "Detected upgrade of feature... trigger restart" );
                                   performRestart ();
                                   break loop;
                               }
                           }
                       }
                   }
                   catch ( Exception e )
                   {
                       _log.error ( "System update failed with error", e );
                       status.add ( new OperationStatus ( OperationStatus.ERROR, Activator.PLUGIN_ID, 93, "System update failed with error", e ) );
                   }
                   finally
                   {
                       monitor.done ();
                   }
                }
            };
            new ProgressMonitorDialog ( _shell ).run ( true, true, op );    
        }
        catch ( InvocationTargetException e )
        {
            _log.error ( "System update failed with error", e );
            status.add ( new OperationStatus ( OperationStatus.ERROR, Activator.PLUGIN_ID, 94, "System update failed with error", e ) );
        }
        catch ( InterruptedException e )
        {
            _log.error ( "System update cancelled" );
            status.add ( new OperationStatus ( OperationStatus.ERROR, Activator.PLUGIN_ID, 95, "System update cancelled", null ) );
        }
        if ( !status.isOK () )
        {
            ErrorDialog dialog = new ErrorDialog ( _shell, "Update", "Error during automatic update", status, OperationStatus.ERROR | OperationStatus.WARNING );
            dialog.open ();
        }
    }

    /**
     * Perform update of a single feature
     * @param monitor status monitor
     * @param id feature id
     * @param status status report object
     */
    private void performUpdate ( IProgressMonitor monitor, String id, MultiStatus status )
    {
        try
        {
            UpdateCommand uc = new UpdateCommand ( id, "false" );
            boolean success = uc.run ( monitor );
            if ( !success )
            {
                _log.info ( NLS.bind ( "Update of Feature {0} failed", id ) );
                status.add ( new OperationStatus ( OperationStatus.INFO, Activator.PLUGIN_ID, 91, NLS.bind ( "Update of Feature {0} failed", id ), null) );
            }
        }
        catch ( Exception e )
        {
            _log.warn ( NLS.bind ( "Update of Feature {0} failed with error", id ), e );
            status.add ( new OperationStatus ( OperationStatus.WARNING, Activator.PLUGIN_ID, 92, NLS.bind ( "Update of Feature {0} failed with error", id ), e) );
        }
    }
    
    public void selectionChanged ( IAction action, ISelection selection )
    {
    }

    /**
     * Check if a restart is requested
     * @return <code>true</code> if a restart is requested
     */
    public boolean isRestart ()
    {
        return _restart;
    }
    
    private void performRestart ()
    {
        _shell.getDisplay ().syncExec ( new Runnable (){

            public void run ()
            {
                _restart = MessageDialog.openQuestion ( _shell, "Update", "System was successfully updated.\nIt is recommended to restart the application.\nRestart now?" );
                if ( _restart )
                {
                    try
                    {
                        PlatformUI.getWorkbench ().restart ();
                    }
                    catch ( Exception e )
                    {
                    }
                }       
            }}  );
    }

}
