package org.openscada.ae.server.common.event.internal;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.openscada.ae.event.EventManager;
import org.openscada.ae.server.common.event.EventQuery;
import org.openscada.ae.server.storage.Storage;
import org.openscada.utils.concurrent.NamedThreadFactory;
import org.openscada.utils.osgi.FilterUtil;
import org.openscada.utils.osgi.SingleServiceListener;
import org.openscada.utils.osgi.SingleServiceTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventPoolManager
{

    private final static Logger logger = LoggerFactory.getLogger ( EventPoolManager.class );

    private final BundleContext context;

    private final SingleServiceTracker eventManagerTracker;

    private final SingleServiceTracker storageTracker;

    private Storage storage;

    private EventManager eventManager;

    private EventPoolImpl pool;

    private ServiceRegistration poolHandle;

    private final String id;

    private final ExecutorService executor;

    private final String filter;

    public EventPoolManager ( final BundleContext context, final String id, final String filter ) throws InvalidSyntaxException
    {
        this.context = context;
        this.id = id;
        this.filter = filter;

        this.executor = Executors.newSingleThreadExecutor ( new NamedThreadFactory ( "EventPoolManager/" + id ) );

        this.eventManagerTracker = new SingleServiceTracker ( this.context, FilterUtil.createClassFilter ( EventManager.class.getName () ), new SingleServiceListener () {

            public void serviceChange ( final ServiceReference reference, final Object service )
            {
                EventPoolManager.this.setEventManager ( (EventManager)service );
            }
        } );
        this.eventManagerTracker.open ();

        this.storageTracker = new SingleServiceTracker ( this.context, FilterUtil.createClassFilter ( Storage.class.getName () ), new SingleServiceListener () {

            public void serviceChange ( final ServiceReference reference, final Object service )
            {
                EventPoolManager.this.setStorageService ( (Storage)service );
            }
        } );
        this.storageTracker.open ();
    }

    protected synchronized void setStorageService ( final Storage service )
    {
        this.storage = service;
        checkInit ();
    }

    protected synchronized void setEventManager ( final EventManager service )
    {
        this.eventManager = service;
        checkInit ();
    }

    private void checkInit ()
    {
        // FIXME: async exec

        if ( this.storage != null && this.eventManager != null )
        {
            createPool ( this.storage, this.eventManager );
        }
        else
        {
            disposePool ();
        }
    }

    private void createPool ( final Storage storage, final EventManager eventManager )
    {
        this.pool = new EventPoolImpl ( this.executor, storage, eventManager, this.filter, 10000 );

        try
        {
            this.pool.start ();

            final Dictionary<String, String> properties = new Hashtable<String, String> ();
            properties.put ( Constants.SERVICE_PID, this.id );
            this.poolHandle = this.context.registerService ( EventQuery.class.getName (), this.pool, properties );
        }
        catch ( final Exception e )
        {
            logger.warn ( "Failed to create event pool: " + this.id, e );
        }
    }

    public void dispose ()
    {
        disposePool ();
        this.executor.shutdown ();
    }

    private synchronized void disposePool ()
    {
        if ( this.poolHandle != null )
        {
            this.poolHandle.unregister ();
            this.poolHandle = null;
        }
        if ( this.pool != null )
        {
            this.pool.stop ();
            this.pool = null;
        }
    }
}
