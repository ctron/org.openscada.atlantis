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
import java.util.Iterator;
import java.util.LinkedList;

import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.openscada.da.client.test.impl.DataItemEntry;
import org.openscada.da.client.test.impl.HiveItem;
import org.openscada.da.client.test.views.realtime.ListEntry;

public class ShowDetailsAction implements IObjectActionDelegate
{
    private Collection<DataItemHolder> items = new LinkedList<DataItemHolder> ();

    private IWorkbenchPage page;

    public void setActivePart ( IAction action, IWorkbenchPart targetPart )
    {
        this.page = targetPart.getSite ().getPage ();
    }

    public void run ( IAction action )
    {
        MultiStatus status = new MultiStatus ( Activator.PLUGIN_ID, 0, "Opening details", null );
        for ( DataItemHolder item : items )
        {
            try
            {
                showItem ( item );
            }
            catch ( PartInitException e )
            {
                status.add ( e.getStatus () );
            }
        }
        if ( !status.isOK () )
        {
            ErrorDialog.openError ( this.page.getWorkbenchWindow ().getShell (), "View Error", "Failed to show data item details", status );
        }
    }

    private void showItem ( DataItemHolder item ) throws PartInitException
    {
        DetailsViewPart view = (DetailsViewPart)this.page.showView ( DetailsViewPart.VIEW_ID, asSecondardId ( item ), IWorkbenchPage.VIEW_ACTIVATE );
        view.setDataItem ( item );
    }

    private String asSecondardId ( DataItemHolder item )
    {
        return item.getItemId ().replace ( "_", "__" ).replace ( ':', '_' );
    }

    public void selectionChanged ( IAction action, ISelection selection )
    {
        clearSelection ();
        if ( ! ( selection instanceof IStructuredSelection ) )
        {
            return;
        }

        IStructuredSelection sel = (IStructuredSelection)selection;
        for ( Iterator<?> i = sel.iterator (); i.hasNext (); )
        {
            Object o = i.next ();
            if ( o instanceof ListEntry )
            {
                ListEntry entry = (ListEntry)o;
                items.add ( new DataItemHolder ( entry.getDataItem ().getConnection ().getConnection (), entry.getDataItem ().getConnection ().getItemManager (), entry.getDataItem ().getId () ) );
            }
            else if ( o instanceof HiveItem )
            {
                HiveItem item = (HiveItem)o;
                items.add ( new DataItemHolder ( item.getConnection ().getConnection (), item.getConnection ().getItemManager (), item.getId () ) );
            }
            else if ( o instanceof DataItemEntry )
            {
                DataItemEntry entry = (DataItemEntry)o;
                items.add ( new DataItemHolder ( entry.getConnection ().getConnection (), entry.getConnection ().getItemManager (), entry.getId () ) );
            }
        }
    }

    private void clearSelection ()
    {
        items.clear ();
    }

}
