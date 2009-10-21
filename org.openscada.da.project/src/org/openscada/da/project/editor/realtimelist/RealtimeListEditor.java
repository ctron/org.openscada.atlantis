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

import java.net.URI;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.URLTransfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.openscada.core.Variant;
import org.openscada.da.client.base.realtime.ItemDropAdapter;
import org.openscada.da.client.base.realtime.ItemListContentProvider;
import org.openscada.da.client.base.realtime.ItemListLabelProvider;
import org.openscada.da.client.base.realtime.ListData;
import org.openscada.da.client.base.realtime.ListEntry;
import org.openscada.da.client.base.realtime.ListEntryComparator;
import org.openscada.da.client.base.realtime.RealtimeListAdapter;
import org.openscada.da.dataItemList.ItemType;
import org.openscada.da.dataItemList.ItemsDocument;
import org.openscada.da.dataItemList.ItemsType;
import org.openscada.da.project.Activator;
import org.openscada.da.ui.connection.data.Item;
import org.openscada.da.ui.connection.dnd.ItemTransfer;

public class RealtimeListEditor extends EditorPart implements RealtimeListAdapter
{

    private TreeViewer viewer;

    private final ListData list = new ListData ();

    private boolean dirty = false;

    @Override
    public void doSave ( final IProgressMonitor monitor )
    {
        final List<ListEntry> list = this.list.getItems ();

        monitor.beginTask ( "Saving", list.size () );
        try
        {
            final ItemsDocument doc = ItemsDocument.Factory.newInstance ();
            final ItemsType items = doc.addNewItems ();
            for ( final ListEntry entry : list )
            {
                final ItemType itemType = items.addNewItem ();
                final Item item = entry.getItem ();
                itemType.setItemId ( item.getId () );
                itemType.setUri ( item.getConnectionString () );
            }

            final IFile file = ( (IFileEditorInput)getEditorInput () ).getFile ();
            file.setContents ( doc.newInputStream (), true, true, monitor );

            cleanDirty ();
        }
        catch ( final CoreException e )
        {
            ErrorDialog.openError ( getSite ().getShell (), "Error", "Error saving file", e.getStatus () );
        }
        finally
        {
            monitor.done ();
        }
    }

    @Override
    public void doSaveAs ()
    {

    }

    @Override
    public void init ( final IEditorSite site, final IEditorInput input ) throws PartInitException
    {
        setSite ( site );
        setInput ( input );
    }

    @Override
    protected void setInput ( final IEditorInput input )
    {
        super.setInput ( input );

        this.list.clear ();

        final IFile file = ( (IFileEditorInput)getEditorInput () ).getFile ();

        try
        {
            final ItemsDocument doc = ItemsDocument.Factory.parse ( file.getContents () );

            for ( final ItemType itemType : doc.getItems ().getItemList () )
            {
                final Item item = new Item ( itemType.getUri (), itemType.getItemId () );
                final URI uri = new URI ( itemType.getUri () );

                this.list.add ( item );
            }
        }
        catch ( final CoreException e )
        {
            ErrorDialog.openError ( getSite ().getShell (), "Error", "Error loading list", e.getStatus () );
        }
        catch ( final Throwable e )
        {
            ErrorDialog.openError ( getSite ().getShell (), "Error", "Error loading list", new Status ( Status.ERROR, Activator.PLUGIN_ID, "Failed to load list", e ) );
        }
    }

    @Override
    public boolean isDirty ()
    {
        return this.dirty;
    }

    @Override
    public boolean isSaveAsAllowed ()
    {
        return false;
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

        getSite ().setSelectionProvider ( this.viewer );

        this.viewer.addDoubleClickListener ( new IDoubleClickListener () {

            public void doubleClick ( final DoubleClickEvent event )
            {
                RealtimeListEditor.this.handleDoubleClick ( event );
            }
        } );

        hookContextMenu ();

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
        manager.add ( new Separator () );
        manager.add ( new Separator ( IWorkbenchActionConstants.MB_ADDITIONS ) );
    }

    private void addDropSupport ()
    {
        this.viewer.addDropSupport ( DND.DROP_COPY | DND.DROP_MOVE, new Transfer[] { ItemTransfer.getInstance (), URLTransfer.getInstance () }, new ItemDropAdapter ( this.viewer, this ) );
    }

    @Override
    public void setFocus ()
    {
        this.viewer.getControl ().setFocus ();
    }

    public void remove ( final ListEntry entry )
    {
        this.list.remove ( entry );
        makeDirty ();
    }

    public void add ( final ListEntry entry )
    {
        this.list.add ( entry );
        makeDirty ();
    }

    protected void makeDirty ()
    {
        this.dirty = true;
        firePropertyChange ( PROP_DIRTY );
    }

    protected void cleanDirty ()
    {
        this.dirty = false;
        firePropertyChange ( PROP_DIRTY );
    }

}
