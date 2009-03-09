/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
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

package org.openscada.da.client.dataitem.details;

import java.util.Collection;
import java.util.LinkedList;

import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.openscada.da.client.base.connection.ConnectionManager;
import org.openscada.da.client.base.item.DataItemHolder;
import org.openscada.da.client.base.item.ItemSelectionHelper;

public class ShowDetailsAction implements IObjectActionDelegate
{
    private final Collection<DataItemHolder> items = new LinkedList<DataItemHolder> ();

    private IWorkbenchPage page;

    public void setActivePart ( final IAction action, final IWorkbenchPart targetPart )
    {
        this.page = targetPart.getSite ().getPage ();
    }

    public void run ( final IAction action )
    {
        final MultiStatus status = new MultiStatus ( Activator.PLUGIN_ID, 0, "Opening details", null );
        for ( final DataItemHolder item : this.items )
        {
            try
            {
                showItem ( item );
            }
            catch ( final PartInitException e )
            {
                status.add ( e.getStatus () );
            }
        }
        if ( !status.isOK () )
        {
            ErrorDialog.openError ( this.page.getWorkbenchWindow ().getShell (), "View Error", "Failed to show data item details", status );
        }
    }

    private void showItem ( final DataItemHolder item ) throws PartInitException
    {
        final DetailsViewPart view = (DetailsViewPart)this.page.showView ( DetailsViewPart.VIEW_ID, asSecondardId ( item ), IWorkbenchPage.VIEW_ACTIVATE );
        view.setDataItem ( item );
    }

    private String asSecondardId ( final DataItemHolder item )
    {
        return item.getItemId ().replace ( "_", "__" ).replace ( ':', '_' );
    }

    public void selectionChanged ( final IAction action, final ISelection selection )
    {
        clearSelection ();

        this.items.addAll ( ItemSelectionHelper.getSelectionHookedUp ( selection, ConnectionManager.getDefault () ) );
    }

    private void clearSelection ()
    {
        this.items.clear ();
    }

}
