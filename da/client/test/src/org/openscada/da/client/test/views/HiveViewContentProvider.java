/**
 * 
 */
package org.openscada.da.client.test.views;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.openscada.da.client.test.impl.BrowserEntry;
import org.openscada.da.client.test.impl.FolderEntry;
import org.openscada.da.client.test.impl.HiveConnection;
import org.openscada.da.client.test.impl.HiveItem;
import org.openscada.da.client.test.impl.HiveRepository;

class HiveViewContentProvider implements IStructuredContentProvider, ITreeContentProvider
{

    /**
     * 
     */
    private Viewer _viewer = null;
    private HiveRepository _repository = null;
    
    
    public void inputChanged ( Viewer v, Object oldInput, Object newInput )
    {
        clearInput ();
        
        _viewer = v;
        if ( newInput instanceof HiveRepository )
        {
            _repository = (HiveRepository)newInput;
        }
    }
    
    public void clearInput ()
    {
        if ( _repository != null )
        {
            _repository = null;
        }
    }
    
    public void dispose()
    {
        clearInput ();
    }
    
    public Object[] getElements ( Object parent )
    {
        if ( parent == null )
            return null;

        return getChildren ( parent );
    }
    
    public Object getParent ( Object child )
    {
        if (child instanceof HiveConnection)
        {
            return _repository;
        }
        else if ( child instanceof HiveItem )
        {
            return ((HiveItem)child).getConnection();
        }
        else if ( child instanceof BrowserEntry )
        {
            return ((BrowserEntry)child).getParent ();
        }
        return null;
    }
    public Object [] getChildren(Object parent)
    {
        if ( parent instanceof HiveRepository )
        {
            return((HiveRepository)parent).getConnections().toArray(new HiveConnection[0]);
        }
        else if ( parent instanceof HiveConnection )
        {
            FolderEntry rootFolder = ((HiveConnection)parent).getRootFolder ();
            if ( rootFolder != null )
                return rootFolder.getEntries ();
            else
                return new Object [0];
        }
        else if ( parent instanceof FolderEntry )
        {
            return ((FolderEntry)parent).getEntries ();
        }
        return new Object[0];
    }
    public boolean hasChildren(Object parent)
    {
        if (parent instanceof HiveRepository)
        {
            return ((HiveRepository)parent).getConnections().size() > 0;
        }
        else if ( parent instanceof HiveConnection )
        {
            FolderEntry rootFolder = ((HiveConnection)parent).getRootFolder ();
            if ( rootFolder != null )
                return rootFolder.hasChildren ();
            else
                return false;
        }
        else if ( parent instanceof FolderEntry )
        {
            return ((FolderEntry)parent).hasChildren ();
        }
        return false;
    }

}