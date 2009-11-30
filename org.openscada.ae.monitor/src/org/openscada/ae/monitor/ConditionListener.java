package org.openscada.ae.monitor;

import org.openscada.ae.ConditionStatusInformation;

public interface ConditionListener
{
    public void statusChanged ( ConditionStatusInformation status );
}
