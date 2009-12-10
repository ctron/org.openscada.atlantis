package org.openscada.ae.monitor.common.handler.impl;

import java.util.Date;

import org.openscada.ae.Event;
import org.openscada.ae.Event.EventBuilder;
import org.openscada.ae.monitor.common.AbstractConditionService;

public class InitHandler extends UnsafeHandler
{

    public InitHandler ( final AbstractConditionService service )
    {
        super ( service, new Context () );

        final EventBuilder builder = Event.create ();
        builder.sourceTimestamp ( new Date () );
        builder.attribute ( Event.Fields.SOURCE.getName (), service.getId () );
        builder.attribute ( Event.Fields.EVENT_TYPE.getName (), "INIT" );
        builder.attribute ( "message", "Initializing monitor" );
        service.publishEvent ( builder.build () );
        setValue ( null, null );
    }

    @Override
    public void activate ()
    {
        super.activate ();
    }

}
