package org.openscada.ae;

/**
 * Must be automatically garbage collected if the {@link QueryListener} implementation
 * does not store the instance itself 
 * @author Jens Reimann
 * @author Jürgen Rose
 * @since 0.15.0
 */
public interface Query
{
    /**
     * Load more data
     * @param count the number of entries to load, must be greater than zero
     * @throws IllegalArgumentException if the count is negative or zero
     */
    public void loadMore ( int count );

    public void close ();
}
