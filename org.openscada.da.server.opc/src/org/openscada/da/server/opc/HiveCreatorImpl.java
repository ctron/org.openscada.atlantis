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

package org.openscada.da.server.opc;

import org.eclipse.scada.da.core.server.Hive;
import org.eclipse.scada.da.core.server.HiveCreator;
import org.openscada.da.opc.configuration.RootType;

public class HiveCreatorImpl implements HiveCreator
{

    @Override
    public Hive createHive ( final String reference, final Object configuration ) throws Exception
    {
        if ( !reference.equals ( org.openscada.da.server.opc.Hive.class.getName () ) )
        {
            return null;
        }

        if ( configuration instanceof RootType )
        {
            return new org.openscada.da.server.opc.Hive ( (RootType)configuration );
        }
        else if ( configuration instanceof String )
        {
            return new org.openscada.da.server.opc.Hive ( (String)configuration );
        }
        else
        {
            return null;
        }
    }

}
