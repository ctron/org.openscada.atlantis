package org.openscada.da.datasource.sum;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Executor;

import org.openscada.core.OperationException;
import org.openscada.core.Variant;
import org.openscada.core.subscription.SubscriptionState;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.client.DataItemValue.Builder;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.core.WriteResult;
import org.openscada.da.datasource.WriteInformation;
import org.openscada.da.datasource.base.AbstractMultiSourceDataSource;
import org.openscada.da.datasource.base.DataSourceHandler;
import org.openscada.utils.concurrent.InstantErrorFuture;
import org.openscada.utils.concurrent.NotifyFuture;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;

public class SumDataSource extends AbstractMultiSourceDataSource
{
    private final Executor executor;

    public SumDataSource ( final ObjectPoolTracker poolTracker, final Executor executor )
    {
        super ( poolTracker );
        this.executor = executor;
    }

    @Override
    protected Executor getExecutor ()
    {
        return this.executor;
    }

    public NotifyFuture<WriteAttributeResults> startWriteAttributes ( final WriteInformation writeInformation, final Map<String, Variant> attributes )
    {
        return new InstantErrorFuture<WriteAttributeResults> ( new OperationException ( "Not supported" ) );
    }

    public NotifyFuture<WriteResult> startWriteValue ( final WriteInformation writeInformation, final Variant value )
    {
        return new InstantErrorFuture<WriteResult> ( new OperationException ( "Not supported" ) );
    }

    public void update ( final Map<String, String> parameters ) throws Exception
    {
    }

    @Override
    protected synchronized void handleChange ()
    {
        final Collection<DataItemValue> values = new ArrayList<DataItemValue> ( this.sources.size () );
        for ( final DataSourceHandler handler : this.sources.values () )
        {
            values.add ( handler.getValue () );
        }

        updateData ( aggregate ( values ) );
    }

    private DataItemValue aggregate ( final Collection<DataItemValue> values )
    {
        final Builder builder = new Builder ();
        builder.setSubscriptionState ( SubscriptionState.CONNECTED );
        builder.setValue ( new Variant ( values.size () ) );

        return builder.build ();
    }

}
