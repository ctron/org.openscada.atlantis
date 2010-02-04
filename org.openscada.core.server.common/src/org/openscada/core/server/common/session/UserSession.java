package org.openscada.core.server.common.session;

import org.openscada.core.server.Session;
import org.openscada.sec.UserInformation;

public interface UserSession extends Session
{
    public UserInformation getUserInformation ();
}
