package org.openscada.ae.storage.net;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.openscada.ae.core.QueryDescriptor;
import org.openscada.ae.net.ConnectionServerImpl;
import org.openscada.ae.net.ListQueryResult;
import org.openscada.ae.net.Query;
import org.openscada.ae.net.SubscriptionInformation;
import org.openscada.ae.storage.Session;
import org.openscada.ae.storage.Storage;
import org.openscada.core.CreateSessionData;
import org.openscada.core.InvalidSessionException;
import org.openscada.core.UnableToCreateSessionException;
import org.openscada.core.Variant;
import org.openscada.net.mina.Messenger;

public class NetConnectionServerImpl extends ConnectionServerImpl
{
    private static Logger logger = Logger.getLogger ( NetConnectionServerImpl.class );

    private final Storage storage;

    private Session session;

    private final ServerConnectionHandler serverConnectionHandler;

    private static final String VERSION = "0.2.0";

    public NetConnectionServerImpl ( final Messenger messenger, final Storage storage, final ServerConnectionHandler serverConnectionHandler )
    {
        super ( messenger );
        this.storage = storage;
        this.serverConnectionHandler = serverConnectionHandler;
    }

    public void dispose ()
    {
        if ( this.session != null )
        {
            try
            {
                this.storage.closeSession ( this.session );
            }
            catch ( final InvalidSessionException e )
            {
                logger.warn ( "Failed to dipose session", e );
            }
        }
    }

    public void closeSession () throws InvalidSessionException
    {
        logger.debug ( "Request close session" );

        if ( this.session != null )
        {
            this.storage.closeSession ( this.session );
            this.session = null;
        }

        this.serverConnectionHandler.cleanUp ();
    }

    public void createSession ( final CreateSessionData request ) throws UnableToCreateSessionException
    {
        logger.debug ( "Request session" );

        if ( this.session != null )
        {
            logger.info ( "We already have a session" );
            throw new UnableToCreateSessionException ( "Connection already bound to a session" );
        }

        logger.debug ( "Session properties" );
        final Properties props = new Properties ();
        if ( request.getProperties () != null )
        {
            for ( final Map.Entry<String, Variant> entry : request.getProperties ().entrySet () )
            {
                logger.debug ( String.format ( "   %s - %s", entry.getKey (), entry.getValue ().toLabel () ) );
                props.setProperty ( entry.getKey (), entry.getValue ().toLabel () );
            }
        }

        final String clientVersion = props.getProperty ( "client-version", "" );
        if ( !VERSION.equals ( clientVersion ) )
        {
            logger.warn ( "Client version mismatch" );
            throw new UnableToCreateSessionException ( String.format ( "Requested version '%s' but need version '%s'", clientVersion, VERSION ) );
        }

        logger.debug ( "Create session" );
        this.session = this.storage.createSession ( props );

        if ( this.session == null )
        {
            logger.info ( "No session created" );
            throw new UnableToCreateSessionException ( "No session was created" );
        }
    }

    public ListQueryResult listQueries () throws InvalidSessionException
    {
        final Collection<QueryDescriptor> queries = this.storage.listQueries ( this.session );

        final ListQueryResult result = new ListQueryResult ();

        final Collection<Query> queriesData = new LinkedList<Query> ();
        for ( final QueryDescriptor desc : queries )
        {
            final Query query = new Query ();

            query.setName ( desc.getId () );
            query.setAttributes ( new HashMap<String, Variant> ( desc.getAttributes () ) );

            queriesData.add ( query );
        }

        result.setQueries ( queriesData );

        return result;
    }

    public void subscribe ( final SubscriptionInformation request )
    {
    }

    public void unsubscribe ( final SubscriptionInformation request )
    {
    }

}
