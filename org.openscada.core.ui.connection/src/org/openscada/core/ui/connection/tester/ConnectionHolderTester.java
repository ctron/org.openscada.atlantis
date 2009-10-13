package org.openscada.core.ui.connection.tester;

import org.eclipse.core.expressions.PropertyTester;
import org.openscada.core.ui.connection.data.ConnectionHolder;

public class ConnectionHolderTester extends PropertyTester
{

    public boolean test ( final Object receiver, final String property, final Object[] args, final Object expectedValue )
    {
        if ( ! ( receiver instanceof ConnectionHolder ) )
        {
            return false;
        }

        final ConnectionHolder holder = (ConnectionHolder)receiver;

        if ( "stored".equals ( property ) && expectedValue instanceof Boolean )
        {
            // check if the connection holder was coming from a store
            if ( (Boolean)expectedValue )
            {
                return holder.getDiscoverer ().getStore () != null;
            }
            else
            {
                return holder.getDiscoverer ().getStore () == null;
            }
        }

        if ( "interfaceName".equals ( property ) && expectedValue != null )
        {
            return holder.getConnectionInformation ().getInterface ().equals ( expectedValue );
        }

        // default to false
        return false;
    }

}
