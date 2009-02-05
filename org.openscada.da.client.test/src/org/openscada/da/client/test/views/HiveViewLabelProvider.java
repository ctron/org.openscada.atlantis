/**
 * 
 */
package org.openscada.da.client.test.views;

import java.util.EnumSet;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.openscada.da.base.browser.BrowserEntry;
import org.openscada.da.base.browser.DataItemEntry;
import org.openscada.da.base.browser.FolderEntry;
import org.openscada.da.base.browser.HiveConnection;
import org.openscada.da.base.browser.HiveItem;
import org.openscada.da.client.test.Activator;
import org.openscada.da.client.test.ISharedImages;
import org.openscada.da.core.IODirection;

class HiveViewLabelProvider extends LabelProvider
{

    @Override
    public String getText ( final Object obj )
    {
        if ( obj instanceof HiveConnection )
        {
            final HiveConnection connection = (HiveConnection)obj;
            String label = connection.getConnectionInformation ().toString () + " (";
            if ( connection.isValid () )
            {
                label += connection.getConnection ().getState ().toString ();
            }
            else
            {
                label += "INVALID";
            }
            label += ")";
            return label;
        }
        else if ( obj instanceof HiveItem )
        {
            return ( (HiveItem)obj ).getId ();
        }
        else if ( obj instanceof BrowserEntry )
        {
            return ( (BrowserEntry)obj ).getName ();
        }
        return obj.toString ();
    }

    @Override
    public Image getImage ( final Object obj )
    {
        String imageKey;

        if ( obj instanceof HiveConnection )
        {
            final HiveConnection connection = (HiveConnection)obj;
            if ( !connection.isValid () )
            {
                imageKey = ISharedImages.IMG_HIVE_CONNECTION;
            }
            else if ( connection.isConnectionRequested () )
            {
                switch ( connection.getConnection ().getState () )
                {
                case CLOSED:
                    imageKey = ISharedImages.IMG_HIVE_DISCONNECTED;
                    break;
                case CONNECTED:
                    imageKey = ISharedImages.IMG_HIVE_CONNECTION;
                    break;
                case BOUND:
                    imageKey = ISharedImages.IMG_HIVE_CONNECTED;
                    break;
                default:
                    imageKey = ISharedImages.IMG_HIVE_DISCONNECTED;
                    break;
                }
            }
            else
            {
                imageKey = ISharedImages.IMG_HIVE_CONNECTION;
            }
        }
        else if ( obj instanceof DataItemEntry )
        {
            final DataItemEntry hiveItem = (DataItemEntry)obj;
            final EnumSet<IODirection> io = hiveItem.getIoDirection ();
            if ( io.containsAll ( EnumSet.of ( IODirection.INPUT, IODirection.OUTPUT ) ) )
            {
                imageKey = ISharedImages.IMG_HIVE_ITEM_IO;
            }
            else if ( io.contains ( IODirection.INPUT ) )
            {
                imageKey = ISharedImages.IMG_HIVE_ITEM_I;
            }
            else if ( io.contains ( IODirection.OUTPUT ) )
            {
                imageKey = ISharedImages.IMG_HIVE_ITEM_O;
            }
            else
            {
                imageKey = ISharedImages.IMG_HIVE_ITEM;
            }
        }
        else if ( obj instanceof FolderEntry )
        {
            imageKey = ISharedImages.IMG_HIVE_FOLDER;
        }
        else
        {
            return PlatformUI.getWorkbench ().getSharedImages ().getImage ( org.eclipse.ui.ISharedImages.IMG_OBJ_ELEMENT );
        }

        return Activator.getDefault ().getImageRegistry ().get ( imageKey );
    }
}