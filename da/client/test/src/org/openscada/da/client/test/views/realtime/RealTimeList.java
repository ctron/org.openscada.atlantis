package org.openscada.da.client.test.views.realtime;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ColumnWeightData;
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
import org.openscada.da.client.test.dnd.ItemTransfer;

public class RealTimeList extends ViewPart
{

    public static final String VIEW_ID = "org.openscada.da.test.views.RealTimeList";
    
    private RemoveAction _removeAction = null;
    
    private TreeViewer _viewer;
    private ListData _list = new ListData ();
    
    public RealTimeList ()
    {
        super ();
        _removeAction = new RemoveAction ( this );
    }

    @Override
    public void createPartControl ( Composite parent )
    {
        _viewer = new TreeViewer ( parent );
        
        TreeColumn col;
        
        col = new TreeColumn ( _viewer.getTree (), SWT.NONE );
        col.setText ( "ID" );
        col = new TreeColumn ( _viewer.getTree (), SWT.NONE );
        col.setText ( "Type" );
        col = new TreeColumn ( _viewer.getTree (), SWT.NONE );
        col.setText ( "Value" );
        
        _viewer.getTree ().setHeaderVisible ( true );
        
        TableLayout tableLayout = new TableLayout ();
        tableLayout.addColumnData ( new ColumnWeightData ( 100, 100, true ) );
        tableLayout.addColumnData ( new ColumnWeightData ( 50, 50, true ) );
        tableLayout.addColumnData ( new ColumnWeightData ( 75, 75, true ) );
        _viewer.getTree ().setLayout ( tableLayout );
        
        _viewer.setLabelProvider ( new ItemListLabelProvider () );
        _viewer.setContentProvider ( new ItemListContentProvider () );
        _viewer.setInput ( _list );
        
        getViewSite ().setSelectionProvider ( _viewer );
        
        _viewer.addSelectionChangedListener ( _removeAction );
        
        hookContextMenu ();
        contributeToActionBars ();
        
        addDropSupport ();
    }
    
    private void hookContextMenu ()
    {
        MenuManager menuMgr = new MenuManager ( "#PopupMenu" );
        menuMgr.setRemoveAllWhenShown ( true );
        menuMgr.addMenuListener ( new IMenuListener () {
            public void menuAboutToShow(IMenuManager manager) {
                fillContextMenu ( manager );
            }
        });
        Menu menu = menuMgr.createContextMenu ( _viewer.getControl () );
        _viewer.getControl ().setMenu ( menu );
        getSite ().registerContextMenu ( menuMgr, _viewer );
    }
    
    private void fillContextMenu ( IMenuManager manager )
    {
        // Other plug-ins can contribute there actions here
        
        manager.add ( _removeAction );
        manager.add ( new Separator () );
        manager.add ( new Separator ( IWorkbenchActionConstants.MB_ADDITIONS ) );
    }
    
    private void contributeToActionBars ()
    {
        IActionBars bars = getViewSite ().getActionBars ();
        fillLocalPullDown ( bars.getMenuManager () );
        fillLocalToolBar ( bars.getToolBarManager () );
    }
    
    private void fillLocalToolBar ( IToolBarManager manager )
    {
        manager.add ( _removeAction );
    }
    
    private void fillLocalPullDown ( IMenuManager manager )
    {
        manager.add ( _removeAction );
    }

    private void addDropSupport ()
    {
        _viewer.addDropSupport ( DND.DROP_COPY | DND.DROP_MOVE, new Transfer[] { ItemTransfer.getInstance () }, new ItemDropAdapter ( _viewer ) );
    }

    @Override
    public void setFocus ()
    {
        _viewer.getControl ().setFocus ();
    }

    public void remove ( ListEntry entry )
    {
        _list.remove ( entry );
    }

}
