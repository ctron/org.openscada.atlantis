package org.openscada.da.client.viewer.model.types;

import java.beans.PropertyEditorSupport;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorEditor extends PropertyEditorSupport
{
    public static Pattern p = Pattern.compile ( "#([0-9a-fA-F]{2})([0-9a-fA-F]{2})([0-9a-fA-F]{2})" ); 
    
    @Override
    public void setAsText ( String text ) throws IllegalArgumentException
    {
        Matcher m = p.matcher ( text );
        if ( !m.matches () )
            throw new IllegalArgumentException ( "Color must be in HTML style e.g. #AABBEE" );
        
        Color c = new Color ();
        c.setRed ( Integer.decode ( "#" + m.group ( 1 ) ) );
        c.setGreen ( Integer.decode ( "#" + m.group ( 2 ) ) );
        c.setBlue ( Integer.decode ( "#" + m.group ( 3 ) ) );
        
        setValue ( c );
    }
    
    @Override
    public String getAsText ()
    {
        Color color = (Color)getValue ();
        
        return String.format ( "#%X%X%X", color.getRed (), color.getGreen (), color.getBlue () );
    }
}
