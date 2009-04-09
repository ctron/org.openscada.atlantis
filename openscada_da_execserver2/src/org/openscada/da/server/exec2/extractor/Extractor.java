package org.openscada.da.server.exec2.extractor;

import org.openscada.da.server.common.item.factory.FolderItemFactory;
import org.openscada.da.server.exec2.Hive;
import org.openscada.da.server.exec2.command.ExecutionResult;

public interface Extractor
{
    public void process ( ExecutionResult result );

    public void register ( Hive hive, FolderItemFactory folderItemFactory );

    public void unregister ();
}
