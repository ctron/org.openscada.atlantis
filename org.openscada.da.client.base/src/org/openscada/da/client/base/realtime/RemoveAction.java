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

package org.openscada.da.client.base.realtime;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

public class RemoveAction extends Action implements ISelectionChangedListener, IEditorActionDelegate
{
    private RealtimeListAdapter view = null;

    private Collection<ListEntry> entries;

    public RemoveAction ( final RealtimeListAdapter view )
    {
        super ( Messages.getString ( "RemoveAction.Title" ), Action.AS_PUSH_BUTTON ); //$NON-NLS-1$

        this.view = view;
    }

    @Override
    public void run ()
    {
        if ( this.entries == null || this.view == null )
        {
            return;
        }
        for ( final ListEntry entry : this.entries )
        {
            this.view.remove ( entry );
        }
    }

    public void selectionChanged ( final SelectionChangedEvent event )
    {
        setSelection ( event.getSelection () );
    }

    public void setActiveEditor ( final IAction action, final IEditorPart targetEditor )
    {
        if ( targetEditor instanceof RealtimeListAdapter )
        {
            this.view = (RealtimeListAdapter)targetEditor;
        }
    }

    public void run ( final IAction action )
    {
        run ();
    }

    public void selectionChanged ( final IAction action, final ISelection selection )
    {
        setSelection ( selection );
    }

    private void setSelection ( final ISelection selection )
    {
        this.entries = new LinkedList<ListEntry> ();

        if ( selection instanceof IStructuredSelection )
        {
            final Iterator<?> i = ( (IStructuredSelection)selection ).iterator ();
            while ( i.hasNext () )
            {
                final Object o = i.next ();
                if ( o instanceof ListEntry )
                {
                    this.entries.add ( (ListEntry)o );
                }
            }
        }
    }
}
