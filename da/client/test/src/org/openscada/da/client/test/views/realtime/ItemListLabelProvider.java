package org.openscada.da.client.test.views.realtime;

import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.openscada.da.client.test.impl.VariantHelper;

public class ItemListLabelProvider extends LabelProvider implements ITableLabelProvider, ITableFontProvider, ITableColorProvider
{

    public Image getColumnImage ( Object element, int columnIndex )
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String getColumnText ( Object element, int columnIndex )
    {
        if ( element instanceof ListEntry )
        {
            ListEntry listEntry = (ListEntry)element;
            switch ( columnIndex )
            {
            case 0:
                return listEntry.getDataItem ().getId ();
            case 1:
                if ( listEntry.getSubscriptionError () != null )
                {
                    return String.format ( "%s (%s)", listEntry.getSubscriptionChange (),
                            listEntry.getSubscriptionError ().getMessage () );
                }
                else
                {
                    return listEntry.getSubscriptionChange ().name ();
                }
            case 2:
                if ( listEntry.getValue () != null )
                    return VariantHelper.toValueType ( listEntry.getValue () ).name ();
            case 3:
                if ( listEntry.getValue () != null )
                    return listEntry.getValue ().asString ( "<null>" );
            default:
                return null;
            }
        }
        else if ( element instanceof ListEntry.AttributePair )
        {
            ListEntry.AttributePair ap = (ListEntry.AttributePair)element;
            switch ( columnIndex )
            {
            case 0:
                return ap.key;
            case 2:
                if ( ap.value != null )
                    return VariantHelper.toValueType ( ap.value ).name ();
            case 3:
                if ( ap.value != null )
                    return ap.value.asString ( "<null>" );
            default:
                return null;
            }
        }
        return null;
    }

    public Font getFont ( Object element, int columnIndex )
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Color getBackground ( Object element, int columnIndex )
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Color getForeground ( Object element, int columnIndex )
    {
        // TODO Auto-generated method stub
        return null;
    }

}
