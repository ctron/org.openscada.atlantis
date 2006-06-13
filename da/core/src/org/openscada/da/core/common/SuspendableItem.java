package org.openscada.da.core.common;

/**
 * Interface for suspendable items
 * <p>
 * If a DataItem implements this interface it may suspend value collection
 * when the {@link #suspend()} method is called and must restart the value collection
 * when {@link #wakeup()} is called.
 * <p>
 * Only the automatic value collection may be suspended. Read or write requests must
 * be processed at any time!
 * @author jens
 *
 */
public interface SuspendableItem
{
    /**
     * Called <em>before</em> the first listener is subscribed
     *
     */
    void wakeup ();
    /**
     * Called <em>after</em> the last listner is unsubscribed
     *
     */
    void suspend ();
}
