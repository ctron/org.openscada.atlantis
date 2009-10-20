package org.openscada.da.ui.connection;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IAdapterFactory;
import org.openscada.da.core.browser.DataItemEntry;
import org.openscada.da.core.browser.FolderEntry;
import org.openscada.da.ui.connection.data.Item;

public class FolderEntryWrapperAdapterFactory implements IAdapterFactory
{

    @SuppressWarnings ( "unchecked" )
    public Object getAdapter ( final Object adaptableObject, final Class adapterType )
    {
        if ( adaptableObject instanceof IAdaptable )
        {
            return ( (IAdaptable)adaptableObject ).getAdapter ( adapterType );
        }
        return null;
    }

    @SuppressWarnings ( "unchecked" )
    public Class[] getAdapterList ()
    {
        return new Class[] { DataItemEntry.class, FolderEntry.class, Item.class };
    }

}
