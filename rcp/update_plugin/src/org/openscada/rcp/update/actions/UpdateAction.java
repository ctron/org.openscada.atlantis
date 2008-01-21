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

package org.openscada.rcp.update.actions;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.openscada.rcp.update.Updater;

public class UpdateAction implements IWorkbenchWindowActionDelegate
{
    private static Logger _log = Logger.getLogger ( UpdateAction.class );
    
    private Shell _shell;
    private Display _display;
    private IWorkbenchWindow _window;
    
    public void dispose ()
    {
        if ( _window == null )
        {
            _shell.close ();
            _display.dispose ();
        }
    }

    public void init ( IWorkbenchWindow window )
    {
        _window = window;
        _shell = window.getShell ();
        _display = _shell.getDisplay (); 
    }
    
    
    public void run ( IAction action )
    {
        if ( _shell == null )
        {
            _display = Display.getCurrent ();
            _shell = new Shell ( _display, SWT.NONE );
        }
        
        new Updater ( _shell ).performUpdate ();
        
    }

        
    public void selectionChanged ( IAction action, ISelection selection )
    {
    }

}
