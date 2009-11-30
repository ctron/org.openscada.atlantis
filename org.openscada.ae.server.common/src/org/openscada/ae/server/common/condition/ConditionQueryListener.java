package org.openscada.ae.server.common.condition;

import org.openscada.ae.ConditionStatusInformation;

public interface ConditionQueryListener
{
    public void dataChanged ( ConditionStatusInformation[] addedOrUpdated, String[] removed );
}
