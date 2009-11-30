package org.openscada.da.datasource.item;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

import org.openscada.da.server.common.DataItem;
import org.openscada.da.server.common.DataItemInformationBase;
import org.openscada.utils.osgi.ca.factory.AbstractServiceConfigurationFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceRegistration;

public class FactoryImpl extends AbstractServiceConfigurationFactory<DataItemImpl>
{

    private final BundleContext context;

    public FactoryImpl ( final BundleContext context )
    {
        super ( context );
        this.context = context;
    }

    @Override
    protected Entry<DataItemImpl> createService ( final String configurationId, final BundleContext context, final Map<String, String> parameters ) throws Exception
    {
        return createDataItem ( configurationId, context, parameters );
    }

    @Override
    protected void disposeService ( final DataItemImpl service )
    {
        service.dispose ();
    }

    @Override
    protected Entry<DataItemImpl> updateService ( final String configurationId, final Entry<DataItemImpl> entry, final Map<String, String> parameters ) throws Exception
    {
        return createDataItem ( configurationId, this.context, parameters );
    }

    protected Entry<DataItemImpl> createDataItem ( final String configurationId, final BundleContext context, final Map<String, String> parameters ) throws InvalidSyntaxException
    {
        final String itemId = parameters.get ( "item.id" );
        final String datasourceId = parameters.get ( "datasource.id" );
        final DataItemImpl item = new DataItemImpl ( context, new DataItemInformationBase ( itemId ), datasourceId );

        final Dictionary<String, String> properties = new Hashtable<String, String> ();
        properties.put ( Constants.SERVICE_DESCRIPTION, "inavare GmbH" );
        final ServiceRegistration handle = context.registerService ( DataItem.class.getName (), item, properties );

        return new Entry<DataItemImpl> ( item, handle );
    }
}
