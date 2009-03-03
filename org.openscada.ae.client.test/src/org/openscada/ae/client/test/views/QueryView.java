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
        public int compare ( final Viewer viewer, final Object e1, final Object e2 )
        {
            if ( e1 instanceof EventData && e2 instanceof EventData )
            {
                final EventData ev1 = (EventData)e1;
                final EventData ev2 = (EventData)e2;
                final int cmp = ev1.getEvent ().getTimestamp ().compareTo ( ev2.getEvent ().getTimestamp () );
                if ( cmp != 0 )
                {
                    return cmp;
                }
                return ev1.getEvent ().getId ().compareTo ( ev2.getEvent ().getId () );
            }
            else if ( e1 instanceof AttributePair && e2 instanceof AttributePair )
            {
                final AttributePair ap1 = (AttributePair)e1;
                final AttributePair ap2 = (AttributePair)e2;

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
    public void createPartControl ( final Composite parent )
    {
        this._viewer = new TreeViewer ( parent, SWT.NONE );
        final TreeColumn idCol = new TreeColumn ( this._viewer.getTree (), SWT.NONE );
        idCol.setText ( "ID" );
        final TreeColumn dataCol = new TreeColumn ( this._viewer.getTree (), SWT.NONE );
        dataCol.setText ( "Data" );

        // show headers
        this._viewer.getTree ().setHeaderVisible ( true );

        // set model
        this._viewer.setContentProvider ( new QueryDataContentProvider () );
        this._viewer.setLabelProvider ( new QueryDataLabelProvider ( this._viewer.getTree ().getFont () ) );
        this._viewer.setSorter ( new NameSorter () );

        final TableLayout tableLayout = new TableLayout ();
        tableLayout.addColumnData ( new ColumnWeightData ( 50, 75, true ) );
        tableLayout.addColumnData ( new ColumnWeightData ( 50, 75, true ) );
        this._viewer.getTree ().setLayout ( tableLayout );

        getViewSite ().setSelectionProvider ( this._viewer );
    }

    @Override
    public void setFocus ()
    {
        this._viewer.getTree ().setFocus ();
    }

    public void setQuery ( final StorageQuery query )
    {
        clear ();

        this._query = query;
        this._model = new QueryDataModel ();
        this._controller = new QueryDataController ( this._model );

        this._query.getConnection ().getConnection ().subscribe ( this._query.getQueryDescription ().getId (), this._controller, 10, -1 );
        this._viewer.setInput ( this._model );
    }

    public void clear ()
    {
        if ( this._query != null )
        {
            this._query.getConnection ().getConnection ().unsubscribe ( this._query.getQueryDescription ().getId (), this._controller );
        }
        this._query = null;
        this._model = null;
        this._controller = null;
    }
}
