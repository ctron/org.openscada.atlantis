package org.openscada.ae.server.storage.jdbc.internal;

import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.CompositeCustomType;
import org.hibernate.type.CustomType;
import org.openscada.core.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class JdbcStorageDAOImpl extends HibernateTemplate implements JdbcStorageDAO
{
    private static final Logger logger = LoggerFactory.getLogger ( JdbcStorageDAOImpl.class );

    public MutableEvent loadEvent ( final UUID id )
    {
        logger.debug ( "loadEvent: {}", id );
        return (MutableEvent)this.get ( MutableEvent.class, id );
    }

    @SuppressWarnings ( "unchecked" )
    public List<MutableEvent> queryEvent ( final String hql, final Object... parameters )
    {
        logger.debug ( "queryEvent: {} ({})", hql, parameters );
        return (List<MutableEvent>)executeWithNativeSession ( new HibernateCallback () {
            public Object doInHibernate ( final Session paramSession ) throws HibernateException, SQLException
            {
                final Query q = getSession ().createQuery ( hql );
                int i = 0;
                for ( final Object object : parameters )
                {
                    if ( object instanceof UUID )
                    {
                        q.setParameter ( i, object, new CustomType ( UUIDHibernateType.class, new Properties () ) );
                    }
                    else if ( object instanceof Variant )
                    {
                        q.setParameter ( i, object, new CompositeCustomType ( VariantHibernateType.class, new Properties () ) );
                    }
                    else
                    {
                        q.setParameter ( i, object );
                    }
                    i += 1;
                }
                return q.list ();
            }
        } );
    }

    @SuppressWarnings ( "unchecked" )
    public List<MutableEvent> queryEventSlice ( final String hql, final int first, final int max, final Object... parameters )
    {
        logger.debug ( "queryEvent: {} from {} with {} elements ({})", new Object[] { hql, first, max, parameters } );
        return (List<MutableEvent>)executeWithNativeSession ( new HibernateCallback () {
            public Object doInHibernate ( final Session paramSession ) throws HibernateException, SQLException
            {
                final Query q = getSession ().createQuery ( hql );
                q.setFirstResult ( first );
                q.setMaxResults ( max );
                int i = 0;
                for ( final Object object : parameters )
                {
                    if ( object instanceof UUID )
                    {
                        q.setParameter ( i, object, new CustomType ( UUIDHibernateType.class, new Properties () ) );
                    }
                    else if ( object instanceof Variant )
                    {
                        q.setParameter ( i, object, new CompositeCustomType ( VariantHibernateType.class, new Properties () ) );
                    }
                    else
                    {
                        q.setParameter ( i, object );
                    }
                    i += 1;
                }
                return q.list ();
            }
        } );
    }

    public void storeEvent ( final MutableEvent event )
    {
        logger.debug ( "storeEvent: {}", MutableEvent.toEvent ( event ) );
        this.saveOrUpdate ( event );
        this.flush ();
    }
}
