/**
 * 
 */
package org.openscada.da.client.test.views.realtime;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

public class RealTimeListComparator extends ViewerComparator
{
    @Override
    public int compare ( Viewer viewer, Object e1, Object e2 )
    {
        if ( e1 instanceof ListEntry && e2 instanceof ListEntry )
        {
            ListEntry l1 = (ListEntry)e1;
            ListEntry l2 = (ListEntry)e2;
            return l1.getDataItem ().getId ().compareTo ( l2.getDataItem ().getId () );
        }
        if ( e1 instanceof ListEntry.AttributePair && e2 instanceof ListEntry.AttributePair )
        {
            ListEntry.AttributePair l1 = (ListEntry.AttributePair)e1;
            ListEntry.AttributePair l2 = (ListEntry.AttributePair)e2;
            return l1.key.compareTo ( l2.key );
        }
        return super.compare ( viewer, e1, e2 );
    }
}