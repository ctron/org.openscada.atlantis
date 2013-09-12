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

package org.openscada.ca.server;

import java.util.Collection;

import org.eclipse.scada.core.InvalidSessionException;
import org.eclipse.scada.sec.callback.CallbackHandler;
import org.eclipse.scada.utils.concurrent.NotifyFuture;
import org.openscada.ca.Configuration;
import org.openscada.ca.Factory;
import org.openscada.ca.data.DiffEntry;
import org.openscada.core.data.OperationParameters;

/**
 * This interface specifies the operations provided by the server
 * 
 * @author Jens Reimann
 * @since 1.0.0
 */
public interface Service extends org.openscada.core.server.Service<Session>
{
    public NotifyFuture<Void> applyDiff ( Session session, Collection<DiffEntry> changeSet, OperationParameters operationParameters, CallbackHandler callbackHandler ) throws InvalidSessionException;

    public NotifyFuture<FactoryWithData> getFactory ( Session session, String factoryId ) throws InvalidSessionException;

    public NotifyFuture<Configuration[]> getConfigurations ( Session session, String factoryId ) throws InvalidSessionException;

    public NotifyFuture<Factory[]> getKnownFactories ( Session session ) throws InvalidSessionException;

    public NotifyFuture<Configuration> getConfiguration ( Session session, String factoryId, String configurationId ) throws InvalidSessionException;
}