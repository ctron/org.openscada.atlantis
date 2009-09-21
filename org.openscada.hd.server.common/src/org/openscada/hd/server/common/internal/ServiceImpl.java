package org.openscada.hd.server.common.internal;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.openscada.core.InvalidSessionException;
import org.openscada.core.UnableToCreateSessionException;
import org.openscada.hd.HistoricalItemInformation;
import org.openscada.hd.InvalidItemException;
import org.openscada.hd.Query;
import org.openscada.hd.QueryListener;
import org.openscada.hd.QueryParameters;
import org.openscada.hd.server.Service;
import org.openscada.hd.server.Session;
import org.openscada.hd.server.common.HistoricalItem;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceImpl implements Service, ServiceTrackerCustomizer
{

    private final static Logger logger = LoggerFactory.getLogger ( ServiceImpl.class );

    private final ReadWriteLock sessionLock = new ReentrantReadWriteLock ();

    private final Set<SessionImpl> sessions = new HashSet<SessionImpl> ();

    private final BundleContext context;

    private final ServiceTracker tracker;

    private final Map<String, HistoricalItem> items = new HashMap<String, HistoricalItem> ();

    private final Set<HistoricalItemInformation> itemInformations = new HashSet<HistoricalItemInformation> ();

    public ServiceImpl ( final BundleContext context ) throws InvalidSyntaxException
    {
        this.context = context;
        this.tracker = new ServiceTracker ( this.context, HistoricalItem.class.getName (), this );
    }

    public void closeSession ( final org.openscada.core.server.Session session ) throws InvalidSessionException
    {
        SessionImpl sessionImpl = null;

        try
        {
            this.sessionLock.writeLock ().lock ();
            if ( this.sessions.remove ( session ) )
            {
                sessionImpl = (SessionImpl)session;

                sessionImpl.dispose ();
            }
        }
        finally
        {
            this.sessionLock.writeLock ().unlock ();
        }

        if ( sessionImpl != null )
        {
        }
    }

    public org.openscada.core.server.Session createSession ( final Properties properties ) throws UnableToCreateSessionException
    {
        final SessionImpl session = new SessionImpl ( properties.getProperty ( "user", null ) );
        try
        {
            this.sessionLock.writeLock ().lock ();
            synchronized ( this )
            {
                // bad locking strategy ...
                this.sessions.add ( session );
                logger.info ( "Sending known items: {}", this.itemInformations.size () );
                session.listChanged ( this.itemInformations, null, true );
            }
        }
        finally
        {
            this.sessionLock.writeLock ().unlock ();
        }
        return session;
    }

    public void start () throws Exception
    {
        logger.info ( "Staring new service" );
        this.tracker.open ();
    }

    public void stop () throws Exception
    {
        logger.info ( "Stopping service" );
        this.tracker.close ();
    }

    protected SessionImpl validateSession ( final Session session ) throws InvalidSessionException
    {
        if ( ! ( session instanceof Session ) )
        {
            throw new InvalidSessionException ();
        }

        try
        {
            this.sessionLock.readLock ().lock ();
            if ( !this.sessions.contains ( session ) )
            {
                throw new InvalidSessionException ();
            }
        }
        finally
        {
            this.sessionLock.readLock ().unlock ();
        }

        return (SessionImpl)session;
    }

    public Query createQuery ( final Session session, final String itemId, final QueryParameters parameters, final QueryListener listener ) throws InvalidSessionException, InvalidItemException
    {
        final SessionImpl sessionImpl = validateSession ( session );
        synchronized ( this )
        {
            final HistoricalItem item = this.items.get ( itemId );
            if ( item == null )
            {
                throw new InvalidItemException ( itemId );
            }
            final QueryImpl query = new QueryImpl ( sessionImpl, listener );
            query.setQuery ( item.createQuery ( parameters, query ) );

            return query;
        }
    }

    protected void fireListChanged ( final Set<HistoricalItemInformation> addedOrModified, final Set<String> removed, final boolean full )
    {
        try
        {
            this.sessionLock.readLock ().lock ();
            for ( final SessionImpl session : this.sessions )
            {
                session.listChanged ( addedOrModified, removed, full );
            }
        }
        finally
        {
            this.sessionLock.readLock ().unlock ();
        }
    }

    public Object addingService ( final ServiceReference reference )
    {
        logger.info ( "Adding service: {}", reference );
        final HistoricalItem item = (HistoricalItem)this.context.getService ( reference );
        final HistoricalItemInformation info = item.getInformation ();

        synchronized ( this )
        {
            if ( this.items.containsKey ( info.getId () ) )
            {
                this.context.ungetService ( reference );
                return null;
            }
            else
            {
                this.items.put ( info.getId (), item );
                this.itemInformations.add ( info );
                fireListChanged ( new HashSet<HistoricalItemInformation> ( Arrays.asList ( info ) ), null, false );
                return item;
            }
        }
    }

    public void modifiedService ( final ServiceReference reference, final Object service )
    {
    }

    public void removedService ( final ServiceReference reference, final Object service )
    {
        final String itemId = (String)reference.getProperty ( "itemId" );

        synchronized ( this )
        {
            final HistoricalItem item = this.items.remove ( itemId );
            if ( item != null )
            {
                this.itemInformations.remove ( item.getInformation () );
                fireListChanged ( null, new HashSet<String> ( Arrays.asList ( itemId ) ), false );
            }
        }
    }
}
