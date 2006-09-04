package org.openscada.ae.client.test.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.PartInitException;
import org.openscada.ae.client.test.Activator;
import org.openscada.ae.client.test.impl.StorageQuery;
import org.openscada.ae.client.test.views.QueryView;
import org.openscada.ae.core.QueryDescription;

public class ShowQueryAction implements IViewActionDelegate
{

    private StorageQuery _query = null;
    private IWorkbenchSite _site = null;
    
    public void init ( IViewPart view )
    {
        _site = view.getSite ();
    }

    public void run ( IAction action )
    {
        try
        {
            IViewPart viewer = _site.getPage ().showView ( QueryView.VIEW_ID, _query.getQueryDescription ().getId (), IWorkbenchPage.VIEW_ACTIVATE );
            if ( viewer instanceof QueryView )
            {
                ((QueryView)viewer).setQuery ( _query );
            }
        }
        catch ( PartInitException e )
        {
            
            Activator.getDefault ().notifyError ( "Unable to show query", e );
        }
    }

    public void selectionChanged ( IAction action, ISelection selection )
    {
        _query = null;
        
        if ( selection instanceof IStructuredSelection )
        {
            IStructuredSelection structuredSelection = (IStructuredSelection)selection;
            if ( structuredSelection.getFirstElement () instanceof StorageQuery )
            {
                _query = (StorageQuery)structuredSelection.getFirstElement ();
            }
        }
    }

    public StorageQuery getQuery ()
    {
        return _query;
    }

    public void setQuery ( StorageQuery query )
    {
        _query = query;
    }

}
