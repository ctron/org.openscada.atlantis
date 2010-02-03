package org.openscada.core.server.common.session;

import org.openscada.core.server.common.UserInformation;

public abstract class AbstractSessionImpl implements UserSession
{
    private final UserInformation userInformation;

    public AbstractSessionImpl ( final UserInformation userInformation )
    {
        this.userInformation = userInformation;
    }

    public UserInformation getUserInformation ()
    {
        return this.userInformation;
    }
}
