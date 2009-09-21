package org.openscada.hd.server.common;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.openscada.core.InvalidSessionException;
import org.openscada.core.UnableToCreateSessionException;
import org.openscada.core.Variant;
import org.openscada.hd.HistoricalItemInformation;
import org.openscada.hd.Query;
import org.openscada.hd.QueryListener;
import org.openscada.hd.QueryParameters;
import org.openscada.hd.server.Service;
import org.openscada.hd.server.Session;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceImpl implements Service
{

    private final static Logger logger = LoggerFactory.getLogger ( ServiceImpl.class );

    private final ReadWriteLock sessionLock = new ReentrantReadWriteLock ();

    private final Set<SessionImpl> sessions = new HashSet<SessionImpl> ();

    private final BundleContext context;

    public ServiceImpl ( final BundleContext context ) throws InvalidSyntaxException
    {
        this.context = context;
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
            this.sessions.add ( session );
            fireListChanged ( new HashSet<HistoricalItemInformation> ( Arrays.asList ( new HistoricalItemInformation ( "test1", new HashMap<String, Variant> () ) ) ), null, true );
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
    }

    public void stop () throws Exception
    {
        logger.info ( "Stopping service" );
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

    public Query createQuery ( final Session session, final String itemId, final QueryParameters parameters, final QueryListener listener )
    {
        // TODO Auto-generated method stub
        return null;
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

}
