package org.openscada.da.server.exec2.extractor;

import org.openscada.core.Variant;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.da.server.common.item.factory.FolderItemFactory;
import org.openscada.da.server.exec2.Hive;

/**
 * Extract information based on the nagios scheme using the return code
 * @author Jens Reimann
 *
 */
public class NagiosExtractor extends AbstractReturnCodeExtractor
{
    private DataItemInputChained errorItem;

    private DataItemInputChained warningItem;

    public NagiosExtractor ( final String id )
    {
        super ( id );
    }

    @Override
    protected void handleReturnCode ( final int rc )
    {
        if ( rc < 0 )
        {
            throw new RuntimeException ( String.format ( "Command excution failed: rc = %s", rc ) );
        }

        if ( rc == 0 )
        {
            this.errorItem.updateData ( new Variant ( rc >= 2 ), null, null );
            this.warningItem.updateData ( new Variant ( rc >= 1 ), null, null );
        }
    }

    @Override
    public void register ( final Hive hive, final FolderItemFactory folderItemFactory )
    {
        super.register ( hive, folderItemFactory );
        this.errorItem = createInput ( "error" );
        this.warningItem = createInput ( "warning" );
    }

}
