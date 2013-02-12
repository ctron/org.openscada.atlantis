/*
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
 *
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.ae.monitor.datasource.common.list;

import java.util.Map;

import org.openscada.ae.monitor.common.AbstractConfiguration;
import org.openscada.ae.monitor.datasource.AbstractMasterItemMonitor;
import org.openscada.ae.monitor.datasource.common.ListSeverity;
import org.openscada.core.Variant;

class Configuration extends AbstractConfiguration
{
    ListSeverity defaultSeverity;

    boolean defaultAck;

    Map<Variant, ListSeverity> severityMap;

    Map<Variant, Boolean> ackMap;

    String messageAttribute;

    public Configuration ( final Configuration currentConfiguration, final AbstractMasterItemMonitor monitor )
    {
        super ( currentConfiguration, monitor );
        if ( currentConfiguration != null )
        {
            this.defaultSeverity = currentConfiguration.defaultSeverity;
            this.defaultAck = currentConfiguration.defaultAck;
            this.severityMap = currentConfiguration.severityMap;
            this.ackMap = currentConfiguration.ackMap;
            this.messageAttribute = currentConfiguration.messageAttribute;
        }
    }

    public void setMessageAttribute ( final String messageAttribute )
    {
        this.messageAttribute = messageAttribute;
    }

    public void setDefaultAck ( final boolean defaultAck )
    {
        this.defaultAck = defaultAck;
    }

    public void setDefaultSeverity ( final ListSeverity defaultSeverity )
    {
        this.defaultSeverity = defaultSeverity;
    }

    public void setAckMap ( final Map<Variant, Boolean> ackMap )
    {
        this.ackMap = ackMap;
    }

    public void setSeverityMap ( final Map<Variant, ListSeverity> severityMap )
    {
        this.severityMap = severityMap;
    }
}