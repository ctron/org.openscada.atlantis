package org.openscada.da.client.viewer.model.types;

import java.beans.PropertyEditorSupport;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.draw2d.geometry.Point;

public class PointListEditor extends PropertyEditorSupport
{
    private static final Pattern pattern = Pattern.compile ( "\\(([0-9]+,[0-9]+)\\)(,|$)" );

    @Override
    public void setAsText ( String text ) throws IllegalArgumentException
    {
        PointList list = new PointList ();

        Matcher m = pattern.matcher ( text );

        while ( m.find () )
        {
            String touple = m.group (1);
            String[] tok = touple.split ( "," );
            if ( tok.length != 2 )
            {
                throw new IllegalArgumentException ( String.format ( "Point list entry has length not equal to 2 (%d)",
                        tok.length ) );
            }
            Point p = new Point ();
            try
            {
                p.x = Integer.valueOf ( tok[0] );
                p.y = Integer.valueOf ( tok[1] );
            }
            catch ( NumberFormatException e )
            {
                throw new IllegalArgumentException ( "Touple contains invalid data", e );
            }
            list.addPoint ( p );
        }

        setValue ( list );
    }

    @Override
    public String getAsText ()
    {
        PointList list = (PointList)getValue ();

        int i = 0;
        StringBuilder sb = new StringBuilder ();
        for ( Point p : list.toList () )
        {
            if ( i > 0 )
            {
                sb.append ( "," );
            }
            sb.append ( "(" );
            sb.append ( p.x );
            sb.append ( "," );
            sb.append ( p.y );
            sb.append ( ")" );
            i++;
        }

        return sb.toString ();
    }
}
