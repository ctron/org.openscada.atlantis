package org.openscada.core.ui.connection.data;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.databinding.observable.Observables;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.set.WritableSet;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IActionFilter;
import org.openscada.core.ConnectionInformation;
import org.openscada.core.ui.connection.ConnectionDiscoverer;
import org.openscada.core.ui.connection.ConnectionDiscoveryListener;
import org.openscada.core.ui.connection.ConnectionStore;
import org.osgi.framework.InvalidSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionDiscovererBean implements IAdaptable, ConnectionDiscoveryListener, IActionFilter
{

    private final static Logger logger = LoggerFactory.getLogger ( ConnectionDiscovererBean.class );

    private final String id;

    private final String name;

    private final ConnectionDiscoverer discoverer;

    private final ImageDescriptor imageDescriptor;

    private final WritableSet knownConnections = new WritableSet ();

    private final Map<ConnectionInformation, ConnectionHolder> connections = new HashMap<ConnectionInformation, ConnectionHolder> ();

    public ConnectionDiscovererBean ( final String id, final String name, final ImageDescriptor imageDescriptor, final ConnectionDiscoverer discoverer )
    {
        this.id = id;
        this.name = name;
        this.discoverer = discoverer;
        this.imageDescriptor = imageDescriptor;

        register ();
    }

    private void register ()
    {
        this.discoverer.addConnectionListener ( this );
    }

    public IObservableSet getKnownConnections ()
    {
        return Observables.proxyObservableSet ( this.knownConnections );
    }

    public ImageDescriptor getImageDescriptor ()
    {
        return this.imageDescriptor;
    }

    public String getName ()
    {
        return this.name;
    }

    public String getId ()
    {
        return this.id;
    }

    @Override
    public String toString ()
    {
        return this.id;
    }

    @SuppressWarnings ( "unchecked" )
    public Object getAdapter ( final Class adapter )
    {
        logger.info ( "Get adaper: {}", adapter );
        if ( adapter == ConnectionDiscoverer.class )
        {
            return this.discoverer;
        }
        if ( adapter == ConnectionStore.class && this.discoverer instanceof ConnectionStore )
        {
            return this.discoverer;
        }
        if ( this.discoverer instanceof IAdaptable )
        {
            return ( (IAdaptable)this.discoverer ).getAdapter ( adapter );
        }
        return null;
    }

    public void discoveryUpdate ( final ConnectionInformation[] added, final ConnectionInformation[] removed )
    {
        this.knownConnections.getRealm ().asyncExec ( new Runnable () {

            public void run ()
            {
                ConnectionDiscovererBean.this.handleDiscoveryUpdate ( added, removed );
            }
        } );

    }

    protected void handleDiscoveryUpdate ( final ConnectionInformation[] added, final ConnectionInformation[] removed )
    {

        if ( removed != null )
        {
            for ( final ConnectionInformation info : removed )
            {
                final ConnectionHolder holder = this.connections.get ( info );
                if ( holder != null )
                {
                    this.knownConnections.remove ( holder );
                    holder.dispose ();
                }
            }
        }
        if ( added != null )
        {
            for ( final ConnectionInformation info : added )
            {
                try
                {
                    final ConnectionHolder holder = new ConnectionHolder ( this, info );
                    this.knownConnections.add ( holder );
                    this.connections.put ( info, holder );
                }
                catch ( final InvalidSyntaxException e )
                {
                    logger.warn ( "Failed to create connection holder: {}", info, e );
                }

            }
        }
    }

    public ConnectionStore getStore ()
    {
        if ( this.discoverer instanceof ConnectionStore )
        {
            return (ConnectionStore)this.discoverer;
        }
        return null;
    }

    public boolean isStore ()
    {
        return this.discoverer instanceof ConnectionStore;
    }

    public boolean testAttribute ( final Object target, final String name, final String value )
    {
        if ( "isStore".equals ( name ) )
        {
            return isStore () == Boolean.valueOf ( value );
        }
        return false;
    }
}
