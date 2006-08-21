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
import org.openscada.da.client.test.impl.DataItemEntry;
import org.openscada.da.client.test.impl.HiveConnection;
import org.openscada.da.client.test.impl.VariantHelper.ValueType;
import org.openscada.da.core.data.Variant;

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
        
        public AttributeEntry ( String name, ValueType valueType, String value )
        {
            super ();
            _name = name;
            _valueType = valueType;
            setValue ( value );
        }
        
        public String getName ()
        {
            return _name;
        }
        public void setName ( String name )
        {
            _name = name;
        }
        public Variant getValue ()
        {
            return _value;
        }
        
        public String getValueString ()
        {
            return _valueString;
        }
        
        public void setValue ( String value )
        {
           try
           {
               _valueString = value;
               _value = _valueType.convertTo ( value );
               _valueError = null;
           }
           catch ( Exception e )
           {
               _valueError = e;
           }
        }

        public ValueType getValueType ()
        {
            return _valueType;
        }

        public void setValueType ( ValueType valueType )
        {
            _valueType = valueType;
            setValue ( _valueString );
        }

        public Throwable getValueError ()
        {
            return _valueError;
        }
    }
    
    private class Attributes
    {
        private List<AttributeEntry> _entries = new ArrayList<AttributeEntry> ();

        public void add ( AttributeEntry entry )
        {
            _entries.add (  entry );
        }
        
        public void remove ( AttributeEntry entry )
        {
            _entries.remove ( entry );
        }
        
        public List<AttributeEntry> getEntries ()
        {
            return _entries;
        }
    }
    
    private class MyLabelProvider extends LabelProvider implements ITableLabelProvider
    {

        public Image getColumnImage ( Object element, int columnIndex )
        {
            return getImage ( element );
        }

        public String getColumnText ( Object element, int columnIndex )
        {
            _log.info ( "Label for: " + element + ":" + columnIndex );
            
            if ( element instanceof AttributeEntry )
            {
                AttributeEntry entry = (AttributeEntry)element;
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
        public Object[] getElements ( Object inputElement )
        {
            if ( inputElement instanceof Attributes )
            {
                Attributes attributes = (Attributes)inputElement;
                return attributes.getEntries ().toArray ( new AttributeEntry[0] );
            }
            return new Object[0];
        }

        public void dispose ()
        {
        }

        public void inputChanged ( Viewer viewer, Object oldInput, Object newInput )
        {
        }
        
    }
    
    private ComboBoxCellEditor _valueTypeEditor;
    private String [] PROPERTIES = new String [] { "name", "value-type", "value", "value-error" };
    
    private class MyCellModifier implements ICellModifier
    {
        private TableViewer _viewer = null;
        
        public MyCellModifier ( TableViewer viewer )
        {
            _viewer = viewer;
        }
        
        public boolean canModify ( Object element, String property )
        {
            _log.debug ( "Can modify: " + element + ":" + property );
            
            if ( element instanceof AttributeEntry )
            {
                if ( property.equals ( "value" ) )
                    return true;
                if ( property.equals ( "name" ) )
                    return true;
                if ( property.equals ( "value-type" ) )
                    return true;
            }
            return false;
        }

        public Object getValue ( Object element, String property )
        {
            _log.debug ( "Get Value: " + element + ":" + property );
            
            if ( element instanceof AttributeEntry )
            {
                AttributeEntry entry = (AttributeEntry)element;
                if ( property.equals ( "value" ) )
                    return entry.getValueString ();
                if ( property.equals ( "name" ) )
                    return entry.getName ();
                if ( property.equals ( "value-type" ) )
                {
                    return entry.getValueType ().index ();
                }
            }
            return null;  
        }

        public void modify ( Object element, String property, Object value )
        {
            _log.debug ( "Modify Value: " + element + ":" + property + ":" + value );
            
            TableItem tableItem = (TableItem) element;

            if ( tableItem.getData() instanceof AttributeEntry )
            {
                AttributeEntry entry = (AttributeEntry)tableItem.getData();
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
                    Integer i = (Integer)value;
                    String valueType = _valueTypeEditor.getItems ()[i];
                    for ( ValueType vt : ValueType.values () )
                    {
                        if ( vt.label ().equals ( valueType ) )
                            entry.setValueType ( vt );
                    }
                }
                _viewer.update ( entry, PROPERTIES );
                dialogChanged ();
            }
        }
        
    }
    private Attributes _attributes = new Attributes ();
    
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
            AttributeEntry entry = new AttributeEntry ( "", ValueType.STRING, "" );
            _attributes.add ( entry );
            _table.add ( entry );
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
            if ( _selection instanceof IStructuredSelection )
            {
                IStructuredSelection selection = (IStructuredSelection)_selection;
                Iterator i = selection.iterator ();
                while ( i.hasNext () )
                {
                    Object o = i.next ();
                    if ( o instanceof AttributeEntry )
                    {
                        _attributes.remove ( (AttributeEntry)o );
                        _table.remove ( o );
                    }
                }
                dialogChanged ();
            }
        }
       
        public void selectionChanged ( SelectionChangedEvent event )
        {
            _selection = event.getSelection ();
        }
        
    }
    
    private AddAction _addAction = new AddAction ();
    private RemoveAction _removeAction = new RemoveAction ();
    
    protected WriteAttributesOperationWizardValuePage (  )
    {
        super ( "wizardPage" );
        setTitle ( "Write Attributes" );
        setDescription ( "Configure the attributes to write" );
    }

    public void createControl ( Composite parent )
    {
        Composite container = new Composite ( parent, SWT.NONE );
        
        GridLayout layout = new GridLayout();
        container.setLayout(layout);
        layout.numColumns = 3;
        layout.verticalSpacing = 9;
        
        
        Label label = new Label ( container, SWT.NONE );
        label.setText("&Item:");

        _itemIdText = new Text ( container, SWT.BORDER | SWT.SINGLE );
        GridData gd = new GridData ( GridData.FILL_HORIZONTAL );
        _itemIdText.setLayoutData ( gd );
        _itemIdText.addModifyListener ( new ModifyListener() {
            public void modifyText(ModifyEvent e)
            {
                dialogChanged();
            }
        });
        
        label = new Label ( container, SWT.NONE );
       
        // row 2
        
        gd = new GridData ( GridData.FILL_HORIZONTAL );
        gd.horizontalSpan = 3;
        gd.grabExcessHorizontalSpace = true;
        ToolBar toolbar = new ToolBar ( container, SWT.NONE );
        toolbar.setLayoutData ( gd );
        ToolBarManager tbm = new ToolBarManager ( toolbar );
        tbm.add ( _addAction );
        tbm.add ( _removeAction );
        tbm.update ( true );
        
        // row 3
        
        _attributes.add ( new AttributeEntry ( "test", ValueType.STRING, "1.23" ) );
        
        gd = new GridData ( GridData.FILL_BOTH );
        gd.horizontalSpan = 3;
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        _table = new TableViewer ( container, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL );
        
        TableColumn col;
        
        col = new TableColumn (_table.getTable (), SWT.NONE );
        col.setText ( "Name" );
        col = new TableColumn (_table.getTable (), SWT.NONE );
        col.setText ( "Value Type" );
        col = new TableColumn (_table.getTable (), SWT.NONE );
        col.setText ( "Value" );
        col = new TableColumn (_table.getTable (), SWT.NONE );
        col.setText ( "Value Error" );
        _table.getTable ().setHeaderVisible ( true );
        
        try
        {
            _table.setLabelProvider ( new MyLabelProvider () );
            _table.setContentProvider ( new MyContentProvider () );
            
            
            _table.setColumnProperties ( PROPERTIES );
            _table.setCellModifier ( new MyCellModifier ( _table ) );
            
            TextCellEditor nameEditor = new TextCellEditor ( _table.getTable () );
            
            List<String> values = new LinkedList<String> ();
            for ( ValueType vt : ValueType.values () )
            {
                values.add ( vt.label () );   
            }
            _valueTypeEditor = new ComboBoxCellEditor ( _table.getTable (), values.toArray ( new String [0] )  );
            
            TextCellEditor valueEditor = new TextCellEditor ( _table.getTable () );
            _table.setCellEditors ( new CellEditor[] { nameEditor, _valueTypeEditor, valueEditor, new TextCellEditor ( _table.getTable () ) } );
            
            TableLayout tableLayout = new TableLayout();
            tableLayout.addColumnData ( new ColumnWeightData ( 50, 75, true ) );
            tableLayout.addColumnData ( new ColumnWeightData ( 50, 75, true ) );
            tableLayout.addColumnData ( new ColumnWeightData ( 50, 75, true ) );
            tableLayout.addColumnData ( new ColumnWeightData ( 50, 75, true ) );
            _table.getTable ().setLayout ( tableLayout );
            
            _table.setInput ( _attributes );
        }
        catch ( Exception e )
        {
            _log.warn ( "Unable to create control", e );
        }
        
        _table.getTable ().setLayoutData ( gd );
        _table.addSelectionChangedListener ( _removeAction );
        
        setControl ( container );
        fillFromSelection ();
        dialogChanged ();
    }

    private void fillFromSelection ()
    {
        if ( _selection == null )
            return;
        
        Object obj = _selection.getFirstElement ();
        if ( obj == null )
            return;
        if ( !(obj instanceof DataItemEntry) )
            return;
        
        _itemIdText.setText ( ((DataItemEntry)obj).getId () );
    }
    
    private void dialogChanged ()
    {
        // connection
        if ( _connection == null )
        {
            updateStatus ( "No hive connection selection" );
            return;
        }
        
        // item
        if ( _itemIdText.getText ().length () <= 0 )
        {
            updateStatus ( "Item name must not be empty" );
            return;
        }
        
        if ( _attributes._entries.size () <= 0 )
        {
            updateStatus ( "No attributes" );
            return;
        }
        
        for ( AttributeEntry entry : _attributes._entries )
        {
            if ( entry._name.equals ( "" ) )
            {
                updateStatus ( "Attribute with an empty name is not allowed" );
                return;
            }
        }

        updateStatus ( null );
    }

    private void updateStatus ( String message )
    {
        setErrorMessage ( message );
        setPageComplete ( message == null );
    }
    
    public String getItem ()
    {
        return _itemIdText.getText ();
    }
    
    public Map<String, Variant> getAttributes ()
    {
        Map<String, Variant> attributes = new HashMap<String, Variant> ();
        
        for ( AttributeEntry entry : _attributes._entries )
        {
            attributes.put ( entry.getName (), entry.getValue () );
        }
        
        return attributes;
    }
    
    public HiveConnection getConnection()
    {
        return _connection;
    }

    public void setSelection ( IStructuredSelection selection )
    {
        _selection = selection;
        
        Object obj = _selection.getFirstElement ();
        if ( obj instanceof HiveConnection )
            _connection = (HiveConnection)obj;
        else if ( obj instanceof DataItemEntry )
            _connection = ((DataItemEntry)obj).getConnection ();
    }
}