package org.openscada.ae.server.common.condition;

import java.util.HashMap;
import java.util.Map;

import org.openscada.ae.ConditionStatusInformation;

public class ConditionQuery
{
    private ConditionQueryListener listener;

    private final Map<String, ConditionStatusInformation> cachedData;

    public ConditionQuery ()
    {
        this.cachedData = new HashMap<String, ConditionStatusInformation> ();
    }

    public void setListener ( final ConditionQueryListener listener )
    {
        synchronized ( this )
        {
            this.listener = listener;
            if ( this.listener != null )
            {
                this.listener.dataChanged ( this.cachedData.values ().toArray ( new ConditionStatusInformation[0] ), null );
            }
        }
    }

    protected void updateData ( final ConditionStatusInformation[] data, final String[] removed )
    {
        synchronized ( this )
        {
            if ( this.listener != null )
            {
                this.listener.dataChanged ( data, removed );
            }
            if ( data != null )
            {
                for ( final ConditionStatusInformation info : data )
                {
                    this.cachedData.put ( info.getId (), info );
                }
            }
            if ( removed != null )
            {
                for ( final String entry : removed )
                {
                    this.cachedData.remove ( entry );
                }
            }
        }
    }

    public synchronized void dispose ()
    {
        if ( this.listener != null )
        {
            this.listener.dataChanged ( null, this.cachedData.keySet ().toArray ( new String[0] ) );
        }
        this.cachedData.clear ();
        this.listener = null;
    }
}
