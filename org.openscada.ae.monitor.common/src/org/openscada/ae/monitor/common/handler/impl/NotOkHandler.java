package org.openscada.ae.monitor.common.handler.impl;

import java.util.Date;

import org.openscada.ae.ConditionStatus;
import org.openscada.core.Variant;

public class NotOkHandler extends StateAdapter
{

    public NotOkHandler ( final StateAdapter source )
    {
        super ( source, ConditionStatus.NOT_OK );
    }

    @Override
    public void disable ()
    {
        switchHandler ( new InactiveHandler ( this, false ) );
    }

    @Override
    public void ok ( final Variant value, final Date timestamp )
    {
        publishOkEvent ( value );

        setValue ( value, timestamp );
        switchHandler ( new OkHandler ( this ) );
    }

    @Override
    public void requireAkn ()
    {
        switchHandler ( new NotOkNotAknHandler ( this ) );
    }

    @Override
    public void unsafe ()
    {
        publishUnsafeEvent ();
        setValue ( null, null );
        switchHandler ( new UnsafeHandler ( this ) );
    }
}
