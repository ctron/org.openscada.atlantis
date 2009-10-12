package org.openscada.core.ui.connection.data;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IAdapterFactory;
import org.openscada.core.connection.provider.ConnectionService;

public class ConnectionHolderAdapterFactory implements IAdapterFactory
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
        return new Class[] { ConnectionService.class };
    }

}
