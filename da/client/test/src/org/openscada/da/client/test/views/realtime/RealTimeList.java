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

package org.openscada.da.client.test.views.realtime;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.ViewPart;
import org.openscada.core.Variant;
import org.openscada.da.client.WriteOperationCallback;
import org.openscada.da.client.test.dnd.ItemTransfer;

public class RealTimeList extends ViewPart
{

    public static final String VIEW_ID = "org.openscada.da.test.views.RealTimeList";

    private RemoveAction _removeAction = null;

    private TreeViewer _viewer;

    private final ListData _list = new ListData ();

    public RealTimeList ()
    {
        super ();
        this._removeAction = new RemoveAction ( this );
    }

    @Override
    public void createPartControl ( final Composite parent )
    {
        this._viewer = new TreeViewer ( parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION );

        TreeColumn col;

        col = new TreeColumn ( this._viewer.getTree (), SWT.NONE );
        col.setText ( "ID" );
        col = new TreeColumn ( this._viewer.getTree (), SWT.NONE );
        col.setText ( "State" );
        col = new TreeColumn ( this._viewer.getTree (), SWT.NONE );
        col.setText ( "Type" );
        col = new TreeColumn ( this._viewer.getTree (), SWT.NONE );
        col.setText ( "Value" );

        this._viewer.getTree ().setHeaderVisible ( true );

        final TableLayout tableLayout = new TableLayout ();
        tableLayout.addColumnData ( new ColumnWeightData ( 100, 100, true ) );
        tableLayout.addColumnData ( new ColumnWeightData ( 50, 50, true ) );
        tableLayout.addColumnData ( new ColumnWeightData ( 50, 50, true ) );
        tableLayout.addColumnData ( new ColumnWeightData ( 75, 75, true ) );
        this._viewer.getTree ().setLayout ( tableLayout );

        this._viewer.setLabelProvider ( new ItemListLabelProvider () );
        this._viewer.setContentProvider ( new ItemListContentProvider () );
        this._viewer.setComparator ( new RealTimeListComparator () );
        this._viewer.setInput ( this._list );

        getViewSite ().setSelectionProvider ( this._viewer );

        this._viewer.addSelectionChangedListener ( this._removeAction );
        this._viewer.addDoubleClickListener ( new IDoubleClickListener () {

            public void doubleClick ( final DoubleClickEvent event )
            {
                RealTimeList.this.handleDoubleClick ( event );
            }
        } );

        hookContextMenu ();
        contributeToActionBars ();

        addDropSupport ();
    }

    protected void handleDoubleClick ( final DoubleClickEvent event )
    {
        if ( ! ( event.getSelection () instanceof IStructuredSelection ) )
        {
            return;
        }

        final Object o = ( (IStructuredSelection)event.getSelection () ).getFirstElement ();
        if ( ! ( o instanceof ListEntry ) )
        {
            return;
        }

        final ListEntry entry = (ListEntry)o;

        Variant value = entry.getValue ();
        if ( value == null )
        {
            return;
        }
        if ( !value.isBoolean () )
        {
            return;
        }

        value = new Variant ( !value.asBoolean () );

        entry.getConnection ().getConnection ().write ( entry.getDataItem ().getItemId (), value, new WriteOperationCallback () {

            public void complete ()
            {
                // TODO Auto-generated method stub

            }

            public void error ( final Throwable e )
            {
                // TODO Auto-generated method stub

            }

            public void failed ( final String error )
            {
                // TODO Auto-generated method stub

            }
        } );
    }

    private void hookContextMenu ()
    {
        final MenuManager menuMgr = new MenuManager ( "#PopupMenu" );
        menuMgr.setRemoveAllWhenShown ( true );
        menuMgr.addMenuListener ( new IMenuListener () {
            public void menuAboutToShow ( final IMenuManager manager )
            {
                fillContextMenu ( manager );
            }
        } );
        final Menu menu = menuMgr.createContextMenu ( this._viewer.getControl () );
        this._viewer.getControl ().setMenu ( menu );
        getSite ().registerContextMenu ( menuMgr, this._viewer );
    }

    private void fillContextMenu ( final IMenuManager manager )
    {
        // Other plug-ins can contribute there actions here

        manager.add ( this._removeAction );
        manager.add ( new Separator () );
        manager.add ( new Separator ( IWorkbenchActionConstants.MB_ADDITIONS ) );
    }

    private void contributeToActionBars ()
    {
        final IActionBars bars = getViewSite ().getActionBars ();
        fillLocalPullDown ( bars.getMenuManager () );
        fillLocalToolBar ( bars.getToolBarManager () );
    }

    private void fillLocalToolBar ( final IToolBarManager manager )
    {
        manager.add ( this._removeAction );
    }

    private void fillLocalPullDown ( final IMenuManager manager )
    {
        manager.add ( this._removeAction );
    }

    private void addDropSupport ()
    {
        this._viewer.addDropSupport ( DND.DROP_COPY | DND.DROP_MOVE, new Transfer[] { ItemTransfer.getInstance () }, new ItemDropAdapter ( this._viewer ) );
    }

    @Override
    public void setFocus ()
    {
        this._viewer.getControl ().setFocus ();
    }

    public void remove ( final ListEntry entry )
    {
        this._list.remove ( entry );
    }

}
