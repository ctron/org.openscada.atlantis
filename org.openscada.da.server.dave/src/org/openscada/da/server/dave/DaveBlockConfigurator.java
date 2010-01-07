package org.openscada.da.server.dave;

import java.util.HashMap;
import java.util.Map;

import org.openscada.protocols.dave.DaveReadRequest.Request;
import org.openscada.utils.osgi.FilterUtil;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DaveBlockConfigurator
{

    private final static Logger logger = LoggerFactory.getLogger ( DaveBlockConfigurator.class );

    private final DaveDevice device;

    private final BundleContext context;

    private ServiceTracker tracker;

    private final Map<ServiceReference, String> blocks = new HashMap<ServiceReference, String> ();

    public DaveBlockConfigurator ( final DaveDevice device, final BundleContext context )
    {
        this.device = device;
        this.context = context;

        final Map<String, String> parameters = new HashMap<String, String> ();
        parameters.put ( "daveDevice", device.getId () );
        try
        {
            final Filter filter = FilterUtil.createAndFilter ( BlockConfiguration.class.getName (), parameters );
            this.tracker = new ServiceTracker ( context, filter, new ServiceTrackerCustomizer () {

                public void removedService ( final ServiceReference reference, final Object service )
                {
                    if ( service instanceof BlockConfiguration )
                    {
                        if ( DaveBlockConfigurator.this.removeBlock ( reference, (BlockConfiguration)service ) )
                        {
                            context.ungetService ( reference );
                        }
                    }
                }

                public void modifiedService ( final ServiceReference reference, final Object service )
                {
                    DaveBlockConfigurator.this.modifyBlock ( reference, (BlockConfiguration)service );
                }

                public Object addingService ( final ServiceReference reference )
                {
                    final Object o = DaveBlockConfigurator.this.context.getService ( reference );
                    try
                    {
                        DaveBlockConfigurator.this.addBlock ( reference, (BlockConfiguration)o );
                        return o;
                    }
                    catch ( final Throwable e )
                    {
                        logger.warn ( "Failed to add block", e );
                        return o;
                    }
                }
            } );
        }
        catch ( final Exception e )
        {
        }
        if ( this.tracker != null )
        {
            this.tracker.open ();
        }
    }

    protected void modifyBlock ( final ServiceReference reference, final BlockConfiguration service )
    {
        // will be a quick remove and add operation
        addBlock ( reference, service );
    }

    protected boolean removeBlock ( final ServiceReference reference, final BlockConfiguration block )
    {
        final String oldBlock = this.blocks.remove ( reference );
        if ( oldBlock != null )
        {
            this.device.removeBlock ( oldBlock );
            return true;
        }
        return false;
    }

    protected void addBlock ( final ServiceReference reference, final BlockConfiguration block )
    {
        logger.info ( String.format ( "Adding block - ref: %s, block: %s", new Object[] { reference, block } ) );

        final String oldBlock = this.blocks.put ( reference, block.getId () );

        if ( oldBlock != null )
        {
            logger.info ( "Replacing exisiting block" );
            this.device.removeBlock ( oldBlock );
        }

        final DaveRequestBlock deviceBlock = makeBlock ( block );
        try
        {
            this.device.addBlock ( block.getId (), deviceBlock );
        }
        catch ( final Exception e )
        {
            logger.warn ( "Failed to add block", e );
            deviceBlock.dispose ();
        }
    }

    private DaveRequestBlock makeBlock ( final BlockConfiguration block )
    {
        final Request request = new Request ( (byte)block.getArea (), (short)block.getBlock (), (short)block.getStart (), (short)block.getCount () );

        final DaveRequestBlock deviceBlock = new DaveRequestBlock ( block.getId (), block.getName (), this.device, this.context, request, block.isEnableStatistics (), block.getPeriod () );
        new DaveRequestBlockConfigurator ( this.device.getExecutor (), deviceBlock, block.getType () );
        return deviceBlock;
    }

    public void dispose ()
    {
        if ( this.tracker != null )
        {
            this.tracker.close ();
        }
    }

}
