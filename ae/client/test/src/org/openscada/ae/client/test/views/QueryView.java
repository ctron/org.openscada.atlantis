package org.openscada.ae.client.test.views;

import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableTreeViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.part.ViewPart;
import org.openscada.ae.client.test.impl.QueryDataController;
import org.openscada.ae.client.test.impl.QueryDataModel;
import org.openscada.ae.client.test.impl.StorageConnection;
import org.openscada.ae.client.test.impl.StorageQuery;

public class QueryView extends ViewPart
{
    public final static String VIEW_ID = "org.openscada.ae.client.test.views.QueryView";

    private TreeViewer _viewer = null;
    
    private StorageQuery _query = null;
    private QueryDataModel _model = null;
    private QueryDataController _controller = null;
    
    public QueryView ()
    {
        super ();
    }
    
    @Override
    public void dispose ()
    {
        clear ();
        super.dispose ();
    }

    @Override
    public void createPartControl ( Composite parent )
    {
        _viewer = new TreeViewer ( parent, SWT.NONE );
        TreeColumn idCol = new TreeColumn ( _viewer.getTree (), SWT.NONE );
        idCol.setText ( "ID" );
        TreeColumn dataCol = new TreeColumn ( _viewer.getTree (), SWT.NONE );
        dataCol.setText ( "Data" );
        
        // show headers
        _viewer.getTree ().setHeaderVisible ( true );
        
        // set model
        _viewer.setContentProvider ( new QueryDataContentProvider () );
        _viewer.setLabelProvider ( new QueryDataLabelProvider () );
        
        TableLayout tableLayout = new TableLayout ();
        tableLayout.addColumnData ( new ColumnWeightData ( 50, 75, true ) );
        tableLayout.addColumnData ( new ColumnWeightData ( 50, 75, true ) );
        _viewer.getTree ().setLayout ( tableLayout );
    }

    @Override
    public void setFocus ()
    {
        _viewer.getTree ().setFocus ();
    }

    public void setQuery ( StorageQuery query )
    {
        clear ();
        
        _query = query;
        _model = new QueryDataModel ();
        _controller = new QueryDataController ( _model );
        
        _query.getConnection ().getConnection ().subscribe ( _query.getQueryDescription ().getId (), _controller, 10, -1 );
        _viewer.setInput ( _model );
    }

    public void clear ()
    {
        if ( _query != null )
        {
            _query.getConnection ().getConnection ().unsubscribe ( _query.getQueryDescription ().getId (), _controller );
        }
        _query = null;
        _model = null;
        _controller = null;
    }
}
