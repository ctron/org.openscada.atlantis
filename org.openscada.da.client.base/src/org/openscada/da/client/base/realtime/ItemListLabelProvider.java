/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscada.da.client.base.realtime;

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
import org.openscada.da.client.base.browser.VariantHelper;
import org.openscada.da.client.base.realtime.ListEntry.AttributePair;

public class ItemListLabelProvider extends LabelProvider implements ITableLabelProvider, ITableFontProvider, ITableColorProvider
{

    private final ResourceManager resourceManager = new LocalResourceManager ( JFaceResources.getResources () );

    public Image getColumnImage ( final Object element, final int columnIndex )
    {
        if ( columnIndex == 0 && element instanceof ListEntry )
        {
            final ListEntry entry = (ListEntry)element;
            if ( isError ( entry ) )
            {
                try
                {
                    return this.resourceManager.createImage ( ImageDescriptor.createFromFile ( ItemListLabelProvider.class, "icons/alarm.png" ) ); //$NON-NLS-1$
                }
                catch ( final Throwable e )
                {
                    return null;
                }
            }
        }
        return null;
    }

    public String getColumnText ( final Object element, final int columnIndex )
    {
        if ( element instanceof ListEntry )
        {
            final ListEntry listEntry = (ListEntry)element;
            switch ( columnIndex )
            {
            case 0:
                return listEntry.getDataItem ().getItem ().getId ();
            case 1:
                if ( listEntry.getSubscriptionError () != null )
                {
                    return String.format ( "%s (%s)", listEntry.getSubscriptionState (), listEntry.getSubscriptionError ().getMessage () ); //$NON-NLS-1$
                }
                else
                {
                    return listEntry.getSubscriptionState ().name ();
                }
            case 2:
                if ( listEntry.getValue () != null )
                {
                    return VariantHelper.toValueType ( listEntry.getValue () ).name ();
                }
            case 3:
                if ( listEntry.getValue () != null )
                {
                    return listEntry.getValue ().asString ( "<null>" ); //$NON-NLS-1$
                }
            default:
                return null;
            }
        }
        else if ( element instanceof ListEntry.AttributePair )
        {
            final ListEntry.AttributePair ap = (ListEntry.AttributePair)element;
            switch ( columnIndex )
            {
            case 0:
                return ap.key;
            case 2:
                if ( ap.value != null )
                {
                    return VariantHelper.toValueType ( ap.value ).name ();
                }
            case 3:
                if ( ap.value != null )
                {
                    return ap.value.asString ( "<null>" ); //$NON-NLS-1$
                }
            default:
                return null;
            }
        }
        return null;
    }

    public Font getFont ( final Object element, final int columnIndex )
    {
        return null;
    }

    private boolean isAttribute ( final ListEntry entry, final String attributeName, final boolean defaultValue )
    {
        for ( final AttributePair pair : entry.getAttributes () )
        {
            if ( pair.key.equals ( attributeName ) )
            {
                return pair.value.asBoolean ();
            }
        }
        return defaultValue;
    }

    public Color getBackground ( final Object element, final int columnIndex )
    {
        try
        {
            if ( element instanceof ListEntry )
            {
                final ListEntry entry = (ListEntry)element;
                if ( isError ( entry ) )
                {
                    return this.resourceManager.createColor ( ColorDescriptor.createFrom ( new RGB ( 255, 255, 0 ) ) );
                }
                else if ( isAlarm ( entry ) )
                {
                    return this.resourceManager.createColor ( ColorDescriptor.createFrom ( new RGB ( 255, 0, 0 ) ) );
                }
            }
        }
        catch ( final Throwable e )
        {
        }

        return null;
    }

    public Color getForeground ( final Object element, final int columnIndex )
    {
        try
        {
            if ( element instanceof ListEntry )
            {
                final ListEntry entry = (ListEntry)element;
                if ( isError ( entry ) )
                {
                    return null;
                }
                else if ( isManual ( entry ) )
                {
                    return this.resourceManager.createColor ( ColorDescriptor.createFrom ( new RGB ( 0, 0, 255 ) ) );
                }
            }
        }
        catch ( final Throwable e )
        {
        }
        return null;
    }

    private boolean isManual ( final ListEntry entry )
    {
        return isAttribute ( entry, "org.openscada.da.manual.active", false ) || isAttribute ( entry, "manual", false ); //$NON-NLS-1$ $NON-NLS-2$
    }

    private boolean isAlarm ( final ListEntry entry )
    {
        return isAttribute ( entry, "alarm", false ); //$NON-NLS-1$
    }

    private boolean isError ( final ListEntry entry )
    {
        return isAttribute ( entry, "error", false ); //$NON-NLS-1$
    }

    @Override
    public void dispose ()
    {
        this.resourceManager.dispose ();
        super.dispose ();
    }

}
