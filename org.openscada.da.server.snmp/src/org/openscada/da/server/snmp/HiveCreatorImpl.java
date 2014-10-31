/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openscada.da.server.snmp;

import org.eclipse.scada.da.core.server.Hive;
import org.eclipse.scada.da.core.server.HiveCreator;
import org.openscada.da.server.snmp.mib.MibManager;
import org.openscada.da.snmp.configuration.ConfigurationType;

public class HiveCreatorImpl implements HiveCreator
{
    private MibManager mibManager;

    public void setMibManager ( final MibManager mibManager )
    {
        this.mibManager = mibManager;
    }

    public void unsetMibManager ( final MibManager mibManager )
    {
        if ( this.mibManager == mibManager )
        {
            this.mibManager = null;
        }
    }

    @Override
    public Hive createHive ( final String reference, final Object configuration ) throws Exception
    {
        if ( !reference.equals ( org.openscada.da.server.snmp.Hive.class.getName () ) )
        {
            return null;
        }

        if ( configuration instanceof ConfigurationType )
        {
            return new org.openscada.da.server.snmp.Hive ( (ConfigurationType)configuration, this.mibManager );
        }
        else if ( configuration instanceof String )
        {
            return new org.openscada.da.server.snmp.Hive ( (String)configuration, this.mibManager );
        }
        else
        {
            return null;
        }
    }

}
