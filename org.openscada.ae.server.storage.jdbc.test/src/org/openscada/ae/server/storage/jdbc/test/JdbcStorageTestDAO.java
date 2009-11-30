package org.openscada.ae.server.storage.jdbc.test;

import java.util.List;

import org.openscada.ae.Event;
import org.openscada.ae.server.storage.jdbc.internal.JdbcStorageDAO;

public interface JdbcStorageTestDAO extends JdbcStorageDAO
{
    List<Event> findAll();
}
