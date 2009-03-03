/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
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

package org.openscada.ae.client.test.views;

import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.openscada.ae.client.test.impl.EventData;

public class QueryDataLabelProvider extends LabelProvider implements ITableLabelProvider, ITableFontProvider, ITableColorProvider
{
    private Font _boldFont = null;

    private Font _italicFont = null;

    private static final Color RED = new Color ( null, 255, 0, 0 );

    private static final Color GRAY = new Color ( null, 200, 200, 200 );

    private static final Color BLACK = new Color ( null, 0, 0, 0 );

    private static final Color YELLOW = new Color ( null, 255, 255, 0 );

    public QueryDataLabelProvider ( final Font defaultFont )
    {
        super ();

        createBoldFont ( defaultFont );
        createItalicFont ( defaultFont );
    }

    private void createBoldFont ( final Font defaultFont )
    {
        // create bold font
        final FontData[] fdset = defaultFont.getFontData ();
        for ( final FontData fd : fdset )
        {
            fd.setStyle ( SWT.BOLD );
        }
        this._boldFont = new Font ( defaultFont.getDevice (), fdset );
    }

    private void createItalicFont ( final Font defaultFont )
    {
        // create bold font
        final FontData[] fdset = defaultFont.getFontData ();
        for ( final FontData fd : fdset )
        {
            fd.setStyle ( SWT.ITALIC );
        }
        this._italicFont = new Font ( defaultFont.getDevice (), fdset );
    }

    @Override
    public void dispose ()
    {
        this._boldFont.dispose ();
        this._boldFont = null;
    }

    public Image getColumnImage ( final Object element, final int columnIndex )
    {
        return null;
    }

    public String getColumnText ( final Object element, final int columnIndex )
    {
        if ( element instanceof EventData )
        {
            final EventData event = (EventData)element;
            switch ( columnIndex )
            {
            case 0:
                return event.getEvent ().getId ();
            case 1:
                return String.format ( "%1$TF %1$TT %1$TN", event.getEvent ().getTimestamp () );
            default:
                return null;
            }
        }
        else if ( element instanceof QueryDataContentProvider.AttributePair )
        {
            final QueryDataContentProvider.AttributePair pair = (QueryDataContentProvider.AttributePair)element;
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

    public Font getFont ( final Object element, final int columnIndex )
    {
        if ( element instanceof EventData )
        {
            final EventData event = (EventData)element;
            if ( !event.getEvent ().getAttributes ().containsKey ( "severity" ) )
            {
                return null;
            }
            final String severity = event.getEvent ().getAttributes ().get ( "severity" ).asString ( "" ).toUpperCase ();

            if ( severity.equals ( "DEBUG" ) )
            {
                return this._italicFont;
            }
            else if ( severity.equals ( "FATAL" ) )
            {
                return this._boldFont;
            }
        }
        return null;
    }

    public Color getBackground ( final Object element, final int columnIndex )
    {
        if ( element instanceof EventData )
        {
            final EventData event = (EventData)element;
            if ( !event.getEvent ().getAttributes ().containsKey ( "severity" ) )
            {
                return null;
            }
            final String severity = event.getEvent ().getAttributes ().get ( "severity" ).asString ( "" ).toUpperCase ();

            if ( severity.equals ( "WARNING" ) )
            {
                return YELLOW;
            }
            else if ( severity.equals ( "ERROR" ) )
            {
                return RED;
            }
            else if ( severity.equals ( "FATAL" ) )
            {
                return BLACK;
            }
        }
        return null;
    }

    public Color getForeground ( final Object element, final int columnIndex )
    {
        if ( element instanceof EventData )
        {
            final EventData event = (EventData)element;
            if ( !event.getEvent ().getAttributes ().containsKey ( "severity" ) )
            {
                return null;
            }
            final String severity = event.getEvent ().getAttributes ().get ( "severity" ).asString ( "" ).toUpperCase ();

            if ( severity.equals ( "FATAL" ) )
            {
                return RED;
            }
            else if ( severity.equals ( "DEBUG" ) )
            {
                return GRAY;
            }
        }
        return null;
    }

}
