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

import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.URLTransfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.openscada.core.client.ConnectionState;
import org.openscada.da.base.browser.FolderEntry;
import org.openscada.da.base.browser.HiveConnection;
import org.openscada.da.base.browser.HiveRepository;
import org.openscada.da.base.dnd.ItemDragSourceListener;
import org.openscada.da.base.dnd.ItemTransfer;
import org.openscada.da.client.test.Activator;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class HiveView extends ViewPart implements Observer
{
    public static final String VIEW_ID = "org.openscada.da.client.test.views.HiveView";

    private static Logger _log = Logger.getLogger ( HiveView.class );

    private TreeViewer _viewer;

    private final HiveRepository _repository;

    class NameSorter extends ViewerSorter
    {
    }

    /**
     * The constructor.
     */
    public HiveView ()
    {
        this._repository = Activator.getRepository ();
        this._repository.addObserver ( this );
        registerAllConnections ();
    }

    @Override
    public void dispose ()
    {
        unregisterAllConnections ();
        this._repository.deleteObserver ( this );
        super.dispose ();
    }

    public void update ( final Observable o, final Object arg )
    {
        _log.debug ( "Update: " + o + " / " + arg );
        if ( o == this._repository )
        {
            triggerUpdateRepository ( null );
        }
        else if ( o instanceof HiveConnection )
        {
            if ( arg instanceof FolderEntry )
            {
                refreshForFolder ( (FolderEntry)arg );
            }
            else
            {
                triggerUpdateRepository ( o );
            }
        }
    }

    private void refreshForFolder ( final FolderEntry folder )
    {
        if ( folder == null )
        {
            return;
        }

        if ( folder.getParent () == null )
        {
            triggerUpdateRepository ( folder.getConnection () );
        }
        else
        {
            triggerUpdateRepository ( folder );
        }
    }

    public void triggerUpdateRepository ( final Object arg0 )
    {
        if ( !this._viewer.getControl ().isDisposed () )
        {
            this._viewer.getControl ().getDisplay ().asyncExec ( new Runnable () {

                public void run ()
                {
                    if ( !HiveView.this._viewer.getControl ().isDisposed () )
                    {
                        performUpdateRepository ( arg0 );
                    }
                }
            } );
        }
    }

    private void performUpdateRepository ( final Object arg0 )
    {
        _log.debug ( "Perform update on: " + arg0 );
        if ( arg0 == null )
        {
            unregisterAllConnections ();
            this._viewer.refresh ( true );
            registerAllConnections ();
        }
        else
        {
            this._viewer.refresh ( arg0, true );
        }
    }

    /**
     * This is a callback that will allow us to create the viewer and initialize
     * it.
     */
    @Override
    public void createPartControl ( final Composite parent )
    {
        this._viewer = new TreeViewer ( parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI );
        this._viewer.setContentProvider ( new HiveViewContentProvider () );
        this._viewer.setLabelProvider ( new HiveViewLabelProvider () );
        this._viewer.setSorter ( new NameSorter () );
        this._viewer.setInput ( this._repository );

        addDragSupport ();

        hookContextMenu ();
        hookDoubleClickAction ();
        contributeToActionBars ();

        getViewSite ().setSelectionProvider ( this._viewer );
    }

    private void addDragSupport ()
    {
        this._viewer.addDragSupport ( DND.DROP_COPY | DND.DROP_MOVE, new Transfer[] { ItemTransfer.getInstance (), URLTransfer.getInstance () }, new ItemDragSourceListener ( this._viewer ) );
    }

    private void hookContextMenu ()
    {
        final MenuManager menuMgr = new MenuManager ( "#PopupMenu" );
        menuMgr.setRemoveAllWhenShown ( true );
        menuMgr.addMenuListener ( new IMenuListener () {
            public void menuAboutToShow ( final IMenuManager manager )
            {
                HiveView.this.fillContextMenu ( manager );
            }
        } );
        final Menu menu = menuMgr.createContextMenu ( this._viewer.getControl () );
        this._viewer.getControl ().setMenu ( menu );
        getSite ().registerContextMenu ( menuMgr, this._viewer );
    }

    private void contributeToActionBars ()
    {
        final IActionBars bars = getViewSite ().getActionBars ();
        fillLocalPullDown ( bars.getMenuManager () );
        fillLocalToolBar ( bars.getToolBarManager () );
    }

    private void fillLocalPullDown ( final IMenuManager manager )
    {
        // manager.add(connectAction);
        // manager.add(new Separator());
    }

    private void fillContextMenu ( final IMenuManager manager )
    {
        // manager.add(connectAction);
        // manager.add(new Separator());
        // drillDownAdapter.addNavigationActions ( manager );
        // Other plug-ins can contribute there actions here

        manager.add ( new Separator ( IWorkbenchActionConstants.MB_ADDITIONS ) );
        manager.add ( new Separator () );
    }

    private void fillLocalToolBar ( final IToolBarManager manager )
    {
        // manager.add(connectAction);
        // manager.add(new Separator());
        // drillDownAdapter.addNavigationActions ( manager );
    }

    private void hookDoubleClickAction ()
    {
        // Allow double click connect and disconnect
        this._viewer.addDoubleClickListener ( new IDoubleClickListener () {
            public void doubleClick ( final DoubleClickEvent event )
            {
                if ( event.getSelection () instanceof IStructuredSelection )
                {
                    final IStructuredSelection sel = (IStructuredSelection)event.getSelection ();
                    final Iterator<?> i = sel.iterator ();
                    while ( i.hasNext () )
                    {
                        final Object o = i.next ();
                        if ( o instanceof HiveConnection )
                        {
                            final HiveConnection con = (HiveConnection)o;
                            if ( con.getConnection ().getState () == ConnectionState.CLOSED )
                            {
                                con.connect ();
                            }
                            else if ( con.getConnection ().getState () == ConnectionState.BOUND )
                            {
                                con.disconnect ();
                            }
                        }
                    }
                }
            }
        } );
    }

    /**
     * Passing the focus request to the viewer's control.
     */
    @Override
    public void setFocus ()
    {
        this._viewer.getControl ().setFocus ();
    }

    synchronized private void unregisterAllConnections ()
    {
        for ( final HiveConnection connection : this._repository.getConnections () )
        {
            connection.deleteObserver ( this );
        }
    }

    synchronized private void registerAllConnections ()
    {
        unregisterAllConnections ();

        for ( final HiveConnection connection : this._repository.getConnections () )
        {
            connection.addObserver ( this );
        }
    }

    @SuppressWarnings ( "unchecked" )
    @Override
    public Object getAdapter ( final Class adapter )
    {
        _log.debug ( "getAdapter: " + adapter );
        if ( adapter.equals ( org.eclipse.ui.views.properties.IPropertySheetPage.class ) )
        {
            final PropertySheetPage psd = new PropertySheetPage ();
            return psd;
        }
        else
        {
            return super.getAdapter ( adapter );
        }
    }
}