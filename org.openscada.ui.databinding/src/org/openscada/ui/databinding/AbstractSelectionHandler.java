package org.openscada.ui.databinding;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public abstract class AbstractSelectionHandler extends AbstractHandler
{

    private IWorkbenchWindow activeWindow;

    protected IWorkbenchPage getActivePage ()
    {
        return this.activeWindow.getActivePage ();
    }

    /**
     * Returns the selection of the active workbench window.
     *
     * @return the current selection in the active workbench window or <code>null</code>
     */
    protected final IStructuredSelection getSelection ()
    {
        final IWorkbenchWindow window = getWorkbenchWindow ();
        if ( window != null )
        {
            final ISelection sel = window.getSelectionService ().getSelection ();
            if ( sel instanceof IStructuredSelection )
            {
                return (IStructuredSelection)sel;
            }
        }
        return null;
    }

    /**
     * Returns the active workbench window.
     *
     * @return the active workbench window or <code>null</code> if not available
     */
    protected final IWorkbenchWindow getWorkbenchWindow ()
    {
        if ( this.activeWindow == null )
        {
            this.activeWindow = PlatformUI.getWorkbench ().getActiveWorkbenchWindow ();
        }
        return this.activeWindow;
    }

}