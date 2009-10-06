package org.openscada.hd.server.storage;

/**
 * This interface extends the StorageChannel interface with methods for managing StorageChannel objects.
 * This can be useful when complex storage channel sturctures have to be created and handled.
 * @author Ludwig Straub
 */
public interface StorageChannelManager extends ExtendedStorageChannel
{
    /**
     * This method adds a sub storage channel to the current channel.
     * All data the current channel receives will also be propagated to the sub channel
     * @param storageChannel storage channel that has to be added to the current channel
     */
    public abstract void registerStorageChannel ( ExtendedStorageChannel storageChannel );

    /**
     * This method removes a sub storage channel from the current channel.
     * All data the current channel receives will also be propagated to the sub channel
     * @param storageChannel storage channel that has to be added to the current channel
     */
    public abstract void unregisterStorageChannel ( ExtendedStorageChannel storageChannel );
}
