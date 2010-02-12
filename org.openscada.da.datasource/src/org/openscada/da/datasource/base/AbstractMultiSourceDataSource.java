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

    protected abstract void handleChange ();

    protected final ObjectPoolTracker poolTracker;

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

    private synchronized void addDataSource ( final String datasourceKey, final String datasourceId ) throws InvalidSyntaxException
    {
        logger.info ( "Adding data source: {} -> {}", new Object[] { datasourceKey, datasourceId } );

        final DataSourceHandler dsHandler = new DataSourceHandler ( this.poolTracker, datasourceId, new DataSourceHandlerListener () {

            public void handleChange ()
            {
                AbstractMultiSourceDataSource.this.handleChange ();
            }
        } );
        this.sources.put ( datasourceKey, dsHandler );
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

    public void dispose ()
    {
        clearSources ();
    }
}