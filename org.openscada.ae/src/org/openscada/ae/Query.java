package org.openscada.ae;

/**
 * Must be automatically garbage collected if the {@link QueryListener} implementation
 * does not store the instance itself 
 * @author Jens Reimann
 * @author Jürgen Rose
 *
 */
public interface Query
{
    public void loadMore ( long count );

    public void dispose ();
}
