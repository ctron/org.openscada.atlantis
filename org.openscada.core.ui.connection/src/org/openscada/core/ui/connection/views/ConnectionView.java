package org.openscada.core.ui.connection.views;

import org.eclipse.ui.navigator.CommonNavigator;
import org.openscada.core.ui.connection.Activator;

public class ConnectionView extends CommonNavigator
{
    @Override
    protected Object getInitialInput ()
    {
        return Activator.ROOT;
    }
}
