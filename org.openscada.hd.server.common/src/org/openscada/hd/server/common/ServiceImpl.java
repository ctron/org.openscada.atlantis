package org.openscada.hd.server.common;

import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.openscada.core.InvalidSessionException;
import org.openscada.core.UnableToCreateSessionException;
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

    private final Set<SessionImpl> sessions = new CopyOnWriteArraySet<SessionImpl> ();

    private final BundleContext context;

    public ServiceImpl ( final BundleContext context ) throws InvalidSyntaxException
    {
        this.context = context;
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
        }
    }

    public org.openscada.core.server.Session createSession ( final Properties properties ) throws UnableToCreateSessionException
    {
        final SessionImpl session = new SessionImpl ( properties.getProperty ( "user", null ) );
        synchronized ( this.sessions )
        {
            this.sessions.add ( session );
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

    public Query createQuery ( final Session session, final String itemId, final QueryParameters parameters, final QueryListener listener )
    {
        // TODO Auto-generated method stub
        return null;
    }
}
