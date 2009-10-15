package org.openscada.core.ui.connection.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;
import org.openscada.core.ui.connection.Activator;

public class ConnectionView extends CommonNavigator
{

    @Override
    protected CommonViewer createCommonViewerObject ( final Composite aParent )
    {
        // quick fix to automatically expand some content
        final CommonViewer result = super.createCommonViewerObject ( aParent );
        result.setAutoExpandLevel ( 2 );
        return result;
    }

    @Override
    protected Object getInitialInput ()
    {
        return Activator.ROOT;
    }
}
