package org.openscada.da.master.common.sum;

import org.openscada.da.master.common.CommonSumHandler;
import org.osgi.framework.BundleContext;

public class HandlerImpl extends CommonSumHandler
{

    public HandlerImpl ( final BundleContext context )
    {
        super ( context, "error", 0 );
    }

}
