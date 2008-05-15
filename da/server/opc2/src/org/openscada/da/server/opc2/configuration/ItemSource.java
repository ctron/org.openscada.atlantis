package org.openscada.da.server.opc2.configuration;


public interface ItemSource
{
    /**
     * Activate processing of the item source.
     * <p>
     * The item source may only call
     * the listener methods when the activate method has been called. Also must
     * all listeners be registered with this item source it they want to received
     * events from the beginning. 
     */
    public void activate ();
    public void deactivate ();
    
    /**
     * Add a listener to the item source.
     * <p>
     * Be sure to register all listeners prior calling the {@link #activate()} method
     * @param listener the listener to add
     */
    public void addListener ( ItemSourceListener listener );
    public void removeListener ( ItemSourceListener listener );
}
