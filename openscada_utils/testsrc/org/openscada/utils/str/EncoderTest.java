/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2007 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openscada.utils.str;

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
        assertEquals ( string, StringEncoder.decode ( StringEncoder.encode ( string ) ) );
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
