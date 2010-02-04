package org.openscada.ae.monitor;

import java.util.Date;

import org.openscada.sec.UserInformation;

public interface MonitorService
{
    public String getId ();

    public void addStatusListener ( ConditionListener listener );

    public void removeStatusListener ( ConditionListener listener );

    public void akn ( UserInformation userInformation, Date aknTimestamp );

    public void setActive ( boolean state );
}
