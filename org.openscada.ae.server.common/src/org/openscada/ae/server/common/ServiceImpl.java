package org.openscada.ae.server.common;

import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.log4j.Logger;
import org.openscada.ae.BrowserEntry;
import org.openscada.ae.BrowserType;
import org.openscada.ae.QueryListener;
import org.openscada.ae.UnknownQueryException;
import org.openscada.ae.server.Service;
import org.openscada.ae.server.Session;
import org.openscada.ae.server.common.akn.AknHandler;
import org.openscada.ae.server.common.condition.ConditionQuery;
import org.openscada.ae.server.common.condition.ConditionQuerySource;
import org.openscada.ae.server.common.event.EventQuery;
import org.openscada.ae.server.common.event.EventQuerySource;
import org.openscada.core.InvalidSessionException;
import org.openscada.core.UnableToCreateSessionException;
import org.openscada.core.Variant;
import org.openscada.core.subscription.SubscriptionManager;
import org.openscada.core.subscription.ValidationException;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

public class ServiceImpl implements Service, ServiceListener
{
    private final static Logger logger = Logger.getLogger ( ServiceImpl.class );

    private final Set<SessionImpl> sessions = new CopyOnWriteArraySet<SessionImpl> ();

    private final SubscriptionManager conditionSubscriptions;

    private final SubscriptionManager eventSubscriptions;

    private final BundleContext context;

    private final Map<String, ConditionQuery> conditionQueryRefs = new HashMap<String, ConditionQuery> ();

    private final Map<String, EventQuery> eventQueryRefs = new HashMap<String, EventQuery> ();

    private final Map<String, BrowserEntry> browserCache = new HashMap<String, BrowserEntry> ();

    private final ServiceTracker aknTracker;

    public ServiceImpl ( final BundleContext context ) throws InvalidSyntaxException
    {
        this.context = context;
        this.conditionSubscriptions = new SubscriptionManager ();
        this.eventSubscriptions = new SubscriptionManager ();

        // create akn handler
        this.aknTracker = new ServiceTracker ( context, AknHandler.class.getName (), null );

        // create query listener
        {
            context.addServiceListener ( this, "(" + Constants.OBJECTCLASS + "=" + ConditionQuery.class.getName () + ")" );
            final ServiceReference[] refs = context.getServiceReferences ( ConditionQuery.class.getName (), null );
            if ( refs != null )
            {
                for ( final ServiceReference ref : refs )
                {
                    checkAddConditionQuery ( ref );
                }
            }
        }

        {
            context.addServiceListener ( this, "(" + Constants.OBJECTCLASS + "=" + EventQuery.class.getName () + ")" );
            final ServiceReference[] refs = context.getServiceReferences ( EventQuery.class.getName (), null );
            if ( refs != null )
            {
                for ( final ServiceReference ref : refs )
                {
                    checkAddEventQuery ( ref );
                }
            }
        }
    }

    public void acknowledge ( final Session session, final String conditionId, final Date aknTimestamp ) throws InvalidSessionException
    {
        final SessionImpl sessionImpl = validateSession ( session );
        logger.debug ( String.format ( "Request akn: %s (%s)", conditionId, aknTimestamp ) );

        final String aknUser = sessionImpl.getCurrentUser ();

        // FIXME: implemention of akn
        for ( final Object o : this.aknTracker.getServices () )
        {
            if ( o instanceof AknHandler )
            {
                if ( ( (AknHandler)o ).acknowledge ( conditionId, aknUser, aknTimestamp ) )
                {
                    break;
                }
            }
        }
    }

    protected void addConditionQuery ( final String id, final ConditionQuery query )
    {
        logger.info ( "Adding new query: " + id );
        this.conditionSubscriptions.setSource ( id, new ConditionQuerySource ( id, query ) );

        final Map<String, Variant> attributes = new HashMap<String, Variant> ();
        final BrowserEntry entry = new BrowserEntry ( id, EnumSet.of ( BrowserType.CONDITIONS ), attributes );

        triggerBrowserChange ( new BrowserEntry[] { entry }, null, true );
    }

    protected void removeConditionQuery ( final String id, final ConditionQuery query )
    {
        logger.info ( "Removing query: " + id );
        this.conditionSubscriptions.setSource ( id, null );

        triggerBrowserChange ( null, new String[] { id }, true );
    }

    protected void addEventQuery ( final String id, final EventQuery query )
    {
        logger.info ( "Adding new event query: " + id );
        this.eventSubscriptions.setSource ( id, new EventQuerySource ( id, query ) );

        final Map<String, Variant> attributes = new HashMap<String, Variant> ();
        final BrowserEntry entry = new BrowserEntry ( id, EnumSet.of ( BrowserType.EVENTS ), attributes );

        triggerBrowserChange ( new BrowserEntry[] { entry }, null, true );
    }

    protected void removeEventQuery ( final String id, final EventQuery query )
    {
        logger.info ( "Removing query: " + id );
        this.eventSubscriptions.setSource ( id, null );

        triggerBrowserChange ( null, new String[] { id }, true );
    }

    protected void triggerBrowserChange ( final BrowserEntry[] entries, final String[] removed, final boolean full )
    {
        synchronized ( this.sessions )
        {
            if ( removed != null )
            {
                for ( final String id : removed )
                {
                    this.browserCache.remove ( id );
                }
            }
            if ( entries != null )
            {
                for ( final BrowserEntry entry : entries )
                {
                    this.browserCache.put ( entry.getId (), entry );
                }
            }
            for ( final SessionImpl session : this.sessions )
            {
                session.dataChanged ( entries, removed, full );
            }
        }
    }

    public void startQuery ( final Session session, final String queryType, final String queryData, final QueryListener listener ) throws InvalidSessionException
    {
        // FIXME: implement
    }

    public void subscribeConditionQuery ( final Session session, final String queryId ) throws InvalidSessionException, UnknownQueryException
    {
        final SessionImpl sessionImpl = validateSession ( session );
        logger.info ( "Request condition subscription: " + queryId );

        try
        {
            this.conditionSubscriptions.subscribe ( queryId, sessionImpl.getConditionListener () );
        }
        catch ( final ValidationException e )
        {
            logger.warn ( "Failed to subscribe", e );
            throw new UnknownQueryException ();
        }
    }

    public void unsubscribeConditionQuery ( final Session session, final String queryId ) throws InvalidSessionException
    {
        final SessionImpl sessionImpl = validateSession ( session );

        logger.info ( "Request condition unsubscription: " + queryId );
        this.conditionSubscriptions.unsubscribe ( queryId, sessionImpl.getConditionListener () );
    }

    public void subscribeEventQuery ( final Session session, final String queryId ) throws InvalidSessionException, UnknownQueryException
    {
        final SessionImpl sessionImpl = validateSession ( session );
        logger.info ( "Request event subscription: " + queryId );

        try
        {
            this.eventSubscriptions.subscribe ( queryId, sessionImpl.getEventListener () );
        }
        catch ( final ValidationException e )
        {
            logger.warn ( "Failed to subscribe", e );
            throw new UnknownQueryException ();
        }
    }

    public void unsubscribeEventQuery ( final Session session, final String queryId ) throws InvalidSessionException
    {
        final SessionImpl sessionImpl = validateSession ( session );

        logger.info ( "Request event unsubscription: " + queryId );
        this.eventSubscriptions.unsubscribe ( queryId, sessionImpl.getEventListener () );
    }

    public void closeSession ( final org.openscada.core.server.Session session ) throws InvalidSessionException
    {
        SessionImpl sessionImpl = null;
        synchronized ( this.sessions )
        {
            if ( this.sessions.remove ( session ) )
            {
                sessionImpl = (SessionImpl)session;

                sessionImpl.dispose ();
            }
        }

        if ( sessionImpl != null )
        {
            this.conditionSubscriptions.unsubscribeAll ( sessionImpl.getConditionListener () );
            // FIXME: unsubscribe event manager
        }
    }

    public org.openscada.core.server.Session createSession ( final Properties properties ) throws UnableToCreateSessionException
    {
        final SessionImpl session = new SessionImpl ( properties.getProperty ( "user", null ) );
        synchronized ( this.sessions )
        {
            this.sessions.add ( session );
            session.dataChanged ( this.browserCache.values ().toArray ( new BrowserEntry[0] ), null, true );
        }
        return session;
    }

    public void start () throws Exception
    {
        logger.info ( "Staring new service" );
        this.aknTracker.open ( true );
    }

    public void stop () throws Exception
    {
        logger.info ( "Stopping service" );
        this.aknTracker.close ();
    }

    protected SessionImpl validateSession ( final Session session ) throws InvalidSessionException
    {
        if ( !this.sessions.contains ( session ) )
        {
            throw new InvalidSessionException ();
        }
        if ( ! ( session instanceof Session ) )
        {
            throw new InvalidSessionException ();
        }
        return (SessionImpl)session;
    }

    public void serviceChanged ( final ServiceEvent event )
    {
        final ServiceReference ref = event.getServiceReference ();

        switch ( event.getType () )
        {
        case ServiceEvent.REGISTERED:
            checkAddConditionQuery ( ref );
            checkAddEventQuery ( ref );
            break;
        case ServiceEvent.UNREGISTERING:
            final String id = getQueryId ( ref );
            final ConditionQuery query = this.conditionQueryRefs.remove ( id );
            if ( query != null )
            {
                removeConditionQuery ( id, query );
                this.context.ungetService ( ref );
            }
            final EventQuery eventQuery = this.eventQueryRefs.remove ( id );
            if ( eventQuery != null )
            {
                removeEventQuery ( id, eventQuery );
                this.context.ungetService ( ref );
            }
            break;
        }
    }

    private void checkAddConditionQuery ( final ServiceReference ref )
    {
        logger.info ( "Checking query: " + ref );

        final Object o = this.context.getService ( ref );
        if ( o instanceof ConditionQuery )
        {
            final ConditionQuery query = (ConditionQuery)o;
            final String id = getQueryId ( ref );
            if ( id != null )
            {
                this.conditionQueryRefs.put ( id, query );
                addConditionQuery ( id, query );
            }
        }
        else
        {
            this.context.ungetService ( ref );
        }
    }

    private void checkAddEventQuery ( final ServiceReference ref )
    {
        logger.info ( "Checking query: " + ref );

        final Object o = this.context.getService ( ref );
        if ( o instanceof EventQuery )
        {
            final EventQuery query = (EventQuery)o;
            final String id = getQueryId ( ref );
            if ( id != null )
            {
                this.eventQueryRefs.put ( id, query );
                addEventQuery ( id, query );
            }
        }
        else
        {
            this.context.ungetService ( ref );
        }
    }

    private String getQueryId ( final ServiceReference ref )
    {
        final Object p = ref.getProperty ( Constants.SERVICE_PID );
        if ( p != null )
        {
            return p.toString ();
        }
        else
        {
            return null;
        }

    }
}
