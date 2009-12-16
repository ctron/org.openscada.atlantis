package org.openscada.ae.server.storage.jdbc.internal;

import java.util.List;
import java.util.UUID;

public interface JdbcStorageDAO
{
    public void storeEvent ( final MutableEvent event );

    public MutableEvent loadEvent ( final UUID id );

    public List<MutableEvent> queryEvent ( final String hql, final Object... parameters );

    public List<MutableEvent> queryEventSlice ( final String hql, final int first, final int max, final Object... parameters );
}
