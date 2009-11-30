package org.openscada.ae.monitor.common.handler.impl;

import java.util.Date;

import org.openscada.ae.ConditionStatus;
import org.openscada.core.Variant;

public class NotOkNotAknHandler extends StateAdapter
{

    public NotOkNotAknHandler ( final StateAdapter source )
    {
        super ( source, ConditionStatus.NOT_OK_NOT_AKN );
    }

    @Override
    public void akn ( final String aknUser, final Date aknTimestamp )
    {
        setAknInformation ( aknUser, aknTimestamp );
        publishAknEvent ();
        switchHandler ( new NotOkAknHandler ( this ) );
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
        setValue ( value, timestamp );
        switchHandler ( new OkNotAknHandler ( this ) );
    }
}
