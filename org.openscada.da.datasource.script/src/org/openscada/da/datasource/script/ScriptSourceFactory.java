package org.openscada.da.datasource.script;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.Executor;

import org.openscada.da.datasource.DataSource;
import org.openscada.utils.osgi.ca.factory.AbstractServiceConfigurationFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class ScriptSourceFactory extends AbstractServiceConfigurationFactory<ScriptDataSource>
{

    private final Executor executor;

    public ScriptSourceFactory ( final BundleContext context, final Executor executor )
    {
        super ( context );
        this.executor = executor;
    }

    @Override
    protected Entry<ScriptDataSource> createService ( final String configurationId, final BundleContext context, final Map<String, String> parameters ) throws Exception
    {
        final ScriptDataSource source = new ScriptDataSource ( context, this.executor );
        source.update ( parameters );

        final Dictionary<String, String> properties = new Hashtable<String, String> ();
        properties.put ( DataSource.DATA_SOURCE_ID, configurationId );
        final ServiceRegistration handle = context.registerService ( DataSource.class.getName (), source, properties );

        return new Entry<ScriptDataSource> ( source, handle );
    }

    @Override
    protected void disposeService ( final ScriptDataSource service )
    {
        service.dispose ();
    }

    @Override
    protected Entry<ScriptDataSource> updateService ( final String configurationId, final Entry<ScriptDataSource> entry, final Map<String, String> parameters ) throws Exception
    {
        entry.getService ().update ( parameters );
        return null;
    }

}
