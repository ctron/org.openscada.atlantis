package org.openscada.ae.monitor.common.handler.impl;

import java.util.Date;

import org.openscada.ae.ConditionStatus;
import org.openscada.core.Variant;

public class InactiveHandler extends StateAdapter
{

    private Boolean currentState;

    public InactiveHandler ( final StateAdapter source, final Boolean currentState )
    {
        super ( source, ConditionStatus.INACTIVE );
        this.currentState = currentState;
    }

    @Override
    public void enable ()
    {
        if ( this.currentState == null )
        {
            switchHandler ( new UnsafeHandler ( this ) );
        }
        else if ( this.currentState )
        {
            switchHandler ( new OkHandler ( this ) );
        }
        else
        {
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

    @Override
    public void ok ( final Variant value, final Date timestamp )
    {
        setValue ( value, timestamp );
        this.currentState = true;
    }

    @Override
    public void fail ( final Variant value, final Date timestamp )
    {
        setValue ( value, timestamp );
        this.currentState = false;
    }

    @Override
    public void unsafe ()
    {
        this.currentState = null;
    }
}
