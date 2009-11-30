package org.openscada.ae.monitor.common.handler.impl;

import org.openscada.ae.monitor.common.AbstractConditionService;

public class InitHandler extends UnsafeHandler
{

    public InitHandler ( final AbstractConditionService service )
    {
        super ( service, new Context () );
        setValue ( null, null );
    }

    @Override
    public void activate ()
    {
        super.activate ();
    }

}
