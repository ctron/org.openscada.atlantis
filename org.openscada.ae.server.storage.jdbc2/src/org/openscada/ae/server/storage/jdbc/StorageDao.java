package org.openscada.ae.server.storage.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.UUID;

import org.openscada.ae.Event;
import org.openscada.utils.filter.Filter;

public interface StorageDao
{

    public void storeEvent ( Event event ) throws Exception;

    public void updateComment ( UUID id, String comment ) throws Exception;

    public Event loadEvent ( UUID id ) throws SQLException;

    public ResultSet queryEvents ( Filter filter ) throws SQLException, NotSupportedException;

    public boolean toEventList ( ResultSet rs, Collection<Event> events, boolean isBeforeFirst, long count ) throws SQLException;

}