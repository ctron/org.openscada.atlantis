package org.openscada.hd.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IObjectActionDelegate;

public class RemoveQuery extends AbstractQueryAction implements IObjectActionDelegate
{
    public void run ( final IAction action )
    {
        this.query.remove ();
    }
}
