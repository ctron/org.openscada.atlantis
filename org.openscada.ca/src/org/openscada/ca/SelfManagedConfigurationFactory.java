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

package org.openscada.ca;

import java.util.Map;

import org.openscada.utils.concurrent.NotifyFuture;

public interface SelfManagedConfigurationFactory
{
    /**
     * Add a new configuration listener to the factory
     * <p>
     * If the listener is already registered the method has no effect.
     * </p>
     * <p>
     * The listener is added to the factory and will receive updates from now on.
     * Before the method return it will call the listener once with the already
     * known configuration element using an "add" notification.
     * </p> 
     * @param listener The new listener to add
     */
    public void addConfigurationListener ( ConfigurationListener listener );

    /**
     * Remove a listener from the factory
     * <p>
     * If the listener is not currently attache to the factory the call has no effect.
     * </p>
     * @param listener
     */
    public void removeConfigurationListener ( ConfigurationListener listener );

    public NotifyFuture<Configuration> update ( String configurationId, Map<String, String> properties, boolean fullSet );

    public NotifyFuture<Configuration> delete ( String configurationId );

    /**
     * Delete all configurations at once
     * @return the future for this operation
     */
    public NotifyFuture<Void> purge ();
}
