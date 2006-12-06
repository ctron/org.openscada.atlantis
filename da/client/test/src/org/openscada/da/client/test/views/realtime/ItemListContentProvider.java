package org.openscada.da.client.test.views.realtime;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

public class ItemListContentProvider implements ITreeContentProvider, Listener
{
    private static Logger _log = Logger.getLogger ( ItemListContentProvider.class );
    
    private Viewer _viewer = null;
    private ListData _data = null;

    public Object[] getChildren ( Object parentElement )
    {
        if ( _data == null )
            return null;
        
        if ( parentElement instanceof ListData )
        {
            ListData listData = (ListData)parentElement;
            return listData.getItems ().toArray ( new ListEntry[0] );
        }
        else if ( parentElement instanceof ListEntry )
        {
            return ((ListEntry)parentElement).getAttributes ().toArray ( new ListEntry.AttributePair[0] );
        }
        
        return new Object[0];
    }

    public Object getParent ( Object element )
    {
        if ( _data == null )
            return null;
        
        if ( element instanceof ListEntry )
        {
            return _data;
        }
        
        return null;
    }

    public boolean hasChildren ( Object element )
    {
        if ( _data == null )
            return false;
        
        if ( element instanceof ListEntry )
        {
            return ((ListEntry)element).hasAttributes ();
        }
        
        return false;
    }

    public Object[] getElements ( Object inputElement )
    {
        return getChildren ( inputElement ); 
    }

    public void dispose ()
    {
        unsubscribe ();
    }

    public void inputChanged ( Viewer viewer, Object oldInput, Object newInput )
    {
        unsubscribe ();
        
        _viewer = viewer;
        
        if ( newInput != null )
        {
            subcribe ( newInput );
        }
    }

    private void subcribe ( Object newInput )
    {
        if ( !(newInput instanceof ListData) )
            return;
        
        _data = (ListData)newInput;
        _data.addListener ( this );
    }

    private void unsubscribe ()
    {
        if ( _data != null )
        {
            _data.removeListener ( this );
            _data = null;
        }
    }

    public void added ( final ListEntry[] entries )
    {
        try
        {
            if ( _viewer != null )
                _viewer.getControl ().getDisplay ().asyncExec ( new Runnable ()
                {
                    public void run ()
                    {
                        performAdded ( entries );
                    }} 
                );
        }
        catch ( Exception e )
        {
            _log.warn ( "Failed to notify viewer", e );
        }
    }
    
    protected void performAdded ( ListEntry [] entries )
    {
        if ( _viewer.getControl ().isDisposed () )
            return;
        
        if ( _viewer instanceof TreeViewer )
        {
            ((TreeViewer)_viewer).add ( _data, entries );
        }
        else if ( _viewer != null )
            _viewer.refresh ();
    }

    public void removed ( final ListEntry[] entries )
    {
        try
        {
            if ( _viewer != null )
                _viewer.getControl ().getDisplay ().asyncExec ( new Runnable ()
                {
                    public void run ()
                    {
                        performRemoved ( entries );
                    }} 
                );
        }
        catch ( Exception e )
        {
            _log.warn ( "Failed to notify viewer", e );
        }
    }
    
    public void performRemoved ( ListEntry[] entries )
    {
        if ( _viewer.getControl ().isDisposed () )
            return;
        
        if ( _viewer instanceof TreeViewer )
        {
            ((TreeViewer)_viewer).remove ( entries );
        }
        else if ( _viewer != null )
            _viewer.refresh ();
    }
    
    public void updated ( final ListEntry[] entries )
    {
        try
        {
            if ( _viewer != null )
                _viewer.getControl ().getDisplay ().asyncExec ( new Runnable ()
                {
                    public void run ()
                    {
                        performUpdated ( entries );
                    }} 
                );
        }
        catch ( Exception e )
        {
            _log.warn ( "Failed to notify viewer", e );
        }
    }

    public void performUpdated ( ListEntry[] entries )
    {
        if ( _viewer.getControl ().isDisposed () )
            return;
        
        if ( _viewer instanceof TreeViewer )
        {
            for ( ListEntry entry : entries )
            {
                ((TreeViewer)_viewer).refresh ( entry );    
            }
            ((TreeViewer)_viewer).update ( entries, null );
        }
        else if ( _viewer != null )
            _viewer.refresh ();
    }

}
