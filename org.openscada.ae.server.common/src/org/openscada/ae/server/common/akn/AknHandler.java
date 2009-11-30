package org.openscada.ae.server.common.akn;

import java.util.Date;

public interface AknHandler
{
    public boolean acknowledge ( String conditionId, String aknUser, Date aknTimestamp );
}
