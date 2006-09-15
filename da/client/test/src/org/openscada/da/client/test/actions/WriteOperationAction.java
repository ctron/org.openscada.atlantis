/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscada.da.client.test.actions;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWizard;
import org.openscada.da.client.test.wizards.WriteOperationWizard;

public class WriteOperationAction implements IObjectActionDelegate, IViewActionDelegate
{
    @SuppressWarnings("unused")
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
