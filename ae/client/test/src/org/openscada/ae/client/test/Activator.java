package org.openscada.ae.client.test;

import org.eclipse.core.commands.operations.OperationStatus;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.openscada.ae.client.test.impl.StorageConnection;
import org.openscada.ae.client.test.impl.StorageConnectionInformation;
import org.openscada.ae.client.test.impl.StorageRepository;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.openscada.ae.client.test";

	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
    
    public static String getId()
    {
        return getDefault().getBundle().getSymbolicName();
    }
    
    public static void logError ( int code, String msg, Throwable ex )
    {
        getDefault().getLog().log(new Status(IStatus.ERROR, getId(), code, msg, ex));
    }
    

    public void notifyError ( final String message, final Throwable error )
    {
        
        final Display display = getWorkbench ().getDisplay ();
        
        if ( !display.isDisposed () )
        {
            display.asyncExec ( new Runnable() {

                public void run ()
                {
                    Shell shell = getWorkbench ().getActiveWorkbenchWindow ().getShell ();
                    if ( !shell.isDisposed () )
                    {
                        IStatus status = new OperationStatus ( OperationStatus.ERROR, PLUGIN_ID, 0, error.getMessage (), error );
                        ErrorDialog.openError ( shell, null, message, status );
                    }
                }} );
        }
    }
    
    @Override
    protected void initializeImageRegistry ( ImageRegistry reg )
    {
        super.initializeImageRegistry ( reg );
        
        getImageRegistry().put ( ISharedImages.IMG_HIVE_CONNECTION, getImageDescriptor ( "icons/stock_channel.png" ) );
        getImageRegistry().put ( ISharedImages.IMG_HIVE_CONNECTED, getImageDescriptor ( "icons/stock_connect.png" ) );
        getImageRegistry().put ( ISharedImages.IMG_HIVE_DISCONNECTED, getImageDescriptor ( "icons/stock_disconnect.png" ) );
    }

    private static StorageRepository _repository = null;
    public static StorageRepository getRepository ()
    {
        if ( _repository == null )
        {
            _repository = new StorageRepository ();
            
            IPath storages = getRepostoryFile ();
            if ( storages.toFile ().canRead () )
                _repository.load ( storages );
            else
            {
                StorageConnectionInformation connection = new StorageConnectionInformation();
                connection.setHost ( "localhost" );
                connection.setPort ( 1302 );
                _repository.getConnections ().add(new StorageConnection ( connection ));
                _repository.save ( storages );
            }
            
            
        }
        return _repository;
    }
    
    public static IPath getRepostoryFile ()
    {
        return getDefault ().getStateLocation ().append ( "storages.xml" );
    }
    
}
