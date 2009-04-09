package org.openscada.da.server.exec2.util;

import org.openscada.da.core.IODirection;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.common.HiveServiceRegistry;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.da.server.common.chain.item.LevelAlarmChainItem;
import org.openscada.da.server.common.chain.item.ManualErrorOverrideChainItem;
import org.openscada.da.server.common.chain.item.ManualOverrideChainItem;
import org.openscada.da.server.common.chain.item.ScaleInputItem;
import org.openscada.da.server.common.chain.item.SumAlarmChainItem;
import org.openscada.da.server.common.chain.item.SumErrorChainItem;
import org.openscada.da.server.common.impl.HiveCommon;
import org.openscada.da.server.common.item.factory.FolderItemFactory;
import org.openscada.da.server.common.item.factory.ItemFactory;

public class DefaultItemFactory extends FolderItemFactory
{

    private final HiveServiceRegistry hiveServiceRegistry;

    public DefaultItemFactory ( final HiveCommon hive, final FolderCommon parentFolder, final String baseId, final String folderName )
    {
        super ( hive, parentFolder, baseId, folderName );
        this.hiveServiceRegistry = hive;
    }

    public DefaultItemFactory ( final ItemFactory parentFactory, final HiveCommon hive, final FolderCommon parentFolder, final String baseId, final String folderName, final String idDelimiter )
    {
        super ( parentFactory, hive, parentFolder, baseId, folderName, idDelimiter );
        this.hiveServiceRegistry = hive;
    }

    public DefaultItemFactory ( final ItemFactory parentFactory, final HiveCommon hive, final FolderCommon parentFolder, final String baseId, final String folderName )
    {
        super ( parentFactory, hive, parentFolder, baseId, folderName );
        this.hiveServiceRegistry = hive;
    }

    @Override
    protected DataItemInputChained constructInput ( final String localId )
    {
        final DataItemInputChained item = super.constructInput ( localId );

        item.addChainElement ( IODirection.INPUT, new SumErrorChainItem ( this.hiveServiceRegistry ) );
        item.addChainElement ( IODirection.INPUT, new ScaleInputItem ( this.hive ) );
        item.addChainElement ( IODirection.INPUT, new ManualOverrideChainItem ( this.hiveServiceRegistry ) );
        item.addChainElement ( IODirection.INPUT, new ManualErrorOverrideChainItem () );
        item.addChainElement ( IODirection.INPUT, new LevelAlarmChainItem ( this.hiveServiceRegistry ) );
        item.addChainElement ( IODirection.INPUT, new SumAlarmChainItem ( this.hiveServiceRegistry ) );

        return item;
    }

    @Override
    public FolderItemFactory createSubFolderFactory ( final String name )
    {
        final FolderItemFactory factory = new DefaultItemFactory ( this, this.hive, this.getFolder (), name, name );
        addSubFactory ( factory );
        return factory;
    }

}
