/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2007 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openscada.da.server.ice;

import java.io.IOException;

import org.openscada.da.core.server.Hive;
import org.openscada.da.server.common.impl.ExporterBase;
import org.openscada.da.server.ice.impl.HiveImpl;

import Ice.Communicator;

public class Exporter extends ExporterBase implements Runnable
{
    private Communicator _communicator = null;

    public Exporter ( String hiveClassName, Communicator communicator ) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException
    {
        super ( hiveClassName );
        _communicator = communicator;
    }
    
    public Exporter ( Hive hive, Communicator communicator ) throws IOException
    {
        super ( hive );
        _communicator = communicator;
    }
    
    public Exporter ( Class hiveClass, Communicator communicator ) throws InstantiationException, IllegalAccessException, IOException
    {
        super ( hiveClass );
        _communicator = communicator;
    }

    public void run ()
    {
        Ice.ObjectAdapter adapter = _communicator.createObjectAdapter ( "Hive" );
        adapter.add ( new HiveImpl ( _hive, adapter ), _communicator.stringToIdentity ( "hive" ) );
        adapter.activate ();
        _communicator.waitForShutdown ();
    }

}
