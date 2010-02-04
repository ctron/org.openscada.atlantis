package org.openscada.ae.monitor.common.handler.impl;

import java.util.Date;

import org.openscada.ae.ConditionStatus;
import org.openscada.core.Variant;
import org.openscada.sec.UserInformation;

public class NotOkNotAknHandler extends StateAdapter
{

    public NotOkNotAknHandler ( final StateAdapter source )
    {
        super ( source, ConditionStatus.NOT_OK_NOT_AKN );
    }

    @Override
    public void akn ( final UserInformation aknUser, final Date aknTimestamp )
    {
        setAknInformation ( aknUser, aknTimestamp );
        publishAknEvent ();
        switchHandler ( new NotOkAknHandler ( this ) );
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

        switchHandler ( new OkNotAknHandler ( this ) );
    }
}
