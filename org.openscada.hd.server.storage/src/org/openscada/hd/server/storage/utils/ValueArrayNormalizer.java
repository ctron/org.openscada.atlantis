package org.openscada.hd.server.storage.utils;

import java.util.ArrayList;
import java.util.List;

import org.openscada.hd.server.storage.datatypes.BaseValue;

/**
 * This class provides methods for normalizing arrrays of base values.
 * Normalizing means, that all time differences of adjacent elements within the array are equal in size.
 * @author Ludwig Straub
 */
public class ValueArrayNormalizer
{
    /**
     * This method extracts a sub array out of the passed array of elements matching the specified criteria.
     * If the exact specified start time is not available in the array then a new virtual entry will be created at the beginning of the resulting array with the start time.
     * If the exact specified end time is not available in the array then a new virtual entry will be created at the beginning of the resulting array with the start time.
     * @param values values to be processed
     * @param startTime start time for extracting
     * @param endTime end time for extracting
     * @param emptyResultArray empty array that can be used as template for the result
     * @return normalized array
     */
    public static BaseValue[] extractSubArray ( BaseValue[] values, final long startTime, final long endTime, BaseValue[] emptyResultArray )
    {
        if ( ( values == null ) || ( values.length == 0 ) )
        {
            return emptyResultArray;
        }
        final List<BaseValue> blockValues = new ArrayList<BaseValue> ();
        int firstRelevantEntryIndex = 0;
        int lastRelevantEntryIndex = values.length;
        for ( int i = firstRelevantEntryIndex; i < values.length; i++ )
        {
            if ( values[i].getTime () <= startTime )
            {
                firstRelevantEntryIndex = i;
            }
            if ( values[i].getTime () >= endTime )
            {
                lastRelevantEntryIndex = i;
                break;
            }
        }
        final BaseValue firstValue = values[firstRelevantEntryIndex];
        final long firstStartTime = firstValue.getTime ();
        if ( firstStartTime != startTime )
        {
            if ( firstStartTime < startTime )
            {
                blockValues.add ( firstValue.createNewValue ( startTime, firstValue.getQualityIndicator (), firstValue.getBaseValueCount () ) );
            }
            else
            {
                blockValues.add ( firstValue.createNewValue ( startTime, 0, 0 ) );
            }
        }
        for ( int i = firstRelevantEntryIndex + 1; i < lastRelevantEntryIndex; i++ )
        {
            blockValues.add ( values[i] );
        }
        final BaseValue lastValue = blockValues.get ( blockValues.size () - 1 );
        if ( lastValue.getTime () != endTime )
        {
            blockValues.add ( lastValue.createNewValue ( endTime, lastValue.getQualityIndicator (), lastValue.getBaseValueCount () ) );
        }
        return blockValues.toArray ( emptyResultArray );
    }
}
