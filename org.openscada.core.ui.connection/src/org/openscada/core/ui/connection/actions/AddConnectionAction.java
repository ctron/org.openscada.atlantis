package org.openscada.core.ui.connection.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.openscada.core.ui.connection.wizards.AddConnectionWizard;

public class AddConnectionAction implements IObjectActionDelegate
{

    private Shell shell;

    private IWorkbench workbench;

    private IStructuredSelection selection;

    public AddConnectionAction ()
    {
    }

    public void setActivePart ( final IAction action, final IWorkbenchPart targetPart )
    {
        this.shell = targetPart.getSite ().getShell ();
        this.workbench = targetPart.getSite ().getWorkbenchWindow ().getWorkbench ();
    }

    public void run ( final IAction action )
    {
        // TODO Auto-generated method stub

        final AddConnectionWizard wizard = new AddConnectionWizard ();
        wizard.init ( this.workbench, this.selection );

        final WizardDialog dialog = new WizardDialog ( this.shell, wizard );
        dialog.create ();
        dialog.open ();
    }

    public void selectionChanged ( final IAction action, final ISelection selection )
    {
        this.selection = null;
        if ( selection instanceof IStructuredSelection )
        {
            this.selection = (IStructuredSelection)selection;
        }

        action.setEnabled ( selection != null );
    }
}
