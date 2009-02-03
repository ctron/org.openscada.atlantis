/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openscada.ae.client.net.test;

import java.util.Map;

import org.apache.log4j.Logger;
import org.openscada.ae.core.EventInformation;
import org.openscada.ae.core.Listener;
import org.openscada.core.Variant;

public class DumpListener implements Listener
{
    private static Logger logger = Logger.getLogger ( DumpListener.class );

    public void events ( final EventInformation[] events )
    {
        logger.debug ( String.format ( "Received events: %d", events.length ) );
        for ( final EventInformation event : events )
        {
            logger.debug ( String.format ( "============================================" ) );
            logger.debug ( String.format ( "Action: %1$d", event.getAction () ) );
            logger.debug ( String.format ( "Timestamp: %1$TF %1$TT", event.getTimestamp () ) );
            logger.debug ( String.format ( "\tId: %s", event.getEvent ().getId () ) );
            logger.debug ( String.format ( "\tTimestamp: %1$TF %1$TT", event.getEvent ().getTimestamp () ) );
            for ( final Map.Entry<String, Variant> entry : event.getEvent ().getAttributes ().entrySet () )
            {
                logger.debug ( String.format ( "\t\t'%1$s'=>'%2$s'", entry.getKey (), entry.getValue ().toString () ) );
            }
        }
    }

    public void unsubscribed ( final String reason )
    {
        logger.debug ( String.format ( "LongRunningListener unsubscribed: %s", reason ) );
    }

}
