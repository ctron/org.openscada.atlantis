package org.openscada.hd.server.storage.internal;

import org.openscada.hd.Query;
import org.openscada.hd.QueryListener;
import org.openscada.hd.QueryParameters;
import org.openscada.hd.server.storage.ShiService;

/**
 * This is the internal implementation of the HD query interface.
 * @see org.openscada.hd.Query
 * @author Ludwig Straub
 */
public class QueryImpl implements Query
{
    /** Service that created the query object. */
    private final ShiService service;

    /** Listener that should receive the data. */
    private final QueryListener listener;

    /** Input parameters of the query. */
    private QueryParameters parameters;

    /** Flag indicating whether the result should be periodically updated or not. */
    private final boolean updateData;

    /**
     * Constructor.
     * @param service service that created the query object
     * @param listener listener that should receive the data
     * @param parameters input parameters of the query
     * @param updateData flag indicating whether the result should be periodically updated or not
     */
    public QueryImpl ( final ShiService service, final QueryListener listener, final QueryParameters parameters, final boolean updateData )
    {
        this.service = service;
        this.listener = listener;
        this.parameters = parameters;
        this.updateData = updateData;
    }

    /**
     * @see org.openscada.hd.Query#changeParameters
     */
    public void changeParameters ( QueryParameters parameters )
    {
        this.parameters = parameters;
    }

    /**
     * @see org.openscada.hd.Query#close
     */
    public void close ()
    {
    }
}
