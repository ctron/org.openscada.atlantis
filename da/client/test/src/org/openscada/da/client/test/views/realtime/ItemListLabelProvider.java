package org.openscada.da.client.test.views.realtime;

import org.eclipse.jface.resource.ColorDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.openscada.da.client.test.impl.VariantHelper;
import org.openscada.da.client.test.views.realtime.ListEntry.AttributePair;

public class ItemListLabelProvider extends LabelProvider implements ITableLabelProvider, ITableFontProvider, ITableColorProvider
{
    
    private ResourceManager resourceManager = new LocalResourceManager ( JFaceResources.getResources () );
    
    public Image getColumnImage ( Object element, int columnIndex )
    {
        if ( columnIndex == 0 && element instanceof ListEntry )
        {
            ListEntry entry = (ListEntry)element;
            if ( isError ( entry ) )
            {
                return resourceManager.createImage ( ImageDescriptor.createFromFile ( ItemListLabelProvider.class, "icons/alarm.png" ) );
            }
        }
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
                    return String.format ( "%s (%s)", listEntry.getSubscriptionChange (), listEntry.getSubscriptionError ().getMessage () );
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
        return null;
    }

    private boolean isAttribute ( ListEntry entry, String attributeName, boolean defaultValue )
    {
        for ( AttributePair pair : entry.getAttributes () )
        {
            if ( pair.key.equals ( attributeName ) )
            {
                return pair.value.asBoolean ();
            }
        }
        return defaultValue;
    }
    
    public Color getBackground ( Object element, int columnIndex )
    {
        if ( element instanceof ListEntry )
        {
            ListEntry entry = (ListEntry)element;
            if ( isError ( entry ) )
            {
                return resourceManager.createColor ( ColorDescriptor.createFrom ( new RGB ( 255, 255, 0 ) ) );
            }
            else if ( isAlarm ( entry ) )
            {
                return resourceManager.createColor ( ColorDescriptor.createFrom ( new RGB ( 255, 0, 0 ) ) );
            }
        }
        return null;
    }

    public Color getForeground ( Object element, int columnIndex )
    {
        if ( element instanceof ListEntry )
        {
            ListEntry entry = (ListEntry)element;
            if ( isError ( entry ) )
            {
                return null;
            }
            else if ( isManual ( entry )  )
            {
                return resourceManager.createColor ( ColorDescriptor.createFrom ( new RGB ( 0, 0, 255 ) ) );
            }
        }
        return null;
    }
    
    private boolean isManual ( ListEntry entry )
    {
        return isAttribute ( entry, "org.openscada.da.manual.active", false );
    }
    
    private boolean isAlarm ( ListEntry entry )
    {
        return isAttribute ( entry, "alarm", false );
    }
    
    private boolean isError ( ListEntry entry )
    {
        return isAttribute ( entry, "error", false );
    }
    
    @Override
    public void dispose ()
    {
        resourceManager.dispose ();
        super.dispose ();
    }

}
