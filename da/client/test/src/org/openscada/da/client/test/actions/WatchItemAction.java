package org.openscada.da.client.test.actions;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.openscada.da.client.test.impl.HiveItem;
import org.openscada.da.client.test.views.DataItemWatchView;

public class WatchItemAction implements IViewActionDelegate, IObjectActionDelegate
{
    @SuppressWarnings("unused")
    private static Logger _log = Logger.getLogger ( WatchItemAction.class );
    
    private IWorkbenchPartSite _site = null;
    
    private HiveItem _item = null;
    
    public void init ( IViewPart view )
    {
        _site = view.getSite();
    }

    public void run ( IAction action )
    {
        if ( _item == null )
            return;
       
        try
        {
            IViewPart viewer = _site.getPage().showView("org.openscada.da.client.test.views.DataItemWatchView",_item.getItemName(),IWorkbenchPage.VIEW_ACTIVATE);
            if ( viewer instanceof DataItemWatchView )
            {
                ((DataItemWatchView)viewer).setDataItem ( _item );
            }
        }
        catch ( PartInitException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void selectionChanged ( IAction action, ISelection selection )
    {
        _item = null;
        
        if ( selection == null )
            return;
        if ( ! (selection instanceof IStructuredSelection) )
            return;
        
        IStructuredSelection sel = (IStructuredSelection)selection;
        Object obj = sel.getFirstElement();
        
        if ( obj == null )
            return;
        if ( !(obj instanceof HiveItem) )
            return;
        
        _item = (HiveItem)obj;
    }

    public void setActivePart ( IAction action, IWorkbenchPart targetPart )
    {
        _site = targetPart.getSite();
    }

}
