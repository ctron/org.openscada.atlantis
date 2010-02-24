/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2008-2010 inavare GmbH (http://inavare.com)
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

package org.openscada.ae.client;

import java.util.Date;

import org.openscada.ae.BrowserListener;
import org.openscada.ae.Query;
import org.openscada.ae.QueryListener;

/**
 * Interface for client connection
 * @author Jens Reimann
 * @since 0.15.0
 *
 */
public interface Connection extends org.openscada.core.client.Connection
{
    // Conditions
    /**
     * Set the listener that should receive condition updates
     */
    public void setConditionListener ( String conditionQueryId, ConditionListener listener );

    // Event - online
    /**
     * Set the listener that should receive event updates
     */
    public void setEventListener ( String eventQueryId, EventListener listener );

    // Event - offline
    public Query createQuery ( String queryType, String queryData, QueryListener listener );

    /**
     * Add browser listener
     * @param listener the listener to add
     */
    public void addBrowserListener ( BrowserListener listener );

    public void removeBrowserListener ( BrowserListener listener );

    /**
     * Acknowledge the condition if the akn state was reached at or before the provided timestamp
     * @param conditionId the id of the condition
     * @param aknTimestamp the timestamp up to which the state may be acknowledged
     */
    public void acknowledge ( String conditionId, Date aknTimestamp );
}
