package org.openscada.hd.server.common.internal;

import java.util.Map;
import java.util.Set;

import org.openscada.hd.Query;
import org.openscada.hd.QueryListener;
import org.openscada.hd.QueryParameters;
import org.openscada.hd.QueryState;
import org.openscada.hd.Value;
import org.openscada.hd.ValueInformation;

public class QueryImpl implements Query, QueryListener
{
    private Query query;

    private final SessionImpl session;

    private final QueryListener listener;

    private QueryParameters queryParameters;

    public QueryImpl ( final SessionImpl session, final QueryListener listener )
    {
        this.session = session;
        this.listener = listener;
        session.addQuery ( this );
    }

    public void setQuery ( final Query query )
    {
        this.query = query;
    }

    public void close ()
    {
        this.session.removeQuery ( this );
        this.query.close ();
    }

    public void changeParameters ( final QueryParameters parameters )
    {
        this.query.changeParameters ( parameters );
    }

    public void updateParameters ( final QueryParameters parameters, final Set<String> valueTypes )
    {
        if ( parameters == null )
        {
            throw new IllegalArgumentException ( "'parameters' must not be null" );
        }
        if ( valueTypes == null || valueTypes.isEmpty () )
        {
            throw new IllegalArgumentException ( "'valueTypes' must not be null or empty" );
        }

        this.queryParameters = parameters;
        this.listener.updateParameters ( parameters, valueTypes );
    }

    public void updateData ( final int index, final Map<String, Value[]> values, final ValueInformation[] valueInformation )
    {
        if ( values == null )
        {
            throw new IllegalArgumentException ( "'values' must not be null" );
        }
        if ( values == null )
        {
            throw new IllegalArgumentException ( "'valueInformation' must not be null" );
        }
        if ( this.queryParameters == null )
        {
            throw new IllegalStateException ( "'updateData' must be called after a call to 'updateParameters'" );
        }
        if ( index <= 0 || index >= this.queryParameters.getEntries () )
        {
            throw new IllegalArgumentException ( "'index' must be greater or equal to zero and lower than the number of reported entries" );
        }
        for ( final Map.Entry<String, Value[]> entry : values.entrySet () )
        {
            if ( entry.getValue () == null )
            {
                throw new IllegalArgumentException ( String.format ( "The values for '%s' are null", entry.getKey () ) );
            }
            if ( entry.getValue ().length != valueInformation.length )
            {
                throw new IllegalArgumentException ( String.format ( "The number of entries for '%s' is not equal to the rest of the entry count", entry.getKey () ) );
            }
        }
        if ( index + valueInformation.length > this.queryParameters.getEntries () )
        {
            throw new IllegalArgumentException ( "The reported data exceeds reported number of entries" );
        }

        // finally we can pass on the event
        this.listener.updateData ( index, values, valueInformation );
    }

    public void updateState ( final QueryState state )
    {
        if ( state == null )
        {
            throw new IllegalArgumentException ( "'state' must not be null" );
        }

        try
        {
            this.listener.updateState ( state );
        }
        finally
        {
            switch ( state )
            {
            case DISCONNECTED:
                this.session.removeQuery ( this );
                break;
            }
        }
    }

    public void dispose ()
    {
        this.query.close ();
    }
}
