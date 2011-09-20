/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.ae.server.storage.jdbc.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Test;
import org.openscada.ae.Event;
import org.openscada.ae.server.storage.Query;
import org.openscada.ae.server.storage.jdbc.internal.MutableEvent;
import org.openscada.core.Variant;

public class JdbcStorageTest extends JdbcStorageBaseTest
{
    private final static int MAX_ELEMENTS_TO_STORE = 10000;

    @Test
    public void testStore () throws Exception
    {
        Event event;
        event = Event.create ().sourceTimestamp ( new GregorianCalendar ().getTime () ).attribute ( MutableEvent.Fields.SOURCE.getName (), "TEST" ).attribute ( MutableEvent.Fields.PRIORITY.getName (), 5 ).attribute ( Event.Fields.EVENT_TYPE.getName (), "TEST" ).build ();
        final Event result = getStorage ().store ( event );
        final Query query = getStorage ().query ( "(id=" + result.getId () + ")" );
        final List<Event> list = new ArrayList<Event> ( query.getNext ( 1 ) );
        assertNotNull ( list );
        assertEquals ( 1, list.size () );
        assertEquals ( result.getId (), list.get ( 0 ).getId () );
        assertEquals ( Variant.valueOf ( 5 ), list.get ( 0 ).getAttributes ().get ( Event.Fields.PRIORITY.getName () ) );
    }

    @Test
    public void testMassStorage () throws Exception
    {
        for ( int i = 0; i < MAX_ELEMENTS_TO_STORE; i++ )
        {
            final Event event = makeEvent ( i );
            final Event result = getStorage ().store ( event );
            System.out.println ( result );
        }
    }
}
