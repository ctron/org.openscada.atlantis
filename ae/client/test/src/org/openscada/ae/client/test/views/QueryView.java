package org.openscada.ae.client.test.views;

import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.part.ViewPart;
import org.openscada.ae.client.test.impl.EventData;
import org.openscada.ae.client.test.impl.QueryDataController;
import org.openscada.ae.client.test.impl.QueryDataModel;
import org.openscada.ae.client.test.impl.StorageQuery;
import org.openscada.ae.client.test.views.QueryDataContentProvider.AttributePair;

public class QueryView extends ViewPart
{
    public final static String VIEW_ID = "org.openscada.ae.client.test.views.QueryView";

    private TreeViewer _viewer = null;
    
    private StorageQuery _query = null;
    private QueryDataModel _model = null;
    private QueryDataController _controller = null;
    
    class NameSorter extends ViewerSorter
    {
        @Override
        public int compare ( Viewer viewer, Object e1, Object e2 )
        {
            if ( e1 instanceof EventData && e2 instanceof EventData )
            {
                EventData ev1 = (EventData)e1;
                EventData ev2 = (EventData)e2;
                int cmp = ev1.getEvent ().getTimestamp ().compareTo ( ev2.getEvent ().getTimestamp () );
                if ( cmp != 0 )
                    return cmp;
                return ev1.getEvent ().getId ().compareTo ( ev2.getEvent ().getId () );
            }
            else if ( e1 instanceof AttributePair && e2 instanceof AttributePair )
            {
                AttributePair ap1 = (AttributePair)e1;
                AttributePair ap2 = (AttributePair)e2;
                
                return ap1._key.compareTo ( ap2._key );
            }
            return 0;
        }
    }
    
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
        _viewer.setLabelProvider ( new QueryDataLabelProvider ( _viewer.getTree ().getFont () ) );
        _viewer.setSorter ( new NameSorter () );
        
        TableLayout tableLayout = new TableLayout ();
        tableLayout.addColumnData ( new ColumnWeightData ( 50, 75, true ) );
        tableLayout.addColumnData ( new ColumnWeightData ( 50, 75, true ) );
        _viewer.getTree ().setLayout ( tableLayout );
        
        getViewSite ().setSelectionProvider ( _viewer );
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
