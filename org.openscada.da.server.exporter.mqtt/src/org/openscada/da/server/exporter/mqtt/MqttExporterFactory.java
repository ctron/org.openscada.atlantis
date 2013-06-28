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
import java.util.concurrent.ExecutorService;

import org.openscada.ca.common.factory.AbstractServiceConfigurationFactory;
import org.openscada.sec.UserInformation;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MqttExporterFactory extends AbstractServiceConfigurationFactory<MqttExporter>
{
    private final static Logger logger = LoggerFactory.getLogger ( MqttExporterFactory.class );

    private final ExecutorService executor;

    public MqttExporterFactory ( final BundleContext context, final ExecutorService executor )
    {
        super ( context );
        this.executor = executor;
    }

    @Override
    protected Entry<MqttExporter> createService ( final UserInformation userInformation, final String configurationId, final BundleContext context, final Map<String, String> parameters ) throws Exception
    {
        final MqttExporter mqttExporter = new MqttExporter ( context, this.executor );
        mqttExporter.update ( parameters );
        mqttExporter.start ();
        return new Entry<MqttExporter> ( configurationId, mqttExporter );
    }

    @Override
    protected void disposeService ( final UserInformation userInformation, final String configurationId, final MqttExporter mqttExporter )
    {
        try
        {
            mqttExporter.stop ();
        }
        catch ( final Exception e )
        {
            logger.error ( "error on disposing MqttExporter", e );
        }
    }

    @Override
    protected Entry<MqttExporter> updateService ( final UserInformation userInformation, final String configurationId, final Entry<MqttExporter> entry, final Map<String, String> parameters ) throws Exception
    {
        entry.getService ().stop ();
        entry.getService ().update ( parameters );
        entry.getService ().start ();
        return null;
    }

}
