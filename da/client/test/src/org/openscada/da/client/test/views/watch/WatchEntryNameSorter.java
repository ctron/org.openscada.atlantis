/**
 * 
 */
package org.openscada.da.client.test.views.watch;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

class WatchEntryNameSorter extends ViewerSorter
{
    @Override
    public int compare ( Viewer viewer, Object e1, Object e2 )
    {
        if ( e1 instanceof WatchAttributeEntry && e2 instanceof WatchAttributeEntry )
        {
            return ((WatchAttributeEntry)e1).compareTo ( (WatchAttributeEntry)e2 );
        }
        return super.compare ( viewer, e1, e2 );
    }
}