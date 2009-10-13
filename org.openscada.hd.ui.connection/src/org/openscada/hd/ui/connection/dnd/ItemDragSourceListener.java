package org.openscada.hd.ui.connection.dnd;

import java.util.Collection;

import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.openscada.hd.ui.connection.data.Item;
import org.openscada.hd.ui.connection.data.ItemSelectionHelper;

public class ItemDragSourceListener implements DragSourceListener
{

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
                final Collection<Item> items = ItemSelectionHelper.getSelection ( selection );
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

        final Collection<Item> items = ItemSelectionHelper.getSelection ( this.viewer.getSelection () );
        if ( !items.isEmpty () )
        {
            LocalSelectionTransfer.getTransfer ().setSelection ( this.viewer.getSelection () );
            event.doit = true;
        }

    }

}
