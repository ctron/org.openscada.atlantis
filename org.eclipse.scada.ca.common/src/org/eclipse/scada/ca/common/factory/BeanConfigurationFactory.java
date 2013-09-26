/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.eclipse.scada.ca.common.factory;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtilsBean2;
import org.eclipse.scada.sec.UserInformation;
import org.eclipse.scada.utils.lang.Disposable;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A configuration factory that creates simple beans and applies the configuration using
 * setters.
 * <p>
 * If the created bean supports {@link Disposable} then the {@link Disposable#dispose()}
 * method will be called when the object is being removed from the factory. 
 * </p> 
 * @author Jens Reimann
 * @since 0.15.0
 *
 */
public class BeanConfigurationFactory extends AbstractServiceConfigurationFactory<BeanConfigurationFactory.BeanServiceInstance>
{
    private final static Logger logger = LoggerFactory.getLogger ( BeanConfigurationFactory.class );

    private final Class<?> beanClazz;

    private final boolean mergeIdField;

    /**
     * Create a new factory
     * @param context the bundle context
     * @param beanClazz the bean class to use
     * @param mergeIdField apply the configuration id as <q>id</q> if set the <code>true</code>
     */
    public BeanConfigurationFactory ( final BundleContext context, final Class<?> beanClazz, final boolean mergeIdField )
    {
        super ( context );
        this.beanClazz = beanClazz;
        this.mergeIdField = true;
    }

    public BeanConfigurationFactory ( final BundleContext context, final Class<?> beanClazz )
    {
        this ( context, beanClazz, true );
    }

    protected static class BeanServiceInstance
    {
        private final Object targetBean;

        public Object getTargetBean ()
        {
            return this.targetBean;
        }

        public Dictionary<String, ?> getProperties ()
        {
            try
            {
                final Dictionary<String, Object> result = new Hashtable<String, Object> ();

                final Map<?, ?> properties = new BeanUtilsBean2 ().describe ( this.targetBean );
                for ( final Map.Entry<?, ?> entry : properties.entrySet () )
                {
                    if ( entry.getValue () != null )
                    {
                        result.put ( entry.getKey ().toString (), entry.getValue () );
                    }
                }
                return result;
            }
            catch ( final Exception e )
            {
                logger.warn ( "Failed to get dictionary", e );
                return new Hashtable<String, Object> ( 1 );
            }
        }

        public BeanServiceInstance ( final Object targetBean )
        {
            this.targetBean = targetBean;
        }

        public void update ( final Map<String, String> parameters ) throws Exception
        {
            new BeanUtilsBean2 ().populate ( this.targetBean, parameters );
        }
    }

    @Override
    protected Entry<BeanServiceInstance> createService ( final UserInformation userInformation, final String configurationId, final BundleContext context, final Map<String, String> parameters ) throws Exception
    {
        final BeanServiceInstance bean = new BeanServiceInstance ( this.beanClazz.newInstance () );

        if ( this.mergeIdField )
        {
            parameters.put ( "id", configurationId );
        }

        bean.update ( parameters );

        final ServiceRegistration<?> reg = context.registerService ( this.beanClazz.getName (), bean.getTargetBean (), bean.getProperties () );

        return new Entry<BeanServiceInstance> ( configurationId, bean, reg );
    }

    @Override
    protected void disposeService ( final UserInformation userInformation, final String id, final BeanServiceInstance service )
    {
        if ( service instanceof Disposable )
        {
            ( (Disposable)service ).dispose ();
        }
    }

    @Override
    protected Entry<BeanConfigurationFactory.BeanServiceInstance> updateService ( final UserInformation userInformation, final String configurationId, final Entry<BeanConfigurationFactory.BeanServiceInstance> entry, final Map<String, String> parameters ) throws Exception
    {
        entry.getService ().update ( parameters );
        entry.getHandle ().setProperties ( entry.getService ().getProperties () );
        return null;
    }

}
