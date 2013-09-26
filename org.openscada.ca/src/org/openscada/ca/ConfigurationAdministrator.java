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

package org.openscada.ca;

import java.util.Collection;
import java.util.Map;

import org.eclipse.scada.ca.data.DiffEntry;
import org.eclipse.scada.sec.UserInformation;
import org.eclipse.scada.utils.concurrent.NotifyFuture;

public interface ConfigurationAdministrator
{
    public static final String FACTORY_ID = "factoryId";

    /* modifiers */

    public NotifyFuture<Configuration> createConfiguration ( UserInformation userInformation, String factoryId, String configurationId, Map<String, String> initialProperties );

    public NotifyFuture<Configuration> updateConfiguration ( UserInformation userInformation, String factoryId, String configurationId, Map<String, String> newProperties, boolean fullSet );

    public NotifyFuture<Configuration> deleteConfiguration ( UserInformation userInformation, String factoryId, String configurationId );

    public NotifyFuture<Void> purgeFactory ( UserInformation userInformation, String factoryId );

    /**
     * Applies a change set to an existing configuration manager.
     * <p>
     * The operation must be atomic to the configuration system.
     * </p>
     * <p>
     * Note that there are two or more entries for one factory/configuration
     * combination it is unspecified which entry will be applied.
     * </p>
     * 
     * @param changeSet
     *            the change set to apply
     * @return a future which notifies the end of the operation
     */
    public NotifyFuture<Void> applyDiff ( UserInformation userInformation, Collection<DiffEntry> changeSet );

    /* readers */

    public Factory getFactory ( String factoryId );

    public Factory[] getKnownFactories ();

    public Configuration[] getConfigurations ( String factoryId );

    public Configuration getConfiguration ( String factoryId, String configurationId );
}
