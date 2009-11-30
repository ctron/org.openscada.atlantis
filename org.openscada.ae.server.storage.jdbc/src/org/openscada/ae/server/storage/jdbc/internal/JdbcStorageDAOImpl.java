package org.openscada.ae.server.storage.jdbc.internal;

import java.util.List;
import java.util.UUID;

import org.springframework.orm.hibernate3.HibernateTemplate;

public class JdbcStorageDAOImpl extends HibernateTemplate implements JdbcStorageDAO
{
    public MutableEvent loadEvent ( UUID id )
    {
        return (MutableEvent) this.get(MutableEvent.class, id);
    }

    @SuppressWarnings("unchecked")
    public List<MutableEvent> queryEvent ( String hql, Object... parameters )
    {
        return (List<MutableEvent>) this.find(hql, parameters);
    }

    public void storeEvent ( MutableEvent event )
    {
        this.save(event);
        this.flush();
    }
}
