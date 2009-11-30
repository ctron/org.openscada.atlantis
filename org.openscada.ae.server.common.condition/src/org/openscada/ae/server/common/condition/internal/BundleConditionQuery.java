package org.openscada.ae.server.common.condition.internal;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openscada.ae.ConditionStatusInformation;
import org.openscada.ae.monitor.ConditionListener;
import org.openscada.ae.monitor.ConditionService;
import org.openscada.ae.server.common.condition.ConditionQuery;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

public class BundleConditionQuery extends ConditionQuery implements ServiceListener, ConditionListener
{
    private final static Logger logger = Logger.getLogger ( BundleConditionQuery.class );

    private final BundleContext context;

    private final Map<ServiceReference, ConditionService> services = new HashMap<ServiceReference, ConditionService> ();

    public BundleConditionQuery ( final BundleContext context, final String filter ) throws InvalidSyntaxException
    {
        this.context = context;

        String serviceFilter;
        if ( filter == null )
        {
            serviceFilter = "(" + Constants.OBJECTCLASS + "=" + ConditionService.class.getName () + ")";
        }
        else
        {
            serviceFilter = "(&(" + Constants.OBJECTCLASS + "=" + ConditionService.class.getName () + ")" + filter + ")";
        }
        synchronized ( this )
        {
            this.context.addServiceListener ( this, serviceFilter );
            checkAddInitial ( filter );
        }
    }

    public BundleConditionQuery ( final BundleContext context ) throws InvalidSyntaxException
    {
        this ( context, null );
    }

    private void checkAddInitial ( final String filter ) throws InvalidSyntaxException
    {
        ServiceReference[] refs = this.context.getServiceReferences ( ConditionService.class.getName (), filter );
        if ( refs != null )
        {
            for ( ServiceReference ref : refs )
            {
                checkAdd ( ref );
            }
        }
    }

    public void dispose ()
    {
        synchronized ( this )
        {
            this.context.removeServiceListener ( this );
            for ( Map.Entry<ServiceReference, ConditionService> entry : this.services.entrySet () )
            {
                entry.getValue ().removeStatusListener ( this );
                this.context.ungetService ( entry.getKey () );
            }
            this.services.clear ();
        }
    }

    public void serviceChanged ( final ServiceEvent event )
    {
        switch ( event.getType () )
        {
        case ServiceEvent.REGISTERED:
            checkAdd ( event.getServiceReference () );
            break;
        case ServiceEvent.UNREGISTERING:
            checkRemove ( event.getServiceReference () );
            break;
        }
    }

    private void checkRemove ( final ServiceReference serviceReference )
    {
        synchronized ( this )
        {
            ConditionService service = this.services.remove ( serviceReference );
            if ( service != null )
            {
                service.removeStatusListener ( this );
                this.context.ungetService ( serviceReference );
                updateData ( null, new String[] { service.getId () } );
            }
        }
    }

    private void checkAdd ( final ServiceReference serviceReference )
    {
        logger.debug ( "Checking reference: " + serviceReference );
        if ( !serviceReference.isAssignableTo ( this.context.getBundle (), ConditionService.class.getName () ) )
        {
            logger.info ( "Not assignable" );
            return;
        }

        ConditionService service = (ConditionService)this.context.getService ( serviceReference );
        synchronized ( this )
        {
            logger.debug ( "Adding to list" );
            this.services.put ( serviceReference, service );
            service.addStatusListener ( this );
        }
    }

    public void statusChanged ( final ConditionStatusInformation status )
    {
        if ( logger.isDebugEnabled () )
        {
            logger.debug ( "Status changed: " + status );
        }
        updateData ( new ConditionStatusInformation[] { status }, null );
    }
}
