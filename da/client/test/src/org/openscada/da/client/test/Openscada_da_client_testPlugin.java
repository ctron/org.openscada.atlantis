package org.openscada.da.client.test;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.*;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.openscada.da.client.test.config.HiveConnectionInformation;
import org.openscada.da.client.test.impl.HiveConnection;
import org.openscada.da.client.test.impl.HiveRepository;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class Openscada_da_client_testPlugin extends AbstractUIPlugin {

	//The shared instance.
	private static Openscada_da_client_testPlugin plugin = null;
    
	/**
	 * The constructor.
	 */
	public Openscada_da_client_testPlugin() {
		plugin = this;
	}
    
    @Override
    protected void initializeImageRegistry ( ImageRegistry reg )
    {
        super.initializeImageRegistry ( reg );
        
        getImageRegistry().put ( ISharedImages.IMG_HIVE_CONNECTION, getImageDescriptor ( "icons/stock_channel.png" ) );
        getImageRegistry().put ( ISharedImages.IMG_HIVE_CONNECTED, getImageDescriptor ( "icons/stock_connect.png" ) );
        getImageRegistry().put ( ISharedImages.IMG_HIVE_DISCONNECTED, getImageDescriptor ( "icons/stock_disconnect.png" ) );
        
        getImageRegistry().put ( ISharedImages.IMG_HIVE_ITEM, getImageDescriptor ( "icons/stock_dataitem.png" ) );
    }

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static Openscada_da_client_testPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("org.openscada.da.client.test", path);
	}
    
    public static String getId()
    {
        return getDefault().getBundle().getSymbolicName();
    }
    
    public static void logError ( int code, String msg, Throwable ex )
    {
        getDefault().getLog().log(new Status(IStatus.ERROR, getId(), code, msg, ex));
    }
    
    private static HiveRepository _repository = null;
    public static HiveRepository getRepository ()
    {
        if ( _repository == null )
        {
            _repository = new HiveRepository();
            
            IPath hives = getRepostoryFile();
            if ( hives.toFile().canRead() )
                _repository.load(hives);
            else
            {
                HiveConnectionInformation connection = new HiveConnectionInformation();
                connection.setHost ( "localhost" );
                connection.setPort ( 1202 );
                _repository.getConnections().add(new HiveConnection(connection));
                _repository.save(hives);
            }
            
            
        }
        return _repository;
    }
    public static IPath getRepostoryFile ()
    {
        return getDefault().getStateLocation().append("hives.xml");
    }
}
