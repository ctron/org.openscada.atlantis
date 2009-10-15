package org.openscada.core.ui.connection.tester;

import org.eclipse.core.expressions.PropertyTester;
import org.openscada.core.ui.connection.ConnectionStore;
import org.openscada.core.ui.connection.data.ConnectionDiscovererBean;
import org.openscada.ui.databinding.AdapterHelper;

public class ConnectionDiscovererBeanTester extends PropertyTester
{
    public boolean test ( final Object receiver, final String property, final Object[] args, final Object expectedValue )
    {
        if ( ! ( receiver instanceof ConnectionDiscovererBean ) )
        {
            return false;
        }

        if ( "isStore".equals ( property ) && expectedValue instanceof Boolean )
        {
            final boolean isStore = AdapterHelper.adapt ( receiver, ConnectionStore.class ) != null;
            return isStore == (Boolean)expectedValue;
        }

        return false;
    }

}
