package org.openscada.ae.rcp.LocalTestServer;

import java.io.IOException;

import org.eclipse.core.commands.operations.OperationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.openscada.ae.storage.net.Exporter;
import org.openscada.core.ConnectionInformation;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin
{

    // The plug-in ID
    public static final String PLUGIN_ID = "org.openscada.ae.rcp.LocalTestServer";

    // The shared instance
    private static Activator plugin;

    private Exporter exporter = null;

    /**
     * The constructor
     */
    public Activator ()
    {
        plugin = this;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start ( final BundleContext context ) throws Exception
    {
        super.start ( context );
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop ( final BundleContext context ) throws Exception
    {
        plugin = null;
        super.stop ( context );
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static Activator getDefault ()
    {
        return plugin;
    }

    /**
     * Returns an image descriptor for the image file at the given
     * plug-in relative path
     *
     * @param path the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor ( final String path )
    {
        return imageDescriptorFromPlugin ( PLUGIN_ID, path );
    }

    public void startLocalServer () throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException, AlreadyStartedException
    {
        synchronized ( this )
        {
            if ( this.exporter != null )
            {
                throw new AlreadyStartedException ();
            }

            try
            {
                this.exporter = new Exporter ( org.openscada.ae.storage.test.Storage.class.getName (), ConnectionInformation.fromURI ( "ae:net://0.0.0.0:1302" ) );
            }
            catch ( final Throwable e )
            {
                notifyServerError ( e );
            }
        }
    }

    private void notifyServerError ( final Throwable t )
    {
        final Shell shell = getWorkbench ().getActiveWorkbenchWindow ().getShell ();
        final IStatus status = new OperationStatus ( IStatus.ERROR, PLUGIN_ID, 0, "Server execution failed", t );

        if ( !shell.isDisposed () )
        {
            shell.getDisplay ().asyncExec ( new Runnable () {

                public void run ()
                {
                    if ( !shell.isDisposed () )
                    {
                        ErrorDialog.openError ( shell, null, "Server execution failed", status );
                    }
                }
            } );
        }
    }
}
