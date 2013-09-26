/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
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

package org.openscada.ae.server;

import java.util.Date;

import org.eclipse.scada.core.InvalidSessionException;
import org.eclipse.scada.core.data.OperationParameters;
import org.eclipse.scada.sec.callback.CallbackHandler;
import org.eclipse.scada.utils.concurrent.NotifyFuture;
import org.openscada.ae.Query;
import org.openscada.ae.QueryListener;
import org.openscada.ae.UnknownQueryException;

public interface Service extends org.openscada.core.server.Service<Session>
{
    // Event methods - online

    public void subscribeEventQuery ( Session session, String queryId ) throws InvalidSessionException, UnknownQueryException;

    public void unsubscribeEventQuery ( Session session, String queryId ) throws InvalidSessionException;

    // Event methods - offline

    public Query createQuery ( Session session, String queryType, String queryData, QueryListener listener ) throws InvalidSessionException;

    // Condition methods

    public void subscribeConditionQuery ( Session session, String queryId ) throws InvalidSessionException, UnknownQueryException;

    public void unsubscribeConditionQuery ( Session session, String queryId ) throws InvalidSessionException;

    public NotifyFuture<Void> acknowledge ( Session session, String conditionId, Date aknTimestamp, OperationParameters operationParameters, CallbackHandler callbackHandler ) throws InvalidSessionException;
}
