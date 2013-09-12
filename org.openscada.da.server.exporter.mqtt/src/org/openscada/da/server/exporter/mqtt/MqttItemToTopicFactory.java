/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2013 Jürgen Rose (cptmauli@googlemail.com)
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

package org.openscada.da.server.exporter.mqtt;

import java.util.Map;

import org.eclipse.scada.sec.UserInformation;
import org.eclipse.scada.utils.osgi.pool.ObjectPoolHelper;
import org.eclipse.scada.utils.osgi.pool.ObjectPoolImpl;
import org.openscada.ca.common.factory.AbstractServiceConfigurationFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class MqttItemToTopicFactory extends AbstractServiceConfigurationFactory<MqttItemToTopic>
{

    private final ObjectPoolImpl<MqttItemToTopic> objectPool;

    private final ServiceRegistration<?> poolRegistration;

    private final BundleContext context;

    public MqttItemToTopicFactory ( final BundleContext context )
    {
        super ( context );
        this.context = context;
        this.objectPool = new ObjectPoolImpl<MqttItemToTopic> ();
        this.poolRegistration = ObjectPoolHelper.registerObjectPool ( context, this.objectPool, MqttItemToTopic.class );
    }

    @Override
    protected Entry<MqttItemToTopic> createService ( final UserInformation userInformation, final String configurationId, final BundleContext context, final Map<String, String> parameters ) throws Exception
    {
        final MqttItemToTopic service = new MqttItemToTopic ();
        service.update ( parameters );

        this.objectPool.addService ( configurationId, service, null );

        return new Entry<MqttItemToTopic> ( configurationId, service );
    }

    @Override
    protected void disposeService ( final UserInformation userInformation, final String configurationId, final MqttItemToTopic service )
    {
        this.objectPool.removeService ( configurationId, service );
    }

    @Override
    protected Entry<MqttItemToTopic> updateService ( final UserInformation userInformation, final String configurationId, final Entry<MqttItemToTopic> entry, final Map<String, String> parameters ) throws Exception
    {
        disposeService ( userInformation, configurationId, entry.getService () );
        return createService ( userInformation, configurationId, this.context, parameters );
    }

    @Override
    public synchronized void dispose ()
    {
        this.poolRegistration.unregister ();
        this.objectPool.dispose ();
        super.dispose ();
    }
}
