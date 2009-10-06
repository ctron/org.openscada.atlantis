package org.openscada.hd.server.storage;

import org.openscada.hd.server.storage.ExtendedStorageChannel;
import org.openscada.hd.server.storage.StorageChannel;
import org.openscada.hd.server.storage.datatypes.DoubleValue;
import org.openscada.hd.server.storage.datatypes.LongValue;

/**
 * Adapter class for storage channel implementations class that provides the support of additional datatypes.
 * No checks are performed whether the stored data is compatible with the specified datatype.
 * For instance if data is stored as double, the data has to be retrieved as double in the future.
 * Each storage channel should therefore be fixed to exactly one datatype it supports.
 * That datatype setting should not be changed during the whole storage channel's lifespan.
 * @author Ludwig Straub
 */
public class ExtendedStorageChannelAdapter implements ExtendedStorageChannel
{
    /** Storage channel that will be used to store and retrieve data. */
    private StorageChannel storageChannel;

    /**
     * Constructor.
     * @param storageChannel storage channel that will be used to store and retrieve data
     */
    public ExtendedStorageChannelAdapter ( final StorageChannel storageChannel )
    {
        this.storageChannel = storageChannel;
    }

    /**
     * This method returns the storage channel that will be used to store and retrieve data.
     * @return storage channel that will be used to store and retrieve data
     */
    public StorageChannel getStorageChannel ()
    {
        return storageChannel;
    }

    /**
     * This method sets the storage channel that will be used to store and retrieve data.
     * @param storageChannel storage channel that will be used to store and retrieve data
     */
    public void setStorageChannel ( final StorageChannel storageChannel )
    {
        this.storageChannel = storageChannel;
    }

    /**
     * @see org.openscada.hd.server.storage.ExtendedStorageChannel#updateDouble
     */
    public synchronized void updateDouble ( final DoubleValue doubleValue ) throws Exception
    {
        if ( ( storageChannel != null ) && ( doubleValue != null ) )
        {
            storageChannel.updateLong ( new LongValue ( doubleValue.getTime (), doubleValue.getQualityIndicator (), Double.doubleToLongBits ( doubleValue.getValue () ) ) );
        }
    }

    /**
     * @see org.openscada.hd.server.storage.ExtendedStorageChannel#updateDoubles
     */
    public synchronized void updateDoubles ( final DoubleValue[] doubleValues ) throws Exception
    {
        if ( ( storageChannel != null ) && ( doubleValues != null ) )
        {
            LongValue[] longValues = new LongValue[doubleValues.length];
            for ( int i = 0; i < doubleValues.length; i++ )
            {
                DoubleValue doubleValue = doubleValues[i];
                longValues[i] = new LongValue ( doubleValue.getTime (), doubleValue.getQualityIndicator (), Double.doubleToLongBits ( doubleValue.getValue () ) );
            }
            storageChannel.updateLongs ( longValues );
        }
    }

    /**
     * @see org.openscada.hd.server.storage.ExtendedStorageChannel#getDoubleValues
     */
    public synchronized DoubleValue[] getDoubleValues ( final long startTime, final long endTime ) throws Exception
    {
        LongValue[] longValues = storageChannel != null ? storageChannel.getLongValues ( startTime, endTime ) : EMPTY_LONGVALUE_ARRAY;
        DoubleValue[] doubleValues = new DoubleValue[longValues.length];
        for ( int i = 0; i < longValues.length; i++ )
        {
            LongValue longValue = longValues[i];
            doubleValues[i] = new DoubleValue ( longValue.getTime (), longValue.getQualityIndicator (), Double.longBitsToDouble ( longValue.getValue () ) );
        }
        return doubleValues;
    }

    /**
     * @see org.openscada.hd.server.storage.StorageChannel#getLongValues
     */
    public synchronized LongValue[] getLongValues ( long startTime, long endTime ) throws Exception
    {
        return storageChannel != null ? storageChannel.getLongValues ( startTime, endTime ) : EMPTY_LONGVALUE_ARRAY;
    }

    /**
     * @see org.openscada.hd.server.storage.StorageChannel#updateLong
     */
    public synchronized void updateLong ( LongValue longValue ) throws Exception
    {
        if ( storageChannel != null )
        {
            storageChannel.updateLong ( longValue );
        }
    }

    /**
     * @see org.openscada.hd.server.storage.StorageChannel#updateLongs
     */
    public synchronized void updateLongs ( LongValue[] longValues ) throws Exception
    {
        if ( storageChannel != null )
        {
            storageChannel.updateLongs ( longValues );
        }
    }
}
