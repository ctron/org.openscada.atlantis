package org.openscada.da.datasource.proxy.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

import org.openscada.core.OperationException;
import org.openscada.core.Variant;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.core.WriteResult;
import org.openscada.da.datasource.DataSource;
import org.openscada.da.datasource.DataSourceListener;
import org.openscada.da.datasource.base.AbstractDataSource;
import org.openscada.da.datasource.proxy.Service;
import org.openscada.utils.concurrent.InstantErrorFuture;
import org.openscada.utils.concurrent.NotifyFuture;
import org.openscada.utils.osgi.FilterUtil;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyDataSource extends AbstractDataSource implements Service, ServiceTrackerCustomizer
{

    private final static Logger logger = LoggerFactory.getLogger ( ProxyDataSource.class );

    private final Executor executor;

    private final BundleContext context;

    private ServiceTracker tracker;

    private final Map<DataSource, SourceHandler> sources = new HashMap<DataSource, SourceHandler> ();

    private Set<String> sourceIds;

    public ProxyDataSource ( final BundleContext context, final Executor executor )
    {
        this.context = context;
        this.executor = executor;
    }

    @Override
    protected Executor getExecutor ()
    {
        return this.executor;
    }

    public void dispose ()
    {
        for ( final SourceHandler handler : this.sources.values () )
        {
            handler.dispose ();
        }
        if ( this.tracker != null )
        {
            this.tracker.close ();
        }
    }

    public void update ( final Map<String, String> properties ) throws Exception
    {
        setSources ( properties.get ( "sources" ) );
    }

    private void setSources ( final String str ) throws InvalidSyntaxException
    {
        if ( this.tracker != null )
        {
            this.tracker.close ();
            this.tracker = null;
        }

        this.sourceIds = convertSources ( str );

        if ( this.sourceIds.isEmpty () )
        {
            // nothing to do if we don't have any source
            return;
        }

        this.tracker = new ServiceTracker ( this.context, createFilter ( this.sourceIds ), this );
        this.tracker.open ();
    }

    private Filter createFilter ( final Set<String> sources ) throws InvalidSyntaxException
    {
        return FilterUtil.createAndFilter ( FilterUtil.createClassFilter ( DataSource.class.getName () ), FilterUtil.createSimpleOr ( DataSource.DATA_SOURCE_ID, sources ) );
    }

    private Set<String> convertSources ( final String sources )
    {
        if ( sources == null )
        {
            throw new IllegalArgumentException ( "'sources' must be set" );
        }

        return new LinkedHashSet<String> ( Arrays.asList ( sources.split ( "[, ]+" ) ) );
    }

    public Object addingService ( final ServiceReference reference )
    {
        final int priority = getPriority ( reference );

        Object o = this.context.getService ( reference );
        try
        {
            final DataSource dataSource = (DataSource)o;
            addSource ( dataSource, priority );
        }
        finally
        {
            if ( o != null )
            {
                this.context.ungetService ( reference );
                o = null;
            }
        }

        return null;
    }

    private int getPriority ( final ServiceReference reference )
    {
        final Object o = reference.getProperty ( Constants.SERVICE_RANKING );

        if ( o == null )
        {
            return getDefaultPriority ( reference );
        }

        if ( o instanceof Number )
        {
            return ( (Number)o ).intValue ();
        }

        try
        {
            return Integer.parseInt ( o.toString () );
        }
        catch ( final NumberFormatException e )
        {
            return getDefaultPriority ( reference );
        }
    }

    private int getDefaultPriority ( final ServiceReference reference )
    {
        final Object o = reference.getProperty ( DataSource.DATA_SOURCE_ID );
        if ( o == null )
        {
            return 100;
        }

        final String dataSourceId = o.toString ();

        int start = 100;
        for ( final String id : this.sourceIds )
        {
            if ( id != null && id.equals ( dataSourceId ) )
            {
                return start;
            }
            start--;
        }

        logger.warn ( "Getting priority for unknown service: {}", reference );
        return Integer.MIN_VALUE;
    }

    public void modifiedService ( final ServiceReference reference, final Object service )
    {
        updateSource ( service, getPriority ( reference ) );
    }

    public synchronized void removedService ( final ServiceReference reference, final Object service )
    {
        this.context.ungetService ( reference );
        removeSource ( (DataSource)service );
    }

    private class DataItemValueEntry implements Comparable<DataItemValueEntry>
    {
        private final DataItemValue value;

        private final int priority;

        DataItemValueEntry ( final DataItemValue value, final int priority )
        {
            this.value = value;
            this.priority = priority;
        }

        public int compareTo ( final DataItemValueEntry other )
        {
            if ( this.priority == other.priority )
            {
                return 0;
            }
            else if ( this.priority > other.priority )
            {
                return 1;
            }
            else
            {
                return -1;
            }
        }

        public DataItemValue getValue ()
        {
            return this.value;
        }

    }

    private class SourceHandler implements DataSourceListener
    {
        private final DataSource dataSource;

        private int priority;

        private DataItemValue value;

        SourceHandler ( final DataSource dataSource, final int priority )
        {
            this.dataSource = dataSource;
            this.priority = priority;

            dataSource.addListener ( this );
        }

        public void dispose ()
        {
            this.dataSource.removeListener ( this );
        }

        public void setPriority ( final int priority )
        {
            this.priority = priority;
        }

        public DataItemValueEntry getEntry ()
        {
            return new DataItemValueEntry ( this.value, this.priority );
        }

        public void stateChanged ( final DataItemValue value )
        {
            this.value = value;
            ProxyDataSource.this.update ();
        }
    }

    protected synchronized void update ()
    {
        final ArrayList<DataItemValueEntry> entries = new ArrayList<DataItemValueEntry> ( this.sources.size () );

        for ( final SourceHandler handler : this.sources.values () )
        {
            final DataItemValueEntry entry = handler.getEntry ();
            if ( entry != null && entry.getValue () != null )
            {
                entries.add ( entry );
            }
        }

        Collections.sort ( entries );

        DataItemValue value;
        if ( entries.isEmpty () )
        {
            value = null;
        }
        else
        {
            value = entries.get ( entries.size () - 1 ).getValue ();
        }
        updateData ( value );
    }

    private synchronized void addSource ( final DataSource dataSource, final int priority )
    {
        logger.info ( "Adding source: {} / {}", new Object[] { dataSource, priority } );

        final SourceHandler handler = new SourceHandler ( dataSource, priority );

        final SourceHandler oldHandler = this.sources.put ( dataSource, handler );
        if ( oldHandler != null )
        {
            oldHandler.dispose ();
        }

        update ();
    }

    private synchronized void updateSource ( final Object service, final int priority )
    {
        logger.info ( "Updating source: {} / {}", new Object[] { service, priority } );

        final SourceHandler handler = this.sources.get ( service );
        if ( handler != null )
        {
            handler.setPriority ( priority );
            update ();
        }
    }

    private synchronized void removeSource ( final DataSource service )
    {
        logger.info ( "Removing source: {}", service );

        final SourceHandler handler = this.sources.remove ( service );
        if ( handler != null )
        {
            handler.dispose ();
            update ();
        }
    }

    public NotifyFuture<WriteAttributeResults> startWriteAttributes ( final Map<String, Variant> attributes )
    {
        return new InstantErrorFuture<WriteAttributeResults> ( new OperationException ( "'writeAttributes' not supported" ) );
    }

    public NotifyFuture<WriteResult> startWriteValue ( final Variant value )
    {
        return new InstantErrorFuture<WriteResult> ( new OperationException ( "'writeAttributes' not supported" ) );
    }

}
