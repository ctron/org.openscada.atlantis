package org.openscada.ae.event.logger.internal;

import java.util.Date;
import java.util.Map;

import org.openscada.ae.Event;
import org.openscada.ae.Event.EventBuilder;
import org.openscada.ae.Event.Fields;
import org.openscada.ae.event.EventProcessor;
import org.openscada.ca.ConfigurationDataHelper;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.datasource.WriteInformation;
import org.openscada.da.master.AbstractMasterHandlerImpl;
import org.openscada.da.master.WriteRequest;
import org.openscada.da.master.WriteRequestResult;
import org.openscada.sec.UserInformation;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;

public class MasterItemLogger extends AbstractMasterHandlerImpl
{

    private final EventProcessor eventProcessor;

    private String source;

    private String itemId;

    public MasterItemLogger ( final BundleContext context, final ObjectPoolTracker poolTracker, final int priority ) throws InvalidSyntaxException
    {
        super ( poolTracker, priority );
        synchronized ( this )
        {
            this.eventProcessor = new EventProcessor ( context );
            this.eventProcessor.open ();
        }
    }

    @Override
    public synchronized void dispose ()
    {
        this.eventProcessor.close ();
        super.dispose ();
    }

    @Override
    public DataItemValue dataUpdate ( final Map<String, Object> context, final DataItemValue value )
    {
        return null;
    }

    @Override
    public synchronized void update ( final Map<String, String> parameters ) throws Exception
    {
        final ConfigurationDataHelper cfg = new ConfigurationDataHelper ( parameters );
        final String source = cfg.getStringChecked ( "source", "'source' must be set" );
        final String itemId = cfg.getString ( "item.id" );

        super.update ( parameters );

        this.source = source;
        this.itemId = itemId;
    }

    @Override
    public WriteRequestResult processWrite ( final WriteRequest request )
    {
        if ( request.getValue () != null )
        {
            final EventBuilder builder = Event.create ();
            builder.sourceTimestamp ( new Date () );
            builder.attribute ( Event.Fields.SOURCE, this.source );
            if ( this.itemId != null )
            {
                builder.attribute ( Event.Fields.ITEM, this.itemId );
            }
            builder.attribute ( Event.Fields.EVENT_TYPE, "WRITE" );
            builder.attribute ( Event.Fields.MONITOR_TYPE, "LOG" );
            builder.attribute ( Event.Fields.VALUE, request.getValue () );
            builder.attribute ( Event.Fields.MESSAGE, "Write main value" );

            final WriteInformation wi = request.getWriteInformation ();
            if ( wi != null )
            {
                final UserInformation ui = wi.getUserInformation ();
                if ( ui != null )
                {
                    builder.attribute ( Fields.ACTOR_NAME, ui.getName () );
                    builder.attribute ( Fields.ACTOR_TYPE, "USER" );
                }
            }

            this.eventProcessor.publishEvent ( builder.build () );
        }
        return null;
    }

}
