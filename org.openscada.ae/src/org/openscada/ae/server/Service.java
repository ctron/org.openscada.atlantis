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

package org.openscada.ae.server;

import java.util.Date;

import org.openscada.ae.Query;
import org.openscada.ae.QueryListener;
import org.openscada.ae.UnknownQueryException;
import org.openscada.core.InvalidSessionException;

public interface Service extends org.openscada.core.server.Service
{
    // Event methods - online

    public void subscribeEventQuery ( Session session, String queryId ) throws InvalidSessionException, UnknownQueryException;

    public void unsubscribeEventQuery ( Session session, String queryId ) throws InvalidSessionException;

    // Event methods - offline

    public Query createQuery ( Session session, String queryType, String queryData, QueryListener listener ) throws InvalidSessionException;

    // Condition methods

    public void subscribeConditionQuery ( Session session, String queryId ) throws InvalidSessionException, UnknownQueryException;

    public void unsubscribeConditionQuery ( Session session, String queryId ) throws InvalidSessionException;

    public void acknowledge ( Session session, String conditionId, Date aknTimestamp ) throws InvalidSessionException;
}
