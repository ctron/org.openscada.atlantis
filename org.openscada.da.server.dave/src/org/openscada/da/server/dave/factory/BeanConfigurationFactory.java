package org.openscada.da.server.dave.factory;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtilsBean2;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanConfigurationFactory extends AbstractServiceConfigurationFactory
{
    private final static Logger logger = LoggerFactory.getLogger ( BeanConfigurationFactory.class );

    private final Class<?> beanClazz;

    public BeanConfigurationFactory ( final BundleContext context, final Class<?> beanClazz )
    {
        super ( context );
        this.beanClazz = beanClazz;
    }

    private static class BeanServiceInstance
    {
        private final Object targetBean;

        public Object getTargetBean ()
        {
            return this.targetBean;
        }

        public Dictionary<?, ?> getProperties ()
        {
            try
            {
                final Dictionary<Object, Object> result = new Hashtable<Object, Object> ();

                final Map<?, ?> properties = new BeanUtilsBean2 ().describe ( this.targetBean );
                for ( final Map.Entry<?, ?> entry : properties.entrySet () )
                {
                    if ( entry.getValue () != null )
                    {
                        result.put ( entry.getKey (), entry.getValue () );
                    }
                }
                return result;
            }
            catch ( final Exception e )
            {
                logger.warn ( "Failed to get dictionary", e );
                return new Hashtable<Object, Object> ();
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
    protected Entry createService ( final String configurationId, final BundleContext context, final Map<String, String> parameters ) throws Exception
    {
        final BeanServiceInstance bean = new BeanServiceInstance ( this.beanClazz.newInstance () );
        bean.update ( parameters );

        final ServiceRegistration reg = context.registerService ( this.beanClazz.getName (), bean.getTargetBean (), bean.getProperties () );

        return new Entry ( bean, reg );
    }

    @Override
    protected void disposeService ( final Object service )
    {
    }

    @Override
    protected void updateService ( final Entry entry, final Map<String, String> parameters ) throws Exception
    {
        ( (BeanServiceInstance)entry.getService () ).update ( parameters );
        entry.getHandle ().setProperties ( ( (BeanServiceInstance)entry.getService () ).getProperties () );
    }

}
