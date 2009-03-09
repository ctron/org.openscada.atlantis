package org.openscada.da.client.test.views.realtime;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.regex.Pattern;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.openscada.core.ConnectionInformation;
import org.openscada.da.client.base.browser.BrowserEntry;
import org.openscada.da.client.base.browser.DataItemEntry;
import org.openscada.da.client.base.browser.FolderEntry;
import org.openscada.da.client.base.connection.ConnectionManager;
import org.openscada.da.client.base.item.Item;
import org.openscada.da.client.base.realtime.ListEntry;

public class PatternSubscribeAction implements IObjectActionDelegate
{
    private IWorkbenchPart targetPart;

    private Collection<DataItemEntry> items;

    private Pattern pattern;

    public void setActivePart ( final IAction action, final IWorkbenchPart targetPart )
    {
        this.targetPart = targetPart;
    }

    public void run ( final IAction action )
    {
        if ( !fetchInput () )
        {
            return;
        }

        final Random r = new Random ();
        final String secondaryId = String.format ( "%08x%08x", r.nextLong (), r.nextLong () );
        try
        {
            final RealTimeList list = (RealTimeList)this.targetPart.getSite ().getPage ().showView ( RealTimeList.VIEW_ID, secondaryId, IWorkbenchPage.VIEW_ACTIVATE );

            for ( final DataItemEntry entry : this.items )
            {
                if ( entryMatches ( entry ) )
                {
                    final Item item = new Item ( entry.getConnection ().getConnectionInformation ().toString (), entry.getId () );

                    final ListEntry newEntry = new ListEntry ();
                    newEntry.setDataItem ( item, ConnectionManager.getDefault ().getItemManager ( ConnectionInformation.fromURI ( item.getConnectionString () ), true ) );
                    list.add ( newEntry );
                }

            }
        }
        catch ( final PartInitException e )
        {
            ErrorDialog.openError ( this.targetPart.getSite ().getShell (), "Error", "Failed to subscribe", e.getStatus () );
        }
    }

    private boolean fetchInput ()
    {
        final InputDialog dlg = new InputDialog ( this.targetPart.getSite ().getShell (), "Pattern", "Enter the subscribe pattern", ".*", new IInputValidator () {

            public String isValid ( final String newText )
            {
                try
                {
                    Pattern.compile ( newText );
                    return null;
                }
                catch ( final Throwable e )
                {
                    return e.getLocalizedMessage ();
                }
            }
        } );
        final boolean ok = dlg.open () == org.eclipse.jface.dialogs.Dialog.OK;
        if ( ok )
        {
            this.pattern = Pattern.compile ( dlg.getValue () );
        }
        return ok;
    }

    private boolean entryMatches ( final DataItemEntry entry )
    {
        return this.pattern.matcher ( entry.getName () ).matches ();
    }

    public void selectionChanged ( final IAction action, final ISelection selection )
    {
        this.items = new LinkedList<DataItemEntry> ();
        if ( selection instanceof IStructuredSelection )
        {
            final IStructuredSelection s = (IStructuredSelection)selection;
            final Iterator<?> i = s.iterator ();
            while ( i.hasNext () )
            {
                final Object o = i.next ();
                if ( o instanceof FolderEntry )
                {
                    handleFolder ( (FolderEntry)o );
                }
            }
        }
    }

    private void handleFolder ( final FolderEntry folderEntry )
    {
        for ( final BrowserEntry entry : folderEntry.getEntries () )
        {
            if ( entry instanceof DataItemEntry )
            {
                final DataItemEntry dataItemEntry = (DataItemEntry)entry;
                if ( dataItemEntry.getName () != null )
                {
                    this.items.add ( dataItemEntry );
                }
            }
        }
    }
}
