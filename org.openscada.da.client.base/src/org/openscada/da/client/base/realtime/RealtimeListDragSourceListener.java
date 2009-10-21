package org.openscada.da.client.base.realtime;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.URLTransfer;
import org.openscada.da.ui.connection.data.Item;
import org.openscada.da.ui.connection.dnd.ItemTransfer;

public class RealtimeListDragSourceListener implements DragSourceListener
{
    @SuppressWarnings ( "unused" )
    private static Logger log = Logger.getLogger ( RealtimeListDragSourceListener.class );

    private Viewer viewer = null;

    public RealtimeListDragSourceListener ( final Viewer viewer )
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
                    final ListEntry entry = (ListEntry)i.next ();
                    items.add ( entry.getItem () );
                }
                event.data = items.toArray ( new Item[items.size ()] );
            }
            else if ( TextTransfer.getInstance ().isSupportedType ( event.dataType ) )
            {
                final IStructuredSelection selection = (IStructuredSelection)LocalSelectionTransfer.getTransfer ().getSelection ();
                setItemUriData ( event, selection );
            }
            else if ( URLTransfer.getInstance ().isSupportedType ( event.dataType ) )
            {
                final IStructuredSelection selection = (IStructuredSelection)LocalSelectionTransfer.getTransfer ().getSelection ();
                setItemUriData ( event, selection );
            }
        }
        catch ( final Exception e )
        {
            event.doit = false;
        }

    }

    protected void setItemUriData ( final DragSourceEvent event, final IStructuredSelection selection )
    {
        final StringBuilder sb = new StringBuilder ();
        int cnt = 0;
        for ( final Iterator<?> i = selection.iterator (); i.hasNext (); )
        {
            final ListEntry entry = (ListEntry)i.next ();

            if ( cnt > 0 )
            {
                sb.append ( "\n" );
            }

            sb.append ( entry.getItem ().getConnectionString () );
            sb.append ( "#" );
            sb.append ( entry.getItem ().getId () );

            cnt++;
        }
        event.data = sb.toString ();
    }

    protected void setItemStringData ( final DragSourceEvent event, final IStructuredSelection selection )
    {
        final StringBuilder sb = new StringBuilder ();
        int cnt = 0;
        for ( final Iterator<?> i = selection.iterator (); i.hasNext (); )
        {
            final ListEntry entry = (ListEntry)i.next ();
            if ( cnt > 0 )
            {
                sb.append ( "\n" );
            }
            sb.append ( entry.getDataItem ().getItem ().getId () );
            cnt++;
        }
        event.data = sb.toString ();
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
            if ( ! ( o instanceof ListEntry ) )
            {
                return;
            }
        }

        LocalSelectionTransfer.getTransfer ().setSelection ( this.viewer.getSelection () );

        event.doit = true;
    }

}
