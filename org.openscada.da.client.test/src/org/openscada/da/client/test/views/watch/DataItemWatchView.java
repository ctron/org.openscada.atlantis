/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2007 inavare GmbH (http://inavare.com)
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

package org.openscada.da.client.test.views.watch;

import java.util.Calendar;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;
import org.openscada.core.Variant;
import org.openscada.core.subscription.SubscriptionState;
import org.openscada.da.base.browser.DataItemEntry;
import org.openscada.da.client.DataItem;
import org.openscada.da.client.ItemUpdateListener;

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

public class DataItemWatchView extends ViewPart implements ItemUpdateListener
{
    public static final String VIEW_ID = "org.openscada.da.client.test.views.DataItemWatchView";

    private static Logger _log = Logger.getLogger ( DataItemWatchView.class );

    private DataItemEntry _hiveItem = null;

    private TableViewer viewer;

    private Label _valueLabel;

    private StyledText _console;

    class WatchViewContentProvider implements IStructuredContentProvider, Observer
    {
        private Viewer _viewer = null;

        private DataItem _item = null;

        public void inputChanged ( final Viewer v, final Object oldInput, final Object newInput )
        {
            _log.debug ( "Input changed: " + oldInput + " => " + newInput );
            this._viewer = DataItemWatchView.this.viewer;

            clearItem ();

            if ( newInput instanceof DataItemEntry )
            {
                final DataItemEntry hiveItem = (DataItemEntry)newInput;

                this._item = new DataItem ( hiveItem.getId () );
                this._item.addObserver ( this );
                this._item.register ( hiveItem.getConnection ().getItemManager () );
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
            final WatchAttributeEntry[] entries = new WatchAttributeEntry[attrs.size ()];
            int i = 0;

            for ( final Map.Entry<String, Variant> entry : attrs.entrySet () )
            {
                entries[i++] = new WatchAttributeEntry ( entry.getKey (), entry.getValue () );
            }
            return entries;
        }

        public void update ( final Observable o, final Object arg )
        {
            _log.debug ( "Object update" );

            if ( !this._viewer.getControl ().isDisposed () )
            {
                this._viewer.getControl ().getDisplay ().asyncExec ( new Runnable () {

                    public void run ()
                    {
                        try
                        {
                            if ( !WatchViewContentProvider.this._viewer.getControl ().isDisposed () )
                            {
                                if ( WatchViewContentProvider.this._viewer instanceof StructuredViewer )
                                {
                                    ( (StructuredViewer)WatchViewContentProvider.this._viewer ).refresh ( true );
                                }
                                else
                                {
                                    WatchViewContentProvider.this._viewer.refresh ();
                                }
                            }
                        }
                        catch ( final Exception e )
                        {
                            e.printStackTrace ();
                        }
                    }
                } );
            }
        }
    }

    /**
     * This is a callback that will allow us
     * to create the viewer and initialize it.
     */
    @Override
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
        this.viewer.setContentProvider ( new WatchViewContentProvider () );
        this.viewer.setLabelProvider ( new WatchViewLabelProvider () );

        TableColumn col;

        col = new TableColumn ( this.viewer.getTable (), SWT.NONE );
        col.setText ( "Name" );
        col = new TableColumn ( this.viewer.getTable (), SWT.NONE );
        col.setText ( "Value Type" );
        col = new TableColumn ( this.viewer.getTable (), SWT.NONE );
        col.setText ( "Value" );

        this.viewer.getTable ().setHeaderVisible ( true );
        this.viewer.setSorter ( new WatchEntryNameSorter () );

        // set table layout
        final TableLayout tableLayout = new TableLayout ();
        tableLayout.addColumnData ( new ColumnWeightData ( 40, 75, true ) );
        tableLayout.addColumnData ( new ColumnWeightData ( 20, 40, true ) );
        tableLayout.addColumnData ( new ColumnWeightData ( 40, 75, true ) );
        this.viewer.getTable ().setLayout ( tableLayout );

        // console window
        this._console = new StyledText ( box, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.READ_ONLY );
        this._console.setLayoutData ( gd );

        // set up sash
        box.setWeights ( new int[] { 60, 40 } );
        //box.setMaximizedControl ( _console );

    }

    private void appendConsoleMessage ( final String message )
    {
        if ( !this._console.isDisposed () )
        {
            this._console.getDisplay ().asyncExec ( new Runnable () {

                public void run ()
                {
                    if ( !DataItemWatchView.this._console.isDisposed () )
                    {
                        DataItemWatchView.this._console.append ( String.format ( "%tc > ", Calendar.getInstance () ) );
                        DataItemWatchView.this._console.append ( message + "\n" );
                        DataItemWatchView.this._console.setSelection ( DataItemWatchView.this._console.getCharCount () );
                        DataItemWatchView.this._console.showSelection ();
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
                    if ( !DataItemWatchView.this._valueLabel.isDisposed () )
                    {
                        DataItemWatchView.this._valueLabel.setText ( variant.toString () );
                    }
                }
            } );
        }
    }

    /**
     * Passing the focus request to the viewer's control.
     */
    @Override
    public void setFocus ()
    {
        this.viewer.getControl ().setFocus ();
    }

    @Override
    public void dispose ()
    {
        _log.debug ( "Dispose data item watcher" );
        setDataItem ( null );
        super.dispose ();
    }

    public void setDataItem ( final DataItemEntry item )
    {
        if ( this._hiveItem != null )
        {
            this._hiveItem.getConnection ().getItemManager ().removeItemUpdateListener ( this._hiveItem.getId (), this );
            appendConsoleMessage ( "Unsubscribe from item: " + this._hiveItem.getId () );

            setPartName ( "Data Item Viewer" );
            this.viewer.setInput ( null );
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

    public void notifyDataChange ( final Variant value, final Map<String, Variant> attributes, final boolean cache )
    {
        if ( value != null )
        {
            appendConsoleMessage ( "Value change event: " + value + " " + ( cache ? "cache" : "" ) );
            setValue ( value );
        }
        if ( attributes != null )
        {
            appendConsoleMessage ( "Attribute change set " + ( cache ? "(initial)" : "" ) + " " + attributes.size () + " item(s) follow:" );
            int i = 0;
            for ( final Map.Entry<String, Variant> entry : attributes.entrySet () )
            {
                if ( entry.getValue () != null )
                {
                    appendConsoleMessage ( "#" + i + ": " + entry.getKey () + "->" + entry.getValue () );
                }
                else
                {
                    appendConsoleMessage ( "#" + i + ":" + entry.getKey () + " <null>" );
                }
                i++;
            }
        }
    }

    public void notifySubscriptionChange ( final SubscriptionState state, final Throwable subscriptionError )
    {
        final String error = subscriptionError == null ? "<none>" : subscriptionError.getMessage ();
        appendConsoleMessage ( String.format ( "Subscription state changed: %s (Error: %s)", state.name (), error ) );
    }
}