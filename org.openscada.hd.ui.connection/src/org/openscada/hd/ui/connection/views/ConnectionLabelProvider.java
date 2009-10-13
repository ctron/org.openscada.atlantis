package org.openscada.hd.ui.connection.views;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.openscada.core.ui.connection.Activator;
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
        super.updateLabel ( label, element );
    }

}
