package org.openscada.core.ui.connection;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IAdapterFactory;

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
