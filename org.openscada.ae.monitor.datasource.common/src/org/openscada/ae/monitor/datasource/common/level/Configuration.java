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

package org.openscada.ae.monitor.datasource.common.level;

import org.openscada.ae.monitor.common.AbstractConfiguration;
import org.openscada.ae.monitor.common.Severity;
import org.openscada.ae.monitor.datasource.AbstractMasterItemMonitor;
import org.openscada.sec.UserInformation;

class Configuration extends AbstractConfiguration
{

    Double preset;

    boolean lowerOk;

    boolean includedOk;

    boolean cap;

    boolean requireAck;

    Severity severity;

    String secondPrefix;

    public Configuration ( final Configuration currentConfiguration, final AbstractMasterItemMonitor monitor )
    {
        super ( currentConfiguration, monitor );
        if ( currentConfiguration != null )
        {
            this.preset = currentConfiguration.preset;
            this.lowerOk = currentConfiguration.lowerOk;
            this.includedOk = currentConfiguration.includedOk;
            this.cap = currentConfiguration.cap;
            this.severity = currentConfiguration.severity;
            this.secondPrefix = currentConfiguration.secondPrefix;
            this.requireAck = currentConfiguration.requireAck;
        }
    }

    public void setRequireAck ( final UserInformation userInformation, final boolean requireAck )
    {
        this.requireAck = update ( userInformation, this.requireAck, requireAck );
    }

    public void setSeverity ( final UserInformation userInformation, final Severity severity )
    {
        this.severity = update ( userInformation, this.severity, severity );
    }

    public void setPreset ( final UserInformation userInformation, final Double preset )
    {
        this.preset = update ( userInformation, this.preset, preset );
    }

    public void setCap ( final UserInformation userInformation, final boolean cap )
    {
        this.cap = update ( userInformation, this.cap, cap );
    }

    public void setSecondPrefix ( final UserInformation userInformation, final String secondPrefix )
    {
        this.secondPrefix = secondPrefix;
    }

    public void setLowerOk ( final UserInformation userInformation, final boolean lowerOk )
    {
        this.lowerOk = update ( userInformation, this.lowerOk, lowerOk );
    }

    public void setIncludedOk ( final UserInformation userInformation, final boolean includedOk )
    {
        this.includedOk = update ( userInformation, this.includedOk, includedOk );
    }

}