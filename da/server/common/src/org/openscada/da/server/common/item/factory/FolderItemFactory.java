package org.openscada.da.server.common.item.factory;

import java.util.HashMap;
import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.common.DataItem;
import org.openscada.da.server.common.DataItemCommand;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.da.server.common.chain.WriteHandler;
import org.openscada.da.server.common.chain.WriteHandlerItem;
import org.openscada.da.server.common.impl.HiveCommon;

public class FolderItemFactory extends HiveItemFactory
{
    private HiveCommon hive;
    private String folderName;
    private FolderCommon folder;
    private FolderCommon parentFolder;

    public FolderItemFactory ( HiveCommon hive, FolderCommon parentFolder, String baseId, String folderName )
    {
        this ( null, hive, parentFolder, baseId, folderName, null );
    }

    public FolderItemFactory ( ItemFactory parentFactory, HiveCommon hive, FolderCommon parentFolder, String baseId, String folderName )
    {
        this ( parentFactory, hive, parentFolder, baseId, folderName, null );
    }

    public FolderItemFactory ( ItemFactory parentFactory, HiveCommon hive, FolderCommon parentFolder, String baseId, String folderName, String idDelimiter )
    {
        super ( parentFactory, hive, baseId, idDelimiter );
        this.hive = hive;
        this.parentFolder = parentFolder;
        this.folderName = folderName;

        if ( this.parentFolder != null && this.folderName != null )
        {
            folder = new FolderCommon ();
            parentFolder.add ( this.folderName, folder, new HashMap<String, Variant> () );
        }
    }

    @Override
    public void dispose ()
    {
        if ( isDisposed () )
        {
            return;
        }

        if ( parentFolder != null && folder != null && this.folderName != null )
        {
            parentFolder.remove ( this.folderName );
        }
        super.dispose ();
    }

    @Override
    public boolean disposeItem ( DataItem item )
    {
        boolean removed = super.disposeItem ( item );
        if ( folder != null && removed )
        {
            folder.remove ( item );
        }
        return removed;
    }

    @Override
    protected DataItemCommand constructCommand ( String localId )
    {
        final DataItemCommand item = super.constructCommand ( localId );
        addToFolder ( item, localId, new HashMap<String, Variant> () );
        return item;
    }

    @Override
    protected DataItemInputChained constructInput ( String localId )
    {
        final DataItemInputChained item = super.constructInput ( localId );
        addToFolder ( item, localId, new HashMap<String, Variant> () );
        return item;
    }

    @Override
    protected WriteHandlerItem constructInputOutput ( String localId, WriteHandler writeHandler )
    {
        WriteHandlerItem item = super.constructInputOutput ( localId, writeHandler );
        addToFolder ( item, localId, new HashMap<String, Variant> () );
        return item;
    }

    protected void addToFolder ( DataItem item, String name, Map<String, Variant> attributes )
    {
        if ( this.folder == null )
        {
            return;
        }
        this.folder.add ( name, item, attributes );
    }

    public FolderCommon getFolder ()
    {
        return folder;
    }

    public FolderItemFactory createSubFolderFactory ( String name )
    {
        final FolderItemFactory factory = new FolderItemFactory ( this, this.hive, this.folder, generateId ( name ),
                name );
        addSubFactory ( factory );
        return factory;
    }

}
