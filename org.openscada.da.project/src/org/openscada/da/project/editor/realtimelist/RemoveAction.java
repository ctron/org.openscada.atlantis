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

package org.openscada.da.project.editor.realtimelist;

import java.util.Iterator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;

public class RemoveAction extends Action implements ISelectionChangedListener
{
    private RealtimeListEditor _view = null;

    private ISelection _selection = null;

    public RemoveAction ( final RealtimeListEditor view )
    {
        super ( "Remove", Action.AS_PUSH_BUTTON );

        this._view = view;
    }

    @Override
    public void run ()
    {
        if ( this._selection instanceof IStructuredSelection )
        {
            final IStructuredSelection selection = (IStructuredSelection)this._selection;
            final Iterator<?> i = selection.iterator ();
            while ( i.hasNext () )
            {
                final Object o = i.next ();
                if ( o instanceof ListEntry )
                {
                    this._view.remove ( (ListEntry)o );
                }
            }
        }
    }

    public void selectionChanged ( final SelectionChangedEvent event )
    {
        this._selection = event.getSelection ();
    }
}
