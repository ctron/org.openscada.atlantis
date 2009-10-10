package org.openscada.hd.server.storage;

import org.openscada.hd.server.storage.datatypes.LongValue;

/**
 * This interface provides methods for storing and retrieving values of type long.
 * @author Ludwig Straub
 */
public interface StorageChannel
{
    /** Empty array of long values that is used when transforming a list to an array. */
    public final static LongValue[] EMPTY_LONGVALUE_ARRAY = new LongValue[0];

    /**
     * This method updates the passed long value.
     * If a value with the same time stamp already exists, the previous value will be replaced.
     * The implementation decides whether the data is processed or not.
     * See the documentation of the different implementations for more details.
     * @param longValue value that has to be updated
     * @throws Exception in case of read/write problems or file corruption
     */
    public abstract void updateLong ( LongValue longValue ) throws Exception;

    /**
     * This method updates the passed long values.
     * If a value with the same time stamp already exists, the previous value will be replaced.
     * The implementation decides whether the data is processed or not.
     * See the documentation of the different implementations for more details.
     * @param longValues values that have to be updated
     * @throws Exception in case of read/write problems or file corruption
     */
    public abstract void updateLongs ( final LongValue[] longValues ) throws Exception;

    /**
     * This method retrieves all long values that match the specified time span and returns them as array sorted by time.
     * If the lower bound cannot be satisfied by an exact value, the previous value that lies outside the time span will also be returned.
     * @param startTime start of the time span for which the values have to be retrieved
     * @param endTime end of the time span for which the values have to be retrieved
     * @return long values that match the specified time span
     * @throws Exception in case of read/write problems or file corruption
     */
    public abstract LongValue[] getLongValues ( long startTime, long endTime ) throws Exception;

    /**
     * This method deletes old data.
     * This method can only be called after the initialize method.
     * @throws Exception in case of any problem
     */
    public abstract void cleanupRelicts () throws Exception;
}
