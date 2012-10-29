/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.ae.client;

import java.util.Date;

import org.openscada.ae.BrowserListener;
import org.openscada.ae.Query;
import org.openscada.ae.QueryListener;
import org.openscada.sec.UserInformation;

/**
 * Interface for client connection
 * 
 * @author Jens Reimann
 * @since 0.15.0
 */
public interface Connection extends org.openscada.core.client.Connection
{
    // Monitors
    /**
     * Set the listener that should receive monitor updates
     */
    public void setMonitorListener ( String monitorQueryId, MonitorListener listener );

    // Event - online
    /**
     * Set the listener that should receive event updates
     */
    public void setEventListener ( String eventQueryId, EventListener listener );

    // Event - offline
    public Query createQuery ( String queryType, String queryData, QueryListener listener );

    /**
     * Add browser listener
     * 
     * @param listener
     *            the listener to add
     */
    public void addBrowserListener ( BrowserListener listener );

    public void removeBrowserListener ( BrowserListener listener );

    /**
     * Acknowledge the monitor if the akn state was reached at or before the
     * provided timestamp
     * 
     * @param monitorId
     *            the id of the condition
     * @param aknTimestamp
     *            the timestamp up to which the state may be acknowledged
     * @param userInformation
     *            optionally provide some user information which performs the
     *            aknowledgement. If the information is not provided the current
     *            logged in user will be used. If the information is present but
     *            the current logged in user is not allowed to change the user
     *            performing acknowledgement the server may fall back to use the
     *            actual information of the logged in user instead.
     */
    public void acknowledge ( String monitorId, Date aknTimestamp, UserInformation userInformation );
}
