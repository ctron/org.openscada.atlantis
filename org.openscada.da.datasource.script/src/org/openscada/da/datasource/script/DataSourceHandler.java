package org.openscada.da.datasource.script;

import java.util.HashMap;
import java.util.Map;

import org.openscada.da.client.DataItemValue;
import org.openscada.da.datasource.DataSource;
import org.openscada.da.datasource.DataSourceListener;
import org.openscada.utils.osgi.FilterUtil;
import org.openscada.utils.osgi.SingleServiceListener;
import org.openscada.utils.osgi.SingleServiceTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataSourceHandler implements DataSourceListener
{

    private final static Logger logger = LoggerFactory.getLogger ( DataSourceHandler.class );

    private final BundleContext context;

    private final String dataSourceId;

    private final DataSourceHandlerListener listener;

    private final SingleServiceTracker tracker;

    private DataSource service;

    private DataItemValue value;

    public DataSourceHandler ( final BundleContext context, final String datasourceId, final DataSourceHandlerListener listener ) throws InvalidSyntaxException
    {
        this.context = context;
        this.dataSourceId = datasourceId;
        this.listener = listener;

        final Map<String, String> parameters = new HashMap<String, String> ();
        parameters.put ( DataSource.DATA_SOURCE_ID, this.dataSourceId );
        final Filter filter = FilterUtil.createAndFilter ( DataSource.class.getName (), parameters );
        this.tracker = new SingleServiceTracker ( context, filter, new SingleServiceListener () {

            @Override
            public void serviceChange ( final ServiceReference reference, final Object service )
            {
                DataSourceHandler.this.setDataSource ( (DataSource)service );
            }
        } );
        this.tracker.open ();
    }

    protected void setDataSource ( final DataSource service )
    {
        if ( this.service != null )
        {
            this.service.removeListener ( this );
            this.value = null;
            fireValueChange ();
        }

        this.service = service;

        if ( this.service != null )
        {
            this.service.addListener ( this );
        }
    }

    private void fireValueChange ()
    {
        try
        {
            this.listener.handleChange ();
        }
        catch ( final Exception e )
        {
            logger.warn ( "Failed to handle state change", e );
        }
    }

    public void dispose ()
    {
        if ( this.tracker != null )
        {
            this.tracker.close ();
        }
    }

    public DataItemValue getValue ()
    {
        return this.value;
    }

    @Override
    public void stateChanged ( final DataItemValue value )
    {
        this.value = value;
        fireValueChange ();
    }
}
