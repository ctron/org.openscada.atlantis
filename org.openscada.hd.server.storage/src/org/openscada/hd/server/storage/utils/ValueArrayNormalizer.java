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
     * @param values values to be processed
     * @param startTime start time for extracting
     * @param endTime end time for extracting
     * @param startIndex index of first element that is of interest (parameter available for optimization)
     * @param emptyResultArray empty array that can be used as template for the result
     * @return normalized array
     */
    public static BaseValue[] extractSubArray ( BaseValue[] values, final long startTime, final long endTime, final int startIndex, BaseValue[] emptyResultArray )
    {
        if ( ( values == null ) || ( values.length <= startIndex ) )
        {
            return emptyResultArray;
        }
        final List<BaseValue> blockValues = new ArrayList<BaseValue> ();
        int firstRelevantEntryIndex = startIndex;
        int lastRelevantEntryIndex = values.length;
        for ( int i = startIndex; i < values.length; i++ )
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
            blockValues.add ( firstValue.createNewValue ( startTime, firstStartTime < startTime ? firstValue.getQualityIndicator () : 0 ) );
        }
        for ( int i = firstRelevantEntryIndex + 1; i < lastRelevantEntryIndex; i++ )
        {
            blockValues.add ( values[i] );
        }
        return blockValues.toArray ( emptyResultArray );
    }
}
