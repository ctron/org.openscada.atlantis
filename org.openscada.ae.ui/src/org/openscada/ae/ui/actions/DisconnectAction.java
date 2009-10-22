package org.openscada.ae.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IObjectActionDelegate;

public class DisconnectAction extends AbstractConnectionAction implements IObjectActionDelegate
{

    public void run ( final IAction action )
    {
        if ( this.connection != null )
        {
            this.connection.disconnect ();
        }
    }

}
