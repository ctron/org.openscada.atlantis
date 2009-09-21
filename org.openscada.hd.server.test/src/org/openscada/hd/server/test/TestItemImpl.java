package org.openscada.hd.server.test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.openscada.core.Variant;
import org.openscada.hd.HistoricalItemInformation;
import org.openscada.hd.Query;
import org.openscada.hd.QueryListener;
import org.openscada.hd.QueryParameters;
import org.openscada.hd.server.common.HistoricalItem;

public class TestItemImpl implements HistoricalItem
{

    private final Set<TestQueryImpl> queries = new HashSet<TestQueryImpl> ();

    public Query createQuery ( final QueryParameters parameters, final QueryListener listener )
    {
        final TestQueryImpl query = new TestQueryImpl ( this, parameters, listener );
        this.queries.add ( query );
        return query;
    }

    public HistoricalItemInformation getInformation ()
    {
        return new HistoricalItemInformation ( "test1", new HashMap<String, Variant> () );
    }

    public void dispose ()
    {
        for ( final TestQueryImpl query : this.queries )
        {
            query.close ();
        }
    }

}
