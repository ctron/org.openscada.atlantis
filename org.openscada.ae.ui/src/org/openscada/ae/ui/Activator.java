package org.openscada.ae.ui;

import java.util.List;

import org.eclipse.core.databinding.observable.set.ObservableSet;
import org.eclipse.core.databinding.observable.set.WritableSet;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.openscada.ae.ui.data.ConnectionEntry;
import org.openscada.ae.ui.data.ConnectionEntryBean;
import org.openscada.core.ConnectionInformation;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin
{

    // The plug-in ID
    public static final String PLUGIN_ID = "org.openscada.ae.ui";

    // The shared instance
    private static Activator plugin;

    private List<ConnectionEntry> connections;

    private WritableSet connectionSet;

    /**
     * The constructor
     */
    public Activator ()
    {
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    public void start ( final BundleContext context ) throws Exception
    {
        super.start ( context );
        plugin = this;

        this.connectionSet = new WritableSet ( SWTObservables.getRealm ( getWorkbench ().getDisplay () ) );
        this.connectionSet.add ( new ConnectionEntryBean ( ConnectionInformation.fromURI ( "ae:net://localhost:1302" ) ) );
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
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

    public ObservableSet getConnectionSet ()
    {
        return this.connectionSet;
    }

}
