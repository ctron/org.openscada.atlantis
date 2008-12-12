package org.openscada.da.base.dnd;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.openscada.da.base.browser.DataItemEntry;
import org.openscada.da.base.item.Item;

public class ItemDragSourceListener implements DragSourceListener
{
    @SuppressWarnings ( "unused" )
    private static Logger log = Logger.getLogger ( ItemDragSourceListener.class );

    private Viewer viewer = null;

    public ItemDragSourceListener ( final Viewer viewer )
    {
        super ();
        this.viewer = viewer;
    }

    public void dragFinished ( final DragSourceEvent event )
    {
    }

    public void dragSetData ( final DragSourceEvent event )
    {
        try
        {
            if ( ItemTransfer.getInstance ().isSupportedType ( event.dataType ) )
            {
                final IStructuredSelection selection = (IStructuredSelection)LocalSelectionTransfer.getTransfer ().getSelection ();

                final List<Item> items = new ArrayList<Item> ();
                for ( final Iterator<?> i = selection.iterator (); i.hasNext (); )
                {
                    final DataItemEntry entry = (DataItemEntry)i.next ();

                    final Item item = new Item ();
                    item.setId ( entry.getId () );
                    item.setConnectionString ( entry.getConnection ().getConnectionInformation ().toString () );
                    items.add ( item );
                }
                event.data = items.toArray ( new Item[items.size ()] );
            }
        }
        catch ( final Exception e )
        {
            event.doit = false;
        }

    }

    public void dragStart ( final DragSourceEvent event )
    {
        event.doit = false;

        if ( ! ( this.viewer.getSelection () instanceof IStructuredSelection ) )
        {
            return;
        }

        final IStructuredSelection selection = (IStructuredSelection)this.viewer.getSelection ();
        if ( selection.isEmpty () )
        {
            return;
        }

        for ( final Iterator<?> i = selection.iterator (); i.hasNext (); )
        {
            final Object o = i.next ();
            if ( ! ( o instanceof DataItemEntry ) )
            {
                return;
            }
        }

        LocalSelectionTransfer.getTransfer ().setSelection ( this.viewer.getSelection () );

        event.doit = true;
    }

}
