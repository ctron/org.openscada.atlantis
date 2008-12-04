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

package org.openscada.da.client.test.wizards;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.openscada.core.Variant;
import org.openscada.da.client.test.views.realtime.ListEntry;
import org.openscada.rcp.da.client.browser.DataItemEntry;
import org.openscada.rcp.da.client.browser.HiveConnection;
import org.openscada.rcp.da.client.browser.ValueType;

class WriteAttributesOperationWizardValuePage extends WizardPage implements IWizardPage
{
    private static Logger _log = Logger.getLogger ( WriteAttributesOperationWizardValuePage.class );

    private Text _itemIdText = null;

    private IStructuredSelection _selection = null;

    private HiveConnection _connection = null;

    private TableViewer _table = null;

    private class AttributeEntry
    {
        private String _name = "";

        private ValueType _valueType = ValueType.STRING;

        private String _valueString = "";

        private Variant _value = new Variant ();

        private Throwable _valueError = null;

        public AttributeEntry ( final String name, final ValueType valueType, final String value )
        {
            super ();
            this._name = name;
            this._valueType = valueType;
            setValue ( value );
        }

        public String getName ()
        {
            return this._name;
        }

        public void setName ( final String name )
        {
            this._name = name;
        }

        public Variant getValue ()
        {
            return this._value;
        }

        public String getValueString ()
        {
            return this._valueString;
        }

        public void setValue ( final String value )
        {
            try
            {
                this._valueString = value;
                this._value = this._valueType.convertTo ( value );
                this._valueError = null;
            }
            catch ( final Exception e )
            {
                this._valueError = e;
            }
        }

        public ValueType getValueType ()
        {
            return this._valueType;
        }

        public void setValueType ( final ValueType valueType )
        {
            this._valueType = valueType;
            setValue ( this._valueString );
        }

        public Throwable getValueError ()
        {
            return this._valueError;
        }
    }

    private class Attributes
    {
        private final List<AttributeEntry> _entries = new ArrayList<AttributeEntry> ();

        public void add ( final AttributeEntry entry )
        {
            this._entries.add ( entry );
        }

        public void remove ( final AttributeEntry entry )
        {
            this._entries.remove ( entry );
        }

        public List<AttributeEntry> getEntries ()
        {
            return this._entries;
        }
    }

    private class MyLabelProvider extends LabelProvider implements ITableLabelProvider
    {

        public Image getColumnImage ( final Object element, final int columnIndex )
        {
            return getImage ( element );
        }

        public String getColumnText ( final Object element, final int columnIndex )
        {
            _log.info ( "Label for: " + element + ":" + columnIndex );

            if ( element instanceof AttributeEntry )
            {
                final AttributeEntry entry = (AttributeEntry)element;
                _log.info ( "Label: " + entry.getName () );
                switch ( columnIndex )
                {
                case 0:
                    return entry.getName ();
                case 1:
                    return entry.getValueType ().toString ();
                case 2:
                    return entry.getValue ().asString ( "<null>" );
                case 3:
                {
                    if ( entry.getValueError () != null )
                    {
                        return entry.getValueError ().getMessage ();
                    }
                    return "";
                }
                }
            }
            return getText ( element );
        }

    }

    private class MyContentProvider implements IStructuredContentProvider
    {
        public Object[] getElements ( final Object inputElement )
        {
            if ( inputElement instanceof Attributes )
            {
                final Attributes attributes = (Attributes)inputElement;
                return attributes.getEntries ().toArray ( new AttributeEntry[0] );
            }
            return new Object[0];
        }

        public void dispose ()
        {
        }

        public void inputChanged ( final Viewer viewer, final Object oldInput, final Object newInput )
        {
        }

    }

    private ComboBoxCellEditor _valueTypeEditor;

    private final String[] PROPERTIES = new String[] { "name", "value-type", "value", "value-error" };

    private class MyCellModifier implements ICellModifier
    {
        private TableViewer _viewer = null;

        public MyCellModifier ( final TableViewer viewer )
        {
            this._viewer = viewer;
        }

        public boolean canModify ( final Object element, final String property )
        {
            _log.debug ( "Can modify: " + element + ":" + property );

            if ( element instanceof AttributeEntry )
            {
                if ( property.equals ( "value" ) )
                {
                    return true;
                }
                if ( property.equals ( "name" ) )
                {
                    return true;
                }
                if ( property.equals ( "value-type" ) )
                {
                    return true;
                }
            }
            return false;
        }

        public Object getValue ( final Object element, final String property )
        {
            _log.debug ( "Get Value: " + element + ":" + property );

            if ( element instanceof AttributeEntry )
            {
                final AttributeEntry entry = (AttributeEntry)element;
                if ( property.equals ( "value" ) )
                {
                    return entry.getValueString ();
                }
                if ( property.equals ( "name" ) )
                {
                    return entry.getName ();
                }
                if ( property.equals ( "value-type" ) )
                {
                    return entry.getValueType ().index ();
                }
            }
            return null;
        }

        public void modify ( final Object element, final String property, final Object value )
        {
            _log.debug ( "Modify Value: " + element + ":" + property + ":" + value );

            final TableItem tableItem = (TableItem)element;

            if ( tableItem.getData () instanceof AttributeEntry )
            {
                final AttributeEntry entry = (AttributeEntry)tableItem.getData ();
                if ( property.equals ( "value" ) )
                {
                    entry.setValue ( value.toString () );
                }
                else if ( property.equals ( "name" ) )
                {
                    entry.setName ( value.toString () );
                }
                else if ( property.equals ( "value-type" ) )
                {
                    final Integer i = (Integer)value;
                    final String valueType = WriteAttributesOperationWizardValuePage.this._valueTypeEditor.getItems ()[i];
                    for ( final ValueType vt : ValueType.values () )
                    {
                        if ( vt.label ().equals ( valueType ) )
                        {
                            entry.setValueType ( vt );
                        }
                    }
                }
                this._viewer.update ( entry, WriteAttributesOperationWizardValuePage.this.PROPERTIES );
                dialogChanged ();
            }
        }

    }

    private final Attributes _attributes = new Attributes ();

    private class AddAction extends Action
    {
        public AddAction ()
        {
            super ( "Add Entry", Action.AS_PUSH_BUTTON );
            setEnabled ( true );
        }

        @Override
        public void run ()
        {
            final AttributeEntry entry = new AttributeEntry ( "", ValueType.STRING, "" );
            WriteAttributesOperationWizardValuePage.this._attributes.add ( entry );
            WriteAttributesOperationWizardValuePage.this._table.add ( entry );
            dialogChanged ();
        }
    }

    private class RemoveAction extends Action implements ISelectionChangedListener
    {

        private ISelection _selection = null;

        public RemoveAction ()
        {
            super ( "Remove Entry", Action.AS_PUSH_BUTTON );
        }

        @Override
        public void run ()
        {
            if ( this._selection instanceof IStructuredSelection )
            {
                final IStructuredSelection selection = (IStructuredSelection)this._selection;
                final Iterator<?> i = selection.iterator ();
                while ( i.hasNext () )
                {
                    final Object o = i.next ();
                    if ( o instanceof AttributeEntry )
                    {
                        WriteAttributesOperationWizardValuePage.this._attributes.remove ( (AttributeEntry)o );
                        WriteAttributesOperationWizardValuePage.this._table.remove ( o );
                    }
                }
                dialogChanged ();
            }
        }

        public void selectionChanged ( final SelectionChangedEvent event )
        {
            this._selection = event.getSelection ();
        }

    }

    private final AddAction _addAction = new AddAction ();

    private final RemoveAction _removeAction = new RemoveAction ();

    protected WriteAttributesOperationWizardValuePage ()
    {
        super ( "wizardPage" );
        setTitle ( "Write Attributes" );
        setDescription ( "Configure the attributes to write" );
    }

    public void createControl ( final Composite parent )
    {
        final Composite container = new Composite ( parent, SWT.NONE );

        final GridLayout layout = new GridLayout ();
        container.setLayout ( layout );
        layout.numColumns = 3;
        layout.verticalSpacing = 9;

        Label label = new Label ( container, SWT.NONE );
        label.setText ( "&Item:" );

        this._itemIdText = new Text ( container, SWT.BORDER | SWT.SINGLE );
        GridData gd = new GridData ( GridData.FILL_HORIZONTAL );
        this._itemIdText.setLayoutData ( gd );
        this._itemIdText.addModifyListener ( new ModifyListener () {
            public void modifyText ( final ModifyEvent e )
            {
                dialogChanged ();
            }
        } );

        label = new Label ( container, SWT.NONE );

        // row 2

        gd = new GridData ( GridData.FILL_HORIZONTAL );
        gd.horizontalSpan = 3;
        gd.grabExcessHorizontalSpace = true;
        final ToolBar toolbar = new ToolBar ( container, SWT.NONE );
        toolbar.setLayoutData ( gd );
        final ToolBarManager tbm = new ToolBarManager ( toolbar );
        tbm.add ( this._addAction );
        tbm.add ( this._removeAction );
        tbm.update ( true );

        // row 3

        gd = new GridData ( GridData.FILL_BOTH );
        gd.horizontalSpan = 3;
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        this._table = new TableViewer ( container, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL );

        TableColumn col;

        col = new TableColumn ( this._table.getTable (), SWT.NONE );
        col.setText ( "Name" );
        col = new TableColumn ( this._table.getTable (), SWT.NONE );
        col.setText ( "Value Type" );
        col = new TableColumn ( this._table.getTable (), SWT.NONE );
        col.setText ( "Value" );
        col = new TableColumn ( this._table.getTable (), SWT.NONE );
        col.setText ( "Value Error" );
        this._table.getTable ().setHeaderVisible ( true );

        try
        {
            this._table.setLabelProvider ( new MyLabelProvider () );
            this._table.setContentProvider ( new MyContentProvider () );

            this._table.setColumnProperties ( this.PROPERTIES );
            this._table.setCellModifier ( new MyCellModifier ( this._table ) );

            final TextCellEditor nameEditor = new TextCellEditor ( this._table.getTable () );

            final List<String> values = new LinkedList<String> ();
            for ( final ValueType vt : ValueType.values () )
            {
                values.add ( vt.label () );
            }
            this._valueTypeEditor = new ComboBoxCellEditor ( this._table.getTable (), values.toArray ( new String[0] ) );

            final TextCellEditor valueEditor = new TextCellEditor ( this._table.getTable () );
            this._table.setCellEditors ( new CellEditor[] { nameEditor, this._valueTypeEditor, valueEditor, new TextCellEditor ( this._table.getTable () ) } );

            final TableLayout tableLayout = new TableLayout ();
            tableLayout.addColumnData ( new ColumnWeightData ( 50, 75, true ) );
            tableLayout.addColumnData ( new ColumnWeightData ( 50, 75, true ) );
            tableLayout.addColumnData ( new ColumnWeightData ( 50, 75, true ) );
            tableLayout.addColumnData ( new ColumnWeightData ( 50, 75, true ) );
            this._table.getTable ().setLayout ( tableLayout );

            this._table.setInput ( this._attributes );
        }
        catch ( final Exception e )
        {
            _log.warn ( "Unable to create control", e );
        }

        this._table.getTable ().setLayoutData ( gd );
        this._table.addSelectionChangedListener ( this._removeAction );

        setControl ( container );
        fillFromSelection ();
        dialogChanged ();
    }

    private void fillFromSelection ()
    {
        if ( this._selection == null )
        {
            return;
        }

        final Object obj = this._selection.getFirstElement ();
        if ( obj == null )
        {
            return;
        }

        if ( obj instanceof DataItemEntry )
        {
            this._itemIdText.setText ( ( (DataItemEntry)obj ).getId () );
        }
        else if ( obj instanceof ListEntry )
        {
            this._itemIdText.setText ( ( (ListEntry)obj ).getDataItem ().getItemId () );
        }
    }

    private void dialogChanged ()
    {
        // connection
        if ( this._connection == null )
        {
            updateStatus ( "No hive connection selection" );
            return;
        }

        // item
        if ( this._itemIdText.getText ().length () <= 0 )
        {
            updateStatus ( "Item name must not be empty" );
            return;
        }

        if ( this._attributes._entries.size () <= 0 )
        {
            updateStatus ( "No attributes" );
            return;
        }

        for ( final AttributeEntry entry : this._attributes._entries )
        {
            if ( entry._name.equals ( "" ) )
            {
                updateStatus ( "Attribute with an empty name is not allowed" );
                return;
            }
        }

        updateStatus ( null );
    }

    private void updateStatus ( final String message )
    {
        setErrorMessage ( message );
        setPageComplete ( message == null );
    }

    public String getItem ()
    {
        return this._itemIdText.getText ();
    }

    public Map<String, Variant> getAttributes ()
    {
        final Map<String, Variant> attributes = new HashMap<String, Variant> ();

        for ( final AttributeEntry entry : this._attributes._entries )
        {
            attributes.put ( entry.getName (), entry.getValue () );
        }

        return attributes;
    }

    public HiveConnection getConnection ()
    {
        return this._connection;
    }

    public void setSelection ( final IStructuredSelection selection )
    {
        this._selection = selection;

        final Object obj = this._selection.getFirstElement ();
        if ( obj instanceof HiveConnection )
        {
            this._connection = (HiveConnection)obj;
        }
        else if ( obj instanceof DataItemEntry )
        {
            this._connection = ( (DataItemEntry)obj ).getConnection ();
        }
        else if ( obj instanceof ListEntry )
        {
            this._connection = ( (ListEntry)obj ).getConnection ();
        }
    }
}