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

import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.openscada.core.Variant;
import org.openscada.core.subscription.SubscriptionState;
import org.openscada.da.client.DataItem;
import org.openscada.da.client.ItemUpdateListener;
import org.openscada.da.client.base.browser.HiveItem;

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

public class DataItemOperationView extends ViewPart implements ItemUpdateListener
{
    private static Logger _log = Logger.getLogger ( DataItemOperationView.class );

    private HiveItem _hiveItem = null;

    private TableViewer viewer;

    private Label _valueLabel;

    private StyledText _console;

    /*
     * The content provider class is responsible for
     * providing objects to the view. It can wrap
     * existing objects in adapters or simply return
     * objects as-is. These objects may be sensitive
     * to the current input of the view, or ignore
     * it and always show the same content 
     * (like Task List, for example).
     */

    class Entry
    {
        public String name;

        public Variant value;

        public Entry ( final String name, final Variant value )
        {
            this.name = name;
            this.value = value;
        }
    }

    class ViewContentProvider implements IStructuredContentProvider, Observer
    {
        private Viewer _viewer = null;

        private DataItem _item = null;

        public void inputChanged ( final Viewer v, final Object oldInput, final Object newInput )
        {
            this._viewer = DataItemOperationView.this.viewer;

            clearItem ();

            if ( newInput instanceof HiveItem )
            {
                if ( newInput != null )
                {
                    final HiveItem hiveItem = (HiveItem)newInput;

                    this._item = new DataItem ( hiveItem.getId () );
                    this._item.addObserver ( this );
                    this._item.register ( hiveItem.getConnection ().getItemManager () );
                }
            }
        }

        private void clearItem ()
        {
            if ( this._item != null )
            {
                this._item.deleteObserver ( this );
                this._item.unregister ();
                this._item = null;
            }
        }

        public void dispose ()
        {
            clearItem ();
        }

        public Object[] getElements ( final Object parent )
        {
            if ( this._item == null )
            {
                return new Object[0];
            }

            final Map<String, Variant> attrs = this._item.getSnapshotValue ().getAttributes ();
            final Entry[] entries = new Entry[attrs.size ()];
            int i = 0;

            for ( final Map.Entry<String, Variant> entry : attrs.entrySet () )
            {
                entries[i++] = new Entry ( entry.getKey (), entry.getValue () );
            }
            return entries;
        }

        public void update ( final Observable o, final Object arg )
        {
            if ( !this._viewer.getControl ().isDisposed () )
            {
                this._viewer.getControl ().getDisplay ().asyncExec ( new Runnable () {

                    public void run ()
                    {
                        if ( !ViewContentProvider.this._viewer.getControl ().isDisposed () )
                        {
                            ViewContentProvider.this._viewer.refresh ();
                        }
                    }
                } );
            }
        }
    }

    class ViewLabelProvider extends LabelProvider implements ITableLabelProvider
    {
        public String getColumnText ( final Object obj, final int index )
        {
            if ( ! ( obj instanceof Entry ) )
            {
                return "";
            }

            final Entry entry = (Entry)obj;

            switch ( index )
            {
            case 0:
                return entry.name;
            case 1:
                return entry.value.asString ( "<null>" );
            }
            return getText ( obj );
        }

        public Image getColumnImage ( final Object obj, final int index )
        {
            if ( index == 0 )
            {
                return getImage ( obj );
            }
            else
            {
                return null;
            }
        }

        public Image getImage ( final Object obj )
        {
            return PlatformUI.getWorkbench ().getSharedImages ().getImage ( ISharedImages.IMG_OBJ_ELEMENT );
        }
    }

    class NameSorter extends ViewerSorter
    {
    }

    /**
     * The constructor.
     */
    public DataItemOperationView ()
    {

    }

    /**
     * This is a callback that will allow us
     * to create the viewer and initialize it.
     */
    public void createPartControl ( final Composite parent )
    {
        parent.setLayout ( new GridLayout ( 1, false ) );

        GridData gd;

        // value label
        gd = new GridData ();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        gd.verticalAlignment = SWT.CENTER;
        this._valueLabel = new Label ( parent, SWT.NONE );
        this._valueLabel.setLayoutData ( gd );

        final SashForm box = new SashForm ( parent, SWT.VERTICAL );
        gd = new GridData ();
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        gd.verticalAlignment = SWT.FILL;
        box.setLayoutData ( gd );

        // attributes table

        this.viewer = new TableViewer ( box, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL );
        this.viewer.getControl ().setLayoutData ( gd );
        this.viewer.setContentProvider ( new ViewContentProvider () );
        this.viewer.setLabelProvider ( new ViewLabelProvider () );

        TableColumn col;

        col = new TableColumn ( this.viewer.getTable (), SWT.NONE );
        col.setText ( "Name" );
        col.setWidth ( 200 );
        col = new TableColumn ( this.viewer.getTable (), SWT.NONE );
        col.setText ( "Value" );
        col.setWidth ( 500 );

        this.viewer.getTable ().setHeaderVisible ( true );
        this.viewer.setSorter ( new NameSorter () );

        // console window
        this._console = new StyledText ( box, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.READ_ONLY );
        this._console.setLayoutData ( gd );

        // set up sash
        box.setWeights ( new int[] { 60, 40 } );
        //box.setMaximizedControl ( _console );

        // actions
        // makeActions ();
        //hookContextMenu();
        //hookDoubleClickAction();
        //contributeToActionBars();
    }

    private void appendConsoleMessage ( final String message )
    {
        if ( !this._console.isDisposed () )
        {
            this._console.getDisplay ().asyncExec ( new Runnable () {

                public void run ()
                {
                    if ( !DataItemOperationView.this._console.isDisposed () )
                    {
                        DataItemOperationView.this._console.append ( message + "\n" );
                        DataItemOperationView.this._console.setSelection ( DataItemOperationView.this._console.getCharCount () );
                        DataItemOperationView.this._console.showSelection ();
                    }
                }
            } );
        }
    }

    private void setValue ( final Variant variant )
    {
        if ( !this._valueLabel.isDisposed () )
        {
            this._valueLabel.getDisplay ().asyncExec ( new Runnable () {

                public void run ()
                {
                    if ( !DataItemOperationView.this._valueLabel.isDisposed () )
                    {
                        if ( variant.isNull () )
                        {
                            DataItemOperationView.this._valueLabel.setText ( "Value: <null>" );
                        }
                        else
                        {
                            DataItemOperationView.this._valueLabel.setText ( "Value: " + variant.asString ( "BUG!" ) );
                        }
                    }
                }
            } );
        }
    }

    /**
     * Passing the focus request to the viewer's control.
     */
    public void setFocus ()
    {
        this.viewer.getControl ().setFocus ();
    }

    @Override
    public void dispose ()
    {
        setDataItem ( null );
        super.dispose ();
    }

    public void setDataItem ( final HiveItem item )
    {
        if ( this._hiveItem != null )
        {
            this._hiveItem.getConnection ().getItemManager ().removeItemUpdateListener ( this._hiveItem.getId (), this );
            appendConsoleMessage ( "Unsubscribe from item: " + this._hiveItem.getId () );

            setPartName ( "Data Item Viewer" );
        }

        if ( item != null )
        {
            setPartName ( "Data Item Viewer: " + item.getId () );

            _log.info ( "Set data item: " + item.getId () );

            this._hiveItem = item;

            appendConsoleMessage ( "Subscribe to item: " + this._hiveItem.getId () );
            this._hiveItem.getConnection ().getItemManager ().addItemUpdateListener ( this._hiveItem.getId (), this );

            this.viewer.setInput ( item );
        }
    }

    public void notifyDataChange ( final Variant value, final Map<String, Variant> attributes, final boolean initial )
    {
        if ( value != null )
        {
            appendConsoleMessage ( "Value change event: " + value.asString ( "<null>" ) + " " + ( initial ? "initial" : "" ) );
            setValue ( value );

        }
        if ( attributes != null )
        {
            appendConsoleMessage ( "Attribute change set " + ( initial ? "(initial)" : "" ) + " " + attributes.size () + " item(s) follow:" );
            final int i = 0;
            for ( final Map.Entry<String, Variant> entry : attributes.entrySet () )
            {
                final String q = entry.getValue ().isNull () ? "" : "'";
                appendConsoleMessage ( "#" + i + ": " + entry.getKey () + "->" + q + entry.getValue ().asString ( "<null>" ) + q );
            }
        }
    }

    public void notifySubscriptionChange ( final SubscriptionState state, final Throwable subscriptionError )
    {
        final String error = subscriptionError == null ? "<none>" : subscriptionError.getMessage ();
        appendConsoleMessage ( String.format ( "Subscription state changed: %s (Error: %s)", state.name (), error ) );
    }
}