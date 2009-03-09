/**
 * 
 */
package org.openscada.da.client.test.views.watch;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.openscada.da.client.base.browser.ValueType;
import org.openscada.da.client.base.browser.VariantHelper;

class WatchViewLabelProvider extends LabelProvider implements ITableLabelProvider
{
    public String getColumnText ( Object obj, int index )
    {
        if ( ! ( obj instanceof WatchAttributeEntry ) )
            return "";

        WatchAttributeEntry entry = (WatchAttributeEntry)obj;

        switch ( index )
        {
        case 0:
            return entry.name;
        case 1:
            ValueType vt = VariantHelper.toValueType ( entry.value );
            if ( vt != null )
                return vt.toString ();
            else
                return "VT_UNKNOWN";
        case 2:
            return entry.value.asString ( "null" );
        }
        return getText ( obj );
    }

    public Image getColumnImage ( Object obj, int index )
    {
        if ( index == 0 )
            return getImage ( obj );
        else
            return null;
    }

    public Image getImage ( Object obj )
    {
        return PlatformUI.getWorkbench ().getSharedImages ().getImage ( ISharedImages.IMG_OBJ_ELEMENT );
    }
}