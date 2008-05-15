package org.openscada.da.server.common.configuration;

import org.openscada.da.server.browser.common.Folder;
import org.openscada.da.server.common.DataItem;
import org.openscada.da.server.common.HiveServiceRegistry;
import org.openscada.da.server.common.factory.DataItemFactory;
import org.openscada.da.server.common.factory.DataItemFactoryRequest;
import org.openscada.da.server.common.factory.FactoryTemplate;

public interface ConfigurableHive extends HiveServiceRegistry
{

    // data item
    public abstract void registerItem ( DataItem item );

    public abstract void addItemFactory ( DataItemFactory factory );
    
    public abstract void registerTemplate ( FactoryTemplate template );

    /**
     * retrieve a data item by id. Create it using the factories if it does not exists
     * @param id the item id
     * @return the data item or <code>null</code> if the item does not exists and cannot be created
     */
    public abstract DataItem retrieveItem ( DataItemFactoryRequest request );
    
    /**
     * lookup a data item by id. Just look it up in the internal item list, do not
     * create the item if it does not exists.
     * @param id the item id
     * @return the data item or <code>null</code> if the item does not exist
     */
    public abstract DataItem lookupItem ( String id );
    
    public abstract Folder getRootFolder ();

    public abstract void setRootFolder ( Folder folder );
}