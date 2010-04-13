package org.openscada.da.datasource.script;

import java.util.HashMap;
import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.core.VariantEditor;
import org.openscada.da.datasource.DataSource;
import org.openscada.da.datasource.WriteInformation;
import org.openscada.utils.osgi.FilterUtil;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.util.tracker.ServiceTracker;

public class WriterController
{
    private static final long DEFAULT_TIMEOUT = 10000;

    private final BundleContext context;

    public WriterController ( final BundleContext context )
    {
        this.context = context;
    }

    public void write ( final String dataSourceId, final Object value ) throws Exception
    {
        final Variant variant = Variant.valueOf ( value );

        final Filter filter = makeFilter ( dataSourceId );

        final ServiceTracker tracker = new ServiceTracker ( this.context, filter, null );

        tracker.open ();
        try
        {
            final DataSource source = (DataSource)tracker.waitForService ( DEFAULT_TIMEOUT );

            if ( source == null )
            {
                throw new IllegalStateException ( String.format ( "Failed to write. Service not found for filter '%s'", filter ) );
            }

            final WriteInformation writeInformation = new WriteInformation ( null );
            source.startWriteValue ( writeInformation, variant );
        }
        catch ( final InterruptedException e )
        {
        }
        finally
        {
            tracker.close ();
        }
    }

    private Filter makeFilter ( final String dataSourceId ) throws InvalidSyntaxException
    {
        final Map<String, String> parameters = new HashMap<String, String> ();
        parameters.put ( "datasource.id", dataSourceId );
        return FilterUtil.createAndFilter ( DataSource.class.getName (), parameters );
    }

    public void writeAsText ( final String itemId, final String value ) throws Exception
    {
        final VariantEditor ve = new VariantEditor ();
        ve.setAsText ( value );
        write ( itemId, ve.getValue () );
    }
}
