package org.openscada.core.ui.connection.data;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IAdapterFactory;
import org.openscada.core.ui.connection.ConnectionDiscoverer;
import org.openscada.core.ui.connection.ConnectionStore;

public class ConnectionDiscovererAdapterFactory implements IAdapterFactory
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
        return new Class[] { ConnectionStore.class, ConnectionDiscoverer.class };
    }

}
