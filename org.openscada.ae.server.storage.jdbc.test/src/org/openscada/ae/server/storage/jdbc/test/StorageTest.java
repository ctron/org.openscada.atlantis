package org.openscada.ae.server.storage.jdbc.test;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openscada.ae.Event;
import org.openscada.ae.server.storage.Query;
import org.openscada.ae.server.storage.Storage;
import org.openscada.ae.server.storage.jdbc.internal.Activator;
import org.openscada.ae.server.storage.jdbc.internal.MutableEvent;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class StorageTest
{
    private BundleContext bundleContext;

    private ServiceTracker serviceTrackerStorage;

    private ServiceTracker serviceTrackerStorageTestDAO;

    @Before
    public void start () throws Exception
    {
        this.bundleContext = Activator.getBundleContext ();
        serviceTrackerStorage = new ServiceTracker ( bundleContext, this.bundleContext.createFilter ( "(objectClass=" + Storage.class.getCanonicalName () + ")"  ), null );
        serviceTrackerStorage.open ();
        serviceTrackerStorageTestDAO = new ServiceTracker ( bundleContext, this.bundleContext.createFilter ( "(objectClass=" + JdbcStorageTestDAO.class.getCanonicalName () + ")"  ), null );
        serviceTrackerStorageTestDAO.open ();
    }

    @After
    public void end () throws Exception
    {
        serviceTrackerStorage.close ();
        serviceTrackerStorage = null;
        serviceTrackerStorageTestDAO.close ();
        serviceTrackerStorageTestDAO = null;
    }

    private Storage getStorage () throws Exception
    {
        return (Storage)serviceTrackerStorage.waitForService ( 30000 );
    }

    private JdbcStorageTestDAO getStorageTestDAO () throws Exception
    {
        return (JdbcStorageTestDAO)serviceTrackerStorageTestDAO.waitForService ( 30000 );
    }

    @Test
    public void testSetup () throws Exception
    {
        assertNotNull ( bundleContext );
        assertNotNull ( serviceTrackerStorage );
        assertNotNull ( getStorage () );
    }
    
    @Test
    public void testStore () throws  Exception
    {
        Storage storage = getStorage ();
        UUID id = UUID.fromString ( "e216f5cf-4968-46a5-8884-e71b1390d1a1" );
        JdbcStorageTestDAO jdbcStorageTestDAO = getStorageTestDAO ();
        jdbcStorageTestDAO.storeEvent ( MutableEvent.fromEvent ( Event.create ().id ( id ).build () ) );
        List<Event> l = jdbcStorageTestDAO.findAll ();
        assertNotNull ( l );
        System.out.println (l);
        assertEquals ( 1, l.size () );
        System.out.println (l.get ( 0 ));
        Query query = storage.query ( "(id=" + id.toString () + ")" );
        Collection<Event> results = query.getNext ( 100 );
        Iterator<Event> it = results.iterator ();
        Event event = it.next ();
        assertNotNull ( event );
        assertEquals ( id, event.getId () );
    }
}
