package org.openscada.ae.monitor.common.handler.impl;

import java.util.Date;

import org.openscada.ae.ConditionStatus;
import org.openscada.core.Variant;

public class NotOkAknHandler extends StateAdapter
{

    public NotOkAknHandler ( final StateAdapter source )
    {
        super ( source, ConditionStatus.NOT_OK_AKN );
    }

    @Override
    public void ignoreAkn ()
    {
        switchHandler ( new NotOkHandler ( this ) );
    }

    @Override
    public void disable ()
    {
        switchHandler ( new InactiveHandler ( this, false ) );
    }

    @Override
    public void unsafe ()
    {
        publishUnsafeEvent ();
        setValue ( null, null );
        switchHandler ( new UnsafeHandler ( this ) );
    }

    @Override
    public void ok ( final Variant value, final Date timestamp )
    {
        publishOkEvent ( value );
        setValue ( value, timestamp );
        switchHandler ( new OkHandler ( this ) );
    }
}
