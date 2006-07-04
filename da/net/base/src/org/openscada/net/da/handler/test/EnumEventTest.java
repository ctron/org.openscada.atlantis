/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
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

package org.openscada.net.da.handler.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.EnumSet;

import org.junit.Test;
import org.openscada.da.core.DataItemInformation;
import org.openscada.da.core.IODirection;
import org.openscada.da.core.common.DataItemInformationBase;
import org.openscada.net.da.handler.EnumEvent;

public class EnumEventTest
{

    @Test
    public void testCreate ()
    {
        
    }

    @Test
    public void testParse ()
    {
        
    }

    @Test
    public void testEncode ()
    {
        assertEquals ( "item 0", EnumEvent.encode ( new DataItemInformationBase ( "item", EnumSet.noneOf ( IODirection.class ) ) ) );
        assertEquals ( "item 1", EnumEvent.encode ( new DataItemInformationBase ( "item", EnumSet.of ( IODirection.INPUT) ) ) );
        assertEquals ( "item 2", EnumEvent.encode ( new DataItemInformationBase ( "item", EnumSet.of ( IODirection.OUTPUT) ) ) );
        assertEquals ( "item 3", EnumEvent.encode ( new DataItemInformationBase ( "item", EnumSet.of ( IODirection.OUTPUT, IODirection.INPUT ) ) ) );
        
        assertEquals ( "item+3 3", EnumEvent.encode ( new DataItemInformationBase ( "item 3", EnumSet.of ( IODirection.OUTPUT, IODirection.INPUT ) ) ) );
    }

    @Test
    public void testDecode ()
    {
        DataItemInformation info;
        
        info = EnumEvent.decode ( "" );
        assertNull ( info );
        
        info = EnumEvent.decode ( "item 0" );
        assertNotNull ( info );
        assertEquals ( info.getName(), "item" );
        assertEquals ( info.getIODirection ().size (), 0 );
        
        info = EnumEvent.decode ( "item 1" );
        assertNotNull ( info );
        assertEquals ( info.getName(), "item" );
        assertEquals ( info.getIODirection ().size (), 1 );
        assertTrue ( info.getIODirection ().contains ( IODirection.INPUT ) );
        
        info = EnumEvent.decode ( "item 2" );
        assertNotNull ( info );
        assertEquals ( info.getName(), "item" );
        assertEquals ( info.getIODirection ().size (), 1 );
        assertTrue ( info.getIODirection ().contains ( IODirection.OUTPUT ) );
        
        info = EnumEvent.decode ( "item 3" );
        assertNotNull ( info );
        assertEquals ( info.getName(), "item" );
        assertEquals ( info.getIODirection ().size (), 2 );
        assertTrue ( info.getIODirection ().contains ( IODirection.OUTPUT ) );
        assertTrue ( info.getIODirection ().contains ( IODirection.INPUT ) );
        
    }

}
