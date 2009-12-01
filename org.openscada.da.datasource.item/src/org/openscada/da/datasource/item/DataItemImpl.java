package org.openscada.da.datasource.item;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.openscada.core.InvalidOperationException;
import org.openscada.core.OperationException;
import org.openscada.core.Variant;
import org.openscada.core.utils.AttributesHelper;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.core.DataItemInformation;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.core.WriteResult;
import org.openscada.da.datasource.DataSource;
import org.openscada.da.datasource.DataSourceListener;
import org.openscada.da.server.common.DataItemBase;
import org.openscada.utils.concurrent.InstantErrorFuture;
import org.openscada.utils.concurrent.InstantFuture;
import org.openscada.utils.concurrent.NotifyFuture;
import org.openscada.utils.osgi.FilterUtil;
import org.openscada.utils.osgi.SingleServiceListener;
import org.openscada.utils.osgi.SingleServiceTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataItemImpl extends DataItemBase implements DataSourceListener
{
    private final static Logger logger = LoggerFactory.getLogger ( DataItemImpl.class );

    private DataItemValue currentValue = new DataItemValue ();

    private final SingleServiceTracker tracker;

    private final BundleContext context;

    private DataSource dataSource;

    public DataItemImpl ( final BundleContext context, final DataItemInformation information, final String datasourceId ) throws InvalidSyntaxException
    {
        super ( information );
        this.context = context;

        final Map<String, String> parameters = new HashMap<String, String> ();
        parameters.put ( DataSource.DATA_SOURCE_ID, datasourceId );
        final Filter filter = FilterUtil.createAndFilter ( DataSource.class.getName (), parameters );
        this.tracker = new SingleServiceTracker ( this.context, filter, new SingleServiceListener () {

            public void serviceChange ( final ServiceReference reference, final Object service )
            {
                DataItemImpl.this.setDataSource ( (DataSource)service );
            }
        } );
        this.tracker.open ();
    }

    @Override
    protected synchronized Map<String, Variant> getCacheAttributes ()
    {
        final DataItemValue value = this.currentValue;

        if ( value != null )
        {
            return value.getAttributes ();
        }
        else
        {
            return null;
        }
    }

    @Override
    protected synchronized Variant getCacheValue ()
    {
        final DataItemValue value = this.currentValue;

        if ( value != null )
        {
            return value.getValue ();
        }
        else
        {
            return null;
        }
    }

    protected synchronized void setDataSource ( final DataSource dataSource )
    {
        logger.info ( "Setting datasource: {}", dataSource );
        disconnectDatasource ();
        connectDataSource ( dataSource );
    }

    private synchronized void connectDataSource ( final DataSource dataSource )
    {
        this.dataSource = dataSource;
        if ( this.dataSource != null )
        {
            this.dataSource.addListener ( this );
        }
    }

    private synchronized void disconnectDatasource ()
    {
        if ( this.dataSource != null )
        {
            this.dataSource.removeListener ( this );
            this.dataSource = null;
        }
    }

    public synchronized Map<String, Variant> getAttributes ()
    {
        return Collections.unmodifiableMap ( this.currentValue.getAttributes () );
    }

    public synchronized NotifyFuture<Variant> readValue () throws InvalidOperationException
    {
        return new InstantFuture<Variant> ( this.currentValue.getValue () );
    }

    public synchronized NotifyFuture<WriteAttributeResults> startSetAttributes ( final Map<String, Variant> attributes )
    {
        if ( this.dataSource != null )
        {
            return this.dataSource.startWriteAttributes ( attributes );
        }
        else
        {
            return new InstantErrorFuture<WriteAttributeResults> ( new OperationException ( "Disconnected data source" ) );
        }
    }

    public synchronized NotifyFuture<WriteResult> startWriteValue ( final Variant value )
    {
        if ( this.dataSource != null )
        {
            return this.dataSource.startWriteValue ( value );
        }
        else
        {
            return new InstantErrorFuture<WriteResult> ( new OperationException ( "Disconnected data source" ) );
        }
    }

    public synchronized void dispose ()
    {
        if ( this.tracker != null )
        {
            this.tracker.close ();
        }
    }

    public synchronized void stateChanged ( final DataItemValue value )
    {
        final Map<String, Variant> target = new HashMap<String, Variant> ( this.currentValue.getAttributes () );
        final Map<String, Variant> diff = new HashMap<String, Variant> ();

        AttributesHelper.set ( target, value.getAttributes (), diff );

        this.currentValue = value;
        notifyData ( value.getValue (), diff );
    }
}
