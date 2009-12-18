package org.openscada.da.master.internal;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.openscada.ca.ConfigurationFactory;
import org.openscada.da.datasource.DataSource;
import org.openscada.da.master.MasterItem;
import org.openscada.utils.concurrent.NamedThreadFactory;
import org.openscada.utils.osgi.pool.ObjectPoolImpl;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MasterFactory implements ConfigurationFactory
{
    private final static Logger logger = LoggerFactory.getLogger ( MasterFactory.class );

    public static final String ITEM_ID = "item.id";

    public static final String CONNECTION_ID = "connection.id";

    private final BundleContext context;

    private final Map<String, ServiceRegistration> masterRegs = new HashMap<String, ServiceRegistration> ();

    private final Map<String, MasterItemImpl> masterItemImpls = new HashMap<String, MasterItemImpl> ();

    private final ExecutorService executor;

    private final ObjectPoolImpl dataSourcePool;

    public MasterFactory ( final BundleContext context )
    {
        this.context = context;
        this.executor = Executors.newSingleThreadScheduledExecutor ( new NamedThreadFactory ( "MasterItemFactory" ) );

        this.dataSourcePool = new ObjectPoolImpl ();
    }

    public void delete ( final String configurationId )
    {
        deleteMaster ( configurationId );
    }

    public void purge ()
    {
    }

    public void update ( final String configurationId, final Map<String, String> properties ) throws Exception
    {
        final String id = configurationId;

        synchronized ( this )
        {
            final MasterItemImpl masterItemImpl = this.masterItemImpls.get ( id );
            if ( masterItemImpl != null )
            {
                masterItemImpl.update ( properties );
            }
            else
            {
                // create
                final String connectionId = properties.get ( CONNECTION_ID );
                final String itemId = properties.get ( ITEM_ID );
                if ( connectionId == null )
                {
                    throw new IllegalArgumentException ( String.format ( "'%s' must not be null", CONNECTION_ID ) );
                }
                if ( itemId == null )
                {
                    throw new IllegalArgumentException ( String.format ( "'%s' must not be null", ITEM_ID ) );
                }
                createMaster ( id, connectionId, itemId );
            }
        }
    }

    protected synchronized void createMaster ( final String id, final String connectionId, final String itemId ) throws InvalidSyntaxException
    {
        MasterItemImpl masterItemImpl = this.masterItemImpls.get ( id );
        if ( masterItemImpl == null )
        {
            masterItemImpl = new MasterItemImpl ( this.executor, this.context, id, connectionId, itemId );
            this.masterItemImpls.put ( id, masterItemImpl );

            final Dictionary<String, String> properties = new Hashtable<String, String> ();
            properties.put ( Constants.SERVICE_PID, id );
            properties.put ( Constants.SERVICE_VENDOR, "inavare GmbH" );
            properties.put ( Constants.SERVICE_DESCRIPTION, "Master Data Item" );
            properties.put ( CONNECTION_ID, connectionId );
            properties.put ( ITEM_ID, itemId );
            properties.put ( DataSource.DATA_SOURCE_ID, id );
            properties.put ( MasterItem.MASTER_ID, id );

            logger.debug ( "Registering " + id );

            this.dataSourcePool.addService ( id, masterItemImpl, properties );

            this.masterRegs.put ( id, this.context.registerService ( new String[] { MasterItem.class.getName () }, masterItemImpl, properties ) );
        }
    }

    protected void deleteMaster ( final String id )
    {
        MasterItemImpl master;
        ServiceRegistration reg;
        synchronized ( this )
        {
            master = this.masterItemImpls.remove ( id );
            reg = this.masterRegs.remove ( id );
        }

        if ( master != null )
        {
            this.dataSourcePool.removeService ( id, master );
            master.dispose ();
        }

        if ( reg != null )
        {
            reg.unregister ();
        }
    }

    public void dispose ()
    {
        this.dataSourcePool.dispose ();
        removeAllItems ();
        this.executor.shutdown ();
    }

    /*
     * Must only be called from #dispose()
     */
    private synchronized void removeAllItems ()
    {
        for ( final ServiceRegistration reg : this.masterRegs.values () )
        {
            reg.unregister ();
        }
        this.masterRegs.clear ();

        for ( final MasterItemImpl item : this.masterItemImpls.values () )
        {
            // no need to remove services from pool since it is dispsed
            item.dispose ();
        }
        this.masterItemImpls.clear ();
    }

}
