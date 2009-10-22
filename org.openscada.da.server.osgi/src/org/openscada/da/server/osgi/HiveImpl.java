package org.openscada.da.server.osgi;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openscada.core.Variant;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.browser.common.query.GroupFolder;
import org.openscada.da.server.browser.common.query.IDNameProvider;
import org.openscada.da.server.browser.common.query.InvisibleStorage;
import org.openscada.da.server.browser.common.query.ItemDescriptor;
import org.openscada.da.server.browser.common.query.SplitGroupProvider;
import org.openscada.da.server.common.DataItem;
import org.openscada.da.server.common.ValidationStrategy;
import org.openscada.da.server.common.impl.HiveCommon;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class HiveImpl extends HiveCommon
{
    private final static Logger logger = Logger.getLogger ( HiveImpl.class );

    private FolderCommon rootFolder;

    private final BundleContext context;

    private final InvisibleStorage storage;

    private final Map<ServiceReference, ItemDescriptor> items;

    public HiveImpl ( final BundleContext context )
    {
        this.context = context;

        setValidatonStrategy ( ValidationStrategy.GRANT_ALL );

        setRootFolder ( this.rootFolder = new FolderCommon () );

        this.items = new HashMap<ServiceReference, ItemDescriptor> ();

        this.storage = new InvisibleStorage ();
        GroupFolder allItemsFolder = new GroupFolder ( new SplitGroupProvider ( new IDNameProvider (), "\\." ), new IDNameProvider () );
        this.rootFolder.add ( "all", allItemsFolder, new HashMap<String, Variant> () );
        this.storage.addChild ( allItemsFolder );
    }

    @Override
    public void start () throws Exception
    {
        super.start ();
    }

    @Override
    public void stop () throws Exception
    {
        super.stop ();
    }

    public synchronized void addItem ( final ServiceReference serviceReference )
    {
        if ( !serviceReference.isAssignableTo ( this.context.getBundle (), DataItem.class.getName () ) )
        {
            return;
        }
        DataItem item = (DataItem)this.context.getService ( serviceReference );
        registerItem ( item );

        Map<String, Variant> attributes = new HashMap<String, Variant> ();

        ItemDescriptor descriptor = new ItemDescriptor ( item, attributes );
        this.storage.added ( descriptor );

        this.items.put ( serviceReference, descriptor );
        logger.info ( String.format ( "Exporting %s as %s", serviceReference, item.getInformation ().getName () ) );
    }

    public synchronized void removeItem ( final ServiceReference serviceReference )
    {
        logger.info ( String.format ( "Removing %s", serviceReference ) );

        ItemDescriptor descriptor = this.items.remove ( serviceReference );
        this.storage.removed ( descriptor );
        unregisterItem ( descriptor.getItem () );
    }
}
