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
import org.openscada.core.Variant;
import org.openscada.da.client.base.browser.DataItemEntry;
import org.openscada.da.client.base.browser.FolderEntry;
import org.openscada.da.client.base.browser.HiveConnection;
import org.openscada.da.client.test.views.watch.DataItemWatchView;
import org.openscada.da.core.IODirection;

public class NewWatchWizard extends Wizard implements INewWizard
{
    private HiveConnection connection = null;

    private DataItemEntry itemEntry = null;

    private NewWatchWizardPage page = null;

    private IWorkbenchSite site = null;

    @Override
    public boolean performFinish ()
    {
        final String dataItemID = this.page.getDataItemID ();

        try
        {
            try
            {
                final DataItemEntry dataItem = new DataItemEntry ( dataItemID, new HashMap<String, Variant> (), null, this.connection, dataItemID, EnumSet.noneOf ( IODirection.class ) );
                final IViewPart viewer = this.site.getPage ().showView ( DataItemWatchView.VIEW_ID, dataItem.getAsSecondaryId (), IWorkbenchPage.VIEW_ACTIVATE );
                if ( viewer instanceof DataItemWatchView )
                {
                    ( (DataItemWatchView)viewer ).setDataItem ( dataItem );
                }
            }
            catch ( final Exception e )
            {
                throw new InvocationTargetException ( e );
            }
        }
        catch ( final InvocationTargetException e )
        {
            final Throwable realException = e.getTargetException ();
            MessageDialog.openError ( getShell (), Messages.getString ( "NewWatchWizard.errorDialog.title" ), realException.getMessage () ); //$NON-NLS-1$
            return false;
        }
        return true;
    }

    public void init ( final IWorkbench workbench, final IStructuredSelection selection )
    {
        setNeedsProgressMonitor ( true );
        setWindowTitle ( Messages.getString ( "NewWatchWizard.window.title" ) ); //$NON-NLS-1$

        this.site = workbench.getActiveWorkbenchWindow ().getActivePage ().getActivePart ().getSite ();

        final Object o = selection.getFirstElement ();
        if ( o instanceof HiveConnection )
        {
            this.connection = (HiveConnection)o;
        }
        else if ( o instanceof DataItemEntry )
        {
            this.itemEntry = (DataItemEntry)o;
            this.connection = this.itemEntry.getConnection ();
        }
        else if ( o instanceof FolderEntry )
        {
            this.connection = ( (FolderEntry)o ).getConnection ();
        }
    }

    @Override
    public void addPages ()
    {
        super.addPages ();

        addPage ( this.page = new NewWatchWizardPage () );
        if ( this.itemEntry != null )
        {
            this.page.setDataItemId ( this.itemEntry.getId () );
        }
    }

}
