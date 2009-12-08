package org.openscada.ae.monitor.common.handler.impl;

import java.util.Date;

import org.openscada.ae.ConditionStatus;
import org.openscada.ae.monitor.common.AbstractConditionService;
import org.openscada.core.Variant;

public class UnsafeHandler extends StateAdapter
{

    public UnsafeHandler ( final StateAdapter source )
    {
        super ( source, ConditionStatus.UNSAFE );
    }

    protected UnsafeHandler ( final AbstractConditionService service, final Context context )
    {
        super ( service, context, ConditionStatus.UNSAFE );
    }

    @Override
    public void ok ( final Variant value, final Date timestamp )
    {
        setValue ( value, timestamp );
        publishOkEvent ();

        switchHandler ( new OkHandler ( this ) );
    }

    @Override
    public void fail ( final Variant value, final Date timestamp )
    {
        setValue ( value, timestamp );
        publishFailEvent ();

        if ( this.currentContext.isRequireAkn () )
        {
            switchHandler ( new NotOkNotAknHandler ( this ) );
        }
        else
        {
            switchHandler ( new NotOkHandler ( this ) );
        }
    }

}
