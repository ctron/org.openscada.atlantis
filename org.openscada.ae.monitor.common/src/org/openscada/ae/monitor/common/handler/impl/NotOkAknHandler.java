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
        super.ignoreAkn ();
        switchHandler ( new NotOkHandler ( this ) );
    }

    @Override
    public void requireAkn ()
    {
        // nothing to do here
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
        setValue ( value, timestamp );
        publishOkEvent ();

        switchHandler ( new OkHandler ( this ) );
    }
}
