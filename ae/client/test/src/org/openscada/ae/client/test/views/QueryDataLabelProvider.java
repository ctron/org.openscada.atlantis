package org.openscada.ae.client.test.views;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.openscada.ae.core.Event;

public class QueryDataLabelProvider extends LabelProvider implements ITableLabelProvider
{

    public Image getColumnImage ( Object element, int columnIndex )
    {
        return null;
    }

    public String getColumnText ( Object element, int columnIndex )
    {
        if ( element instanceof Event )
        {
            Event event = (Event)element;
            switch ( columnIndex )
            {
            case 0:
                return event.getId ();  
            default:
                return null;
            }
        }
        else if ( element instanceof QueryDataContentProvider.AttributePair )
        {
            QueryDataContentProvider.AttributePair pair = (QueryDataContentProvider.AttributePair)element;
            switch ( columnIndex )
            {
            case 0:
                return pair._key;
            case 1:
                return pair._value.toString ();
            default:
                return null;    
            }
        }
        return null;
    }

}
