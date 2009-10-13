package org.openscada.da.ui.connection.views;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.openscada.core.ui.connection.Activator;
import org.openscada.da.core.browser.DataItemEntry;
import org.openscada.da.core.browser.FolderEntry;
import org.openscada.ui.databinding.AdapterHelper;
import org.openscada.ui.databinding.ListeningLabelProvider;
import org.openscada.ui.databinding.StyledViewerLabel;

public class ConnectionLabelProvider extends ListeningLabelProvider
{
    private final ResourceManager resource = new LocalResourceManager ( JFaceResources.getResources () );

    public ConnectionLabelProvider ()
    {
        super ( Activator.getDefault ().getDiscovererSet (), new ConnectionObservableFactory () );
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
        label.setText ( dataItemEntry.getName () );
    }

    private void updateFolder ( final StyledViewerLabel label, final FolderEntry folderEntry )
    {
        label.setText ( folderEntry.getName () );
    }

}
