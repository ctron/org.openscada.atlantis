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

package org.openscada.da.client.test.wizards;


import java.lang.reflect.InvocationTargetException;
import java.util.EnumSet;
import java.util.HashMap;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchSite;
import org.openscada.da.client.test.impl.DataItemEntry;
import org.openscada.da.client.test.impl.FolderEntry;
import org.openscada.da.client.test.impl.HiveConnection;
import org.openscada.da.client.test.views.DataItemWatchView;
import org.openscada.da.core.Variant;
import org.openscada.da.core.server.IODirection;

public class NewWatchWizard extends Wizard implements INewWizard
{
    private HiveConnection _connection = null;
    private NewWatchWizardPage _page = null;
    private IWorkbenchSite _site = null;
    
    @Override
    public boolean performFinish ()
    {
        final String dataItemID = _page.getDataItemID ();
        
        try
        {
            try
            {
                DataItemEntry dataItem = new DataItemEntry ( dataItemID, new HashMap<String,Variant> (), null, _connection, dataItemID, EnumSet.noneOf ( IODirection.class ) );
                IViewPart viewer = _site.getPage ().showView ( DataItemWatchView.VIEW_ID, dataItem.getId (), IWorkbenchPage.VIEW_ACTIVATE );
                if ( viewer instanceof DataItemWatchView )
                {
                    ((DataItemWatchView)viewer).setDataItem ( dataItem );
                }
            }
            catch ( Exception e )
            {
                throw new InvocationTargetException ( e );
            }
        }
        catch ( InvocationTargetException e )
        {
            Throwable realException = e.getTargetException ();
            MessageDialog.openError ( getShell(), "Error", realException.getMessage () );
            return false;
        }
        return true;
    }    

    public void init ( IWorkbench workbench, IStructuredSelection selection )
    {
        setNeedsProgressMonitor ( true );
        setWindowTitle ( "Request watch" );
        
        _site = workbench.getActiveWorkbenchWindow ().getActivePage ().getActivePart ().getSite ();
        
        Object o = selection.getFirstElement ();
        if ( o instanceof HiveConnection )
        {
            _connection = (HiveConnection)o;
        }
        else if ( o instanceof DataItemEntry )
        {
            _connection = ((DataItemEntry)o).getConnection ();
        }
        else if ( o instanceof FolderEntry )
        {
            _connection = ((FolderEntry)o).getConnection ();
        }
    }
    
    @Override
    public void addPages ()
    {
        super.addPages ();
        
        addPage ( _page = new NewWatchWizardPage () );
    }
    

}
