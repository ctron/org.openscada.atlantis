package org.openscada.utils.lang.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openscada.utils.str.StringEncoder;

public class EncoderTest
{

    @Test
    public void testEncode ()
    {
        assertEquals ( "abc", StringEncoder.encode ( "abc" ) );
        assertEquals ( "123", StringEncoder.encode ( "123" ) );
        assertEquals ( "", StringEncoder.encode ( "" ) );
        assertEquals ( "+", StringEncoder.encode ( " " ) );
        assertEquals ( "+++", StringEncoder.encode ( "   " ) );
        assertEquals ( "%2B", StringEncoder.encode ( "+" ) );
    }

    @Test
    public void testDecode ()
    {
        assertEquals ( "abc", StringEncoder.decode ( "abc" ) );
        assertEquals ( "123", StringEncoder.decode ( "123" ) );
        assertEquals ( "", StringEncoder.decode ( "" ) );
        assertEquals ( " ", StringEncoder.decode ( "+" ) );
        assertEquals ( "   ", StringEncoder.decode ( "+++" ) );
        assertEquals ( "+", StringEncoder.decode ( "%2B" ) );
    }
    
    private void testEqual ( String string )
    {
        assertEquals ( string, StringEncoder.decode ( StringEncoder.encode ( string ) ));
    }
    
    @Test
    public void testBoth ()
    {
        testEqual ( "abc" );
        testEqual ( "123" );
        testEqual ( "" );
        testEqual ( "%" );
        testEqual ( "%20" );
        testEqual ( " " );
        testEqual ( "+" );
    }

}
