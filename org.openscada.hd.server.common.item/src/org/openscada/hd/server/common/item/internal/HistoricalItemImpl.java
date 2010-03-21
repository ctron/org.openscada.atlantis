package org.openscada.hd.server.common.item.internal;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.openscada.core.Variant;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.datasource.DataSource;
import org.openscada.da.datasource.DataSourceListener;
import org.openscada.da.datasource.SingleDataSourceTracker;
import org.openscada.da.datasource.SingleDataSourceTracker.ServiceListener;
import org.openscada.da.master.MasterItem;
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
import org.slf4j.profiler.Profiler;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;

public class HistoricalItemImpl implements HistoricalItem, DataSourceListener
{

    private final static Logger logger = LoggerFactory.getLogger ( HistoricalItemImpl.class );

    private final HistoricalItemInformation itemInformation;

    private String dataSourceId;

    private final BundleContext context;

    private final SingleServiceTracker storageTracker;

    private StorageHistoricalItem service;

    private final Set<Query> openQueries = new HashSet<Query> ();

    // private SingleServiceTracker masterTracker;
    
    private final ObjectPoolTracker poolTracker;
    
    private SingleDataSourceTracker dataSourceTracker;

    private DataSource dataSource;

    private final Queue<DataItemValue> valueBuffer;

    private int maxBufferSize = 1024;

    public HistoricalItemImpl ( final HistoricalItemInformation itemInformation, final String masterId, final BundleContext context ) throws InvalidSyntaxException
    {
        this.itemInformation = itemInformation;
        this.dataSourceId = masterId;
        this.context = context;

        this.valueBuffer = new LinkedList<DataItemValue> ();

        this.storageTracker = new SingleServiceTracker ( context, FilterUtil.createAndFilter ( StorageHistoricalItem.class.getName (), new MapBuilder<String, String> ().put ( Constants.SERVICE_PID, itemInformation.getId () ).getMap () ), new SingleServiceListener () {

            public void serviceChange ( final ServiceReference reference, final Object service )
            {
                HistoricalItemImpl.this.setStorage ( (StorageHistoricalItem)service );
            }
        } );
//        this.masterTracker = new SingleServiceTracker ( context, FilterUtil.createAndFilter ( DataSource.class.getName (), new MapBuilder<String, String> ().put ( DataSource.DATA_SOURCE_ID, this.dataSourceId ).getMap () ), new SingleServiceListener () {
//
//            public void serviceChange ( final ServiceReference reference, final Object service )
//            {
//                HistoricalItemImpl.this.setMasterItem ( (DataSource)service );
//            }
//        } );
        this.poolTracker = new ObjectPoolTracker(context, DataSource.class.getName ());
        updateDataSource();
    }

    protected synchronized void setMasterItem ( final DataSource service )
    {
        logger.info ( "Set master item: {}", service );

        if ( this.dataSource != null )
        {
            this.dataSource.removeListener ( this );
        }
        this.dataSource = service;
        if ( this.dataSource != null )
        {
            this.dataSource.addListener ( this );
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

        if ( this.service != null )
        {
            logger.info ( String.format ( "Pushing %s entries from value buffer", this.valueBuffer.size () ) );
            while ( !this.valueBuffer.isEmpty () )
            {
                service.updateData ( this.valueBuffer.poll () );
            }
        }
    }

    public void start ()
    {
        this.storageTracker.open ();
        //this.masterTracker.open ();
        this.poolTracker.open();
    }

    public void stop ()
    {
        this.storageTracker.close ();
        //this.masterTracker.close ();
        if (this.dataSourceTracker != null) {
        	this.dataSourceTracker.close();
        	this.dataSourceTracker = null;
        }
        this.poolTracker.close ();
    }

    public HistoricalItemImpl ( final String id, final Map<String, Variant> attributes, final String masterId, final BundleContext context ) throws InvalidSyntaxException
    {
        this ( new HistoricalItemInformation ( id, attributes ), masterId, context );
    }

    public synchronized Query createQuery ( final QueryParameters parameters, final QueryListener listener, final boolean updateData )
    {
        final Profiler p = new Profiler ( "hi.createQuery" );
        p.setLogger ( logger );

        if ( this.service == null )
        {
            return null;
        }

        p.start ( "call shi.createQuery" );

        final Query query = this.service.createQuery ( parameters, listener, updateData );
        if ( query != null )
        {
            this.openQueries.add ( query );
        }

        p.stop ().log ();

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
            else
            {
                logger.info ( "State change ignored: {} missing storage", this.itemInformation.getId () );
                final int size = this.valueBuffer.size ();
                if ( size < this.maxBufferSize )
                {
                    logger.debug ( "State change recorded: buffer size: {}", size );
                    this.valueBuffer.add ( value );
                }
            }
        }
    }

    public void update ( final Map<String, String> properties ) throws InvalidSyntaxException
    {
        final String dataSourceId = properties.get ( DataSource.DATA_SOURCE_ID );

        synchronized ( this )
        {
            logger.info ( "Updating..." );

            this.maxBufferSize = Integer.parseInt ( properties.get ( "maxBufferSize" ) );

//            if ( this.masterTracker != null )
//            {
//                this.masterTracker.close ();
//                this.masterTracker = null;
//            }
//
//            if ( dataSourceId != null )
//            {
//                this.masterTracker = new SingleServiceTracker ( this.context, FilterUtil.createAndFilter ( MasterItem.class.getName (), new MapBuilder<String, String> ().put ( Constants.SERVICE_PID, dataSourceId ).getMap () ), new SingleServiceListener () {
//
//                    public void serviceChange ( final ServiceReference reference, final Object service )
//                    {
//                        HistoricalItemImpl.this.setMasterItem ( (MasterItem)service );
//                    }
//                } );
//                this.masterTracker.open ();
//            }
            
            this.dataSourceId = dataSourceId;
            updateDataSource();

            logger.info ( "Updating...done" );
        }
    }

	private void updateDataSource() throws InvalidSyntaxException {
		if (this.dataSourceTracker != null) {
        	this.dataSourceTracker.close();
        	this.dataSourceTracker = null;
        }
		if (dataSourceId != null) {
	        this.dataSourceTracker = new SingleDataSourceTracker(this.poolTracker, this.dataSourceId, new ServiceListener() {
				public void dataSourceChanged(DataSource dataSource) {
					setMasterItem(dataSource);
				}
			});
	        this.dataSourceTracker.open();
		}
	}
}
