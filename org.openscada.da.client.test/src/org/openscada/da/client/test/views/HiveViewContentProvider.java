/**
 * 
 */
package org.openscada.da.client.test.views;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.openscada.da.client.base.browser.BrowserEntry;
import org.openscada.da.client.base.browser.FolderEntry;
import org.openscada.da.client.base.browser.HiveConnection;
import org.openscada.da.client.base.browser.HiveItem;
import org.openscada.da.client.base.browser.HiveRepository;

class HiveViewContentProvider implements IStructuredContentProvider, ITreeContentProvider
{

    /**
     * 
     */
    @SuppressWarnings ( "unused" )
    private Viewer _viewer = null;

    private HiveRepository _repository = null;

    public void inputChanged ( final Viewer v, final Object oldInput, final Object newInput )
    {
        clearInput ();

        this._viewer = v;
        if ( newInput instanceof HiveRepository )
        {
            this._repository = (HiveRepository)newInput;
        }
    }

    public void clearInput ()
    {
        if ( this._repository != null )
        {
            this._repository = null;
        }
    }

    public void dispose ()
    {
        clearInput ();
    }

    public Object[] getElements ( final Object parent )
    {
        if ( parent == null )
        {
            return null;
        }

        return getChildren ( parent );
    }

    public Object getParent ( final Object child )
    {
        if ( child instanceof HiveConnection )
        {
            return this._repository;
        }
        else if ( child instanceof HiveItem )
        {
            return ( (HiveItem)child ).getConnection ();
        }
        else if ( child instanceof BrowserEntry )
        {
            return ( (BrowserEntry)child ).getParent ();
        }
        return null;
    }

    public Object[] getChildren ( final Object parent )
    {
        if ( parent instanceof HiveRepository )
        {
            return ( (HiveRepository)parent ).getConnections ().toArray ( new HiveConnection[0] );
        }
        else if ( parent instanceof HiveConnection )
        {
            final FolderEntry rootFolder = ( (HiveConnection)parent ).getRootFolder ();
            if ( rootFolder != null )
            {
                return rootFolder.getEntries ();
            }
            else
            {
                return new Object[0];
            }
        }
        else if ( parent instanceof FolderEntry )
        {
            return ( (FolderEntry)parent ).getEntries ();
        }
        return new Object[0];
    }

    public boolean hasChildren ( final Object parent )
    {
        if ( parent instanceof HiveRepository )
        {
            return ( (HiveRepository)parent ).getConnections ().size () > 0;
        }
        else if ( parent instanceof HiveConnection )
        {
            final FolderEntry rootFolder = ( (HiveConnection)parent ).getRootFolder ();
            if ( rootFolder != null )
            {
                return rootFolder.hasChildren ();
            }
            else
            {
                return false;
            }
        }
        else if ( parent instanceof FolderEntry )
        {
            return ( (FolderEntry)parent ).hasChildren ();
        }
        return false;
    }

}