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

package org.openscada.da.client.test.views;

import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableLayout;
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
import org.openscada.da.client.test.impl.DataItemEntry;
import org.openscada.da.client.test.impl.VariantHelper;
import org.openscada.da.client.test.impl.VariantHelper.ValueType;

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

        public Entry ( String name, Variant value )
        {
            this.name = name;
            this.value = value;
        }
    }

    class ViewContentProvider implements IStructuredContentProvider, Observer
    {
        private Viewer _viewer = null;

        private DataItem _item = null;

        public void inputChanged ( Viewer v, Object oldInput, Object newInput )
        {
            _log.debug ( "Input changed: " + oldInput + " => " + newInput );
            _viewer = viewer;

            clearItem ();

            if ( newInput instanceof DataItemEntry )
            {
                DataItemEntry hiveItem = (DataItemEntry)newInput;

                _item = new DataItem ( hiveItem.getId () );
                _item.addObserver ( this );
                _item.register ( hiveItem.getConnection ().getItemManager () );
            }
        }

        private void clearItem ()
        {
            if ( _item != null )
            {
                _item.deleteObserver ( this );
                _item.unregister ();
                _item = null;
            }
        }

        public void dispose ()
        {
            clearItem ();
        }

        public Object[] getElements ( Object parent )
        {
            if ( _item == null )
                return new Object[0];

            Map<String, Variant> attrs = _item.getAttributes ();
            Entry[] entries = new Entry[attrs.size ()];
            int i = 0;

            for ( Map.Entry<String, Variant> entry : attrs.entrySet () )
            {
                entries[i++] = new Entry ( entry.getKey (), entry.getValue () );
            }
            return entries;
        }

        public void update ( Observable o, Object arg )
        {
            _log.debug ( "Object update" );

            if ( !_viewer.getControl ().isDisposed () )
            {
                _viewer.getControl ().getDisplay ().asyncExec ( new Runnable () {

                    public void run ()
                    {
                        try
                        {
                            if ( !_viewer.getControl ().isDisposed () )
                            {
                                if ( _viewer instanceof StructuredViewer )
                                    ( (StructuredViewer)_viewer ).refresh ( true );
                                else
                                    _viewer.refresh ();
                            }
                        }
                        catch ( Exception e )
                        {
                            e.printStackTrace ();
                        }
                    }
                } );
            }
        }
    }

    class ViewLabelProvider extends LabelProvider implements ITableLabelProvider
    {
        public String getColumnText ( Object obj, int index )
        {
            if ( ! ( obj instanceof Entry ) )
                return "";

            Entry entry = (Entry)obj;

            switch ( index )
            {
            case 0:
                return entry.name;
            case 1:
                ValueType vt = VariantHelper.toValueType ( entry.value );
                if ( vt != null )
                    return vt.toString ();
                else
                    return "VT_UNKNOWN";
            case 2:
                return entry.value.asString ( "null" );
            }
            return getText ( obj );
        }

        public Image getColumnImage ( Object obj, int index )
        {
            if ( index == 0 )
                return getImage ( obj );
            else
                return null;
        }

        public Image getImage ( Object obj )
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
    public DataItemWatchView ()
    {

    }

    /**
     * This is a callback that will allow us
     * to create the viewer and initialize it.
     */
    public void createPartControl ( Composite parent )
    {
        parent.setLayout ( new GridLayout ( 1, false ) );

        GridData gd;

        // value label
        gd = new GridData ();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        gd.verticalAlignment = SWT.CENTER;
        _valueLabel = new Label ( parent, SWT.NONE );
        _valueLabel.setLayoutData ( gd );

        SashForm box = new SashForm ( parent, SWT.VERTICAL );
        gd = new GridData ();
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        gd.verticalAlignment = SWT.FILL;
        box.setLayoutData ( gd );

        // attributes table

        viewer = new TableViewer ( box, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL );
        viewer.getControl ().setLayoutData ( gd );
        viewer.setContentProvider ( new ViewContentProvider () );
        viewer.setLabelProvider ( new ViewLabelProvider () );

        TableColumn col;

        col = new TableColumn ( viewer.getTable (), SWT.NONE );
        col.setText ( "Name" );
        col = new TableColumn ( viewer.getTable (), SWT.NONE );
        col.setText ( "Value Type" );
        col = new TableColumn ( viewer.getTable (), SWT.NONE );
        col.setText ( "Value" );

        viewer.getTable ().setHeaderVisible ( true );
        viewer.setSorter ( new NameSorter () );

        // set table layout
        TableLayout tableLayout = new TableLayout ();
        tableLayout.addColumnData ( new ColumnWeightData ( 40, 75, true ) );
        tableLayout.addColumnData ( new ColumnWeightData ( 20, 40, true ) );
        tableLayout.addColumnData ( new ColumnWeightData ( 40, 75, true ) );
        viewer.getTable ().setLayout ( tableLayout );

        // console window
        _console = new StyledText ( box, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.READ_ONLY );
        _console.setLayoutData ( gd );

        // set up sash
        box.setWeights ( new int[] { 60, 40 } );
        //box.setMaximizedControl ( _console );

    }

    private void appendConsoleMessage ( final String message )
    {
        if ( !_console.isDisposed () )
        {
            _console.getDisplay ().asyncExec ( new Runnable () {

                public void run ()
                {
                    if ( !_console.isDisposed () )
                    {
                        _console.append ( message + "\n" );
                        _console.setSelection ( _console.getCharCount () );
                        _console.showSelection ();
                    }
                }
            } );
        }
    }

    private void setValue ( final Variant variant )
    {
        if ( !_valueLabel.isDisposed () )
        {
            _valueLabel.getDisplay ().asyncExec ( new Runnable () {

                public void run ()
                {
                    if ( !_valueLabel.isDisposed () )
                    {
                        _valueLabel.setText ( VariantHelper.toString ( variant ) );
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
        viewer.getControl ().setFocus ();
    }

    @Override
    public void dispose ()
    {
        _log.debug ( "Dispose data item watcher" );
        setDataItem ( null );
        super.dispose ();
    }

    public void setDataItem ( DataItemEntry item )
    {
        if ( _hiveItem != null )
        {
            _hiveItem.getConnection ().getItemManager ().removeItemUpdateListener ( _hiveItem.getId (), this );
            appendConsoleMessage ( "Unsubscribe from item: " + _hiveItem.getId () );

            setPartName ( "Data Item Viewer" );
            viewer.setInput ( null );
        }

        if ( item != null )
        {
            setPartName ( "Data Item Viewer: " + item.getId () );

            _log.info ( "Set data item: " + item.getId () );

            _hiveItem = item;

            appendConsoleMessage ( "Subscribe to item: " + _hiveItem.getId () );
            _hiveItem.getConnection ().getItemManager ().addItemUpdateListener ( _hiveItem.getId (), this );

            viewer.setInput ( item );
        }
    }

    public void notifyValueChange ( Variant value, boolean initial )
    {
        appendConsoleMessage ( "Value change event: " + VariantHelper.toString ( value ) + " "
                + ( initial ? "initial" : "" ) );
        setValue ( value );
    }

    public void notifyAttributeChange ( Map<String, Variant> attributes, boolean initial )
    {
        appendConsoleMessage ( "Attribute change set " + ( initial ? "(initial)" : "" ) + " " + attributes.size ()
                + " item(s) follow:" );
        int i = 0;
        for ( Map.Entry<String, Variant> entry : attributes.entrySet () )
        {
            if ( entry.getValue () != null )
            {
                appendConsoleMessage ( "#" + i + ": " + entry.getKey () + "->"
                        + VariantHelper.toString ( entry.getValue () ) );
            }
            else
            {
                appendConsoleMessage ( "#" + i + ":" + entry.getKey () + " <null>" );
            }
            i++;
        }
    }
    
    public void notifySubscriptionChange ( SubscriptionState state, Throwable subscriptionError )
    {
        String error = subscriptionError == null ? "<none>" : subscriptionError.getMessage ();
        appendConsoleMessage ( String.format ( "Subscription state changed: %s (Error: %s)", state.name (), error ) );
    }
}