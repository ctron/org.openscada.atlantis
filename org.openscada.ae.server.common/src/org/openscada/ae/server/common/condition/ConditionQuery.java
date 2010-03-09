package org.openscada.ae.server.common.condition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openscada.ae.ConditionStatusInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConditionQuery
{
    private final static Logger logger = LoggerFactory.getLogger ( ConditionQuery.class );

    private ConditionQueryListener listener;

    private final Map<String, ConditionStatusInformation> cachedData;

    public ConditionQuery ()
    {
        this.cachedData = new HashMap<String, ConditionStatusInformation> ();
    }

    public synchronized void setListener ( final ConditionQueryListener listener )
    {
        this.listener = listener;
        fireListener ( this.cachedData.values ().toArray ( new ConditionStatusInformation[0] ), null );
    }

    private synchronized void fireListener ( final ConditionStatusInformation[] addedOrUpdated, final String[] removed )
    {
        if ( this.listener != null )
        {
            this.listener.dataChanged ( addedOrUpdated, removed );
        }
    }

    protected synchronized void updateData ( final ConditionStatusInformation[] data, final String[] removed )
    {
        if ( data != null )
        {
            for ( final ConditionStatusInformation info : data )
            {
                this.cachedData.put ( info.getId (), info );
            }
        }
        final Set<String> removedItems = new HashSet<String> ();
        if ( removed != null )
        {
            for ( final String entry : removed )
            {
                if ( this.cachedData.remove ( entry ) != null )
                {
                    removedItems.add ( entry );
                }
            }
        }
        fireListener ( data, removedItems.toArray ( new String[removedItems.size ()] ) );
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
        logger.debug ( "Set new data: {}", data.length );

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
