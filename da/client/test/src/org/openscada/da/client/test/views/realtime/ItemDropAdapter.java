package org.openscada.da.client.test.views.realtime;

import java.net.URISyntaxException;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.TransferData;
import org.openscada.core.ConnectionInformation;
import org.openscada.da.client.test.Openscada_da_client_testPlugin;
import org.openscada.da.client.test.dnd.Item;
import org.openscada.da.client.test.dnd.ItemTransfer;
import org.openscada.da.client.test.impl.HiveConnection;
import org.openscada.da.client.test.impl.HiveItem;

public class ItemDropAdapter extends ViewerDropAdapter
{

    public ItemDropAdapter ( Viewer viewer )
    {
        super ( viewer );
        setFeedbackEnabled ( true );
        setSelectionFeedbackEnabled ( true );
    }
    
    @Override
    public boolean performDrop ( Object data )
    {
        Item[] items = (Item[])data;
        
        ListData listData = (ListData)getViewer ().getInput ();
        TreeViewer viewer = (TreeViewer)getViewer ();
        
        for ( Item item : items )
        {
            try
            {
                dropItem ( item, listData, viewer );
            }
            catch ( URISyntaxException e )
            {
                e.printStackTrace();
            }
        }
        
        return true;
    }

    private void dropItem ( Item item, ListData listData, TreeViewer viewer ) throws URISyntaxException
    {
        ConnectionInformation connectionInformation = ConnectionInformation.fromURI ( item.getConnectionString () );
        
        HiveConnection connection = Openscada_da_client_testPlugin.getRepository ().findConnection ( connectionInformation );
        if ( connection != null )
        {
            HiveItem hiveItem = new HiveItem ( connection, item.getId () );
            listData.add ( hiveItem );
        }
    }

    @Override
    public boolean validateDrop ( Object target, int operation, TransferData transferType )
    {
        return ItemTransfer.getInstance ().isSupportedType ( transferType );
    }

}
