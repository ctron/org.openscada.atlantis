package org.openscada.ae.server.common.akn;

import java.util.Date;

import org.openscada.sec.UserInformation;

public interface AknHandler
{
    public boolean acknowledge ( String conditionId, UserInformation userInformation, Date aknTimestamp );
}
