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
import org.openscada.da.client.test.impl.DataItemEntry;
import org.openscada.da.client.test.views.DataItemWatchView;

public class WatchItemAction implements IViewActionDelegate, IObjectActionDelegate
{
    @SuppressWarnings("unused")
    private static Logger _log = Logger.getLogger ( WatchItemAction.class );
    
    private IWorkbenchPartSite _site = null;
    
    private DataItemEntry _item = null;
    
    public void init ( IViewPart view )
    {
        _site = view.getSite();
    }

    public void run ( IAction action )
    {
        if ( _item == null )
            return;
       
        try
        {
            IViewPart viewer = _site.getPage ().showView ( DataItemWatchView.VIEW_ID, _item.getId(), IWorkbenchPage.VIEW_ACTIVATE );
            if ( viewer instanceof DataItemWatchView )
            {
                ((DataItemWatchView)viewer).setDataItem ( _item );
            }
        }
        catch ( PartInitException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void selectionChanged ( IAction action, ISelection selection )
    {
        _item = null;
        
        if ( selection == null )
            return;
        if ( ! (selection instanceof IStructuredSelection) )
            return;
        
        IStructuredSelection sel = (IStructuredSelection)selection;
        Object obj = sel.getFirstElement();
        
        if ( obj == null )
            return;
        if ( !(obj instanceof DataItemEntry) )
            return;
        
        _item = (DataItemEntry)obj;
    }

    public void setActivePart ( IAction action, IWorkbenchPart targetPart )
    {
        _site = targetPart.getSite();
    }

}
