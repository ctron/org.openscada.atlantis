package org.openscada.ae.ui.connection.commands;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.openscada.ae.ui.connection.data.ConditionStatusBean;

public class AknHandler extends AbstractMonitorHandler
{

    public Object execute ( final ExecutionEvent event ) throws ExecutionException
    {
        for ( final ConditionStatusBean bean : getMonitors () )
        {
            bean.akn ();
        }
        return null;
    }

}
