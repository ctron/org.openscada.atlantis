package org.openscada.ae.server.storage.jdbc.internal;

import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.DistinctRootEntityResultTransformer;
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

    private int maxLength = 0;

    @Override
    public MutableEvent loadEvent ( final UUID id )
    {
        logger.debug ( "loadEvent: {}", id );
        return (MutableEvent)this.get ( MutableEvent.class, id );
    }

    @Override
    @SuppressWarnings ( "unchecked" )
    public List<MutableEvent> queryEvent ( final String hql, final Object... parameters )
    {
        logger.debug ( "queryEvent: {} ({})", hql, parameters );
        return (List<MutableEvent>)executeWithNativeSession ( new HibernateCallback () {
            @Override
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

    @Override
    @SuppressWarnings ( "unchecked" )
    public List<MutableEvent> queryEventSlice ( final String hql, final int first, final int max, final Object... parameters )
    {
        logger.debug ( "queryEvent: {} from {} with {} elements ({})", new Object[] { hql, first, max, parameters } );
        return (List<MutableEvent>)executeWithNativeSession ( new HibernateCallback () {
            @Override
            public Object doInHibernate ( final Session paramSession ) throws HibernateException, SQLException
            {
                final Query q = getSession ().createQuery ( hql );
                q.setFirstResult ( first );
                q.setMaxResults ( max );
                q.setResultTransformer ( DistinctRootEntityResultTransformer.INSTANCE );
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

    @Override
    public void storeEvent ( final MutableEvent event )
    {
        logger.debug ( "storeEvent: {}", MutableEvent.toEvent ( event ) );
        if ( this.maxLength > 0 )
        {
            clipStrings ( event );
        }
        this.saveOrUpdate ( event );
        flush ();
    }

    private void clipStrings ( final MutableEvent event )
    {
        if ( event.getMonitorType () != null && event.getMonitorType ().length () > 32 )
        {
            event.setMonitorType ( event.getMonitorType ().substring ( 0, 32 ) );
        }
        if ( event.getEventType () != null && event.getEventType ().length () > 32 )
        {
            event.setEventType ( event.getEventType ().substring ( 0, 32 ) );
        }
        if ( event.getMessageCode () != null && event.getMessageCode ().length () > 255 )
        {
            event.setMessageCode ( event.getMessageCode ().substring ( 0, 255 ) );
        }
        if ( event.getMessage () != null && event.getMessage ().length () > 255 )
        {
            event.setMessage ( event.getMessage ().substring ( 0, 255 ) );
        }
        if ( event.getSource () != null && event.getSource ().length () > 255 )
        {
            event.setSource ( event.getSource ().substring ( 0, 255 ) );
        }
        if ( event.getActorName () != null && event.getActorName ().length () > 128 )
        {
            event.setActorName ( event.getActorName ().substring ( 0, 128 ) );
        }
        if ( event.getActorType () != null && event.getActorType ().length () > 32 )
        {
            event.setActorType ( event.getActorType ().substring ( 0, 32 ) );
        }
        if ( event.getValue () != null && event.getValue ().isString () && event.getValue ().asString ( "" ).length () > this.maxLength )
        {
            event.setValue ( new Variant ( event.getValue ().asString ( "" ).substring ( 0, this.maxLength ) ) );
        }
        for ( final String key : event.getAttributes ().keySet () )
        {
            final Variant value = event.getAttributes ().get ( key );
            if ( value != null && value.isString () && value.asString ( "" ).length () > this.maxLength )
            {
                event.getAttributes ().put ( key, new Variant ( value.asString ( "" ).substring ( 0, this.maxLength ) ) );
            }
        }
    }

    public int getMaxLength ()
    {
        return this.maxLength;
    }

    public void setMaxLength ( final int maxLength )
    {
        this.maxLength = maxLength;
    }
}
