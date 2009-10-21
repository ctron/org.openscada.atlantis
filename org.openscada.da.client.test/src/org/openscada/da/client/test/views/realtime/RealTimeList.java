/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
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
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.URLTransfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.openscada.core.Variant;
import org.openscada.da.client.base.realtime.ItemDropAdapter;
import org.openscada.da.client.base.realtime.ItemListContentProvider;
import org.openscada.da.client.base.realtime.ItemListLabelProvider;
import org.openscada.da.client.base.realtime.ListData;
import org.openscada.da.client.base.realtime.ListEntry;
import org.openscada.da.client.base.realtime.ListEntryComparator;
import org.openscada.da.client.base.realtime.RealtimeListAdapter;
import org.openscada.da.client.base.realtime.RealtimeListDragSourceListener;
import org.openscada.da.client.base.realtime.RemoveAction;
import org.openscada.da.ui.connection.data.Item;
import org.openscada.da.ui.connection.dnd.ItemTransfer;

public class RealTimeList extends ViewPart implements RealtimeListAdapter
{

    public static final String VIEW_ID = "org.openscada.da.test.views.RealTimeList";

    private RemoveAction removeAction = null;

    private TreeViewer viewer;

    private final ListData list = new ListData ();

    public RealTimeList ()
    {
        super ();
        this.removeAction = new RemoveAction ( this );
    }

    @Override
    public void createPartControl ( final Composite parent )
    {
        this.viewer = new TreeViewer ( parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION );

        TreeColumn col;

        col = new TreeColumn ( this.viewer.getTree (), SWT.NONE );
        col.setText ( "ID" );
        col = new TreeColumn ( this.viewer.getTree (), SWT.NONE );
        col.setText ( "State" );
        col = new TreeColumn ( this.viewer.getTree (), SWT.NONE );
        col.setText ( "Type" );
        col = new TreeColumn ( this.viewer.getTree (), SWT.NONE );
        col.setText ( "Value" );

        this.viewer.getTree ().setHeaderVisible ( true );

        final TableLayout tableLayout = new TableLayout ();
        tableLayout.addColumnData ( new ColumnWeightData ( 100, 100, true ) );
        tableLayout.addColumnData ( new ColumnWeightData ( 50, 50, true ) );
        tableLayout.addColumnData ( new ColumnWeightData ( 50, 50, true ) );
        tableLayout.addColumnData ( new ColumnWeightData ( 75, 75, true ) );
        this.viewer.getTree ().setLayout ( tableLayout );

        this.viewer.setLabelProvider ( new ItemListLabelProvider () );
        this.viewer.setContentProvider ( new ItemListContentProvider () );
        this.viewer.setComparator ( new ListEntryComparator () );
        this.viewer.setInput ( this.list );

        getViewSite ().setSelectionProvider ( this.viewer );

        this.viewer.addSelectionChangedListener ( this.removeAction );
        this.viewer.addDoubleClickListener ( new IDoubleClickListener () {

            public void doubleClick ( final DoubleClickEvent event )
            {
                RealTimeList.this.handleDoubleClick ( event );
            }
        } );

        hookContextMenu ();
        contributeToActionBars ();

        addDropSupport ();
        addDragSupport ();
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

        entry.getDataItem ().write ( value );
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
        final Menu menu = menuMgr.createContextMenu ( this.viewer.getControl () );
        this.viewer.getControl ().setMenu ( menu );
        getSite ().registerContextMenu ( menuMgr, this.viewer );
    }

    private void fillContextMenu ( final IMenuManager manager )
    {
        // Other plug-ins can contribute there actions here

        manager.add ( this.removeAction );
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
        manager.add ( this.removeAction );
    }

    private void fillLocalPullDown ( final IMenuManager manager )
    {
        manager.add ( this.removeAction );
    }

    private void addDropSupport ()
    {
        this.viewer.addDropSupport ( DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK, new Transfer[] { ItemTransfer.getInstance () }, new ItemDropAdapter ( this.viewer, this ) );
    }

    private void addDragSupport ()
    {
        this.viewer.addDragSupport ( DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK, new Transfer[] { ItemTransfer.getInstance (), URLTransfer.getInstance (), TextTransfer.getInstance () }, new RealtimeListDragSourceListener ( this.viewer ) );
    }

    @Override
    public void setFocus ()
    {
        this.viewer.getControl ().setFocus ();
    }

    /* (non-Javadoc)
     * @see org.openscada.da.client.test.views.realtime.RealtimeListAdapter#remove(org.openscada.da.client.test.views.realtime.ListEntry)
     */
    public void remove ( final ListEntry entry )
    {
        this.list.remove ( entry );
    }

    public void add ( final ListEntry entry )
    {
        this.list.add ( entry );
    }

    @Override
    public void saveState ( final IMemento memento )
    {
        super.saveState ( memento );

        if ( memento != null )
        {
            for ( final ListEntry entry : this.list.getItems () )
            {
                final Item item = entry.getItem ();
                saveItem ( memento, item );
            }
        }
    }

    private void saveItem ( final IMemento memento, final Item item )
    {
        final IMemento child = memento.createChild ( "item" );
        child.putString ( "id", item.getId () );
        child.putString ( "connection", item.getConnectionString () );
    }

    @Override
    public void init ( final IViewSite site, final IMemento memento ) throws PartInitException
    {
        super.init ( site, memento );

        if ( memento != null )
        {
            for ( final IMemento child : memento.getChildren ( "item" ) )
            {
                final Item item = new Item ( child.getString ( "connection" ), child.getString ( "id" ) );
                this.list.add ( item );
            }
        }
    }
}
