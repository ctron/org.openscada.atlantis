package org.openscada.da.server.exec2.extractor;

import org.openscada.core.Variant;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.da.server.common.item.factory.FolderItemFactory;
import org.openscada.da.server.exec2.Hive;

/**
 * Extract information based on the return code of the process
 * @author Jens Reimann
 *
 */
public class SimpleReturnCodeExtractor extends AbstractReturnCodeExtractor
{
    private DataItemInputChained failedItem;

    public SimpleReturnCodeExtractor ( final String id )
    {
        super ( id );
    }

    protected void handleReturnCode ( final int rc )
    {
        this.failedItem.updateData ( new Variant ( rc < 0 ), null, null );
    }

    @Override
    public void register ( final Hive hive, final FolderItemFactory folderItemFactory )
    {
        super.register ( hive, folderItemFactory );
        this.failedItem = createInput ( "failed" );
    }

}
