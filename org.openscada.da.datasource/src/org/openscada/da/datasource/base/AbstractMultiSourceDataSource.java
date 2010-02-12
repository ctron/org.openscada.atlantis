package org.openscada.da.datasource.base;

import java.util.HashMap;
import java.util.Map;

import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.InvalidSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractMultiSourceDataSource extends AbstractDataSource
{

    private final static Logger logger = LoggerFactory.getLogger ( AbstractMultiSourceDataSource.class );

    protected final Map<String, DataSourceHandler> sources = new HashMap<String, DataSourceHandler> ();

    protected final ObjectPoolTracker poolTracker;

    private boolean disposed;

    public AbstractMultiSourceDataSource ( final ObjectPoolTracker poolTracker )
    {
        super ();
        this.poolTracker = poolTracker;
    }

    protected synchronized void setDataSources ( final Map<String, String> parameters ) throws InvalidSyntaxException
    {
        clearSources ();

        for ( final Map.Entry<String, String> entry : parameters.entrySet () )
        {
            final String key = entry.getKey ();
            final String value = entry.getValue ();
            if ( key.startsWith ( "datasource." ) )
            {
                addDataSource ( key.substring ( "datasource.".length () ), value );
            }
        }
    }

    protected abstract void handleChange ();

    private synchronized void addDataSource ( final String datasourceKey, final String datasourceId ) throws InvalidSyntaxException
    {
        logger.info ( "Adding data source: {} -> {}", new Object[] { datasourceKey, datasourceId } );

        final DataSourceHandler dsHandler = new DataSourceHandler ( this.poolTracker, datasourceId, new DataSourceHandlerListener () {

            public void handleChange ()
            {
                AbstractMultiSourceDataSource.this.triggerHandleChange ();
            }
        } );
        this.sources.put ( datasourceKey, dsHandler );
    }

    protected synchronized void triggerHandleChange ()
    {
        if ( this.disposed )
        {
            return;
        }
        handleChange ();
    }

    /**
     * Clear all datasources
     */
    protected synchronized void clearSources ()
    {
        for ( final DataSourceHandler source : this.sources.values () )
        {
            source.dispose ();
        }
        this.sources.clear ();
    }

    public synchronized void dispose ()
    {
        this.disposed = true;
        clearSources ();
    }
}