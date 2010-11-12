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

import java.util.GregorianCalendar;
import java.util.Random;
import java.util.UUID;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.BeforeClass;
import org.openscada.ae.Event;
import org.openscada.ae.Event.EventBuilder;
import org.openscada.ae.server.storage.Storage;
import org.openscada.ae.server.storage.jdbc.internal.Activator;
import org.openscada.ae.server.storage.jdbc.internal.MutableEvent;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.jdbc.core.JdbcTemplate;

public class JdbcStorageBaseTest
{
    public static final String TABLE_EVENTS = "org_openscada_ae_server_storage_events";

    public static final String TABLE_EVENT_ATTRIBS = "org_openscada_ae_server_storage_event_attribs";

    public final static long SERVICE_TRACKER_TIMEOUT = 5000l;

    public static BeanFactory appContext;

    @BeforeClass
    public static void start () throws Exception
    {
        final BundleContext bundleContext = Activator.getBundleContext ();

        // get spring application context
        final ServiceTracker stSpring = new ServiceTracker ( bundleContext, bundleContext.createFilter ( "(objectClass=org.springframework.context.ApplicationContext)" ), null );
        stSpring.open ();
        appContext = (BeanFactory)stSpring.waitForService ( SERVICE_TRACKER_TIMEOUT );
        stSpring.close ();
    }

    @Before
    public void prepareDatabase () throws Exception
    {
        getJdbcTemplate ().execute ( "DELETE FROM " + TABLE_EVENT_ATTRIBS );
        getJdbcTemplate ().execute ( "DELETE FROM " + TABLE_EVENTS );
    }

    public JdbcTemplate getJdbcTemplate () throws Exception
    {
        return new JdbcTemplate ( (DataSource)appContext.getBean ( "dataSource" ) );
    }

    public Storage getStorage ()
    {
        return (Storage)appContext.getBean ( "jdbcStorage" );
    }

    public UUID makeUUID ( final long seed ) throws Exception
    {
        final Random rnd = new Random ( seed );
        final long l1 = rnd.nextLong ();
        final long l2 = rnd.nextLong ();
        return UUID.nameUUIDFromBytes ( ( Long.toBinaryString ( l1 ) + Long.toBinaryString ( l2 ) ).getBytes ( "ASCII" ) );
    }

    public Event makeEvent ( final int nr ) throws Exception
    {
        final EventBuilder eb = Event.create ();
        eb.sourceTimestamp ( new GregorianCalendar ().getTime () );
        eb.attribute ( MutableEvent.Fields.SOURCE.getName (), "TEST" );
        eb.attribute ( MutableEvent.Fields.PRIORITY.getName (), 5 );
        eb.attribute ( MutableEvent.Fields.EVENT_TYPE.getName (), "TEST" );
        eb.attribute ( "nr", nr );
        return eb.build ();
    }
}
