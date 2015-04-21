/*******************************************************************************
 * Copyright (c) 2015 IBH SYSTEMS GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBH SYSTEMS GmbH - initial API and implementation
 *******************************************************************************/
package org.openscada.da.server.opc.xmlda;

import java.util.Map;

import org.eclipse.scada.ca.common.factory.AbstractServiceConfigurationFactory;
import org.eclipse.scada.sec.UserInformation;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

public class ConfigurationFactory extends AbstractServiceConfigurationFactory<ServerConnection>
{
    private final Hive hive;

    public ConfigurationFactory ( final Hive hive )
    {
        super ( FrameworkUtil.getBundle ( ConfigurationFactory.class ).getBundleContext (), true );

        this.hive = hive;
    }

    @Override
    protected Entry<ServerConnection> createService ( final UserInformation userInformation, final String configurationId, final BundleContext context, final Map<String, String> parameters ) throws Exception
    {
        final ServerConnection service = this.hive.addServer ( configurationId, parameters );
        return new Entry<ServerConnection> ( configurationId, service );
    }

    @Override
    protected void disposeService ( final UserInformation userInformation, final String configurationId, final ServerConnection service )
    {
        this.hive.removeServer ( configurationId );
    }

    @Override
    protected Entry<ServerConnection> updateService ( final UserInformation userInformation, final String configurationId, final Entry<ServerConnection> entry, final Map<String, String> parameters ) throws Exception
    {
        return null; // we are "create only"
    }

}
