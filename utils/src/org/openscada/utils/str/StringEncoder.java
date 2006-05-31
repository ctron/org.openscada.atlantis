package org.openscada.utils.str;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class StringEncoder
{
    public static String encode ( String s )
    {
        try
        {
            return URLEncoder.encode ( s, "utf-8" );
        }
        catch ( UnsupportedEncodingException e )
        {
            return s;
        }    
    }
    
    public static String decode ( String s )
    {
        try
        {
            return URLDecoder.decode ( s, "utf-8" );
        }
        catch ( UnsupportedEncodingException e )
        {
            return s;
        }   
    }
}
