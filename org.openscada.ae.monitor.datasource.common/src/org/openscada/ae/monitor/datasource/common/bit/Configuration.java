/*
 * This file is part of the openSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.ae.monitor.datasource.common.bit;

import org.openscada.ae.monitor.common.AbstractConfiguration;
import org.openscada.ae.monitor.common.Severity;
import org.openscada.ae.monitor.datasource.AbstractMasterItemMonitor;
import org.openscada.sec.UserInformation;

class Configuration extends AbstractConfiguration
{
    Boolean reference;

    Severity severity = Severity.ERROR;

    boolean requireAck;

    public Configuration ( final Configuration currentConfiguration, final AbstractMasterItemMonitor monitor )
    {
        super ( currentConfiguration, monitor );
        if ( currentConfiguration != null )
        {
            this.reference = currentConfiguration.reference;
            this.severity = currentConfiguration.severity;
            this.requireAck = currentConfiguration.requireAck;
        }
    }

    public void setRequireAck ( final UserInformation userInformation, final boolean requireAck )
    {
        this.requireAck = update ( userInformation, this.requireAck, requireAck );
    }

    public void setReference ( final UserInformation userInformation, final Boolean reference )
    {
        this.reference = update ( userInformation, this.reference, reference );
    }

    public void setSeverity ( final UserInformation userInformation, final Severity severity )
    {
        this.severity = update ( userInformation, this.severity, severity );
    }
}