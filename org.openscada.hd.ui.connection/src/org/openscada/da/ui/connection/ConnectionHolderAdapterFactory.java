package org.openscada.da.ui.connection;

import org.eclipse.core.runtime.IAdapterFactory;
import org.openscada.core.ui.connection.data.ConnectionHolder;
import org.openscada.da.client.connection.service.ConnectionService;

public class ConnectionHolderAdapterFactory implements IAdapterFactory
{

    @SuppressWarnings ( "unchecked" )
    public Object getAdapter ( final Object adaptableObject, final Class adapterType )
    {
        if ( adapterType == ConnectionService.class && adaptableObject instanceof ConnectionHolder )
        {
            final ConnectionHolder holder = (ConnectionHolder)adaptableObject;
            final org.openscada.core.connection.provider.ConnectionService service = holder.getConnectionService ();
            if ( service instanceof ConnectionService )
            {
                return service;
            }
        }
        return null;
    }

    @SuppressWarnings ( "unchecked" )
    public Class[] getAdapterList ()
    {
        return new Class[] { ConnectionService.class };
    }

}
