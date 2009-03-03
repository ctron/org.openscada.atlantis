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

import java.util.ArrayList;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.openscada.ae.client.test.Activator;
import org.openscada.ae.client.test.impl.EventData;
import org.openscada.ae.client.test.impl.QueryDataModel;
import org.openscada.ae.core.Event;
import org.openscada.core.Variant;

public class QueryDataContentProvider implements ITreeContentProvider, Observer
{
    public class AttributePair
    {
        public String _key = null;

        public Variant _value = null;
    }

    private QueryDataModel _model = null;

    private TreeViewer _viewer = null;

    public void dispose ()
    {
        disconnect ();
    }

    public void inputChanged ( final Viewer viewer, final Object oldInput, final Object newInput )
    {
        if ( viewer instanceof TreeViewer )
        {
            this._viewer = (TreeViewer)viewer;
        }

        disconnect ();

        if ( newInput != null )
        {
            if ( newInput instanceof QueryDataModel )
            {
                connect ( (QueryDataModel)newInput );
            }
        }

    }

    public Object[] getElements ( final Object inputElement )
    {
        if ( this._model == null )
        {
            return new Object[0];
        }

        return this._model.getEvents ().toArray ( new Event[0] );
    }

    public Object[] getChildren ( final Object parentElement )
    {
        if ( parentElement instanceof EventData )
        {
            final EventData event = (EventData)parentElement;
            final ArrayList<AttributePair> pairs = new ArrayList<AttributePair> ( event.getEvent ().getAttributes ().size () );
            for ( final Map.Entry<String, Variant> entry : event.getEvent ().getAttributes ().entrySet () )
            {
                final AttributePair pair = new AttributePair ();
                pair._key = entry.getKey ();
                pair._value = entry.getValue ();
                pairs.add ( pair );
            }
            return pairs.toArray ( new AttributePair[pairs.size ()] );
        }
        return new Object[0];
    }

    public Object getParent ( final Object element )
    {
        if ( element instanceof EventData )
        {
            final EventData eventData = (EventData)element;
            return eventData.getQuery ();
        }
        return null;
    }

    public boolean hasChildren ( final Object element )
    {
        if ( element instanceof EventData )
        {
            final EventData event = (EventData)element;
            return event.getEvent ().getAttributes ().size () > 0;
        }
        return false;
    }

    synchronized protected void disconnect ()
    {
        if ( this._model != null )
        {
            this._model.deleteObserver ( this );
            this._model = null;
        }
    }

    synchronized protected void connect ( final QueryDataModel model )
    {
        disconnect ();

        if ( model != null )
        {
            this._model = model;
            this._model.addObserver ( this );
        }
    }

    public void update ( final Observable o, final Object arg )
    {
        // just in case
        if ( o != this._model )
        {
            return;
        }

        try
        {
            this._viewer.getTree ().getDisplay ().asyncExec ( new Runnable () {

                public void run ()
                {
                    performUpdate ( arg );
                }
            } );
        }
        catch ( final Exception e )
        {
            Activator.logError ( 0, "Unable to update view", e );
        }
    }

    private void performUpdate ( final Object arg )
    {
        if ( this._viewer == null )
        {
            return;
        }
        if ( this._viewer.getTree ().isDisposed () )
        {
            return;
        }

        if ( ! ( arg instanceof QueryDataModel.UpdateData ) )
        {
            this._viewer.refresh ();
        }
        else
        {
            final QueryDataModel.UpdateData updateData = (QueryDataModel.UpdateData)arg;
            this._viewer.add ( this._model, updateData.added.toArray ( new EventData[updateData.added.size ()] ) );
            this._viewer.remove ( updateData.removed.toArray ( new EventData[updateData.removed.size ()] ) );
            this._viewer.refresh ( updateData.modified.toArray ( new EventData[updateData.modified.size ()] ) );
        }
    }

}
