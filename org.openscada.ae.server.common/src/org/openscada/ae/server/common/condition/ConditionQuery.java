package org.openscada.ae.server.common.condition;

import java.util.ArrayList;
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
            fireListener ( this.cachedData.values ().toArray ( new ConditionStatusInformation[0] ), null );
        }
    }

    private synchronized void fireListener ( final ConditionStatusInformation[] addedOrUpdated, final String[] removed )
    {
        if ( this.listener != null )
        {
            this.listener.dataChanged ( addedOrUpdated, removed );
        }
    }

    protected void updateData ( final ConditionStatusInformation[] data, final String[] removed )
    {
        synchronized ( this )
        {
            fireListener ( data, removed );
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
        clear ();
        this.listener = null;
    }

    /**
     * Set current data set. Will handle notifications accordingly.
     * @param data the new data set
     */
    protected synchronized void setData ( final ConditionStatusInformation[] data )
    {
        clear ();

        final ArrayList<ConditionStatusInformation> newData = new ArrayList<ConditionStatusInformation> ( data.length );
        for ( final ConditionStatusInformation ci : data )
        {
            newData.add ( ci );
            final ConditionStatusInformation oldCi = this.cachedData.put ( ci.getId (), ci );
            if ( oldCi != null )
            {
                newData.remove ( oldCi );
            }
        }
        fireListener ( newData.toArray ( new ConditionStatusInformation[newData.size ()] ), null );
    }

    protected synchronized void clear ()
    {
        fireListener ( null, this.cachedData.keySet ().toArray ( new String[0] ) );
        this.cachedData.clear ();
    }
}
