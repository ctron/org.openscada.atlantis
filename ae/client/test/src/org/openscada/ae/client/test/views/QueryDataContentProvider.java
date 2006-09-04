package org.openscada.ae.client.test.views;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.openscada.ae.client.test.Activator;
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

    public void inputChanged ( Viewer viewer, Object oldInput, Object newInput )
    {
        if ( viewer instanceof TreeViewer )
        {
            _viewer = (TreeViewer)viewer;
        }
        
        disconnect ();
        
        if ( newInput != null )
        {
            if ( newInput instanceof QueryDataModel )
                connect ( (QueryDataModel)newInput ); 
        }
        
    }
    
    public Object[] getElements ( Object inputElement )
    {
        if ( _model == null )
            return new Object[0];
        
        return _model.getEvents ().toArray ( new Event[0] );
    }

    public Object[] getChildren ( Object parentElement )
    {
        if ( parentElement instanceof Event )
        {
            Event event = (Event)parentElement;
            ArrayList<AttributePair> pairs = new ArrayList<AttributePair> ( event.getAttributes ().size () );
            for ( Map.Entry<String, Variant> entry : event.getAttributes ().entrySet () )
            {
                AttributePair pair = new AttributePair ();
                pair._key = entry.getKey ();
                pair._value = entry.getValue ();
                pairs.add ( pair );
            }
            return pairs.toArray ( new AttributePair [ pairs.size () ] );
        }
        return new Object[0];
    }

    public Object getParent ( Object element )
    {
        return null; 
    }

    public boolean hasChildren ( Object element )
    {
        if ( element instanceof Event )
        {
            Event event = (Event)element;
            return event.getAttributes ().size () > 0;
        }
        return false;
    }

    synchronized protected void disconnect ()
    {
        if ( _model != null )
        {
            _model.deleteObserver ( this );
            _model = null;
        }
    }
    
    synchronized protected void connect ( QueryDataModel model )
    {
        disconnect ();
        
        if ( model != null )
        {
            _model = model;
            _model.addObserver ( this );
        }
    }

    public void update ( Observable o, Object arg )
    {
        // just in case
        if ( o != _model )
            return;
        
        try
        {
        _viewer.getTree ().getDisplay ().asyncExec ( new Runnable () {

            public void run ()
            {
                performUpdate ();
            }} );
        }
        catch ( Exception e )
        {
            Activator.logError ( 0, "Unable to update view", e );
        }
    }
    
    private void performUpdate ()
    {
        if ( _viewer != null )
            _viewer.refresh ();
    }

}
