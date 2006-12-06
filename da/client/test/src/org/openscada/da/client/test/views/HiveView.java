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

package org.openscada.da.client.test.views;

import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.SameShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.dialogs.PropertyDialogAction;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.openscada.da.client.test.Openscada_da_client_testPlugin;
import org.openscada.da.client.test.actions.ConnectHiveAction;
import org.openscada.da.client.test.dnd.ItemDragSourceListener;
import org.openscada.da.client.test.dnd.ItemTransfer;
import org.openscada.da.client.test.impl.FolderEntry;
import org.openscada.da.client.test.impl.HiveConnection;
import org.openscada.da.client.test.impl.HiveRepository;


/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class HiveView extends ViewPart implements Observer
{
    public static final String VIEW_ID = "org.openscada.da.client.test.views.HiveView";
    
    private static Logger _log = Logger.getLogger ( HiveView.class );
    
    private TreeViewer _viewer;
    private DrillDownAdapter drillDownAdapter;
    
    private IViewActionDelegate connectAction;
    private Action propertiesAction;
    
    private HiveRepository _repository;
    
    class NameSorter extends ViewerSorter
    {
    }
    
    /**
     * The constructor.
     */
    public HiveView()
    {
        _repository = Openscada_da_client_testPlugin.getRepository ();
        _repository.addObserver ( this );
        registerAllConnections ();
    }
    
    @Override
    public void dispose ()
    {
        unregisterAllConnections ();
        _repository.deleteObserver ( this );
        super.dispose ();
    }
    
    public void update ( Observable o, Object arg )
    {
        _log.debug ( "Update: " + o + " / " + arg );
        if ( o == _repository )
        {
            triggerUpdateRepository ( null );
        }
        else if ( o instanceof HiveConnection )
        {
            if ( arg instanceof FolderEntry )
            {
                refreshForFolder ( (FolderEntry)arg  );
            }
            else
                triggerUpdateRepository ( o );
        }
    }
    
    private void refreshForFolder ( FolderEntry folder )
    {
        if ( folder == null )
            return;
        
        if ( folder.getParent () == null )
            triggerUpdateRepository ( folder.getConnection () );
        else
            triggerUpdateRepository ( folder );
    }
    
    public void triggerUpdateRepository ( final Object arg0 )
    {
        if ( !_viewer.getControl ().isDisposed () )
        {
            _viewer.getControl().getDisplay().asyncExec(new Runnable(){

                public void run ()
                {
                    if ( !_viewer.getControl().isDisposed() )
                        performUpdateRepository ( arg0 );
                }});
        }
    }
    
    private void performUpdateRepository ( Object arg0 )
    {
        _log.debug ( "Perform update on: " + arg0 );
        if ( arg0 == null )
        {
            unregisterAllConnections ();
            _viewer.refresh ( true );
            registerAllConnections ();
        }
        else
        {
            _viewer.refresh ( arg0, true );
        }
    }
    
    /**
     * This is a callback that will allow us
     * to create the viewer and initialize it.
     */
    public void createPartControl ( Composite parent )
    {
        _viewer = new TreeViewer ( parent, SWT.H_SCROLL | SWT.V_SCROLL );
        drillDownAdapter = new DrillDownAdapter(_viewer);
        _viewer.setContentProvider ( new HiveViewContentProvider () );
        _viewer.setLabelProvider ( new HiveViewLabelProvider () );
        _viewer.setSorter ( new NameSorter() );
        _viewer.setInput ( _repository );
        
        addDragSupport ();
        
        makeActions ();
        hookContextMenu ();
        hookDoubleClickAction ();
        contributeToActionBars ();
        
        getViewSite ().setSelectionProvider ( _viewer );
    }
    
    private void addDragSupport ()
    {
        _viewer.addDragSupport ( DND.DROP_COPY | DND.DROP_MOVE, new Transfer[] { ItemTransfer.getInstance () }, new ItemDragSourceListener ( _viewer ) );
    }
    
    private void hookContextMenu()
    {
        MenuManager menuMgr = new MenuManager ( "#PopupMenu" );
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager manager) {
                HiveView.this.fillContextMenu(manager);
            }
        });
        Menu menu = menuMgr.createContextMenu(_viewer.getControl());
        _viewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, _viewer);
    }
    
    private void contributeToActionBars()
    {
        IActionBars bars = getViewSite().getActionBars();
        fillLocalPullDown(bars.getMenuManager());
        fillLocalToolBar(bars.getToolBarManager());
    }
    
    private void fillLocalPullDown(IMenuManager manager)
    {
        //manager.add(connectAction);
        //manager.add(new Separator());
    }
    
    private void fillContextMenu(IMenuManager manager)
    {
        //manager.add(connectAction);
        //manager.add(new Separator());
        //drillDownAdapter.addNavigationActions ( manager );
        // Other plug-ins can contribute there actions here
        
        manager.add ( new Separator ( IWorkbenchActionConstants.MB_ADDITIONS ) );
        manager.add ( new Separator () );
        manager.add ( propertiesAction );
    }
    
    private void fillLocalToolBar(IToolBarManager manager)
    {
        //manager.add(connectAction);
        //manager.add(new Separator());
        //drillDownAdapter.addNavigationActions ( manager );
    }
    
    private void makeActions()
    {
        // Connect Action

        /*
        connectAction = new Action() {
            public void run() {
                performConnect();
            }
        };
        connectAction.setText("Connect");
        connectAction.setToolTipText("Establish connection to hive");
        connectAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
                getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
        */
        connectAction = new ConnectHiveAction();
        propertiesAction = new PropertyDialogAction ( new SameShellProvider ( getViewSite ().getShell () ), _viewer );
        _viewer.addSelectionChangedListener ( new ISelectionChangedListener() {

            public void selectionChanged ( SelectionChangedEvent event )
            {
                IStructuredSelection ss = (IStructuredSelection)event.getSelection ();
                if ( ss.size () == 1 )
                {
                    propertiesAction.setEnabled ( true );
                }
                else
                {
                    propertiesAction.setEnabled ( false );
                }
            }} );
    }
    
    private void hookDoubleClickAction()
    {
        _viewer.addDoubleClickListener ( new IDoubleClickListener()
        {
            public void doubleClick ( DoubleClickEvent event )
            {
                connectAction.selectionChanged ( null, event.getSelection () );
                connectAction.run ( null );
            }
        });
    }
   
    /**
     * Passing the focus request to the viewer's control.
     */
    public void setFocus()
    {
        _viewer.getControl ().setFocus ();
    }
    
    synchronized private void unregisterAllConnections ()
    {
        for ( HiveConnection connection : _repository.getConnections() )
        {
            connection.deleteObserver ( this );
        }
    }
    
    synchronized private void registerAllConnections ()
    {
        unregisterAllConnections();
        
        for ( HiveConnection connection : _repository.getConnections() )
        {
            connection.addObserver ( this );
        }
    }
    
    @Override
    public Object getAdapter ( Class adapter )
    {
        _log.debug ( "getAdapter: " + adapter );
        if ( adapter.equals ( org.eclipse.ui.views.properties.IPropertySheetPage.class ) )
        {
            PropertySheetPage psd = new PropertySheetPage ();
            return psd;
        }
        else
            return super.getAdapter ( adapter );
    }
}