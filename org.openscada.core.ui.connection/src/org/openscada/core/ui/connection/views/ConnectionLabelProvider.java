package org.openscada.core.ui.connection.views;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.openscada.core.client.Connection;
import org.openscada.core.connection.provider.ConnectionService;
import org.openscada.core.ui.connection.data.ConnectionDiscovererBean;
import org.openscada.core.ui.connection.data.ConnectionHolder;
import org.openscada.ui.databinding.CommonListeningLabelProvider;
import org.openscada.ui.databinding.StyledViewerLabel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionLabelProvider extends CommonListeningLabelProvider implements PropertyChangeListener
{
    private final static Logger logger = LoggerFactory.getLogger ( ConnectionLabelProvider.class );

    private final ResourceManager resource = new LocalResourceManager ( JFaceResources.getResources () );

    public ConnectionLabelProvider ()
    {
        super ( "org.openscada.core.ui.connection.provider" );
    }

    @Override
    public void dispose ()
    {
        this.resource.dispose ();
        super.dispose ();
    }

    private StyledString getConnectionString ( final ConnectionHolder holder )
    {
        final ConnectionService service = holder.getConnectionService ();

        final StyledString str = new StyledString ( holder.toString () );

        if ( service != null )
        {
            str.append ( " [", StyledString.DECORATIONS_STYLER );
            final Connection connection = service.getConnection ();
            if ( connection != null )
            {
                str.append ( String.format ( "%s", holder.getConnectionState () ), StyledString.DECORATIONS_STYLER );
            }
            str.append ( "]", StyledString.DECORATIONS_STYLER );
        }
        return str;
    }

    @Override
    public void updateLabel ( final StyledViewerLabel label, final Object element )
    {
        if ( element instanceof ConnectionDiscovererBean )
        {
            final ConnectionDiscovererBean bean = (ConnectionDiscovererBean)element;
            if ( bean.getImageDescriptor () != null )
            {
                label.setImage ( this.resource.createImage ( ( (ConnectionDiscovererBean)element ).getImageDescriptor () ) );
            }
            label.setText ( bean.getName () );
        }
        else if ( element instanceof ConnectionHolder )
        {
            final Image image = this.resource.createImage ( ImageDescriptor.createFromFile ( ConnectionLabelProvider.class, "icons/connection.gif" ) );
            label.setImage ( image );
            label.setStyledText ( getConnectionString ( (ConnectionHolder)element ) );
        }
    }

    @Override
    protected void addListenerTo ( final Object next )
    {
        super.addListenerTo ( next );
        if ( next instanceof ConnectionHolder )
        {
            ( (ConnectionHolder)next ).addPropertyChangeListener ( this );
        }
    }

    @Override
    protected void removeListenerFrom ( final Object next )
    {
        if ( next instanceof ConnectionHolder )
        {
            ( (ConnectionHolder)next ).removePropertyChangeListener ( this );
        }
        super.removeListenerFrom ( next );
    }

    public void propertyChange ( final PropertyChangeEvent evt )
    {
        logger.debug ( "Detected a property change: {}", evt );
        fireChangeEvent ( Arrays.asList ( evt.getSource () ) );
    }

}
