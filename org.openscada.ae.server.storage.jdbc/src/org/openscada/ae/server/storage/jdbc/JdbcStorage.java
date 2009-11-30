package org.openscada.ae.server.storage.jdbc;

import java.util.GregorianCalendar;
import java.util.UUID;

import org.openscada.ae.Event;
import org.openscada.ae.server.storage.Query;
import org.openscada.ae.server.storage.Storage;
import org.openscada.ae.server.storage.jdbc.internal.JdbcStorageDAO;
import org.openscada.ae.server.storage.jdbc.internal.MutableEvent;
import org.openscada.utils.filter.FilterParser;

public class JdbcStorage implements Storage
{

    private JdbcStorageDAO jdbcStorageDAO;

    public JdbcStorageDAO getJdbcStorageDAO ()
    {
        return jdbcStorageDAO;
    }

    public void setJdbcStorageDAO ( JdbcStorageDAO jdbcStorageDAO )
    {
        this.jdbcStorageDAO = jdbcStorageDAO;
    }

    public void start () throws Exception
    {
        System.out.println ( "JDBC Storage instanciated!!! " );
    }

    public void stop () throws Exception
    {
        System.out.println ( "JDBC Storage destroyed!!" );
    }

    public Query query ( String filter ) throws Exception
    {
        return new JdbcQuery(jdbcStorageDAO, new FilterParser(filter).getFilter());
    }

    public Event store ( Event event )
    {
        Event result = Event.create ().event ( event ).id ( UUID.randomUUID () ).entryTimestamp ( new GregorianCalendar ().getTime () ).build ();
        storeAsync ( result );
        return result;
    }

    private void storeAsync ( Event result )
    {
        // FIXME: it is not async yet!!!
        jdbcStorageDAO.storeEvent ( MutableEvent.fromEvent ( result ) );
    }
}
