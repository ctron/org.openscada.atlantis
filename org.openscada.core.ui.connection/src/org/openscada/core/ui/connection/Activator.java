package org.openscada.core.ui.connection;

import org.eclipse.core.databinding.observable.Observables;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.set.ObservableSet;
import org.eclipse.core.databinding.observable.set.WritableSet;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.openscada.core.ui.connection.data.ConnectionDiscovererAdapterFactory;
import org.openscada.core.ui.connection.data.ConnectionDiscovererBean;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin
{

    // The plug-in ID
    public static final String PLUGIN_ID = "org.openscada.core.ui.connection";

    private static final String EXTP_CONNECTON_DISCOVERER = "org.openscada.core.ui.connection.discoverer";

    private static final String ELE_DISCOVERER = "discoverer";

    // The shared instance
    private static Activator plugin;

    private ObservableSet discoverers;

    private final ConnectionDiscovererAdapterFactory adaperFactory;

    public static final Root ROOT = new Root ();

    /**
     * The constructor
     */
    public Activator ()
    {
        this.adaperFactory = new ConnectionDiscovererAdapterFactory ();
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    public void start ( final BundleContext context ) throws Exception
    {
        super.start ( context );
        plugin = this;
        Platform.getAdapterManager ().registerAdapters ( this.adaperFactory, ConnectionDiscovererBean.class );
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop ( final BundleContext context ) throws Exception
    {
        Platform.getAdapterManager ().unregisterAdapters ( this.adaperFactory );

        plugin = null;
        super.stop ( context );
    }

    public IObservableSet getDiscovererSet ()
    {
        synchronized ( this )
        {
            if ( this.discoverers == null )
            {
                this.discoverers = createDiscoverers ();
            }
            return Observables.proxyObservableSet ( this.discoverers );
        }
    }

    private ObservableSet createDiscoverers ()
    {
        final WritableSet result = new WritableSet ( SWTObservables.getRealm ( this.getWorkbench ().getDisplay () ) );

        for ( final IConfigurationElement ele : Platform.getExtensionRegistry ().getConfigurationElementsFor ( EXTP_CONNECTON_DISCOVERER ) )
        {
            if ( ELE_DISCOVERER.equals ( ele.getName () ) )
            {
                final String id = ele.getAttribute ( "id" );
                String name = ele.getAttribute ( "name" );
                if ( name == null )
                {
                    name = id;
                }

                final String icon = ele.getAttribute ( "icon" );
                ImageDescriptor imageDescriptor = null;
                if ( icon != null )
                {
                    imageDescriptor = Activator.imageDescriptorFromPlugin ( ele.getContributor ().getName (), icon );
                }

                // create
                try
                {
                    final ConnectionDiscovererBean bean = new ConnectionDiscovererBean ( id, name, imageDescriptor, (ConnectionDiscoverer)ele.createExecutableExtension ( "class" ) );
                    result.add ( bean );
                }
                catch ( final CoreException e )
                {
                    getLog ().log ( e.getStatus () );
                }
            }
        }

        return result;
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

}
