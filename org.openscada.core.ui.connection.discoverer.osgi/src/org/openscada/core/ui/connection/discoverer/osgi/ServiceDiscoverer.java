package org.openscada.core.ui.connection.discoverer.osgi;

import java.util.HashSet;
import java.util.Set;

import org.openscada.core.ConnectionInformation;
import org.openscada.core.connection.provider.ConnectionService;
import org.openscada.core.ui.connection.AbstractConnectionDiscoverer;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceDiscoverer extends AbstractConnectionDiscoverer implements ServiceListener
{

    private final static Logger logger = LoggerFactory.getLogger ( ServiceDiscoverer.class );

    private final BundleContext context;

    private final Set<ServiceReference> references = new HashSet<ServiceReference> ();

    public ServiceDiscoverer ()
    {
        this.context = Activator.getDefault ().getBundle ().getBundleContext ();

        setup ();
    }

    private synchronized void setup ()
    {
        try
        {
            this.context.addServiceListener ( this, String.format ( "(%s=%s)", Constants.OBJECTCLASS, ConnectionService.class.getName () ) );
            final ServiceReference[] refs = this.context.getAllServiceReferences ( ConnectionService.class.getName (), null );
            if ( refs != null )
            {
                for ( final ServiceReference ref : refs )
                {
                    addReference ( ref );
                }
            }
        }
        catch ( final InvalidSyntaxException e )
        {
            logger.warn ( "Invalid syntax when setting up filter", e );
            return;
        }

    }

    private ConnectionInformation fromReference ( final ServiceReference ref )
    {
        final Object o = ref.getProperty ( ConnectionService.CONNECTION_URI );
        if ( o instanceof String )
        {
            final String uri = (String)o;
            return ConnectionInformation.fromURI ( uri );
        }
        return null;
    }

    private void addReference ( final ServiceReference ref )
    {
        logger.info ( "Adding service: {}", ref );

        if ( this.references.add ( ref ) )
        {
            update ();
        }
    }

    private void removeReference ( final ServiceReference ref )
    {
        logger.info ( "Removing service: {}", ref );

        if ( this.references.remove ( ref ) )
        {
            update ();
        }
    }

    /**
     * Gather all ConnectionInformation objects and set them as connections
     */
    private void update ()
    {
        final Set<ConnectionInformation> infos = new HashSet<ConnectionInformation> ();
        for ( final ServiceReference ref : this.references )
        {
            final ConnectionInformation ci = fromReference ( ref );
            if ( ci != null )
            {
                infos.add ( ci );
            }
        }
        setConnections ( infos );
    }

    @Override
    public void dispose ()
    {
        super.dispose ();
        this.context.removeServiceListener ( this );
    }

    public synchronized void serviceChanged ( final ServiceEvent event )
    {
        switch ( event.getType () )
        {
        case ServiceEvent.REGISTERED:
            addReference ( event.getServiceReference () );
            break;
        case ServiceEvent.MODIFIED:
            update ();
            break;
        case ServiceEvent.UNREGISTERING:
            removeReference ( event.getServiceReference () );
            break;
        }
    }

}
