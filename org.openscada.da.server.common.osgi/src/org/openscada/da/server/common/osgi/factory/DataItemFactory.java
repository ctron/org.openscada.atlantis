package org.openscada.da.server.common.osgi.factory;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.Executor;

import org.openscada.core.Variant;
import org.openscada.da.server.common.DataItem;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class DataItemFactory
{
    private final BundleContext context;

    private final String globalId;

    private final Executor executor;

    private final Map<String, DataItem> items = new HashMap<String, DataItem> ();

    private final Map<String, ServiceRegistration> itemRegs = new HashMap<String, ServiceRegistration> ();

    private final String delimiter = ".";

    public DataItemFactory ( final BundleContext context, final Executor executor, final String globalId )
    {
        this.executor = executor;
        this.context = context;
        this.globalId = globalId;
    }

    public synchronized DataItemInputChained createInput ( final String localId, final Map<String, Variant> properties )
    {
        Map<String, Variant> localProperties;
        if ( properties != null )
        {
            localProperties = properties;
        }
        else
        {
            localProperties = new HashMap<String, Variant> ();
        }

        final DataItem item = this.items.get ( localId );
        if ( item == null )
        {
            final String id = getId ( localId );
            final DataItemInputChained newItem = new DataItemInputChained ( id, this.executor );
            this.items.put ( localId, item );
            registerItem ( newItem, localId, localProperties );
            return newItem;
        }
        else
        {
            if ( item instanceof DataItemInputChained )
            {
                return (DataItemInputChained)item;
            }
            else
            {
                return null;
            }
        }
    }

    protected void registerItem ( final DataItemInputChained newItem, final String localId, final Map<String, Variant> properties )
    {
        final Dictionary<String, String> props = new Hashtable<String, String> ();

        fillProperties ( properties, props );

        final ServiceRegistration handle = this.context.registerService ( DataItem.class.getName (), newItem, props );
        this.itemRegs.put ( localId, handle );
    }

    protected void fillProperties ( final Map<String, Variant> properties, final Dictionary<String, String> props )
    {
    }

    protected String getId ( final String localId )
    {
        return this.globalId + this.delimiter + localId;
    }

    public synchronized void dispose ()
    {
        for ( final ServiceRegistration reg : this.itemRegs.values () )
        {
            reg.unregister ();
        }
        this.items.clear ();
        this.itemRegs.clear ();
    }
}
