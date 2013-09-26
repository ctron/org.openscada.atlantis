/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.ca.servlet.jaxws;

import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.jws.WebService;

import org.eclipse.scada.ca.data.ConfigurationInformation;
import org.eclipse.scada.ca.data.DiffEntry;
import org.eclipse.scada.ca.data.FactoryInformation;

@WebService
public interface RemoteConfigurationAdministrator
{
    public abstract boolean hasService ();

    /* read calls */

    /**
     * Get factory information without content
     * 
     * @return the factories without a content
     */
    public abstract FactoryInformation[] getFactories ();

    public abstract FactoryInformation[] getCompleteConfiguration ();

    public abstract FactoryInformation getFactory ( String factoryId );

    public abstract ConfigurationInformation getConfiguration ( String factoryId, String configurationId );

    /* update calls */

    public abstract void purge ( final String factoryId, final int timeout ) throws InterruptedException, ExecutionException, TimeoutException;

    public abstract void delete ( final String factoryId, final String[] configurations, final int timeout ) throws InterruptedException, ExecutionException, TimeoutException;

    public abstract void update ( final String factoryId, final ConfigurationInformation[] configurations, final int timeout ) throws InterruptedException, ExecutionException, TimeoutException;

    public abstract void create ( final String factoryId, final ConfigurationInformation[] configurations, final int timeout ) throws InterruptedException, ExecutionException, TimeoutException;

    public abstract void applyDiff ( Collection<DiffEntry> changeSet, int timeout ) throws InterruptedException, ExecutionException, TimeoutException;
}