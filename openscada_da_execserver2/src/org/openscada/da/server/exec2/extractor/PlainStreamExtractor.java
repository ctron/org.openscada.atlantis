package org.openscada.da.server.exec2.extractor;

import java.util.HashMap;
import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.da.server.common.AttributeMode;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.da.server.common.item.factory.FolderItemFactory;
import org.openscada.da.server.exec2.Hive;
import org.openscada.da.server.exec2.command.ExecutionResult;

/**
 * Extractor which takes the stream string value as value
 * @author Jens Reimann
 *
 */
public class PlainStreamExtractor extends AbstractBaseExtractor
{
    private DataItemInputChained valueItem;

    public PlainStreamExtractor ( final String id )
    {
        super ( id );
    }

    @Override
    protected void doProcess ( final ExecutionResult result )
    {
        final Map<String, Variant> attributes = new HashMap<String, Variant> ();
        final Variant value = new Variant ( result.getOutput () );
        attributes.put ( "exec.error", new Variant ( false ) );
        attributes.put ( "exec.error.message", null );
        fillNoError ( attributes );

        this.valueItem.updateData ( value, attributes, AttributeMode.UPDATE );
    }

    @Override
    public void register ( final Hive hive, final FolderItemFactory folderItemFactory )
    {
        super.register ( hive, folderItemFactory );
        this.valueItem = createInput ( "value" );
    }
}
