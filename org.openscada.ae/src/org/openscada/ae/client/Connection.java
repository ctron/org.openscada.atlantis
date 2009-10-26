package org.openscada.ae.client;

import java.util.Date;

import org.openscada.ae.BrowserListener;
import org.openscada.ae.QueryListener;

/**
 * Interface for client connection
 * @author Jens Reimann
 * @since 0.15.0
 *
 */
public interface Connection extends org.openscada.core.client.Connection
{
    // Conditions
    /**
     * Set the listener that should receive condition updates
     */
    public void setConditionListener ( String conditionQueryId, ConditionListener listener );

    // Event - online
    /**
     * Set the listener that should receive event updates
     */
    public void setEventListener ( String eventQueryId, EventListener listener );

    // Event - offline
    public void createQuery ( String queryType, String queryData, QueryListener listener );

    /**
     * Add browser listener
     * @param listener the listener to add
     */
    public void addBrowserListener ( BrowserListener listener );

    public void removeBrowserListener ( BrowserListener listener );

    /**
     * Acknowledge the condition if the akn state was reached at or before the provided timestamp
     * @param conditionId the id of the condition
     * @param aknTimestamp the timestamp up to which the state may be acknowledged
     */
    public void acknowledge ( String conditionId, Date aknTimestamp );
}
