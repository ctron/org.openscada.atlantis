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

package org.openscada.ae.client.test.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.PartInitException;
import org.openscada.ae.client.test.Activator;
import org.openscada.ae.client.test.impl.StorageQuery;
import org.openscada.ae.client.test.views.QueryView;

public class ShowQueryAction implements IViewActionDelegate
{

    private StorageQuery _query = null;
    private IWorkbenchSite _site = null;
    
    public void init ( IViewPart view )
    {
        _site = view.getSite ();
    }

    public void run ( IAction action )
    {
        try
        {
            IViewPart viewer = _site.getPage ().showView ( QueryView.VIEW_ID, _query.getQueryDescription ().getId (), IWorkbenchPage.VIEW_ACTIVATE );
            if ( viewer instanceof QueryView )
            {
                ((QueryView)viewer).setQuery ( _query );
            }
        }
        catch ( PartInitException e )
        {
            
            Activator.getDefault ().notifyError ( "Unable to show query", e );
        }
    }

    public void selectionChanged ( IAction action, ISelection selection )
    {
        _query = null;
        
        if ( selection instanceof IStructuredSelection )
        {
            IStructuredSelection structuredSelection = (IStructuredSelection)selection;
            if ( structuredSelection.getFirstElement () instanceof StorageQuery )
            {
                _query = (StorageQuery)structuredSelection.getFirstElement ();
            }
        }
    }

    public StorageQuery getQuery ()
    {
        return _query;
    }

    public void setQuery ( StorageQuery query )
    {
        _query = query;
    }

}
