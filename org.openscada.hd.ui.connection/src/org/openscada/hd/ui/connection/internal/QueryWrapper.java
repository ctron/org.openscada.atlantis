package org.openscada.hd.ui.connection.internal;

import org.eclipse.core.databinding.observable.Observables;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.set.WritableSet;
import org.openscada.hd.connection.provider.ConnectionService;
import org.openscada.hd.ui.data.QueryBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryWrapper
{
    private final static Logger logger = LoggerFactory.getLogger ( QueryWrapper.class );

    private final ConnectionService service;

    private final WritableSet queries = new WritableSet ();

    public QueryWrapper ( final ConnectionService service )
    {
        this.service = service;
    }

    public ConnectionService getService ()
    {
        return this.service;
    }

    public void dispose ()
    {
        for ( final Object query : this.queries )
        {
            ( (QueryBuffer)query ).close ();
        }
        this.queries.clear ();
    }

    public IObservableSet getQueriesObservable ()
    {
        return Observables.proxyObservableSet ( this.queries );
    }

    public void createQuery ( final String id )
    {
        logger.info ( "Create new query for: {}", id ); //$NON-NLS-1$

        final QueryBufferBean query = new QueryBufferBean ( this, id );
        this.queries.add ( query );
    }

    protected void fakeIt ( final QueryBufferBean query )
    {
        this.queries.setStale ( true );
        this.queries.remove ( query );
        this.queries.add ( query );
        this.queries.setStale ( false );
    }

    /**
     * Remove the query and close it
     * @param queryBufferBean the query to remove and close
     */
    public void removeQuery ( final QueryBufferBean queryBufferBean )
    {
        this.queries.remove ( queryBufferBean );
        queryBufferBean.close ();
    }
}
