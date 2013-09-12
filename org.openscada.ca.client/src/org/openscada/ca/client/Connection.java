/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.ca.client;

import java.util.List;

import org.eclipse.scada.sec.callback.CallbackHandler;
import org.eclipse.scada.utils.concurrent.NotifyFuture;
import org.openscada.ca.data.ConfigurationInformation;
import org.openscada.ca.data.DiffEntry;
import org.openscada.ca.data.FactoryInformation;
import org.openscada.core.data.OperationParameters;

public interface Connection extends org.openscada.core.client.Connection
{
    public void addFactoriesListener ( FactoriesListener listener );

    public void removeFactoriesListener ( FactoriesListener listener );

    public NotifyFuture<FactoryInformation[]> getFactories ();

    public NotifyFuture<FactoryInformation> getFactoryWithData ( String factoryId );

    public NotifyFuture<ConfigurationInformation> getConfiguration ( String factoryId, String configurationId );

    public NotifyFuture<Void> applyDiff ( final List<DiffEntry> changeSet, OperationParameters operationParameters, CallbackHandler callbackHandler );
}
