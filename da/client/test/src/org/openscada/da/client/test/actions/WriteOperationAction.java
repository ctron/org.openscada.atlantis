package org.openscada.da.client.test.actions;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PartInitException;
import org.openscada.da.client.test.impl.HiveConnection;
import org.openscada.da.client.test.impl.HiveItem;
import org.openscada.da.client.test.views.DataItemWatchView;
import org.openscada.da.client.test.wizards.WriteOperationWizard;

public class WriteOperationAction implements IObjectActionDelegate, IViewActionDelegate
{
    private static Logger _log = Logger.getLogger ( WriteOperationAction.class );
    
    private IWorkbenchPartSite _site = null;
    
    private IStructuredSelection _selection = null;
   
    public void run ( IAction action )
    {
        if ( _selection == null )
            return;
        
        IWorkbenchWizard wiz = new WriteOperationWizard();
        wiz.init ( _site.getWorkbenchWindow ().getWorkbench (), _selection );
        
        // Embed the wizard into a dialog
        WizardDialog dialog = new WizardDialog ( _site.getShell () , wiz );
        dialog.open();
    }

    public void selectionChanged ( IAction action, ISelection selection )
    {     
        if ( selection == null )
            return;
        if ( ! (selection instanceof IStructuredSelection) )
            return;
        
        _selection = (IStructuredSelection)selection;
    }

    public void setActivePart ( IAction action, IWorkbenchPart targetPart )
    {
        _site = targetPart.getSite ();
    }

    public void init ( IViewPart view )
    {
        _site = view.getSite ();
    }

}
