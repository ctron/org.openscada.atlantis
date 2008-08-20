package org.openscada.da.client.test.dnd;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.openscada.da.client.test.impl.DataItemEntry;

public class ItemDragSourceListener implements DragSourceListener
{
    @SuppressWarnings("unused")
    private static Logger _log = Logger.getLogger ( ItemDragSourceListener.class );
    
    private Viewer _viewer = null;
    
    public ItemDragSourceListener ( Viewer viewer )
    {
        super ();
        _viewer = viewer;
    }
    
    public void dragFinished ( DragSourceEvent event )
    {
        // TODO Auto-generated method stub
    }

    public void dragSetData ( DragSourceEvent event )
    {
        try
        {

            if ( ItemTransfer.getInstance ().isSupportedType ( event.dataType ) )
            {
                IStructuredSelection selection = (IStructuredSelection)_viewer.getSelection ();
                if ( selection.isEmpty () )
                    return;

                List<Item> items = new ArrayList<Item> ();
                for ( Iterator<?> i = selection.iterator (); i.hasNext (); )
                {
                    DataItemEntry entry = (DataItemEntry)i.next ();

                    Item item = new Item ();
                    item.setId ( entry.getId () );
                    item.setConnectionString ( entry.getConnection ().getConnectionInformation ().toString () );
                    items.add ( item );
                }
                event.data = items.toArray ( new Item[items.size ()] );
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace ( System.err );
        }
         
    }

    public void dragStart ( DragSourceEvent event )
    {
        event.doit = false;
        
        if ( ! (_viewer.getSelection () instanceof IStructuredSelection ) )
        {
            return;
        }
        
        IStructuredSelection selection = (IStructuredSelection)_viewer.getSelection ();
        if ( selection.isEmpty () )
            return;
        
        for ( Iterator<?> i = selection.iterator (); i.hasNext (); )
        {
            Object o = i.next ();
            if ( !(o instanceof DataItemEntry ) )
                return;
        }
        
        event.doit = true;
    }


}
