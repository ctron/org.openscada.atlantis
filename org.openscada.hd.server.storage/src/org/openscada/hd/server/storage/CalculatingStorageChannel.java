package org.openscada.hd.server.storage;

import org.openscada.hd.server.storage.datatypes.LongValue;

/**
 * This class serves as base implementation for storage channel implementations providing calculation methods.
 * @author Ludwig Straub
 */
public class CalculatingStorageChannel extends SimpleStorageChannelManager
{
    /** Storage channel that is used as main channel when writing results of calculations. */
    private final StorageChannel baseStorageChannel;

    /** Storage channel that is used to request data if data is missing for instance after startup. */
    private final StorageChannel inputStorageChannel;

    /**
     * Fully initializing constructor.
     * @param baseStorageChannel storage channel that is used as main channel when writing results of calculations
     * @param inputStorageChannel storage channel that is used to request data if data is missing for instance after startup
     */
    public CalculatingStorageChannel ( final StorageChannel baseStorageChannel, final StorageChannel inputStorageChannel )
    {
        this.baseStorageChannel = baseStorageChannel;
        this.inputStorageChannel = inputStorageChannel;
    }

    /**
     * @see org.openscada.hd.server.storage.StorageChannel#updateLong
     */
    public synchronized void updateLong ( final LongValue longValue ) throws Exception
    {
        if ( baseStorageChannel != null )
        {
            baseStorageChannel.updateLong ( longValue );
        }
        super.updateLong ( longValue );
    }

    /**
     * @see org.openscada.hd.server.storage.StorageChannel#updateLongs
     */
    public synchronized void updateLongs ( final LongValue[] longValues ) throws Exception
    {
        if ( baseStorageChannel != null )
        {
            baseStorageChannel.updateLongs ( longValues );
        }
        super.updateLongs ( longValues );
    }

    /**
     * @see org.openscada.hd.server.storage.StorageChannel#getLongValues
     */
    public synchronized LongValue[] getLongValues ( final long startTime, final long endTime ) throws Exception
    {
        if ( baseStorageChannel != null )
        {
            return baseStorageChannel.getLongValues ( startTime, endTime );
        }
        if ( inputStorageChannel != null )
        {
            return inputStorageChannel.getLongValues ( startTime, endTime );
        }
        return EMPTY_LONGVALUE_ARRAY;
    }
}
