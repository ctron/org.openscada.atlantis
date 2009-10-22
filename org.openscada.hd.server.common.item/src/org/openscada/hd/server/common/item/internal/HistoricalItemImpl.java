package org.openscada.hd.server.common.item.internal;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openscada.core.Variant;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.master.MasterItem;
import org.openscada.da.master.DataSourceListener;
import org.openscada.hd.HistoricalItemInformation;
import org.openscada.hd.Query;
import org.openscada.hd.QueryListener;
import org.openscada.hd.QueryParameters;
import org.openscada.hd.server.common.HistoricalItem;
import org.openscada.hd.server.common.StorageHistoricalItem;
import org.openscada.utils.collection.MapBuilder;
import org.openscada.utils.osgi.FilterUtil;
import org.openscada.utils.osgi.SingleServiceListener;
import org.openscada.utils.osgi.SingleServiceTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HistoricalItemImpl implements HistoricalItem, DataSourceListener
{

    private final static Logger logger = LoggerFactory.getLogger ( HistoricalItemImpl.class );

    private final HistoricalItemInformation itemInformation;

    private String masterId;

    private final BundleContext context;

    private final SingleServiceTracker storageTracker;

    private StorageHistoricalItem service;

    private final Set<Query> openQueries = new HashSet<Query> ();

    private SingleServiceTracker masterTracker;

    private MasterItem masterItem;

    public HistoricalItemImpl ( final HistoricalItemInformation itemInformation, final String masterId, final BundleContext context ) throws InvalidSyntaxException
    {
        this.itemInformation = itemInformation;
        this.masterId = masterId;
        this.context = context;

        this.storageTracker = new SingleServiceTracker ( context, FilterUtil.createAndFilter ( StorageHistoricalItem.class.getName (), new MapBuilder<String, String> ().put ( Constants.SERVICE_PID, itemInformation.getId () ).getMap () ), new SingleServiceListener () {

            public void serviceChange ( final ServiceReference reference, final Object service )
            {
                HistoricalItemImpl.this.setStorage ( (StorageHistoricalItem)service );
            }
        } );
        this.masterTracker = new SingleServiceTracker ( context, FilterUtil.createAndFilter ( MasterItem.class.getName (), new MapBuilder<String, String> ().put ( Constants.SERVICE_PID, this.masterId ).getMap () ), new SingleServiceListener () {

            public void serviceChange ( final ServiceReference reference, final Object service )
            {
                HistoricalItemImpl.this.setMasterItem ( (MasterItem)service );
            }
        } );
    }

    protected void setMasterItem ( final MasterItem service )
    {
        logger.info ( "Set master item: {}", service );

        if ( this.masterItem != null )
        {
            this.masterItem.removeListener ( this );
        }
        this.masterItem = service;
        if ( this.masterItem != null )
        {
            this.masterItem.addListener ( this );
        }
    }

    protected synchronized void setStorage ( final StorageHistoricalItem service )
    {
        logger.info ( "Setting storage: {}", service );

        if ( this.service == service )
        {
            return;
        }

        // close all open queries
        closeOpenQueries ();

        // remember the new service
        this.service = service;
    }

    public void start ()
    {
        this.storageTracker.open ();
        this.masterTracker.open ();
    }

    public void stop ()
    {
        this.storageTracker.close ();
        this.masterTracker.close ();
    }

    public HistoricalItemImpl ( final String id, final Map<String, Variant> attributes, final String masterId, final BundleContext context ) throws InvalidSyntaxException
    {
        this ( new HistoricalItemInformation ( id, attributes ), masterId, context );
    }

    public synchronized Query createQuery ( final QueryParameters parameters, final QueryListener listener, final boolean updateData )
    {
        if ( this.service == null )
        {
            return null;
        }

        final Query query = this.service.createQuery ( parameters, listener, updateData );
        if ( query != null )
        {
            this.openQueries.add ( query );
        }
        return query;
    }

    public HistoricalItemInformation getInformation ()
    {
        return this.itemInformation;
    }

    private void closeOpenQueries ()
    {
        for ( final Query query : this.openQueries )
        {
            query.close ();
        }
        this.openQueries.clear ();
    }

    public void stateChanged ( final DataItemValue value )
    {
        synchronized ( this )
        {
            if ( this.service != null )
            {
                this.service.updateData ( value );
            }
        }
    }

    public void update ( final Map<String, String> properties ) throws InvalidSyntaxException
    {
        final String masterId = properties.get ( "master.id" );
        if ( masterId == null )
        {
            throw new IllegalArgumentException ( "'master.id' must be set" );
        }

        synchronized ( this )
        {
            logger.info ( "Updating..." );

            this.masterTracker.close ();
            this.masterTracker = new SingleServiceTracker ( this.context, FilterUtil.createAndFilter ( MasterItem.class.getName (), new MapBuilder<String, String> ().put ( Constants.SERVICE_PID, masterId ).getMap () ), new SingleServiceListener () {

                public void serviceChange ( final ServiceReference reference, final Object service )
                {
                    HistoricalItemImpl.this.setMasterItem ( (MasterItem)service );
                }
            } );
            this.masterTracker.open ();

            this.masterId = masterId;

            logger.info ( "Updating...done" );
        }
    }

}
