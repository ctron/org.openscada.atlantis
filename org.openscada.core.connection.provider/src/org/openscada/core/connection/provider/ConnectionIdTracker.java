package org.openscada.core.connection.provider;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

public class ConnectionIdTracker extends ConnectionTracker
{

    private final String connectionId;

    public ConnectionIdTracker ( final BundleContext context, final String connectionId, final Listener listener )
    {
        this ( context, connectionId, listener, null );
    }

    public ConnectionIdTracker ( final BundleContext context, final String connectionId, final Listener listener, final Class<?> clazz )
    {
        super ( context, listener, clazz );
        this.connectionId = connectionId;
    }

    @Override
    protected Map<String, String> createFilterParameters ()
    {
        final Map<String, String> parameters = new HashMap<String, String> ();

        parameters.put ( Constants.SERVICE_PID, this.connectionId );

        return parameters;
    }

    public String getConnectionId ()
    {
        return this.connectionId;
    }
}
