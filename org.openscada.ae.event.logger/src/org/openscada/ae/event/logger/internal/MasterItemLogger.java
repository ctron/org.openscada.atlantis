package org.openscada.ae.event.logger.internal;

import java.util.Date;

import org.openscada.ae.Event;
import org.openscada.ae.Event.EventBuilder;
import org.openscada.ae.event.EventProcessor;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.master.AbstractMasterHandlerImpl;
import org.openscada.da.master.WriteRequest;
import org.openscada.da.master.WriteRequestResult;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;

public class MasterItemLogger extends AbstractMasterHandlerImpl
{

    private final EventProcessor eventProcessor;

    public MasterItemLogger ( final BundleContext context, final int priority ) throws InvalidSyntaxException
    {
        super ( context, priority );
        this.eventProcessor = new EventProcessor ( context );
        this.eventProcessor.open ();
    }

    @Override
    public synchronized void dispose ()
    {
        this.eventProcessor.close ();
        super.dispose ();
    }

    @Override
    public DataItemValue dataUpdate ( final DataItemValue value )
    {
        return null;
    }

    @Override
    public WriteRequestResult processWrite ( final WriteRequest request )
    {
        if ( request.getValue () != null )
        {
            final EventBuilder builder = Event.create ();
            builder.sourceTimestamp ( new Date () );
            builder.attribute ( Event.Fields.SOURCE.getName (), getMasterId () );
            builder.attribute ( Event.Fields.EVENT_TYPE.getName (), "WRITE" );
            builder.attribute ( "value", request.getValue () );
            builder.attribute ( "message", "Write main value" );
            this.eventProcessor.publishEvent ( builder.build () );
        }
        return null;
    }

}
