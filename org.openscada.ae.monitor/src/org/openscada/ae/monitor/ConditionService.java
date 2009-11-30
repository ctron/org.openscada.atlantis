package org.openscada.ae.monitor;

import java.util.Date;

public interface ConditionService
{
    public String getId ();

    public void addStatusListener ( ConditionListener listener );

    public void removeStatusListener ( ConditionListener listener );

    public void akn ( String aknUser, Date aknTimestamp );

    public void setActive ( boolean state );
}
