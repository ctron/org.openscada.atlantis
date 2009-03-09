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
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.openscada.da.client.base.browser.DataItemEntry;
import org.openscada.da.client.test.views.watch.DataItemWatchView;

public class WatchItemAction implements IViewActionDelegate, IObjectActionDelegate
{
    @SuppressWarnings ( "unused" )
    private static Logger _log = Logger.getLogger ( WatchItemAction.class );

    private IWorkbenchPartSite _site = null;

    private DataItemEntry _item = null;

    public void init ( final IViewPart view )
    {
        this._site = view.getSite ();
    }

    public void run ( final IAction action )
    {
        if ( this._item == null )
        {
            return;
        }

        try
        {
            final IViewPart viewer = this._site.getPage ().showView ( DataItemWatchView.VIEW_ID, this._item.getAsSecondaryId (), IWorkbenchPage.VIEW_ACTIVATE );
            if ( viewer instanceof DataItemWatchView )
            {
                ( (DataItemWatchView)viewer ).setDataItem ( this._item );
            }
        }
        catch ( final PartInitException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace ();
        }
    }

    public void selectionChanged ( final IAction action, final ISelection selection )
    {
        this._item = null;

        if ( selection == null )
        {
            return;
        }
        if ( ! ( selection instanceof IStructuredSelection ) )
        {
            return;
        }

        final IStructuredSelection sel = (IStructuredSelection)selection;
        final Object obj = sel.getFirstElement ();

        if ( obj == null )
        {
            return;
        }
        if ( ! ( obj instanceof DataItemEntry ) )
        {
            return;
        }

        this._item = (DataItemEntry)obj;
    }

    public void setActivePart ( final IAction action, final IWorkbenchPart targetPart )
    {
        this._site = targetPart.getSite ();
    }

}
