package org.openscada.core.server.common.session;

import java.util.Set;

import org.openscada.core.server.common.osgi.SessionPrivilegeTracker.PrivilegeListener;

public class PrivilegeListenerImpl implements PrivilegeListener
{

    private final AbstractSessionImpl session;

    public PrivilegeListenerImpl ( final AbstractSessionImpl session )
    {
        this.session = session;
    }

    @Override
    public void privilegesChanged ( final Set<String> granted )
    {
        this.session.setPrivileges ( granted );
    }

}
