package org.openscada.da.client.connection.manager.view.action;

import org.openscada.da.client.connection.manager.view.ConnectionEntry;

public class DisconnectAction extends AbstractBaseConnectionAction
{

    @Override
    public void runFor ( final ConnectionEntry entry )
    {
        entry.getConnection ().disconnect ();
    }
}
