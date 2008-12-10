package org.openscada.da.project.editor.realtimelist;

import java.net.URI;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
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
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.openscada.core.Variant;
import org.openscada.da.client.WriteOperationCallback;
import org.openscada.da.dataItemList.ItemType;
import org.openscada.da.dataItemList.ItemsDocument;
import org.openscada.da.dataItemList.ItemsType;
import org.openscada.da.project.Activator;
import org.openscada.rcp.da.client.dnd.ItemTransfer;

public class RealtimeListEditor extends EditorPart
{

    private final RemoveAction _removeAction;

    private TreeViewer _viewer;

    private final ListData _list = new ListData ();

    private boolean dirty = false;

    public RealtimeListEditor ()
    {
        this._removeAction = new RemoveAction ( this );
    }

    @Override
    public void doSave ( final IProgressMonitor monitor )
    {
        final List<ListEntry> list = this._list.getItems ();

        monitor.beginTask ( "Saving", list.size () );
        try
        {
            final ItemsDocument doc = ItemsDocument.Factory.newInstance ();
            final ItemsType items = doc.addNewItems ();
            for ( final ListEntry entry : list )
            {
                final ItemType item = items.addNewItem ();
                item.setItemId ( entry.getDataItem ().getItemId () );
                item.setUri ( entry.getUri ().toString () );
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

        this._list.clear ();

        final IFile file = ( (IFileEditorInput)getEditorInput () ).getFile ();

        try
        {
            final ItemsDocument doc = ItemsDocument.Factory.parse ( file.getContents () );

            for ( final ItemType item : doc.getItems ().getItemList () )
            {
                final URI uri = new URI ( item.getUri () );
                this._list.add ( item.getItemId (), uri, Activator.getConnectionManager ().getItemManager ( uri, true ) );
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

        getSite ().setSelectionProvider ( this._viewer );

        this._viewer.addSelectionChangedListener ( this._removeAction );
        this._viewer.addDoubleClickListener ( new IDoubleClickListener () {

            public void doubleClick ( final DoubleClickEvent event )
            {
                RealtimeListEditor.this.handleDoubleClick ( event );
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
        final IActionBars bars = getEditorSite ().getActionBars ();
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
        this._viewer.addDropSupport ( DND.DROP_COPY | DND.DROP_MOVE, new Transfer[] { ItemTransfer.getInstance (), URLTransfer.getInstance () }, new ItemDropAdapter ( this, this._viewer ) );
    }

    @Override
    public void setFocus ()
    {
        this._viewer.getControl ().setFocus ();
    }

    public void remove ( final ListEntry entry )
    {
        this._list.remove ( entry );
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
