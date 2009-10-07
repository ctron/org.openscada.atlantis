package org.openscada.hd.server.storage.backend.comparator;

import java.util.Comparator;

import org.openscada.hd.server.storage.StorageChannelMetaData;
import org.openscada.hd.server.storage.backend.BackEnd;

/**
 * Comparator that is used to sort storage channel meta data by time span.
 * @author Ludwig Straub
 */
public class InverseTimeOrderComparator implements Comparator<BackEnd>
{
    /**
     * @see java.util.Comparator#compare
     */
    public int compare ( final BackEnd o1, final BackEnd o2 )
    {
        if ( o1 == null )
        {
            return 1;
        }
        if ( o2 == null )
        {
            return -1;
        }
        StorageChannelMetaData m1 = null;
        try
        {
            m1 = o1.getMetaData ();
        }
        catch ( Exception e )
        {
            return 1;
        }
        StorageChannelMetaData m2 = null;
        try
        {
            m2 = o2.getMetaData ();
        }
        catch ( Exception e )
        {
            return -1;
        }
        if ( m1 == null )
        {
            return 1;
        }
        if ( m2 == null )
        {
            return -1;
        }
        final long endTime1 = m1.getEndTime ();
        final long endTime2 = m2.getEndTime ();
        if ( endTime1 < endTime2 )
        {
            return 1;
        }
        if ( endTime1 > endTime2 )
        {
            return -1;
        }
        final long startTime1 = m1.getStartTime ();
        final long startTime2 = m2.getStartTime ();
        if ( startTime1 < startTime2 )
        {
            return 1;
        }
        if ( startTime1 > startTime2 )
        {
            return -1;
        }
        return 0;
    }
}
