package org.openscada.hd.server.common.item.internal;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.openscada.ca.ConfigurationFactory;
import org.openscada.core.Variant;
import org.openscada.da.datasource.DataSource;
import org.openscada.hd.server.common.HistoricalItem;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurationFactoryImpl implements ConfigurationFactory
{
    private final static Logger logger = LoggerFactory.getLogger ( ConfigurationFactoryImpl.class );

    protected final class ItemWrapper
    {
        private final HistoricalItemImpl item;

        private final ServiceRegistration registration;

        public ItemWrapper ( final HistoricalItemImpl item, final ServiceRegistration registration )
        {
            this.item = item;
            this.registration = registration;
        }

        public HistoricalItemImpl getItem ()
        {
            return this.item;
        }

        public ServiceRegistration getRegistration ()
        {
            return this.registration;
        }

        public void update ( final Map<String, String> properties ) throws Exception
        {
            this.item.update ( properties );
        }
    }

    private final Map<String, ItemWrapper> items = new HashMap<String, ItemWrapper> ();

    private final BundleContext context;

    public ConfigurationFactoryImpl ( final BundleContext context )
    {
        this.context = context;
    }

    public void dispose ()
    {
        final Set<ItemWrapper> items;
        synchronized ( this )
        {
            items = new HashSet<ItemWrapper> ( this.items.values () );
            this.items.clear ();
        }

        for ( final ItemWrapper item : items )
        {
            unregisterItem ( item );
        }
    }

    public void delete ( final String configurationId ) throws Exception
    {
        final ItemWrapper item;
        synchronized ( this.items )
        {
            item = this.items.remove ( configurationId );
        }
        if ( item != null )
        {
            unregisterItem ( item );
        }
    }

    private void unregisterItem ( final ItemWrapper item )
    {
        item.getRegistration ().unregister ();
        item.getItem ().stop ();
    }

    public void update ( final String configurationId, final Map<String, String> properties ) throws Exception
    {
        logger.info ( "Update call for {} -> {}", new Object[] { configurationId, properties } );

        synchronized ( this )
        {
            ItemWrapper item = this.items.get ( configurationId );
            if ( item == null )
            {
                logger.info ( "Creating new item: {}", configurationId );
                item = createItem ( configurationId, properties );
            }
            else
            {
                logger.info ( "Updating {}", configurationId );
                item.update ( properties );
            }
            this.items.put ( configurationId, item );
        }
    }

    private ItemWrapper createItem ( final String configurationId, final Map<String, String> properties ) throws InvalidSyntaxException
    {
        final String masterId = properties.get ( DataSource.DATA_SOURCE_ID );
        if ( masterId == null )
        {
            throw new IllegalArgumentException ( String.format ( "'%s' is not set", DataSource.DATA_SOURCE_ID ) );
        }

        final Dictionary<String, String> serviceProperties = new Hashtable<String, String> ();
        serviceProperties.put ( Constants.SERVICE_PID, configurationId );
        serviceProperties.put ( Constants.SERVICE_DESCRIPTION, "A historical item implementation" );
        serviceProperties.put ( Constants.SERVICE_VENDOR, "inavare GmbH" );

        final Map<String, Variant> attributes = new HashMap<String, Variant> ();
        attributes.put ( Constants.SERVICE_DESCRIPTION, new Variant ( "A historical item implementation" ) );
        attributes.put ( Constants.SERVICE_VENDOR, new Variant ( "inavare GmbH" ) );
        attributes.put ( Constants.SERVICE_PID, Variant.valueOf ( configurationId ) );
        attributes.put ( "master.id", new Variant ( masterId ) );

        final HistoricalItemImpl item = new HistoricalItemImpl ( configurationId, attributes, masterId, this.context );

        final ServiceRegistration registration = this.context.registerService ( HistoricalItem.class.getName (), item, serviceProperties );

        item.start ();

        return new ItemWrapper ( item, registration );
    }

}
