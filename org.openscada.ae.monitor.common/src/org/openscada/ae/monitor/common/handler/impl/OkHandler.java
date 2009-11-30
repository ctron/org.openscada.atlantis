package org.openscada.ae.monitor.common.handler.impl;

import java.util.Date;

import org.openscada.ae.ConditionStatus;
import org.openscada.core.Variant;

public class OkHandler extends StateAdapter
{

    public OkHandler ( final StateAdapter source )
    {
        super ( source, ConditionStatus.OK );

    }

    @Override
    public void fail ( final Variant value, final Date timestamp )
    {
        publishFailEvent ( value );

        setValue ( value, timestamp );

        if ( this.currentContext.isRequireAkn () )
        {
            switchHandler ( new NotOkNotAknHandler ( this ) );
        }
        else
        {
            switchHandler ( new NotOkHandler ( this ) );
        }
    }

    @Override
    public void disable ()
    {
        switchHandler ( new InactiveHandler ( this, true ) );
    }

    @Override
    public void unsafe ()
    {
        publishUnsafeEvent ();
        setValue ( null, null );
        switchHandler ( new UnsafeHandler ( this ) );
    }
}
