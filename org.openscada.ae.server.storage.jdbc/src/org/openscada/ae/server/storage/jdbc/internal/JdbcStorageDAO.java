package org.openscada.ae.server.storage.jdbc.internal;

import java.util.List;
import java.util.UUID;

public interface JdbcStorageDAO
{
    public void storeEvent ( MutableEvent event );

    public MutableEvent loadEvent ( UUID id );

    public List<MutableEvent> queryEvent ( String hql, Object... parameters );
}
