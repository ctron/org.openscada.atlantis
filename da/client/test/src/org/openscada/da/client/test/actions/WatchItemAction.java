package org.openscada.da.client.test.actions;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.openscada.da.client.test.impl.HiveItem;
import org.openscada.da.client.test.views.DataItemView;

public class WatchItemAction implements IViewActionDelegate
{
    private static Logger _log = Logger.getLogger ( WatchItemAction.class );
    
    private IViewPart _viewPart = null;
    
    private HiveItem _item = null;
    
    public void init ( IViewPart view )
    {
        _viewPart = view;
    }

    public void run ( IAction action )
    {
        if ( _item == null )
            return;
        
        _log.debug("Performing action: " + _item.getItemName() );
        
        try
        {
            IViewPart viewer = _viewPart.getSite().getPage().showView("org.openscada.da.client.test.views.DataItemView",_item.getItemName(),IWorkbenchPage.VIEW_CREATE);
            if ( viewer instanceof DataItemView )
            {
                ((DataItemView)viewer).setDataItem ( _item );
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
        _log.debug("Selection changed");
        
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

}
