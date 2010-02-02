package org.openscada.da.datasource.item;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

import org.openscada.da.datasource.DataSource;
import org.openscada.da.server.common.DataItem;
import org.openscada.da.server.common.DataItemInformationBase;
import org.openscada.utils.osgi.ca.factory.AbstractServiceConfigurationFactory;
import org.openscada.utils.osgi.pool.ObjectPoolHelper;
import org.openscada.utils.osgi.pool.ObjectPoolImpl;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceRegistration;

public class DataItemFactoryImpl extends AbstractServiceConfigurationFactory<DataItemImpl>
{
    public static final String FACTORY_ID = "da.dataitem.datasource";

    private final BundleContext context;

    private final ObjectPoolTracker poolTracker;

    private final ObjectPoolImpl itemPool;

    private final ServiceRegistration itemPoolHandle;

    public DataItemFactoryImpl ( final BundleContext context ) throws InvalidSyntaxException
    {
        super ( context );
        this.itemPool = new ObjectPoolImpl ();

        this.itemPoolHandle = ObjectPoolHelper.registerObjectPool ( context, this.itemPool, DataItem.class.getName () );

        this.context = context;
        this.poolTracker = new ObjectPoolTracker ( context, DataSource.class.getName () );
        this.poolTracker.open ();
    }

    @Override
    public synchronized void dispose ()
    {
        this.itemPoolHandle.unregister ();
        this.itemPool.dispose ();

        this.poolTracker.close ();

        super.dispose ();
    }

    @Override
    protected Entry<DataItemImpl> createService ( final String configurationId, final BundleContext context, final Map<String, String> parameters ) throws Exception
    {
        return createDataItem ( configurationId, context, parameters );
    }

    @Override
    protected void disposeService ( final String id, final DataItemImpl service )
    {
        this.itemPool.removeService ( id, service );
        service.dispose ();
    }

    @Override
    protected Entry<DataItemImpl> updateService ( final String configurationId, final Entry<DataItemImpl> entry, final Map<String, String> parameters ) throws Exception
    {
        this.itemPool.removeService ( configurationId, entry.getService () );
        entry.getService ().dispose ();

        return createDataItem ( configurationId, this.context, parameters );
    }

    protected Entry<DataItemImpl> createDataItem ( final String configurationId, final BundleContext context, final Map<String, String> parameters ) throws InvalidSyntaxException
    {
        final String itemId = parameters.get ( "item.id" );
        if ( itemId == null )
        {
            throw new IllegalArgumentException ( "'item.id' must be set" );
        }

        final String datasourceId = parameters.get ( "datasource.id" );
        final DataItemImpl item = new DataItemImpl ( this.poolTracker, new DataItemInformationBase ( itemId ), datasourceId );

        final Dictionary<String, String> properties = new Hashtable<String, String> ();
        properties.put ( Constants.SERVICE_DESCRIPTION, "inavare GmbH" );

        // register
        this.itemPool.addService ( configurationId, item, properties );

        return new Entry<DataItemImpl> ( configurationId, item );
    }
}
