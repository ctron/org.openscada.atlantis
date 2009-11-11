package org.openscada.da.ui.connection.views;

import java.util.Arrays;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.openscada.da.core.IODirection;
import org.openscada.da.core.browser.DataItemEntry;
import org.openscada.da.core.browser.FolderEntry;
import org.openscada.ui.databinding.AdapterHelper;
import org.openscada.ui.databinding.CommonListeningLabelProvider;
import org.openscada.ui.databinding.StyledViewerLabel;

public class ConnectionLabelProvider extends CommonListeningLabelProvider
{
    private final ResourceManager resource = new LocalResourceManager ( JFaceResources.getResources () );

    public ConnectionLabelProvider ()
    {
        super ( "org.openscada.da.ui.connection.provider" );
    }

    @Override
    public void dispose ()
    {
        this.resource.dispose ();
        super.dispose ();
    }

    @Override
    public void updateLabel ( final StyledViewerLabel label, final Object element )
    {
        final FolderEntry folderEntry = (FolderEntry)AdapterHelper.adapt ( element, FolderEntry.class );
        if ( folderEntry != null )
        {
            updateFolder ( label, folderEntry );
        }

        final DataItemEntry dataItemEntry = (DataItemEntry)AdapterHelper.adapt ( element, DataItemEntry.class );
        if ( dataItemEntry != null )
        {
            updateItem ( label, dataItemEntry );
        }
    }

    private void updateItem ( final StyledViewerLabel label, final DataItemEntry dataItemEntry )
    {
        String itemName = dataItemEntry.getName ();
        if ( itemName == null || itemName.length () == 0 )
        {
            itemName = " ";
        }
        label.setText ( itemName );

        if ( dataItemEntry.getIODirections ().containsAll ( Arrays.asList ( IODirection.INPUT, IODirection.OUTPUT ) ) )
        {
            label.setImage ( this.resource.createImage ( ImageDescriptor.createFromFile ( ConnectionLabelProvider.class, "icons/item_io.gif" ) ) );
        }
        else if ( dataItemEntry.getIODirections ().contains ( IODirection.INPUT ) )
        {
            label.setImage ( this.resource.createImage ( ImageDescriptor.createFromFile ( ConnectionLabelProvider.class, "icons/item_i.gif" ) ) );
        }
        else if ( dataItemEntry.getIODirections ().contains ( IODirection.OUTPUT ) )
        {
            label.setImage ( this.resource.createImage ( ImageDescriptor.createFromFile ( ConnectionLabelProvider.class, "icons/item_o.gif" ) ) );
        }
        else
        {
            label.setImage ( this.resource.createImage ( ImageDescriptor.createFromFile ( ConnectionLabelProvider.class, "icons/item.gif" ) ) );
        }
    }

    private void updateFolder ( final StyledViewerLabel label, final FolderEntry folderEntry )
    {
        label.setImage ( this.resource.createImage ( ImageDescriptor.createFromFile ( ConnectionLabelProvider.class, "icons/folder.gif" ) ) );

        String folderName = folderEntry.getName ();
        if ( folderName == null || folderName.length () == 0 )
        {
            folderName = " ";
        }
        label.setText ( folderName );
    }
}
