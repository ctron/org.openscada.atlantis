package org.openscada.hd;

public enum QueryState
{
    /**
     * The query is loading data.
     */
    LOADING,
    /**
     * The query loaded all data. The data is complete up to now.
     */
    COMPLETE,
    /**
     * The query is disconnected and will not receive any more updates.
     */
    DISCONNECTED,
    /**
     * The query was created or changed on the client and is waiting
     * for creation or update.
     */
    REQUESTED
}
