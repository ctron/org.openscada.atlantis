package org.openscada.ae.server.storage.jdbc.internal;

import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;
import org.springframework.orm.hibernate3.LocalSessionFactoryBean;

/**
 * This derived {@link LocalSessionFactoryBean} is needed because hibernate treats
 * a schema which contains only of an empty space just like any other name. So this
 * implementation removes the property <code>hibernate.default_schema</code> if
 * it is empty
 * 
 * @author jrose
 */
public class CustomSessionFactoryBean extends LocalSessionFactoryBean
{

    public CustomSessionFactoryBean ()
    {
        super ();
    }

    @Override
    protected void postProcessConfiguration ( final Configuration config ) throws HibernateException
    {
        super.postProcessConfiguration ( config );
        if ( config.getProperty ( "hibernate.default_schema" ) != null && "".equals ( config.getProperty ( "hibernate.default_schema" ).trim () ) )
        {
            config.getProperties ().remove ( "hibernate.default_schema" );
        }
    }
}
