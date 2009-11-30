package org.openscada.ae.server.storage.jdbc.test;

import java.util.List;

import org.openscada.ae.Event;
import org.openscada.ae.server.storage.jdbc.internal.JdbcStorageDAOImpl;
import org.openscada.ae.server.storage.jdbc.internal.MutableEvent;

public class JdbcStorageTestDAOImpl extends JdbcStorageDAOImpl implements JdbcStorageTestDAO
{
    public void start ()
    {
        System.out.println ("started DAO");
    }
    
    public void stop ()
    {
        System.out.println ("stopped DAO");
    }
    
    public List<Event> findAll ()
    {
        return this.loadAll ( MutableEvent.class );
    }
}
