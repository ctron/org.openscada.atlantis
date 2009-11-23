package org.openscada.hd.ui.views;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.resource.JFaceColors;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.openscada.hd.QueryListener;
import org.openscada.hd.QueryParameters;
import org.openscada.hd.QueryState;
import org.openscada.hd.Value;
import org.openscada.hd.ValueInformation;

public class QueryDataView extends QueryViewPart implements QueryListener
{
    private static final int FIX_COLS = 3;

    private final Map<String, TableColumn> columns = new HashMap<String, TableColumn> ();

    private TableColumn indexCol;

    private Table table;

    private Color invalidColor;

    private String[] colNames;

    private TableColumn qualityCol;
    
    private TableColumn manualCol;

    private TableColumn infoCol;

    @Override
    public void createPartControl ( final Composite parent )
    {
        addListener ();

        parent.setLayout ( new FillLayout () );
        this.table = new Table ( parent, SWT.FULL_SELECTION );
        this.table.setHeaderVisible ( true );

        this.indexCol = new TableColumn ( this.table, SWT.NONE );
        this.indexCol.setText ( Messages.QueryDataView_ColIndex );
        this.indexCol.setWidth ( 50 );

        this.qualityCol = new TableColumn ( this.table, SWT.NONE );
        this.qualityCol.setText ( Messages.QueryDataView_ColQuality );
        this.qualityCol.setWidth ( 75 );

        this.manualCol = new TableColumn ( this.table, SWT.NONE );
        this.manualCol.setText ( Messages.QueryDataView_ColManual );
        this.manualCol.setWidth ( 75 );

        this.invalidColor = JFaceColors.getErrorBackground ( getDisplay () );
    }

    @Override
    public void setFocus ()
    {
        this.table.setFocus ();
    }

    @Override
    protected void clear ()
    {
        clearDataSize ();
        super.clear ();
    }

    public void updateData ( final int index, final Map<String, Value[]> values, final ValueInformation[] valueInformation )
    {
        getDisplay ().asyncExec ( new Runnable () {

            public void run ()
            {
                handleUpdateData ( index, values, valueInformation );
            }
        } );
    }

    private void handleUpdateData ( final int index, final Map<String, Value[]> values, final ValueInformation[] valueInformation )
    {
        // FIXME: implement faster
        final int len = valueInformation.length;
        for ( int i = 0; i < len; i++ )
        {
            final TableItem item = this.table.getItem ( i + index );

            final double quality = valueInformation[i].getQuality ();
            final double manual = valueInformation[i].getManualPercentage ();

            item.setText ( 0, String.format ( Messages.QueryDataView_Format_Index, index + i ) );
            item.setText ( 1, String.format ( Messages.QueryDataView_Format_Quality, quality ) );
            item.setText ( 2, String.format ( Messages.QueryDataView_Format_Manual, manual ) );

            for ( int j = 0; j < this.colNames.length; j++ )
            {
                final Value[] value = values.get ( this.colNames[j] );
                item.setText ( j + FIX_COLS, getValueString ( value[i] ) );
            }

            item.setText ( this.colNames.length + FIX_COLS, valueInformation[i].toString () );

            if ( quality < 0.33 )
            {
                item.setBackground ( this.invalidColor );
            }
            else
            {
                item.setBackground ( null );
            }

        }
    }

    private String getValueString ( final Value value )
    {
        final Number num = value.toNumber ();
        if ( num instanceof Double )
        {
            final Double dNum = (Double)num;
            if ( Double.isInfinite ( dNum ) )
            {
                return "Inf";
            }
            else if ( Double.isNaN ( dNum ) )
            {
                return "NaN";
            }
            return String.format ( Messages.QueryDataView_Format_Value, dNum );
        }
        else if ( num instanceof Long )
        {
            final Long lNum = (Long)num;
            return String.format ( "%s", lNum );
        }
        else
        {
            return String.format ( Messages.QueryDataView_Format_Value, value.toDouble () );
        }

    }

    protected Display getDisplay ()
    {
        try
        {
            return getSite ().getShell ().getDisplay ();
        }
        catch ( final Throwable e )
        {
            return null;
        }
    }

    public void updateParameters ( final QueryParameters parameters, final Set<String> valueTypes )
    {
        getDisplay ().asyncExec ( new Runnable () {

            public void run ()
            {
                setDataSize ( parameters.getEntries (), valueTypes );
            }
        } );
    }

    private void setDataSize ( final int entries, final Set<String> valueTypes )
    {
        clearDataSize ();

        this.colNames = valueTypes.toArray ( new String[0] );
        for ( final String valueType : valueTypes )
        {
            final TableColumn col = new TableColumn ( this.table, SWT.NONE );
            col.setText ( valueType );
            col.setWidth ( 100 );
            col.setAlignment ( SWT.RIGHT );
            this.columns.put ( valueType, col );
        }

        this.infoCol = new TableColumn ( this.table, SWT.NONE );
        this.infoCol.setText ( Messages.QueryDataView_ColInfo );
        this.infoCol.setWidth ( 150 );

        this.table.clearAll ();
        this.table.setItemCount ( entries );

        for ( int i = 0; i < entries; i++ )
        {
            final TableItem item = this.table.getItem ( i );
            item.setBackground ( this.invalidColor );
            item.setText ( 0, String.format ( Messages.QueryDataView_Format_Index, i ) );
        }
    }

    private void clearDataSize ()
    {
        this.table.clearAll ();

        for ( final TableColumn col : this.columns.values () )
        {
            col.dispose ();
        }
        this.columns.clear ();

        if ( this.infoCol != null )
        {
            this.infoCol.dispose ();
            this.infoCol = null;
        }
    }

    public void updateState ( final QueryState state )
    {
    }

}
