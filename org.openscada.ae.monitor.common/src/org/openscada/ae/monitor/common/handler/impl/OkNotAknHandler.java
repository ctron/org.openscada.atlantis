package org.openscada.ae.monitor.common.handler.impl;

import java.util.Date;

import org.openscada.ae.ConditionStatus;
import org.openscada.core.Variant;
import org.openscada.sec.UserInformation;

public class OkNotAknHandler extends StateAdapter
{

    public OkNotAknHandler ( final StateAdapter source )
    {
        super ( source, ConditionStatus.NOT_AKN );
    }

    @Override
    public void akn ( final UserInformation aknUser, final Date aknTimestamp )
    {
        setAknInformation ( aknUser, aknTimestamp );
        publishAknEvent ();
        switchHandler ( new OkHandler ( this ) );
    }

    @Override
    public void fail ( final Variant value, final Date timestamp )
    {
        setValue ( value, timestamp );
        publishFailEvent ();

        switchHandler ( new NotOkNotAknHandler ( this ) );
    }

    @Override
    public void ignoreAkn ()
    {
        super.ignoreAkn ();
        switchHandler ( new OkHandler ( this ) );
    }

    @Override
    public void unsafe ()
    {
        publishUnsafeEvent ();
        setValue ( null, null );
        switchHandler ( new UnsafeHandler ( this ) );
    }

}
