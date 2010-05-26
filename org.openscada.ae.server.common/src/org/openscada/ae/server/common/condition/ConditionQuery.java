/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.ae.server.common.condition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openscada.ae.MonitorStatusInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConditionQuery
{
    private final static Logger logger = LoggerFactory.getLogger ( ConditionQuery.class );

    private ConditionQueryListener listener;

    private final Map<String, MonitorStatusInformation> cachedData;

    public ConditionQuery ()
    {
        this.cachedData = new HashMap<String, MonitorStatusInformation> ();
    }

    public synchronized void setListener ( final ConditionQueryListener listener )
    {
        this.listener = listener;
        fireListener ( this.cachedData.values ().toArray ( new MonitorStatusInformation[0] ), null );
    }

    private synchronized void fireListener ( final MonitorStatusInformation[] addedOrUpdated, final String[] removed )
    {
        if ( this.listener != null )
        {
            this.listener.dataChanged ( addedOrUpdated, removed );
        }
    }

    protected synchronized void updateData ( final MonitorStatusInformation[] data, final String[] removed )
    {
        if ( data != null )
        {
            for ( final MonitorStatusInformation info : data )
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
    protected synchronized void setData ( final MonitorStatusInformation[] data )
    {
        logger.debug ( "Set new data: {}", data.length );

        clear ();

        final ArrayList<MonitorStatusInformation> newData = new ArrayList<MonitorStatusInformation> ( data.length );
        for ( final MonitorStatusInformation ci : data )
        {
            newData.add ( ci );
            final MonitorStatusInformation oldCi = this.cachedData.put ( ci.getId (), ci );
            if ( oldCi != null )
            {
                newData.remove ( oldCi );
            }
        }
        fireListener ( newData.toArray ( new MonitorStatusInformation[newData.size ()] ), null );
    }

    protected synchronized void clear ()
    {
        fireListener ( null, this.cachedData.keySet ().toArray ( new String[0] ) );
        this.cachedData.clear ();
    }
}
