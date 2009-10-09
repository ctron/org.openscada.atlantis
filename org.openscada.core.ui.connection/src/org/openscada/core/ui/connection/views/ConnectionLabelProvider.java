package org.openscada.core.ui.connection.views;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.openscada.core.ui.connection.data.ConnectionDiscovererBean;
import org.openscada.core.ui.connection.data.ConnectionHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionLabelProvider extends BaseLabelProvider implements IStyledLabelProvider, ILabelProvider
{

    private final static Logger logger = LoggerFactory.getLogger ( ConnectionLabelProvider.class );

    private final ResourceManager resource = new LocalResourceManager ( JFaceResources.getResources () );

    public ConnectionLabelProvider ()
    {
        logger.info ( "Init" );
    }

    @Override
    public void dispose ()
    {
        this.resource.dispose ();
        super.dispose ();
    }

    public Image getImage ( final Object element )
    {
        if ( element instanceof ConnectionDiscovererBean )
        {
            if ( ( (ConnectionDiscovererBean)element ).getImageDescriptor () != null )
            {
                return this.resource.createImage ( ( (ConnectionDiscovererBean)element ).getImageDescriptor () );
            }
        }
        else if ( element instanceof ConnectionHolder )
        {
            return null;
        }
        return null;
    }

    public StyledString getStyledText ( final Object element )
    {
        logger.info ( "Get Styled string" );
        if ( element instanceof ConnectionDiscovererBean )
        {
            final String name = ( (ConnectionDiscovererBean)element ).getName ();
            return new StyledString ( name );
        }
        else if ( element instanceof ConnectionHolder )
        {
            final ConnectionHolder holder = (ConnectionHolder)element;
            return new StyledString ( holder.toString () );
        }
        return null;
    }

    public String getText ( final Object element )
    {
        logger.info ( "Get string" );
        return getStyledText ( element ).getString ();
    }

}
