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
    
    public QueryDataLabelProvider ( Font defaultFont )
    {
        super ();
        
        createBoldFont ( defaultFont );
        createItalicFont ( defaultFont );
    }
    
    private void createBoldFont ( Font defaultFont )
    {
        // create bold font
        FontData [] fdset = defaultFont.getFontData ();
        for ( FontData fd : fdset )
        {
            fd.setStyle ( SWT.BOLD );
        }
        _boldFont = new Font ( defaultFont.getDevice (), fdset );
    }
    
    private void createItalicFont ( Font defaultFont )
    {
        // create bold font
        FontData [] fdset = defaultFont.getFontData ();
        for ( FontData fd : fdset )
        {
            fd.setStyle ( SWT.ITALIC );
        }
        _italicFont = new Font ( defaultFont.getDevice (), fdset );
    }

    @Override
    public void dispose ()
    {
        _boldFont.dispose ();
        _boldFont = null;
    }
    
    public Image getColumnImage ( Object element, int columnIndex )
    {
        return null;
    }

    public String getColumnText ( Object element, int columnIndex )
    {
        if ( element instanceof EventData )
        {
            EventData event = (EventData)element;
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

    public Font getFont ( Object element, int columnIndex )
    {
        if ( element instanceof EventData )
        {
            EventData event = (EventData)element;
            if ( !event.getEvent ().getAttributes ().containsKey ( "severity" ) )
                return null;
            String severity = event.getEvent ().getAttributes ().get ( "severity" ).asString ( "" ).toUpperCase ();
            
            if ( severity.equals ( "DEBUG" ) )
            {
                return _italicFont;
            }
            else if ( severity.equals ( "FATAL" ) )
            {
                return _boldFont;
            }
        }
        return null;
    }

    public Color getBackground ( Object element, int columnIndex )
    {
        if ( element instanceof EventData )
        {
            EventData event = (EventData)element;
            if ( !event.getEvent ().getAttributes ().containsKey ( "severity" ) )
                return null;
            String severity = event.getEvent ().getAttributes ().get ( "severity" ).asString ( "" ).toUpperCase ();
            
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

    public Color getForeground ( Object element, int columnIndex )
    {
        if ( element instanceof EventData )
        {
            EventData event = (EventData)element;
            if ( !event.getEvent ().getAttributes ().containsKey ( "severity" ) )
                return null;
            String severity = event.getEvent ().getAttributes ().get ( "severity" ).asString ( "" ).toUpperCase ();
            
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
