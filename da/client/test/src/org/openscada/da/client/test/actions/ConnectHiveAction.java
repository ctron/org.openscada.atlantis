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
import org.eclipse.ui.IWorkbenchPart;
import org.openscada.da.client.test.impl.HiveConnection;

public class ConnectHiveAction implements IObjectActionDelegate, IViewActionDelegate
{
    private static Logger _log = Logger.getLogger ( ConnectHiveAction.class );
    
    private HiveConnection _connection = null;
   
    public void run ( IAction action )
    {
        if ( _connection == null )
        {
            return;
        }
        
        try
        {
            _connection.connect();
        }
        catch ( Throwable e )
        {
            _log.error ( "Connect failed", e );
        }
    }

    public void selectionChanged ( IAction action, ISelection selection )
    {
        _connection = null;
        
        if ( selection == null )
        {
            return;
        }
        if ( ! (selection instanceof IStructuredSelection) )
        {
            return;
        }
        
        IStructuredSelection sel = (IStructuredSelection)selection;
        Object obj = sel.getFirstElement ();
        
        if ( obj == null )
            return;
        if ( !(obj instanceof HiveConnection) )
            return;
        
        _connection = (HiveConnection)obj;
    }

    public void setActivePart ( IAction action, IWorkbenchPart targetPart )
    {
    }

    public void init ( IViewPart view )
    {
    }

}
