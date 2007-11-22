package org.openscada.da.client.test.views.realtime;

import java.util.Iterator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;

public class RemoveAction extends Action implements ISelectionChangedListener
{
    private RealTimeList _view = null;
    private ISelection _selection = null;
    
    public RemoveAction ( RealTimeList view )
    {
        super ( "Remove", Action.AS_PUSH_BUTTON );
        
        _view = view;
    }
    
    @Override
    public void run ()
    {
        if ( _selection instanceof IStructuredSelection )
        {
            IStructuredSelection selection = (IStructuredSelection)_selection;
            Iterator<?> i = selection.iterator ();
            while ( i.hasNext () )
            {
                Object o = i.next ();
                if ( o instanceof ListEntry )
                {
                    _view.remove ( (ListEntry)o );
                }
            }
        }
    }
    
    public void selectionChanged ( SelectionChangedEvent event )
    {
        _selection = event.getSelection ();
    }
}
