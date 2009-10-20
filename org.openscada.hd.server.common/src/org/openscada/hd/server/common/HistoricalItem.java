package org.openscada.hd.server.common;

import org.openscada.hd.HistoricalItemInformation;
import org.openscada.hd.Query;
import org.openscada.hd.QueryListener;
import org.openscada.hd.QueryParameters;
import org.openscada.hd.QueryState;

/**
 * An interface of a historical item
 * 
 * @author Jens Reimann
 *
 */
public interface HistoricalItem
{
    /**
     * Create a new query
     * @param parameters the initial query parameters
     * @param listener the query listener
     * @param updateData if <code>true</code> then additional updates will be made by the query when
     * data changes although the query is {@link QueryState#COMPLETE}
     * @return the new query or <code>null</code> if the query could be be created
     */
    public Query createQuery ( QueryParameters parameters, QueryListener listener, boolean updateData );

    /**
     * Get the item information
     * @return the item information
     */
    public HistoricalItemInformation getInformation ();
}
