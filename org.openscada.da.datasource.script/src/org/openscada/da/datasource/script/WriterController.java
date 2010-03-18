package org.openscada.da.datasource.script;

import org.openscada.core.Variant;
import org.openscada.core.VariantEditor;
import org.openscada.core.connection.provider.ConnectionIdTracker;
import org.openscada.da.connection.provider.ConnectionService;
import org.osgi.framework.BundleContext;

public class WriterController
{
    private static final long DEFAULT_TIMEOUT = 10000;

    private final BundleContext context;

    public WriterController ( final BundleContext context )
    {
        this.context = context;
    }

    public void write ( final String connectionId, final String itemId, final Variant value )
    {
        final ConnectionIdTracker tracker = new ConnectionIdTracker ( this.context, connectionId, null );
        tracker.open ();
        try
        {
            tracker.waitForService ( DEFAULT_TIMEOUT );
            final org.openscada.da.connection.provider.ConnectionService service = (ConnectionService)tracker.getService ();
            service.getConnection ().write ( itemId, value, null );
        }
        catch ( final InterruptedException e )
        {
        }
        finally
        {
            tracker.close ();
        }
    }

    public void write ( final String connectionId, final String itemId, final String value )
    {
        final VariantEditor ve = new VariantEditor ();
        ve.setAsText ( value );
        write ( connectionId, itemId, (Variant)ve.getValue () );
    }
}
