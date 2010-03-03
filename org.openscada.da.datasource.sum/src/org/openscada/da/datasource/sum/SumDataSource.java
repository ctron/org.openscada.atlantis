package org.openscada.da.datasource.sum;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SumDataSource extends AbstractMultiSourceDataSource
{

    private final static Logger logger = LoggerFactory.getLogger ( SumDataSource.class );

    private final Executor executor;

    private Map<String, String> types;

    private Set<String> groups;

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

    public synchronized void update ( final Map<String, String> parameters ) throws Exception
    {
        final Map<String, String> types = new HashMap<String, String> ();

        String groupsString = parameters.get ( "groups" );
        if ( groupsString == null )
        {
            groupsString = "";
        }

        clearSources ();

        this.groups = new HashSet<String> ( Arrays.asList ( groupsString.split ( ", ?" ) ) );

        for ( final Map.Entry<String, String> entry : parameters.entrySet () )
        {
            final String key = entry.getKey ();
            final String value = entry.getValue ();

            if ( key.startsWith ( "datasource." ) )
            {
                final String toks[] = value.split ( "#", 2 );
                final String id = toks[0];

                if ( toks.length > 1 )
                {
                    types.put ( key, toks[1] );
                }
                logger.info ( "Adding datasource: {} -> {}", new Object[] { key, id } );
                addDataSource ( key, id );
            }
        }

        this.types = types;
        handleChange ();
    }

    @Override
    protected synchronized void handleChange ()
    {
        final Map<String, DataItemValue> values = new HashMap<String, DataItemValue> ( this.sources.size () );
        for ( final Map.Entry<String, DataSourceHandler> entry : this.sources.entrySet () )
        {
            values.put ( entry.getKey (), entry.getValue ().getValue () );
        }

        updateData ( aggregate ( values ) );
    }

    private synchronized DataItemValue aggregate ( final Map<String, DataItemValue> values )
    {
        final Builder builder = new Builder ();
        builder.setSubscriptionState ( SubscriptionState.CONNECTED );
        builder.setValue ( new Variant ( values.size () ) );

        final Map<String, Integer> counts = new HashMap<String, Integer> ();

        for ( final Map.Entry<String, DataItemValue> entry : values.entrySet () )
        {
            final DataItemValue value = entry.getValue ();

            if ( value == null || !value.isConnected () )
            {
                if ( this.groups.contains ( "error" ) )
                {
                    increment ( counts, "error" );
                }
                logger.debug ( "Skipping item {} since it is disconnected", entry.getKey () );
                continue;
            }

            // increment by group
            for ( final String group : this.groups )
            {
                if ( value.isAttribute ( group, false ) )
                {
                    increment ( counts, group );
                }
            }

            // increment by main value
            final String type = this.types.get ( entry.getKey () );
            if ( type != null )
            {
                if ( value.getValue ().asBoolean () )
                {
                    increment ( counts, type );
                }
            }
        }

        // convert to attributes
        for ( final Map.Entry<String, Integer> entry : counts.entrySet () )
        {
            builder.setAttribute ( entry.getKey (), Variant.valueOf ( entry.getValue () != 0 ) );
            builder.setAttribute ( entry.getKey () + ".count", new Variant ( entry.getValue () ) );
        }

        return builder.build ();
    }

    private void increment ( final Map<String, Integer> counts, final String group )
    {
        if ( !counts.containsKey ( group ) )
        {
            counts.put ( group, 1 );
            logger.debug ( "Increment group - '{}': set to 1", group );
        }
        else
        {
            int i = counts.get ( group );
            i++;
            counts.put ( group, i );
            logger.debug ( "Increment group - '{}': {}", new Object[] { group, i } );
        }
    }
}
